package org.example.sdb_knt222_zhadan.service;

import org.example.sdb_knt222_zhadan.dao.Factory.DAOFactory;
import org.example.sdb_knt222_zhadan.dao.StatusDAO;
import org.example.sdb_knt222_zhadan.model.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusService {
    private static final Logger logger = LoggerFactory.getLogger(StatusService.class);
    private final StatusDAO mongoDBStatusDAO;
    private final StatusDAO mySQLStatusDAO;

    @Autowired
    public StatusService(@Qualifier("mySQLFactory") DAOFactory mySQLFactory,
                         @Qualifier("mongoDBFactory") DAOFactory mongoDBFactory) {
        this.mySQLStatusDAO = mySQLFactory.createStatusDAO();
        this.mongoDBStatusDAO = mongoDBFactory.createStatusDAO();
    }

    public List<Status> getAllStatus() {
        logger.info("Отримання всіх статусів з обох СУБД");
        try {
            List<Status> statuses = mySQLStatusDAO.getAllStatus();
            logger.info("Отримано {} статусів з MySQL", statuses.size());
            return statuses;
        } catch (Exception e) {
            logger.error("Помилка під час отримання статусів з MySQL", e);
            throw new RuntimeException("Помилка під час отримання статусів", e);
        }
    }

    public Status getStatusById(int statusId) {
        logger.info("Отримання статусу з ID: {} з MySQL", statusId);
        try {
            return mySQLStatusDAO.getStatusById(statusId);
        } catch (Exception e) {
            logger.error("Помилка під час отримання статусу з ID: {}", statusId, e);
            throw new RuntimeException("Помилка під час отримання статусу", e);
        }
    }

    public void addStatus(Status status) {
        logger.info("Додавання нового статусу спочатку в MySQL, потім в MongoDB: {}", status.getName());
        try {
            mySQLStatusDAO.addStatus(status);
            int generatedId = status.getStatusId();
            status.setStatusId(generatedId);
            mongoDBStatusDAO.addStatus(status);
            logger.info("Статус з назвою: {} успішно додано в обидві СУБД", status.getName());
        } catch (Exception e) {
            logger.error("Помилка під час додавання статусу: {}", status.getName(), e);
            try {
                mySQLStatusDAO.deleteStatus(status.getStatusId());
                logger.info("Компенсація виконана: статус видалено з MySQL");
            } catch (Exception ex) {
                logger.error("Помилка під час компенсації: не вдалося видалити статус з MySQL", ex);
            }
            throw new RuntimeException("Помилка під час додавання статусу", e);
        }
    }

    public void updateStatus(Status status) {
        logger.info("Оновлення статусу з ID: {} спочатку в MySQL, потім в MongoDB", status.getStatusId());
        try {
            mySQLStatusDAO.updateStatus(status);
            mongoDBStatusDAO.updateStatus(status);
            logger.info("Статус з ID: {} успішно оновлено в обидві СУБД", status.getStatusId());
        } catch (Exception e) {
            logger.error("Помилка під час оновлення статусу: {}", status.getStatusId(), e);
            throw new RuntimeException("Помилка під час оновлення статусу", e);
        }
    }

    public void deleteStatus(int statusId) {
        logger.info("Видалення статусу з ID: {} спочатку з MySQL, потім з MongoDB", statusId);
        try {
            mySQLStatusDAO.deleteStatus(statusId);
            mongoDBStatusDAO.deleteStatus(statusId);
            logger.info("Статус з ID: {} успішно видалено з обох СУБД", statusId);
        } catch (Exception e) {
            logger.error("Помилка під час видалення статусу з ID: {}", statusId, e);
            throw new RuntimeException("Помилка під час видалення статусу", e);
        }
    }
}
