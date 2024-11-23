package org.example.sdb_knt222_zhadan.dao.MongoDB;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.sdb_knt222_zhadan.config.MongoDBConnection;
import org.example.sdb_knt222_zhadan.dao.Claim_HistoryDAO;
import org.example.sdb_knt222_zhadan.model.Claim_History;
import org.example.sdb_knt222_zhadan.model.User;
import org.example.sdb_knt222_zhadan.model.builder.Claim_HistoryBuilder;
import org.example.sdb_knt222_zhadan.model.builder.UserBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class MongoDBClaim_HistoryDAO implements Claim_HistoryDAO {
    private static final Logger logger = LoggerFactory.getLogger(MongoDBClaim_HistoryDAO.class);

    private final MongoCollection<Document> claimCollection;
    private final MongoCollection<Document> userCollection;

    public MongoDBClaim_HistoryDAO() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        this.claimCollection = database.getCollection("claim");
        this.userCollection = database.getCollection("user");
        logger.info("Колекції claim та user в MongoDB доступні");
    }

    @Override
    public List<Claim_History> getClaimHistoryByClaimId(int claimId) {
        List<Claim_History> historyList = new ArrayList<>();
        Document claimDoc = claimCollection.find(eq("_id", claimId)).first();
        if (claimDoc != null) {
            List<Document> historyDocs = claimDoc.getList("claim_history", Document.class);
            if (historyDocs != null) {
                for (Document historyDoc : historyDocs) {
                    int employeeId = historyDoc.getInteger("employee_id");
                    Document employeeDoc = userCollection.find(eq("user_id", employeeId)).first();
                    User employee = null;
                    if (employeeDoc != null) {
                        employee = new UserBuilder()
                                .setUserId(employeeDoc.getInteger("user_id"))
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
                            .build();

                    historyList.add(history);
                }
            }
        }
        return historyList;
    }
}
