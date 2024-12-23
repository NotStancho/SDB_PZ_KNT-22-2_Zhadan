package org.example.sdb_knt222_zhadan.service;

import org.example.sdb_knt222_zhadan.dao.*;
import org.example.sdb_knt222_zhadan.dao.Factory.DAOFactory;
import org.example.sdb_knt222_zhadan.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MigrationService {
    private static final Logger logger = LoggerFactory.getLogger(MigrationService.class);

    private final UserDAO mySQLUserDAO;
    private final UserDAO mongoDBUserDAO;

    private final EquipmentDAO mySQLEquipmentDAO;
    private final EquipmentDAO mongoDBEquipmentDAO;

    private final StatusDAO mySQLStatusDAO;
    private final StatusDAO mongoDBStatusDAO;

    private final ClaimDAO mySQLClaimDAO;
    private final ClaimDAO mongoDBClaimDAO;

    private final Claim_HistoryDAO mySQLClaimHistoryDAO;
    private final Claim_HistoryDAO mongoDBClaimHistoryDAO;

    @Autowired
    public MigrationService(@Qualifier("mySQLFactory") DAOFactory mySQLFactory,
                            @Qualifier("mongoDBFactory") DAOFactory mongoDBFactory) {
        this.mySQLUserDAO = mySQLFactory.createUserDAO();
        this.mongoDBUserDAO = mongoDBFactory.createUserDAO();

        this.mySQLEquipmentDAO = mySQLFactory.createEquipmentDAO();
        this.mongoDBEquipmentDAO = mongoDBFactory.createEquipmentDAO();

        this.mySQLStatusDAO = mySQLFactory.createStatusDAO();
        this.mongoDBStatusDAO = mongoDBFactory.createStatusDAO();

        this.mySQLClaimDAO = mySQLFactory.createClaimDAO();
        this.mongoDBClaimDAO = mongoDBFactory.createClaimDAO();

        this.mySQLClaimHistoryDAO = mySQLFactory.createClaim_HistoryDAO();
        this.mongoDBClaimHistoryDAO = mongoDBFactory.createClaim_HistoryDAO();
    }

    // Міграція даних з MySQL до MongoDB
    public void migrateMySQLToMongoDB() {
        logger.info("Початок міграції з MySQL до MongoDB");

        migrateUsers(mySQLUserDAO, mongoDBUserDAO);
        migrateStatuses(mySQLStatusDAO, mongoDBStatusDAO);
        migrateEquipment(mySQLEquipmentDAO, mongoDBEquipmentDAO);
        migrateClaims(mySQLClaimDAO, mongoDBClaimDAO, mySQLClaimHistoryDAO, mongoDBClaimHistoryDAO);

        logger.info("Міграція з MySQL до MongoDB завершена");
    }

    // Міграція даних з MongoDB до MySQL
    public void migrateMongoDBToMySQL() {
        logger.info("Початок міграції з MongoDB до MySQL");

        migrateUsers(mongoDBUserDAO, mySQLUserDAO);
        migrateStatuses(mongoDBStatusDAO, mySQLStatusDAO);
        migrateEquipment(mongoDBEquipmentDAO, mySQLEquipmentDAO);
        migrateClaims(mongoDBClaimDAO, mySQLClaimDAO, mongoDBClaimHistoryDAO, mySQLClaimHistoryDAO);

        logger.info("Міграція з MongoDB до MySQL завершена");
    }

    // Метод для міграції користувачів
    private void migrateUsers(UserDAO sourceDAO, UserDAO targetDAO) {
        logger.info("Міграція користувачів");
        List<User> users = sourceDAO.getAllUsers();
        for (User user : users) {
            if (!userExists(targetDAO, user)) {
                targetDAO.addUser(user);
                logger.info("Додано користувача: {}", user.getEmail());
            } else {
                logger.info("Користувач з email {} вже існує. Пропуск.", user.getEmail());
            }
        }
    }

    // Метод для перевірки наявності користувача
    private boolean userExists(UserDAO targetDAO, User user) {
        User existingUser = targetDAO.getUserByEmail(user.getEmail());
        if (existingUser == null) {
            return false;
        }
        return existingUser.getFirstname().equals(user.getFirstname()) &&
                existingUser.getLastname().equals(user.getLastname()) &&
                existingUser.getPhone().equals(user.getPhone()) &&
                existingUser.getRole().equals(user.getRole());
    }

    // Метод для міграції статусів
    private void migrateStatuses(StatusDAO sourceDAO, StatusDAO targetDAO) {
        logger.info("Міграція статусів");
        List<Status> statuses = sourceDAO.getAllStatus();
        for (Status status : statuses) {
            if (!statusExists(targetDAO, status)) {
                targetDAO.addStatus(status);
                logger.info("Додано статус: {}", status.getName());
            } else {
                logger.info("Статус {} вже існує. Пропуск.", status.getName());
            }
        }
    }

    // Метод для перевірки наявності статусу
    private boolean statusExists(StatusDAO targetDAO, Status status) {
        Status existingStatus = targetDAO.getStatusById(status.getStatusId());
        if (existingStatus != null) {
            return existingStatus.getName().equals(status.getName()) &&
                    existingStatus.getDescription().equals(status.getDescription());
        }
        return false;
    }

    // Метод для міграції обладнання
    private void migrateEquipment(EquipmentDAO sourceDAO, EquipmentDAO targetDAO) {
        logger.info("Міграція обладнання");
        List<Equipment> equipmentList = sourceDAO.getAllEquipment();
        for (Equipment equipment : equipmentList) {
            if (!equipmentExists(targetDAO, equipment)) {
                targetDAO.addEquipment(equipment);
                logger.info("Додано обладнання: {}", equipment.getSerialNumber());
            } else {
                logger.info("Обладнання з серійним номером {} вже існує. Пропуск.", equipment.getSerialNumber());
            }
        }
    }

    // Метод для перевірки наявності обладнання
    private boolean equipmentExists(EquipmentDAO targetDAO, Equipment equipment) {
        Equipment existingEquipment = targetDAO.getEquipmentBySerialNumber(equipment.getSerialNumber());
        if (existingEquipment == null) {
            return false;
        }
        return existingEquipment.getModel().equals(equipment.getModel()) &&
                existingEquipment.getType().equals(equipment.getType()) &&
                existingEquipment.getPurchaseDate().equals(equipment.getPurchaseDate());
    }

    // Метод для міграції заявок та історії заявок
    private void migrateClaims(ClaimDAO sourceDAO, ClaimDAO targetDAO,
                               Claim_HistoryDAO sourceHistoryDAO, Claim_HistoryDAO targetHistoryDAO) {
        logger.info("Міграція заявок");
        List<Claim> claims = sourceDAO.getAllClaim();
        for (Claim claim : claims) {
            if (!claimExists(targetDAO, claim)) {
                targetDAO.addClaim(claim, 1);
                logger.info("Додано заявку з ID: {}", claim.getClaimId());

                // Міграція Claim_History
                List<Claim_History> histories = sourceHistoryDAO.getClaimHistoryByClaimId(claim.getClaimId());
                for (Claim_History history : histories) {
                    if (!historyExists(targetHistoryDAO, history)) {
                        targetHistoryDAO.addClaimHistory(history);
                        logger.info("Додано історію заявки з ID: {} для співробітника: {}",
                                claim.getClaimId(), history.getEmployee().getEmail());
                    } else {
                        logger.info("Історія заявки з ID: {} для співробітника: {} вже існує. Пропуск.",
                                claim.getClaimId(), history.getEmployee().getEmail());
                    }
                }

            } else {
                logger.info("Заявка з ID: {} вже існує. Пропуск.", claim.getClaimId());
            }
        }
    }

    // Метод для перевірки наявності заявки
    private boolean claimExists(ClaimDAO targetDAO, Claim claim) {
        Claim existingClaim = targetDAO.getClaimById(claim.getClaimId());
        if (existingClaim != null) {
            // Перевірка відповідності основних полів
            return existingClaim.getDefectDescription().equals(claim.getDefectDescription()) &&
                    existingClaim.getStatus().getStatusId() == claim.getStatus().getStatusId() &&
                    existingClaim.getClient().getUserId() == claim.getClient().getUserId() &&
                    existingClaim.getEquipment().getEquipmentId() == claim.getEquipment().getEquipmentId();
        }
        return false;
    }

    // Метод для перевірки наявності історії заявки
    private boolean historyExists(Claim_HistoryDAO targetHistoryDAO, Claim_History history) {
        List<Claim_History> existingHistories = targetHistoryDAO.getClaimHistoryByClaimId(history.getClaim().getClaimId());
        for (Claim_History existingHistory : existingHistories) {
            boolean isEmployeeEqual = true;

            // Перевірка, чи employee не null
            if (history.getEmployee() != null && existingHistory.getEmployee() != null) {
                isEmployeeEqual = history.getEmployee().getUserId() == existingHistory.getEmployee().getUserId();
            } else if (history.getEmployee() == null && existingHistory.getEmployee() != null || history.getEmployee() != null && existingHistory.getEmployee() == null) {
                isEmployeeEqual = false;
            }

            if (isEmployeeEqual &&
                    existingHistory.getActionDate().equals(history.getActionDate()) &&
                    existingHistory.getActionDescription().equals(history.getActionDescription())) {
                return true;
            }
        }
        return false;
    }
}
