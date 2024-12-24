package org.example.sdb_knt222_zhadan.dao.MongoDB;

import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.example.sdb_knt222_zhadan.config.MongoDBConnection;
import org.example.sdb_knt222_zhadan.dao.EquipmentDAO;
import org.example.sdb_knt222_zhadan.model.Equipment;
import org.example.sdb_knt222_zhadan.model.builder.EquipmentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class MongoDBEquipmentDAO implements EquipmentDAO {
    private static final Logger logger = LoggerFactory.getLogger(MongoDBEquipmentDAO.class);
    private final MongoCollection<Document> equipmentCollection;

    public MongoDBEquipmentDAO() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        this.equipmentCollection = database.getCollection("equipment").withWriteConcern(WriteConcern.W1);
        logger.info("Колекція equipment в MongoDB доступна");
    }

//    public MongoDBEquipmentDAO() {
//        MongoDatabase database = MongoDBConnection.getDatabase();
//        this.equipmentCollection = database.getCollection("equipment");
//        logger.info("Колекція equipment в MongoDB доступна");
//    }

    @Override
    public List<Equipment> getAllEquipment() {
        logger.info("Отримання всього обладнання з MongoDB");
        List<Equipment> equipmentList = new ArrayList<>();
        for (Document doc : equipmentCollection.find()) {
            equipmentList.add(mapDocumentToEquipment(doc));
        }
        return equipmentList;
    }

    @Override
    public Equipment getEquipmentById(int equipmentId) {
        logger.info("Отримання обладнання з ID: {} з MongoDB", equipmentId);
        Document doc = equipmentCollection.find(eq("_id", equipmentId)).first();
        return doc != null ? mapDocumentToEquipment(doc) : null;
    }

    @Override
    public Equipment getEquipmentBySerialNumber(String serialNumber) {
        logger.info("Отримання обладнання з серійним номером: {} з MongoDB", serialNumber);
        Document doc = equipmentCollection.find(eq("serial_number", serialNumber)).first();
        return doc != null ? mapDocumentToEquipment(doc) : null;
    }

//    @Override
//    public void addEquipment(Equipment equipment) {
//        // logger.info("Додавання обладнання до MongoDB: {}", equipment.getSerialNumber());
//        Document doc = new Document("_id", equipment.getEquipmentId())
//                .append("serial_number", equipment.getSerialNumber())
//                .append("model", equipment.getModel())
//                .append("type", equipment.getType())
//                .append("purchase_date", equipment.getPurchaseDate());
//        try {
//            equipmentCollection.insertOne(doc);
//            // logger.info("Обладнання з серійним номером: {} успішно додано до MongoDB", equipment.getSerialNumber());
//        } catch (Exception e) {
//            logger.error("Помилка під час додавання обладнання до MongoDB: {}", equipment.getSerialNumber(), e);
//            throw new RuntimeException("Помилка під час додавання обладнання до MongoDB", e);
//        }
//    }

    @Override
    public void addEquipment(Equipment equipment) {
        Document doc = new Document()
                .append("_id", equipment.getEquipmentId())
                .append("serial_number", equipment.getSerialNumber())
                .append("model", equipment.getModel())
                .append("type", equipment.getType())
                .append("purchase_date", equipment.getPurchaseDate());
        try {
            equipmentCollection.insertOne(doc);
            // logger.info("Обладнання з серійним номером: {} успішно додано до MongoDB", equipment.getSerialNumber());
        } catch (Exception e) {
            logger.error("Помилка під час додавання обладнання до MongoDB: {}", equipment.getSerialNumber(), e);
            throw new RuntimeException("Помилка під час додавання обладнання до MongoDB", e);
        }
    }

    @Override
    public void updateEquipment(Equipment equipment) {
        logger.info("Оновлення обладнання з ID: {} в MongoDB", equipment.getEquipmentId());
        Document update = new Document("$set", new Document("serial_number", equipment.getSerialNumber())
                .append("model", equipment.getModel())
                .append("type", equipment.getType())
                .append("purchase_date", equipment.getPurchaseDate()));
        UpdateResult result = equipmentCollection.updateOne(eq("_id", equipment.getEquipmentId()), update);
        if (result.getModifiedCount() == 0) {
            logger.warn("Обладнання з ID: {} не знайдено для оновлення", equipment.getEquipmentId());
        }
    }

    @Override
    public void deleteEquipment(int equipmentId) {
        logger.info("Видалення обладнання з ID: {} з MongoDB", equipmentId);
        DeleteResult result = equipmentCollection.deleteOne(eq("_id", equipmentId));
        if (result.getDeletedCount() == 0) {
            logger.warn("Обладнання з ID: {} не знайдено для видалення", equipmentId);
        }
    }

    public List<Equipment> getEquipmentByModelAndType(String model, String type) {
        List<Equipment> equipmentList = new ArrayList<>();
        for (Document doc : equipmentCollection.find(
                Filters.and(
                        Filters.eq("model", model),
                        Filters.eq("type", type)
                )
        )) {
            equipmentList.add(mapDocumentToEquipment(doc));
        }
        return equipmentList;
    }

    private Equipment mapDocumentToEquipment(Document doc) {
        return new EquipmentBuilder()
                .setEquipmentId(doc.getInteger("_id"))
                .setSerialNumber(doc.getString("serial_number"))
                .setModel(doc.getString("model"))
                .setType(doc.getString("type"))
                .setPurchaseDate(doc.getDate("purchase_date"))
                .build();
    }
}