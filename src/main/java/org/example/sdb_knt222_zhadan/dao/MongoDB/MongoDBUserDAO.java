package org.example.sdb_knt222_zhadan.dao.MongoDB;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.example.sdb_knt222_zhadan.config.MongoDBConnection;
import org.example.sdb_knt222_zhadan.dao.UserDAO;
import org.example.sdb_knt222_zhadan.model.User;
import org.example.sdb_knt222_zhadan.model.builder.UserBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class MongoDBUserDAO implements UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(MongoDBUserDAO.class);
    private final MongoCollection<Document> userCollection;

    public MongoDBUserDAO() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        this.userCollection = database.getCollection("user");
        logger.info("Колекція user в MongoDB доступна");
    }

    @Override
    public List<User> getAllUsers() {
        logger.info("Отримання всіх користувачів з MongoDB");
        List<User> user = new ArrayList<>();
        FindIterable<Document> documents = userCollection.find();
        for (Document doc : documents) {
            user.add(mapDocumentToUser(doc));
        }
        return user;
    }

    @Override
    public User getUserById(int userId) {
        logger.info("Отримання користувача з ID: {} з MongoDB", userId);
        Document doc = userCollection.find(eq("_id", userId)).first();
        if (doc != null) {
            return mapDocumentToUser(doc);
        } else {
            logger.warn("Користувача з ID: {} не знайдено в MongoDB", userId);
            return null;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        logger.info("Отримання користувача за email: {} з MongoDB", email);
        Document doc = userCollection.find(eq("email", email)).first();
        if (doc != null) {
            return mapDocumentToUser(doc);
        } else {
            logger.warn("Користувача з email: {} не знайдено в MongoDB", email);
            return null;
        }
    }

    @Override
    public void addUser(User user) {
        logger.info("Додавання користувача: {} до MongoDB", user.getEmail());
        Document doc = new Document("_id", user.getUserId())
                .append("firstname", user.getFirstname())
                .append("lastname", user.getLastname())
                .append("email", user.getEmail())
                .append("password", user.getPassword())
                .append("phone", user.getPhone())
                .append("role", user.getRole());
        try {
            userCollection.insertOne(doc);
            logger.info("Користувача з email: {} успішно додано до MongoDB", user.getEmail());
        } catch (Exception e) {
            logger.error("Помилка під час додавання користувача до MongoDB: {}", user.getEmail(), e);
            throw new RuntimeException("Помилка під час додавання користувача до MongoDB", e);
        }
    }

    @Override
    public void updateUser(User user) {
        logger.info("Оновлення користувача з ID: {} в MongoDB", user.getUserId());
        Document updatedDoc = new Document("$set", new Document("firstname", user.getFirstname())
                .append("lastname", user.getLastname())
                .append("email", user.getEmail())
                .append("password", user.getPassword())
                .append("phone", user.getPhone())
                .append("role", user.getRole()));
        UpdateResult result = userCollection.updateOne(eq("_id", user.getUserId()), updatedDoc);
        if (result.getMatchedCount() > 0) {
            logger.info("Користувача з ID: {} успішно оновлено в MongoDB", user.getUserId());
        } else {
            logger.warn("Користувача з ID: {} не знайдено або не потребує оновлення в MongoDB", user.getUserId());
            throw new RuntimeException("Користувача з ID: " + user.getUserId() + " не знайдено в MongoDB");
        }
    }

    @Override
    public void deleteUser(int userId) {
        logger.info("Видалення користувача з ID: {} з MongoDB", userId);
        DeleteResult result = userCollection.deleteOne(eq("_id", userId));
        if (result.getDeletedCount() > 0) {
            logger.info("Користувача з ID: {} успішно видалено з MongoDB", userId);
        } else {
            logger.warn("Користувача з ID: {} не знайдено в MongoDB", userId);
            throw new RuntimeException("Користувача з ID: " + userId + " не знайдено в MongoDB");
        }
    }

    private User mapDocumentToUser(Document doc) {
        return new UserBuilder()
                .setUserId(doc.getInteger("_id"))
                .setFirstname(doc.getString("firstname"))
                .setLastname(doc.getString("lastname"))
                .setEmail(doc.getString("email"))
                .setPassword(doc.getString("password"))
                .setPhone(doc.getString("phone"))
                .setRole(doc.getString("role"))
                .build();
    }
}
