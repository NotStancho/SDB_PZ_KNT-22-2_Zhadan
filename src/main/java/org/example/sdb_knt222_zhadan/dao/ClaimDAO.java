package org.example.sdb_knt222_zhadan.dao;

import org.example.sdb_knt222_zhadan.model.Claim;

import java.util.List;

public interface ClaimDAO {
    List<Claim> getAllClaim();
    List<Claim> getClientClaims(int clientId);
    void addClaim(Claim claim, int employeeId);
    Claim getClaimById(int claimId);
    void updateClaim(Claim claim, int employeeId, String description);
    void deleteClaim(int claimId);
}
