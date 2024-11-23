package org.example.sdb_knt222_zhadan.dao.MySQL;

import org.example.sdb_knt222_zhadan.config.MySQLConnection;
import org.example.sdb_knt222_zhadan.dao.StatusDAO;
import org.example.sdb_knt222_zhadan.model.Status;
import org.example.sdb_knt222_zhadan.model.builder.StatusBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQLStatusDAO implements StatusDAO {
    private static final Logger logger = LoggerFactory.getLogger(MySQLStatusDAO.class);
    private final Connection connection;

    public MySQLStatusDAO() {
        this.connection = MySQLConnection.getConnection();
    }

    @Override
    public List<Status> getAllStatus() {
        List<Status> statuses = new ArrayList<>();
        String sql = "SELECT * FROM status";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                statuses.add(mapResultSetToStatus(resultSet));
            }
            logger.info("Отримано всі статуси. Кількість: {}", statuses.size());
        } catch (SQLException e) {
            logger.error("Помилка під час отримання всіх статусів", e);
        }
        return statuses;
    }

    @Override
    public Status getStatusById(int statusId) {
        String sql = "SELECT * FROM status WHERE status_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, statusId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Status status = mapResultSetToStatus(resultSet);
                    logger.info("Отримано статус з ID: {}", statusId);
                    return status;
                } else {
                    logger.warn("Статус з ID {} не знайдено", statusId);
                }
            }
        } catch (SQLException e) {
            logger.error("Помилка під час отримання статусу з ID: " + statusId, e);
        }
        return null;
    }

    @Override
    public void addStatus(Status status) {
        String sql = "INSERT INTO status (name, description) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, status.getName());
            statement.setString(2, status.getDescription());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    status.setStatusId(generatedId);
                    logger.info("Статус успішно додано з ID: {}", generatedId);
                } else {
                    throw new SQLException("Не вдалося отримати ID для нового статусу.");
                }
            }
        } catch (SQLException e) {
            logger.error("Помилка під час додавання статусу: " + status.getName(), e);
        }
    }

    @Override
    public void updateStatus(Status status) {
        String sql = "UPDATE status SET name = ?, description = ? WHERE status_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.getName());
            statement.setString(2, status.getDescription());
            statement.setInt(3, status.getStatusId());
            statement.executeUpdate();
            logger.info("Статус оновлено: {}", status.getName());
        } catch (SQLException e) {
            logger.error("Помилка під час оновлення статусу: " + status.getName(), e);
        }
    }

    @Override
    public void deleteStatus(int statusId) {
        String sql = "DELETE FROM status WHERE status_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, statusId);
            statement.executeUpdate();
            logger.info("Статус з ID {} видалено", statusId);
        } catch (SQLException e) {
            logger.error("Помилка під час видалення статусу з ID: " + statusId, e);
        }
    }

    private Status mapResultSetToStatus(ResultSet resultSet) throws SQLException {
        return new StatusBuilder()
                .setStatusId(resultSet.getInt("status_id"))
                .setName(resultSet.getString("name"))
                .setDescription(resultSet.getString("description"))
                .build();

    }
}
