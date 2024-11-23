package org.example.sdb_knt222_zhadan.model;

import java.sql.Date;

public class Claim_History {
    private int claimHistoryId;
    private Date actionDate;
    private String actionDescription;
    private User employee;
    private Claim claim;

    public int getClaimHistoryId() {
        return claimHistoryId;
    }

    public void setClaimHistoryId(int claimHistoryId) {
        this.claimHistoryId = claimHistoryId;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }

    public User getEmployee() {
        return employee;
    }

    public void setEmployee(User employee) {
        this.employee = employee;
    }

    public Claim getClaim() {
        return claim;
    }

    public void setClaim(Claim claim) {
        this.claim = claim;
    }
}
