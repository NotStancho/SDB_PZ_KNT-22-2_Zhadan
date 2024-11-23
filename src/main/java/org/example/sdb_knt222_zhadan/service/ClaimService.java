package org.example.sdb_knt222_zhadan.service;

import org.example.sdb_knt222_zhadan.dao.ClaimDAO;
import org.example.sdb_knt222_zhadan.dao.Factory.DAOFactory;
import org.example.sdb_knt222_zhadan.model.Claim;
import org.example.sdb_knt222_zhadan.model.Equipment;
import org.example.sdb_knt222_zhadan.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClaimService {

    private static final Logger logger = LoggerFactory.getLogger(ClaimService.class);
    private final ClaimDAO mongoDBClaimDAO;
    private final ClaimDAO mySQLClaimDAO;
    private final EquipmentService equipmentService;

    @Autowired
    public ClaimService(@Qualifier("mySQLFactory") DAOFactory mySQLFactory,
                        @Qualifier("mongoDBFactory") DAOFactory mongoDBFactory,
                        EquipmentService equipmentService) {
        this.mySQLClaimDAO = mySQLFactory.createClaimDAO();
        this.mongoDBClaimDAO = mongoDBFactory.createClaimDAO();
        this.equipmentService = equipmentService;
    }

    public List<Claim> getAllClaims() {
        logger.info("Отримання всіх заявок з MySQL");
        try {
            return mySQLClaimDAO.getAllClaim();
        } catch (Exception e) {
            logger.error("Помилка під час отримання заявок з MySQL", e);
            throw new RuntimeException("Помилка під час отримання заявок", e);
        }
    }

    public List<Claim> getClientClaims(int clientId) {
        logger.info("Отримання заявок клієнта з ID: {} з MySQL", clientId);
        try {
            return mySQLClaimDAO.getClientClaims(clientId);
        } catch (Exception e) {
            logger.error("Помилка під час отримання заявок клієнта з MySQL", e);
            throw new RuntimeException("Помилка під час отримання заявок клієнта", e);
        }
    }

    public Claim getClaimById(int claimId) {
        logger.info("Отримання заявки з ID: {} з MySQL", claimId);
        try {
            return mySQLClaimDAO.getClaimById(claimId);
        } catch (Exception e) {
            logger.error("Помилка під час отримання заявки з ID: {}", claimId, e);
            throw new RuntimeException("Помилка під час отримання заявки", e);
        }
    }

    public void addClaim(Claim claim, int employeeId) {
        logger.info("Додавання нової заявки з ID: {} спочатку в MongoDB, потім в MySQL", claim.getClaimId());

        Equipment existingEquipment = equipmentService.getOrCreateEquipment(claim.getEquipment());
        if (existingEquipment != null && existingEquipment.getEquipmentId() > 0) {
            claim.setEquipment(existingEquipment);
        } else {
            logger.error("Не вдалося отримати або додати обладнання для серійного номеру: {}",
                    claim.getEquipment().getSerialNumber());
            throw new IllegalStateException("Помилка з обладнанням.");
        }

        Status initialStatus = new Status();
        initialStatus.setStatusId(1); // статус "Отримано" має ID = 1
        claim.setStatus(initialStatus);

        try {
            mySQLClaimDAO.addClaim(claim, employeeId);
            int generatedId = claim.getClaimId();
            claim.setClaimId(generatedId);

            mongoDBClaimDAO.addClaim(claim, employeeId);
            logger.info("Заявка з ID: {} успішно додана в обидві СУБД", claim.getClaimId());
        } catch (Exception e) {
            logger.error("Помилка під час додавання заявки: {}", claim.getClaimId(), e);
            try {
                mySQLClaimDAO.deleteClaim(claim.getClaimId());
                logger.info("Компенсація виконана: заявку видалено з MySQL");
            } catch (Exception ex) {
                logger.error("Помилка під час компенсації: не вдалося видалити заявку з MySQL", ex);
            }
            throw new RuntimeException("Помилка під час додавання заявки", e);
        }
    }

    public void updateClaim(Claim claim, int employeeId, String actionDescription) {
        logger.info("Оновлення заявки з ID: {} спочатку в MongoDB, потім в MySQL", claim.getClaimId());
        try {
            mySQLClaimDAO.updateClaim(claim, employeeId, actionDescription);
            logger.info("Заявку з ID: {} успішно оновлено в MySQL", claim.getClaimId());
            mongoDBClaimDAO.updateClaim(claim, employeeId, actionDescription);
            logger.info("Заявку з ID: {} успішно оновлено в MongoDB", claim.getClaimId());
        } catch (Exception e) {
            logger.error("Помилка під час оновлення заявки з ID: {}", claim.getClaimId(), e);
            throw new RuntimeException("Помилка під час оновлення заявки", e);
        }
    }

    public void deleteClaim(int claimId) {
        logger.info("Видалення заявки з ID: {} спочатку з MySQL, потім з MongoDB", claimId);
        try {
            mySQLClaimDAO.deleteClaim(claimId);
            logger.info("Заявку з ID: {} успішно видалено з MySQL", claimId);
            mongoDBClaimDAO.deleteClaim(claimId);
            logger.info("Заявку з ID: {} успішно видалено з MongoDB", claimId);
        } catch (Exception e) {
            logger.error("Помилка під час видалення заявки з ID: {}", claimId, e);
            throw new RuntimeException("Помилка під час видалення заявки", e);
        }
    }

}

