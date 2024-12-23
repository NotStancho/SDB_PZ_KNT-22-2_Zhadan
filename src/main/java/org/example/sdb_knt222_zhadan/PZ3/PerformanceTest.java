package org.example.sdb_knt222_zhadan.PZ3;

import org.example.sdb_knt222_zhadan.dao.Factory.MongoDBDAOFactory;
import org.example.sdb_knt222_zhadan.dao.Factory.MySQLDAOFactory;
import org.example.sdb_knt222_zhadan.dao.MySQL.MySQLEquipmentDAO;
import org.example.sdb_knt222_zhadan.dao.MongoDB.MongoDBEquipmentDAO;
import org.example.sdb_knt222_zhadan.model.Equipment;
import org.example.sdb_knt222_zhadan.service.MigrationService;


import java.sql.Date;
import java.util.List;
import java.util.Map;

public class PerformanceTest {
    public static void main(String[] args) {
        MySQLDAOFactory mySQLFactory = new MySQLDAOFactory();
        MongoDBDAOFactory mongoDBFactory = new MongoDBDAOFactory();

        // Створення екземпляра MigrationService
        MigrationService migrationService = new MigrationService(mySQLFactory, mongoDBFactory);

        // Виконання міграції з MySQL до MongoDB
        System.out.println("=== Міграція з MySQL до MongoDB ===");
        migrationService.migrateMySQLToMongoDB();

        // Виконання міграції з MongoDB до MySQL
        System.out.println("=== Міграція з MongoDB до MySQL ===");
        migrationService.migrateMongoDBToMySQL();
    }
}

//    public static void main(String[] args) {
//        MySQLEquipmentDAO mySQLDAO = new MySQLEquipmentDAO();
//        MongoDBEquipmentDAO mongoDBDAO = new MongoDBEquipmentDAO();
//
//        // Розміри тестових наборів даних
//        int[] recordCounts = {10};
//        // int[] recordCounts = {100, 1000, 10000, 50000, 100000, 500000};
//
//        for (int count : recordCounts) {
//            System.out.println("Testing with " + count + " records\n");
//
//            // Генерація даних
//            List<Map<String, String>> equipmentList = EquipmentGenerator.generateEquipmentList(count);
//
//            // Тестування вставки
//            testInsertPerformance(mySQLDAO, mongoDBDAO, equipmentList);
//
//            // Тестування читання
//            testSelectPerformance(mySQLDAO, mongoDBDAO, count);
//        }
//    }

//    private static void testInsertPerformance(MySQLEquipmentDAO mySQLDAO, MongoDBEquipmentDAO mongoDBDAO, List<Map<String, String>> equipmentList) {
//        System.out.println("Insert Performance:");
//
//        // MySQL вставка
//        long startMySQL = System.nanoTime();
//        int id = 1;
//        for (Map<String, String> record : equipmentList) {
//            Equipment equipment = mapToEquipment(record, id++);
//            mySQLDAO.addEquipment(equipment);
//        }
//        long endMySQL = System.nanoTime();
//        System.out.println("MySQL INSERT time: " + (endMySQL - startMySQL) + " nanoseconds");
//
//        // MongoDB вставка
//        long startMongoDB = System.nanoTime();
//        id = 1;
//        for (Map<String, String> record : equipmentList) {
//            Equipment equipment = mapToEquipment(record, id++);
//            mongoDBDAO.addEquipment(equipment);
//        }
//        long endMongoDB = System.nanoTime();
//        System.out.println("MongoDB INSERT time: " + (endMongoDB - startMongoDB) + " nanoseconds");
//    }
//
//    private static void testSelectPerformance(MySQLEquipmentDAO mySQLDAO, MongoDBEquipmentDAO mongoDBDAO, int count) {
//        System.out.println("\nSelect Performance:");
//
//        // MySQL читання всіх записів
//        long startMySQL = System.nanoTime();
//        mySQLDAO.getAllEquipment();
//        long endMySQL = System.nanoTime();
//        System.out.println("MySQL SELECT * time for " + count + " records: " + (endMySQL - startMySQL) + " nanoseconds");
//
//        // MongoDB читання всіх записів
//        long startMongoDB = System.nanoTime();
//        mongoDBDAO.getAllEquipment();
//        long endMongoDB = System.nanoTime();
//        System.out.println("MongoDB SELECT * time for " + count + " records: " + (endMongoDB - startMongoDB) + " nanoseconds\n");
//
//        // Читання з умовами WHERE
//        String testModel = "HealthCorp X100";
//        String testType = "Мікроскоп";
//
//        System.out.println("\nSelect Performance with WHERE conditions:");
//        // MySQL: SELECT with WHERE
//        long startMySQLWhere = System.nanoTime();
//        List<Equipment> mysqlFilteredResults = mySQLDAO.getEquipmentByModelAndType(testModel , testType);
//        long endMySQLWhere = System.nanoTime();
//        System.out.println("MySQL SELECT with WHERE time: " + (endMySQLWhere - startMySQLWhere) + " nanoseconds. Records found: " + mysqlFilteredResults.size());
//
//        // MongoDB: SELECT with WHERE
//        long startMongoDBWhere = System.nanoTime();
//        List<Equipment> mongoFilteredResults = mongoDBDAO.getEquipmentByModelAndType(testModel , testType);
//        long endMongoDBWhere = System.nanoTime();
//        System.out.println("MongoDB SELECT with WHERE time: " + (endMongoDBWhere - startMongoDBWhere) + " nanoseconds. Records found: " + mongoFilteredResults.size());
//
//    }
//
//    private static Equipment mapToEquipment(Map<String, String> record, int id) {
//        Equipment equipment = new Equipment();
//        equipment.setEquipmentId(id);
//        equipment.setSerialNumber(record.get("serial_number"));
//        equipment.setModel(record.get("model"));
//        equipment.setType(record.get("type"));
//        equipment.setPurchaseDate(Date.valueOf(record.get("purchase_date")));
//        return equipment;
//    }