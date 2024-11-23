package org.example.sdb_knt222_zhadan.dao.MongoDB;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.example.sdb_knt222_zhadan.config.MongoDBConnection;
import org.example.sdb_knt222_zhadan.dao.StatusDAO;
import org.example.sdb_knt222_zhadan.model.Status;
import org.example.sdb_knt222_zhadan.model.builder.StatusBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class MongoDBStatusDAO implements StatusDAO {
    private static final Logger logger = LoggerFactory.getLogger(MongoDBStatusDAO.class);
    private final MongoCollection<Document> statusCollection;

    public MongoDBStatusDAO() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        this.statusCollection = database.getCollection("status");
        logger.info("Колекція status в MongoDB доступна");
    }

    @Override
    public List<Status> getAllStatus() {
        logger.info("Отримання всіх статусів з MongoDB");
        List<Status> statuses = new ArrayList<>();
        for (Document doc : statusCollection.find()) {
            statuses.add(mapDocumentToStatus(doc));
        }
        return statuses;
    }

    @Override
    public Status getStatusById(int statusId) {
        logger.info("Отримання статусу з ID: {} з MongoDB", statusId);
        Document doc = statusCollection.find(eq("_id", statusId)).first();
        if (doc != null) {
            return mapDocumentToStatus(doc);
        } else {
            logger.warn("Статус з ID: {} не знайдено в MongoDB", statusId);
            return null;
        }
    }

    @Override
    public void addStatus(Status status) {
        logger.info("Додавання статусу: {} до MongoDB", status.getName());
        Document doc = new Document("_id", status.getStatusId())
                .append("name", status.getName())
                .append("description", status.getDescription());
        try {
            statusCollection.insertOne(doc);
            logger.info("Статус успішно додано до MongoDB: {}", status.getName());
        } catch (Exception e) {
            logger.error("Помилка під час додавання статусу до MongoDB: {}", status.getName(), e);
            throw new RuntimeException("Помилка під час додавання статусу до MongoDB", e);
        }
    }

    @Override
    public void updateStatus(Status status) {
        logger.info("Оновлення статусу з ID: {} в MongoDB", status.getStatusId());
        Document updatedDoc = new Document("$set", new Document("name", status.getName())
                .append("description", status.getDescription()));
        UpdateResult result = statusCollection.updateOne(eq("_id", status.getStatusId()), updatedDoc);
        if (result.getModifiedCount() > 0) {
            logger.info("Статус з ID: {} успішно оновлено в MongoDB", status.getStatusId());
        } else {
            logger.warn("Статус з ID: {} не знайдено або не потребує оновлення в MongoDB", status.getStatusId());
        }
    }

    @Override
    public void deleteStatus(int statusId) {
        logger.info("Видалення статусу з ID: {} з MongoDB", statusId);
        DeleteResult result = statusCollection.deleteOne(eq("_id", statusId));
        if (result.getDeletedCount() > 0) {
            logger.info("Статус з ID: {} успішно видалено з MongoDB", statusId);
        } else {
            logger.warn("Статус з ID: {} не знайдено в MongoDB", statusId);
        }
    }

    private Status mapDocumentToStatus(Document doc) {
        return new StatusBuilder()
                .setStatusId(doc.getInteger("_id"))
                .setName(doc.getString("name"))
                .setDescription(doc.getString("description"))
                .build();
    }
}
