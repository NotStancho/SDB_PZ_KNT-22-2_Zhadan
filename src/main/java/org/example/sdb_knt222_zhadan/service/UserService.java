package org.example.sdb_knt222_zhadan.service;

import org.example.sdb_knt222_zhadan.dao.Factory.DAOFactory;
import org.example.sdb_knt222_zhadan.dao.UserDAO;
import org.example.sdb_knt222_zhadan.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDAO mongoDBUserDAO;
    private final UserDAO mySQLUserDAO;

    @Autowired
    public UserService(@Qualifier("mySQLFactory") DAOFactory mySQLFactory,
                       @Qualifier("mongoDBFactory") DAOFactory mongoDBFactory) {
        this.mySQLUserDAO = mySQLFactory.createUserDAO();
        this.mongoDBUserDAO = mongoDBFactory.createUserDAO();
    }

    public List<User> getAllUsers() {
        logger.info("Отримання всіх користувачів з MySQL");
        try {
            List<User> users = mySQLUserDAO.getAllUsers();
            logger.info("Отримано {} користувачів з MySQL", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Помилка під час отримання користувачів з MySQL", e);
            throw new RuntimeException("Помилка під час отримання користувачів", e);
        }
    }

    public User getUserById(int userId) {
        logger.info("Отримання користувача з ID: {} з MySQL", userId);
        try {
            return mySQLUserDAO.getUserById(userId);
        } catch (Exception e) {
            logger.error("Помилка під час отримання користувача з ID: {}", userId, e);
            throw new RuntimeException("Помилка під час отримання користувача", e);
        }
    }

    public User getUserByEmail(String email) {
        logger.info("Отримання користувача за email: {} з MySQL", email);
        try {
            return mySQLUserDAO.getUserByEmail(email);
        } catch (Exception e) {
            logger.error("Помилка під час отримання користувача за email: {}", email, e);
            throw new RuntimeException("Помилка під час отримання користувача", e);
        }
    }

    public void addUser(User user) {
        logger.info("Додавання нового користувача спочатку в MySQL, потім в MongoDB: {}", user.getEmail());
        try {
            mySQLUserDAO.addUser(user);
            int generatedId = user.getUserId();
            user.setUserId(generatedId);
            mongoDBUserDAO.addUser(user);
            logger.info("Користувача з email: {} успішно додано в обидві СУБД", user.getEmail());
        } catch (Exception e) {
            logger.error("Помилка під час додавання користувача: {}", user.getEmail(), e);
            try {
                mySQLUserDAO.deleteUser(user.getUserId());
                logger.info("Компенсація виконана: користувача видалено з MySQL");
            } catch (Exception ex) {
                logger.error("Помилка під час компенсації: не вдалося видалити користувача з MySQL", ex);
            }
            throw new RuntimeException("Помилка під час додавання користувача", e);
        }
    }

    public void updateUser(User user) {
        logger.info("Оновлення користувача з ID: {} спочатку в MySQL, потім в MongoDB", user.getUserId());
        try {
            mySQLUserDAO.updateUser(user);
            mongoDBUserDAO.updateUser(user);
            logger.info("Користувача з ID: {} успішно оновлено в обидві СУБД", user.getUserId());
        } catch (Exception e) {
            logger.error("Помилка під час оновлення користувача: {}", user.getUserId(), e);
            throw new RuntimeException("Помилка під час оновлення користувача", e);
        }
    }

    public void deleteUser(int userId) {
        logger.info("Видалення користувача з ID: {} спочатку з MySQL, потім з MongoDB", userId);
        try {
            mySQLUserDAO.deleteUser(userId);
            mongoDBUserDAO.deleteUser(userId);
            logger.info("Користувача з ID: {} успішно видалено з обох СУБД", userId);
        } catch (Exception e) {
            logger.error("Помилка під час видалення користувача з ID: {}", userId, e);
            throw new RuntimeException("Помилка під час видалення користувача", e);
        }
    }
}