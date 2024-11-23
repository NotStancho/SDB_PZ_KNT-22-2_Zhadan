package org.example.sdb_knt222_zhadan.dao.Factory;

import org.example.sdb_knt222_zhadan.dao.*;
import org.example.sdb_knt222_zhadan.dao.MongoDB.*;
import org.springframework.stereotype.Component;

@Component
public class MongoDBDAOFactory implements DAOFactory {
    @Override
    public UserDAO createUserDAO() {
        return new MongoDBUserDAO();
    }

    @Override
    public ClaimDAO createClaimDAO() {
        return new MongoDBClaimDAO();
    }

    @Override
    public StatusDAO createStatusDAO() {
        return new MongoDBStatusDAO();
    }

    @Override
    public EquipmentDAO createEquipmentDAO() {
        return new MongoDBEquipmentDAO();
    }

    @Override
    public Claim_HistoryDAO createClaim_HistoryDAO() {
        return new MongoDBClaim_HistoryDAO();
    }
}
