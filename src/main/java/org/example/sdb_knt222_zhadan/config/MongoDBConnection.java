package org.example.sdb_knt222_zhadan.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class MongoDBConnection {
    private static final Logger logger = LoggerFactory.getLogger(MongoDBConnection.class);
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    private MongoDBConnection() {}

    public static synchronized MongoDatabase getDatabase() {
        if (database == null) {
            try {
                Properties properties = new Properties();
                try (InputStream input = MongoDBConnection.class.getClassLoader().getResourceAsStream("application.properties")) {
                    if (input == null) {
                        throw new RuntimeException("Не вдалося знайти файл конфігурації application.properties");
                    }
                    properties.load(input);
                }

                String uri = properties.getProperty("spring.data.mongodb.uri");
                String dbName = properties.getProperty("spring.data.mongodb.database");

                mongoClient = MongoClients.create(uri);
                database = mongoClient.getDatabase(dbName);
                logger.info("Підключення до MongoDB встановлено");
            } catch (Exception e) {
                logger.error("Не вдалося підключитися до MongoDB", e);
                throw new RuntimeException("Не вдалося підключитися до MongoDB", e);
            }
        }
        return database;
    }
}
