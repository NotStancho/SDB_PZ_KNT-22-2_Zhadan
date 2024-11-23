package org.example.sdb_knt222_zhadan.controller;

import org.example.sdb_knt222_zhadan.model.Equipment;
import org.example.sdb_knt222_zhadan.service.EquipmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    private static final Logger logger = LoggerFactory.getLogger(EquipmentController.class);
    private final EquipmentService equipmentService;

    @Autowired
    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public List<Equipment> getAllEquipment() {
        logger.info("Отримання всього обладнання");
        List<Equipment> equipmentList = equipmentService.getAllEquipment();
        if (equipmentList.isEmpty()) {
            logger.warn("Обладнання не знайдено");
        }
        return equipmentList;
    }

    @GetMapping("/{id}")
    public Equipment getEquipmentById(@PathVariable int id) {
        logger.info("Отримання обладнання з ID: {}", id);
        Equipment equipment = equipmentService.getEquipmentById(id);
        if (equipment == null) {
            logger.warn("Обладнання з ID {} не знайдено", id);
        }
        return equipment;
    }

    @PostMapping
    public String addEquipment(@RequestBody Equipment equipment) {
        logger.info("Додавання нового обладнання: {}", equipment.getSerialNumber());
        equipmentService.addEquipment(equipment);
        return "Обладнання успішно додано!";
    }

    @PutMapping("/{id}")
    public String updateEquipment(@PathVariable int id, @RequestBody Equipment equipment) {
        logger.info("Оновлення обладнання з ID: {}", id);
        equipment.setEquipmentId(id);
        equipmentService.updateEquipment(equipment);
        return "Обладнання успішно оновлено!";
    }

    @DeleteMapping("/{id}")
    public String deleteEquipment(@PathVariable int id) {
        logger.info("Видалення обладнання з ID: {}", id);
        equipmentService.deleteEquipment(id);
        return "Обладнання успішно видалено!";
    }
}
