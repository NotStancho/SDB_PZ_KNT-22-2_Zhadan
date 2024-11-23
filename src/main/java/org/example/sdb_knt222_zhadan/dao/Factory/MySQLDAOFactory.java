package org.example.sdb_knt222_zhadan.dao.Factory;

import org.example.sdb_knt222_zhadan.dao.*;
import org.example.sdb_knt222_zhadan.dao.MySQL.*;
import org.springframework.stereotype.Component;

@Component
public class MySQLDAOFactory implements DAOFactory {
    @Override
    public UserDAO createUserDAO() {
        return new MySQLUserDAO();
    }

    @Override
    public ClaimDAO createClaimDAO() {
        return new MySQLClaimDAO();
    }

    @Override
    public StatusDAO createStatusDAO() {
        return new MySQLStatusDAO();
    }

    @Override
    public EquipmentDAO createEquipmentDAO() {
        return new MySQLEquipmentDAO();
    }

    @Override
    public Claim_HistoryDAO createClaim_HistoryDAO() {
        return new MySQLClaim_HistoryDAO();
    }
}
