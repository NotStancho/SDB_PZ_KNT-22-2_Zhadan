package org.example.sdb_knt222_zhadan.dao.MySQL;

import org.example.sdb_knt222_zhadan.config.MySQLConnection;
import org.example.sdb_knt222_zhadan.dao.ClaimDAO;
import org.example.sdb_knt222_zhadan.model.*;
import org.example.sdb_knt222_zhadan.model.builder.ClaimBuilder;
import org.example.sdb_knt222_zhadan.model.builder.EquipmentBuilder;
import org.example.sdb_knt222_zhadan.model.builder.StatusBuilder;
import org.example.sdb_knt222_zhadan.model.builder.UserBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLClaimDAO implements ClaimDAO {
    private static final Logger logger = LoggerFactory.getLogger(MySQLClaimDAO.class);
    private final Connection connection;

    public MySQLClaimDAO() {
        this.connection = MySQLConnection.getConnection();
    }

    @Override
    public List<Claim> getAllClaim() {
        List<Claim> claims = new ArrayList<>();
        String sql = "SELECT c.claim_id, c.client_id, c.equipment_id, c.status_id, c.defect_description, "
                + "e.equipment_id, e.serial_number, e.model, e.type, e.purchase_date, "
                + "s.status_id, s.name, s.description, "
                + "u.user_id, u.firstname, u.lastname, u.email, u.phone, u.role "
                + "FROM claim c "
                + "JOIN equipment e ON c.equipment_id = e.equipment_id "
                + "JOIN status s ON c.status_id = s.status_id "
                + "JOIN user u ON c.client_id = u.user_id";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                claims.add(mapResultSetToClaim(resultSet));
            }
            logger.info("Отримано всі заявки. Загалом заявок: {}", claims.size());
        } catch (SQLException e) {
            logger.error("Помилка під час отримання всіх заявок", e);
        }
        return claims;
    }

    public List<Claim> getClientClaims(int clientId) {
        logger.info("Отримання заявок для клієнта з ID: {}", clientId);

        List<Claim> claims = new ArrayList<>();
        String sql = "SELECT c.claim_id, c.client_id, c.equipment_id, c.status_id, c.defect_description, "
                + "e.equipment_id, e.serial_number, e.model, e.type, e.purchase_date, "
                + "s.status_id, s.name, s.description, "
                + "u.user_id, u.firstname, u.lastname, u.email, u.phone, u.role "
                + "FROM claim c "
                + "JOIN equipment e ON c.equipment_id = e.equipment_id "
                + "JOIN status s ON c.status_id = s.status_id "
                + "JOIN user u ON c.client_id = u.user_id "
                + "WHERE c.client_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, clientId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    claims.add(mapResultSetToClaim(resultSet));
                }
                logger.info("Отримано заявки для клієнта з ID: {}", clientId);
            }
        } catch (SQLException e) {
            logger.error("Помилка під час отримання заявок для клієнта з ID: " + clientId, e);
        }
        return claims;
    }
    @Override
    public Claim getClaimById(int claimId) {
        Claim claim = null;
        String sql = "SELECT c.claim_id, c.client_id, c.equipment_id, c.status_id, c.defect_description, "
                + "e.equipment_id, e.serial_number, e.model, e.type, e.purchase_date, "
                + "s.status_id, s.name, s.description, "
                + "u.user_id, u.firstname, u.lastname, u.email, u.phone, u.role "
                + "FROM claim c "
                + "JOIN equipment e ON c.equipment_id = e.equipment_id "
                + "JOIN status s ON c.status_id = s.status_id "
                + "JOIN user u ON c.client_id = u.user_id "
                + "WHERE c.claim_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, claimId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    claim = mapResultSetToClaim(resultSet);
                }
            }
            logger.info("Заявка з ID {} отримана успішно", claimId);
        } catch (SQLException e) {
            logger.error("Помилка під час отримання заявки з ID: " + claimId, e);
        }
        return claim;
    }
    public void addClaim(Claim claim, int employeeId) {
        String sql = "{CALL CreateClaim(?, ?, ?, ?, ?, ?)}";
        try (CallableStatement statement = connection.prepareCall(sql)) {
            statement.setInt(1, claim.getClient().getUserId());
            statement.setInt(2, claim.getEquipment().getEquipmentId());
            statement.setInt(3, claim.getStatus().getStatusId());
            statement.setString(4, claim.getDefectDescription());
            statement.setInt(5, employeeId);
            statement.setString(6, "Заявка додана в систему");

            boolean hasResults = statement.execute();

            if (hasResults) {
                try (ResultSet rs = statement.getResultSet()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt("claim_id");
                        claim.setClaimId(generatedId);
                        logger.info("Заявку успішно додано з ID: {}", claim.getClaimId());
                    }
                }
            }
            logger.info("Заявку успішно додано разом з історією для співробітника з ID: {}", employeeId);
        } catch (SQLException e) {
            logger.error("Помилка під час додавання заявки з історією", e);
            throw new RuntimeException("Помилка під час додавання заявки з історією", e);
        }
    }
    public void updateClaim(Claim claim, int employeeId, String description) {
        logger.info("Оновлення заявки з ID {}. Співробітник ID: {}. Опис дії: {}", claim.getClaimId(), employeeId, description);

        String setVariablesSQL = "SET @employee_id = ?, @description = ?";
        try (PreparedStatement setEmployeeStmt = connection.prepareStatement(setVariablesSQL)) {
            setEmployeeStmt.setInt(1, employeeId);
            setEmployeeStmt.setString(2, description);
            setEmployeeStmt.executeUpdate();
            logger.info("Змінні сесії встановлено: employee_id = {}, description = {}", employeeId, description);
        } catch (SQLException e) {
            logger.error("Помилка при встановленні змінних сесії для тригера", e);
        }

        String updateClaimSQL = "UPDATE claim SET status_id = ?, defect_description = ? WHERE claim_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateClaimSQL)) {
            statement.setInt(1, claim.getStatus().getStatusId());
            statement.setString(2, claim.getDefectDescription());
            statement.setInt(3, claim.getClaimId());

            statement.executeUpdate();

            logger.info("Заявка з ID {} оновлена. Статус і опис дефекту змінено", claim.getClaimId());
        } catch (SQLException e) {
            logger.error("Помилка під час оновлення заявки", e);
        }

        if (claim.getEquipment() != null) {
            try (PreparedStatement equipmentStatement = connection.prepareStatement(
                    "UPDATE equipment SET type = ?, model = ?, serial_number = ?, purchase_date = ? WHERE equipment_id = ?")) {
                equipmentStatement.setString(1, claim.getEquipment().getType());
                equipmentStatement.setString(2, claim.getEquipment().getModel());
                equipmentStatement.setString(3, claim.getEquipment().getSerialNumber());
                equipmentStatement.setDate(4, new Date(claim.getEquipment().getPurchaseDate().getTime()));
                equipmentStatement.setInt(5, claim.getEquipment().getEquipmentId());

                int rowsAffected = equipmentStatement.executeUpdate();
                logger.info("Оновлено {} записів про обладнання для заявки з ID {}", rowsAffected, claim.getClaimId());
            } catch (SQLException e) {
                logger.error("Помилка під час оновлення обладнання для заявки", e);
            }
        }
    }
    @Override
    public void deleteClaim(int claimId) {
        String sql = "DELETE FROM claim WHERE claim_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, claimId);
            statement.executeUpdate();
            logger.info("Заявку з ID {} успішно видалено", claimId);
        } catch (SQLException e) {
            logger.error("Помилка під час видалення заявки з ID: " + claimId, e);
        }
    }

    private Claim mapResultSetToClaim(ResultSet resultSet) throws SQLException {
        Equipment equipment = new EquipmentBuilder()
                .setEquipmentId(resultSet.getInt("equipment_id"))
                .setSerialNumber(resultSet.getString("serial_number"))
                .setModel(resultSet.getString("model"))
                .setType(resultSet.getString("type"))
                .setPurchaseDate(resultSet.getDate("purchase_date"))
                .build();

        Status status = new StatusBuilder()
                .setStatusId(resultSet.getInt("status_id"))
                .setName(resultSet.getString("name"))
                .setDescription(resultSet.getString("description"))
                .build();

        User client = new UserBuilder()
                .setUserId(resultSet.getInt("client_id"))
                .setFirstname(resultSet.getString("firstname"))
                .setLastname(resultSet.getString("lastname"))
                .setEmail(resultSet.getString("email"))
                .setPhone(resultSet.getString("phone"))
                .setRole(resultSet.getString("role"))
                .build();

        return new ClaimBuilder()
                .setClaimId(resultSet.getInt("claim_id"))
                .setDefectDescription(resultSet.getString("defect_description"))
                .setEquipment(equipment)
                .setStatus(status)
                .setClient(client)
                .build();
    }
}