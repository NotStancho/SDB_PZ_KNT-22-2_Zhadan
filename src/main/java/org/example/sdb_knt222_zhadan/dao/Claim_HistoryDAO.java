package org.example.sdb_knt222_zhadan.dao;

import org.example.sdb_knt222_zhadan.model.Claim_History;

import java.util.List;

public interface Claim_HistoryDAO {
    List<Claim_History> getClaimHistoryByClaimId(int claimId);

    void addClaimHistory(Claim_History history);
}
