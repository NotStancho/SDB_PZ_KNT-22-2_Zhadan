package org.example.sdb_knt222_zhadan.model.builder;

import org.example.sdb_knt222_zhadan.model.Claim;
import org.example.sdb_knt222_zhadan.model.Claim_History;
import org.example.sdb_knt222_zhadan.model.User;

import java.sql.Date;

public class Claim_HistoryBuilder {
    private int claimHistoryId;
    private Date actionDate;
    private String actionDescription;
    private User employee;
    private Claim claim;

    public Claim_HistoryBuilder setClaimHistoryId(int claimHistoryId) {
        this.claimHistoryId = claimHistoryId;
        return this;
    }

    public Claim_HistoryBuilder setActionDate(Date actionDate) {
        this.actionDate = actionDate;
        return this;
    }

    public Claim_HistoryBuilder setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
        return this;
    }

    public Claim_HistoryBuilder setEmployee(User employee) {
        this.employee = employee;
        return this;
    }

    public Claim_HistoryBuilder setClaim(Claim claim) {
        this.claim = claim;
        return this;
    }

    public Claim_History build() {
        Claim_History claimHistory = new Claim_History();
        claimHistory.setClaimHistoryId(this.claimHistoryId);
        claimHistory.setActionDate(this.actionDate);
        claimHistory.setActionDescription(this.actionDescription);
        claimHistory.setEmployee(this.employee);
        claimHistory.setClaim(this.claim);
        return claimHistory;
    }
}
