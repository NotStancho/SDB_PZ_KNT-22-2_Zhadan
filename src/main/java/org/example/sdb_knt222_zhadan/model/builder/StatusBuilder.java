package org.example.sdb_knt222_zhadan.model.builder;

import org.example.sdb_knt222_zhadan.model.Status;

public class StatusBuilder {
    private final Status status;

    public StatusBuilder() {
        this.status = new Status();
    }

    public StatusBuilder setStatusId(int statusId) {
        status.setStatusId(statusId);
        return this;
    }

    public StatusBuilder setName(String name) {
        status.setName(name);
        return this;
    }

    public StatusBuilder setDescription(String description) {
        status.setDescription(description);
        return this;
    }

    public Status build() {
        return status;
    }
}
