package org.example.sdb_knt222_zhadan.model.builder;

import org.example.sdb_knt222_zhadan.model.Equipment;

import java.util.Date;

public class EquipmentBuilder {
    private final Equipment equipment;

    public EquipmentBuilder() {
        this.equipment = new Equipment();
    }

    public EquipmentBuilder setEquipmentId(int equipmentId) {
        equipment.setEquipmentId(equipmentId);
        return this;
    }

    public EquipmentBuilder setSerialNumber(String serialNumber) {
        equipment.setSerialNumber(serialNumber);
        return this;
    }

    public EquipmentBuilder setModel(String model) {
        equipment.setModel(model);
        return this;
    }

    public EquipmentBuilder setType(String type) {
        equipment.setType(type);
        return this;
    }

    public EquipmentBuilder setPurchaseDate(Date purchaseDate) {
        equipment.setPurchaseDate(purchaseDate);
        return this;
    }

    public Equipment build() {
        return equipment;
    }
}
