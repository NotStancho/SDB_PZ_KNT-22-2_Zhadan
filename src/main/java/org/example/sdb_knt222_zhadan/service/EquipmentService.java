package org.example.sdb_knt222_zhadan.service;

import org.example.sdb_knt222_zhadan.dao.EquipmentDAO;
import org.example.sdb_knt222_zhadan.dao.Factory.DAOFactory;
import org.example.sdb_knt222_zhadan.model.Equipment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentService {
    private static final Logger logger = LoggerFactory.getLogger(EquipmentService.class);
    private final EquipmentDAO mongoDBEquipmentDAO;
    private final EquipmentDAO mySQLEquipmentDAO;

    @Autowired
    public EquipmentService(@Qualifier("mySQLFactory") DAOFactory mySQLFactory,
                            @Qualifier("mongoDBFactory") DAOFactory mongoDBFactory) {
        this.mySQLEquipmentDAO = mySQLFactory.createEquipmentDAO();
        this.mongoDBEquipmentDAO = mongoDBFactory.createEquipmentDAO();
    }

    public List<Equipment> getAllEquipment() {
        logger.info("Отримання всього обладнання з MySQL");
        try {
            List<Equipment> equipmentList = mySQLEquipmentDAO.getAllEquipment();
            logger.info("Отримано {} обладнання з MySQL", equipmentList.size());
            return equipmentList;
        } catch (Exception e) {
            logger.error("Помилка під час отримання обладнання з MySQL", e);
            throw new RuntimeException("Помилка під час отримання обладнання", e);
        }
    }

    public Equipment getOrCreateEquipment(Equipment equipment) {
        logger.info("Перевірка існування обладнання з серійним номером: {}", equipment.getSerialNumber());
        Equipment existingEquipment = getEquipmentBySerialNumber(equipment.getSerialNumber());

        if (existingEquipment == null) {
            logger.info("Обладнання не знайдено, додаємо нове: {}", equipment.getSerialNumber());
            try {
                addEquipment(equipment);
                existingEquipment = getEquipmentBySerialNumber(equipment.getSerialNumber());
                logger.info("Обладнання додано з ID: {}", existingEquipment.getEquipmentId());
            } catch (Exception e) {
                logger.error("Помилка під час створення обладнання: {}", equipment.getSerialNumber(), e);
                throw new RuntimeException("Помилка під час створення обладнання", e);
            }
        }
        return existingEquipment;
    }

    public Equipment getEquipmentById(int equipmentId) {
        logger.info("Отримання обладнання з ID: {} з MySQL", equipmentId);
        try {
            return mySQLEquipmentDAO.getEquipmentById(equipmentId);
        } catch (Exception e) {
            logger.error("Помилка під час отримання обладнання з ID: {}", equipmentId, e);
            throw new RuntimeException("Помилка під час отримання обладнання", e);
        }
    }

    public Equipment getEquipmentBySerialNumber(String serialNumber) {
        logger.info("Отримання обладнання за серійним номером: {} з MySQL", serialNumber);
        try {
            return mySQLEquipmentDAO.getEquipmentBySerialNumber(serialNumber);
        } catch (Exception e) {
            logger.error("Помилка під час отримання обладнання за серійним номером: {}", serialNumber, e);
            throw new RuntimeException("Помилка під час отримання обладнання", e);
        }
    }

    public void addEquipment(Equipment equipment) {
        logger.info("Додавання нового обладнання спочатку в MySQL, потім в MongoDB: {}", equipment.getSerialNumber());
        try {
            mySQLEquipmentDAO.addEquipment(equipment);
            int generatedId = equipment.getEquipmentId();
            equipment.setEquipmentId(generatedId);
            mongoDBEquipmentDAO.addEquipment(equipment);
            logger.info("Обладнання з серійним номером: {} успішно додано в обидві СУБД", equipment.getSerialNumber());
        } catch (Exception e) {
            logger.error("Помилка під час додавання обладнання: {}", equipment.getSerialNumber(), e);
            try {
                mySQLEquipmentDAO.deleteEquipment(equipment.getEquipmentId());
                logger.info("Компенсація виконана: обладнання видалено з MySQL");
            } catch (Exception ex) {
                logger.error("Помилка під час компенсації: не вдалося видалити обладнання з MySQL", ex);
            }
            throw new RuntimeException("Помилка під час додавання обладнання", e);
        }
    }

    public void updateEquipment(Equipment equipment) {
        logger.info("Оновлення обладнання з ID: {}", equipment.getEquipmentId());
        try {
            mySQLEquipmentDAO.updateEquipment(equipment);
            mongoDBEquipmentDAO.updateEquipment(equipment);
            logger.info("Обладнання з ID: {} успішно оновлено в обидві СУБД", equipment.getEquipmentId());
        } catch (Exception e) {
            logger.error("Помилка під час оновлення обладнання з ID: {}", equipment.getEquipmentId(), e);
            throw new RuntimeException("Помилка під час оновлення обладнання", e);
        }
    }

    public void deleteEquipment(int equipmentId) {
        logger.info("Видалення обладнання з ID: {}", equipmentId);
        try {
            mySQLEquipmentDAO.deleteEquipment(equipmentId);
            mongoDBEquipmentDAO.deleteEquipment(equipmentId);
            logger.info("Обладнання з ID: {} успішно видалено з обох СУБД", equipmentId);
        } catch (Exception e) {
            logger.error("Помилка під час видалення обладнання з ID: {}", equipmentId, e);
            throw new RuntimeException("Помилка під час видалення обладнання", e);
        }
    }
}
