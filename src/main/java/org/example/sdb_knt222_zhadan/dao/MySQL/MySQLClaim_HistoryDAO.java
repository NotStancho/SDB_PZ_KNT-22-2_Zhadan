package org.example.sdb_knt222_zhadan.dao.MySQL;

import org.example.sdb_knt222_zhadan.config.MySQLConnection;
import org.example.sdb_knt222_zhadan.dao.Claim_HistoryDAO;
import org.example.sdb_knt222_zhadan.model.Claim;
import org.example.sdb_knt222_zhadan.model.Claim_History;
import org.example.sdb_knt222_zhadan.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MySQLClaim_HistoryDAO implements Claim_HistoryDAO {
    private static final Logger logger = LoggerFactory.getLogger(MySQLClaim_HistoryDAO.class);
    private final Connection connection;

    public MySQLClaim_HistoryDAO() {
        this.connection = MySQLConnection.getConnection();
    }

    @Override
    public List<Claim_History> getClaimHistoryByClaimId(int claimId) {
        List<Claim_History> historyList = new ArrayList<>();
        String sql = "SELECT ch.claim_history_id, ch.action_date, ch.action_description, "
                + "u.user_id, u.firstname, u.lastname, c.claim_id "
                + "FROM claim_history ch "
                + "LEFT JOIN user u ON ch.employee_id = u.user_id "
                + "JOIN claim c ON ch.claim_id = c.claim_id "
                + "WHERE ch.claim_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, claimId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Claim_History history = new Claim_History();
                    history.setClaimHistoryId(resultSet.getInt("claim_history_id"));
                    history.setActionDate(resultSet.getDate("action_date"));
                    history.setActionDescription(resultSet.getString("action_description"));

                    User employee = new User();
                    employee.setUserId(resultSet.getInt("user_id"));
                    employee.setFirstname(resultSet.getString("firstname"));
                    employee.setLastname(resultSet.getString("lastname"));
                    history.setEmployee(employee);

                    Claim claim = new Claim();
                    claim.setClaimId(resultSet.getInt("claim_id"));
                    history.setClaim(claim);

                    historyList.add(history);
                }
                logger.info("Історія заявки з ID: {} успішно отримана", claimId);
            }
        } catch (SQLException e) {
            logger.error("Помилка під час отримання історії заявки з ID: " + claimId, e);
        }
        return historyList;
    }
}
