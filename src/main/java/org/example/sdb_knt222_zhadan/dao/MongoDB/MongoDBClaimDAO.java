package org.example.sdb_knt222_zhadan.dao.MongoDB;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.sdb_knt222_zhadan.config.MongoDBConnection;
import org.example.sdb_knt222_zhadan.dao.ClaimDAO;
import org.example.sdb_knt222_zhadan.model.*;
import org.example.sdb_knt222_zhadan.model.builder.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class MongoDBClaimDAO implements ClaimDAO {
    private static final Logger logger = LoggerFactory.getLogger(MongoDBClaimDAO.class);
    private final MongoCollection<Document> claimCollection;
    private final MongoCollection<Document> statusCollection;
    private final MongoCollection<Document> userCollection;

    public MongoDBClaimDAO() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        this.claimCollection = database.getCollection("claim");
        this.statusCollection = database.getCollection("status");
        this.userCollection = database.getCollection("user");
        logger.info("Колекції claim, status та user в MongoDB доступні");
    }

    @Override
    public List<Claim> getAllClaim() {
        logger.info("Отримання всіх заявок з MongoDB");
        List<Claim> claims = new ArrayList<>();
        for (Document doc : claimCollection.find()) {
            claims.add(mapDocumentToClaim(doc));
        }
        return claims;
    }

    @Override
    public List<Claim> getClientClaims(int clientId) {
        logger.info("Отримання заявок для клієнта з ID: {} з MongoDB", clientId);
        List<Claim> claims = new ArrayList<>();
        for (Document doc : claimCollection.find(eq("client_id", clientId))) {
            claims.add(mapDocumentToClaim(doc));
        }
        return claims;
    }

    @Override
    public Claim getClaimById(int claimId) {
        logger.info("Отримання заявки з ID: {} з MongoDB", claimId);
        Document doc = claimCollection.find(eq("_id", claimId)).first();
        if (doc != null) {
            return mapDocumentToClaim(doc);
        } else {
            logger.warn("Заявку з ID: {} не знайдено в MongoDB", claimId);
            return null;
        }
    }

    @Override
    public void addClaim(Claim claim, int employeeId) {
        logger.info("Додавання заявки до MongoDB");

        Document historyDoc = new Document()
                .append("employee_id", employeeId)
                .append("action_date", new java.util.Date())
                .append("action_description", "Клієнт подав заявку на ремонт.");

        Document claimDoc = new Document()
                .append("_id", claim.getClaimId())
                .append("client_id", claim.getClient().getUserId())
                .append("equipment_id", claim.getEquipment().getEquipmentId())
                .append("status_id", claim.getStatus().getStatusId())
                .append("defect_description", claim.getDefectDescription())
                .append("claim_history", List.of(historyDoc));

        claimCollection.insertOne(claimDoc);
        logger.info("Заявка успішно додана до MongoDB");
    }

    @Override
    public void updateClaim(Claim claim, int employeeId, String description) {
        logger.info("Оновлення заявки з ID: {} в MongoDB", claim.getClaimId());

        // Отримання старого документу
        Document existingClaim = claimCollection.find(eq("_id", claim.getClaimId())).first();
        if (existingClaim == null) {
            logger.warn("Заявку з ID: {} не знайдено в MongoDB", claim.getClaimId());
            return;
        }
        // Перевірка зміни статусу
        int oldStatus = existingClaim.getInteger("status_id");
        int newStatus = claim.getStatus().getStatusId();

        String oldStatusName = getStatusNameById(oldStatus);
        String newStatusName = getStatusNameById(newStatus);

        String employeeName = getEmployeeNameById(employeeId);

        // Формування опису дії
        String actionDescription = description;
        if (oldStatus != newStatus) {
            actionDescription = String.format(
                    "Статус змінено з '%s' на '%s' співробітником %s. Додатковий опис: %s",
                    oldStatusName, newStatusName, employeeName, description
            );
        }

        // Оновлення заявки
        Document updateFields = new Document("$set", new Document()
                .append("status_id", newStatus)
                .append("defect_description", claim.getDefectDescription()))
                .append("$push", new Document("claim_history", new Document()
                        .append("employee_id", employeeId)
                        .append("action_date", new java.util.Date())
                        .append("action_description", actionDescription)));
        claimCollection.updateOne(eq("_id", claim.getClaimId()), updateFields);
        logger.info("Заявка з ID: {} успішно оновлена в MongoDB", claim.getClaimId());

        if (claim.getEquipment() != null) {
            MongoDBEquipmentDAO equipmentDAO = new MongoDBEquipmentDAO();
            equipmentDAO.updateEquipment(claim.getEquipment());
            logger.info("Обладнання для заявки з ID: {} успішно оновлено", claim.getClaimId());
        }
    }

    private String getStatusNameById(int statusId) {
        Document statusDoc = statusCollection.find(eq("_id", statusId)).first();
        return statusDoc != null ? statusDoc.getString("name") : "Невідомий статус";
    }
    private String getEmployeeNameById(int employeeId) {
        Document employeeDoc = userCollection.find(eq("_id", employeeId)).first();
        if (employeeDoc != null) {
            return employeeDoc.getString("firstname") + " " + employeeDoc.getString("lastname");
        }
        return "Невідомий співробітник";
    }

    @Override
    public void deleteClaim(int claimId) {
        logger.info("Видалення заявки з ID: {} з MongoDB", claimId);
        claimCollection.deleteOne(eq("_id", claimId));
        logger.info("Заявка з ID: {} успішно видалена з MongoDB", claimId);
    }

    private Claim mapDocumentToClaim(Document doc) {
        Equipment equipment = new EquipmentBuilder()
                .setEquipmentId(doc.getInteger("equipment_id"))
                .setSerialNumber(doc.getString("serial_number"))
                .setModel(doc.getString("model"))
                .setType(doc.getString("type"))
                .setPurchaseDate(doc.getDate("purchase_date"))
                .build();

        int statusId = doc.getInteger("status_id");
        Document statusDoc = statusCollection.find(eq("_id", statusId)).first();
        Status status = null;
        if (statusDoc != null) {
            status = new StatusBuilder()
                    .setStatusId(statusId)
                    .setName(statusDoc.getString("name"))
                    .setDescription(statusDoc.getString("description"))
                    .build();
        }

        int clientId = doc.getInteger("client_id");
        Document clientDoc = userCollection.find(eq("_id", clientId)).first();
        User client = null;
        if (clientDoc != null) {
            client = new UserBuilder()
                    .setUserId(clientDoc.getInteger("_id"))
                    .setFirstname(clientDoc.getString("firstname"))
                    .setLastname(clientDoc.getString("lastname"))
                    .setEmail(clientDoc.getString("email"))
                    .setPhone(clientDoc.getString("phone"))
                    .setRole(clientDoc.getString("role"))
                    .build();
        }

        Claim claim = new ClaimBuilder()
                .setClaimId(doc.get("_id", Integer.class))
                .setDefectDescription(doc.getString("defect_description"))
                .setEquipment(equipment)
                .setStatus(status)
                .setClient(client)
                .build();

        List<Claim_History> historyList = new ArrayList<>();
        List<Document> historyDocs = doc.getList("claim_history", Document.class);
        if (historyDocs != null) {
            for (Document historyDoc : historyDocs) {
                int employeeId = historyDoc.getInteger("employee_id");
                Document employeeDoc = userCollection.find(eq("_id", employeeId)).first();
                User employee = null;
                if (employeeDoc != null) {
                    employee = new UserBuilder()
                            .setUserId(employeeDoc.getInteger("_id"))
                            .setFirstname(employeeDoc.getString("firstname"))
                            .setLastname(employeeDoc.getString("lastname"))
                            .setEmail(employeeDoc.getString("email"))
                            .setPhone(employeeDoc.getString("phone"))
                            .setRole(employeeDoc.getString("role"))
                            .build();
                }

                Claim_History history = new Claim_HistoryBuilder()
                        .setActionDate(new java.sql.Date(historyDoc.getDate("action_date").getTime()))
                        .setActionDescription(historyDoc.getString("action_description"))
                        .setEmployee(employee)
                        .setClaim(claim)
                        .build();

                historyList.add(history);
                logger.info("Додано запис історії для заявки з ID: {}", claim.getClaimId());
            }
        }
        return claim;
    }
}
