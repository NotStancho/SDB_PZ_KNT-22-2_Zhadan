package org.example.sdb_knt222_zhadan.model.builder;

import org.example.sdb_knt222_zhadan.model.Claim;
import org.example.sdb_knt222_zhadan.model.Equipment;
import org.example.sdb_knt222_zhadan.model.Status;
import org.example.sdb_knt222_zhadan.model.User;

public class ClaimBuilder {
    private final Claim claim;

    public ClaimBuilder() {
        this.claim = new Claim();
    }

    public ClaimBuilder setClaimId(int claimId) {
        claim.setClaimId(claimId);
        return this;
    }

    public ClaimBuilder setClient(User client) {
        claim.setClient(client);
        return this;
    }

    public ClaimBuilder setEquipment(Equipment equipment) {
        claim.setEquipment(equipment);
        return this;
    }

    public ClaimBuilder setStatus(Status status) {
        claim.setStatus(status);
        return this;
    }

    public ClaimBuilder setDefectDescription(String defectDescription) {
        claim.setDefectDescription(defectDescription);
        return this;
    }

    public Claim build() {
        return claim;
    }
}
