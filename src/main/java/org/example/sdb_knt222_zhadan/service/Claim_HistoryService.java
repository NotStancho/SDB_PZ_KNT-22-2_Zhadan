package org.example.sdb_knt222_zhadan.service;

import org.example.sdb_knt222_zhadan.dao.Claim_HistoryDAO;
import org.example.sdb_knt222_zhadan.dao.Factory.DAOFactory;
import org.example.sdb_knt222_zhadan.model.Claim_History;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Claim_HistoryService {
    private final Claim_HistoryDAO mongoDBClaim_HistoryDAO;
    private final Claim_HistoryDAO mySQLClaim_HistoryDAO;

    @Autowired
    public Claim_HistoryService(@Qualifier("mySQLFactory") DAOFactory mySQLFactory,
                                @Qualifier("mongoDBFactory") DAOFactory mongoDBFactory) {
        this.mySQLClaim_HistoryDAO = mySQLFactory.createClaim_HistoryDAO();
        this.mongoDBClaim_HistoryDAO = mongoDBFactory.createClaim_HistoryDAO();
    }

    public List<Claim_History> getClaimHistory(int claimId) {
        try {
            List<Claim_History> history = mySQLClaim_HistoryDAO.getClaimHistoryByClaimId(claimId);
            return history;
        } catch (Exception e) {
            throw new RuntimeException("Помилка під час отримання історії заявки з ID: " + claimId, e);
        }
    }
}
