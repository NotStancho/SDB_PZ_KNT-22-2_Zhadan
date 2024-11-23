package org.example.sdb_knt222_zhadan.controller;

import org.example.sdb_knt222_zhadan.model.Status;
import org.example.sdb_knt222_zhadan.service.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statuses")
public class StatusController {

    private static final Logger logger = LoggerFactory.getLogger(StatusController.class);
    private final StatusService statusService;

    @Autowired
    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping
    public List<Status> getAllStatuses() {
        logger.info("Отримання всіх статусів");
        List<Status> statuses = statusService.getAllStatus();
        if (statuses.isEmpty()) {
            logger.warn("Статуси не знайдено");
        }
        return statuses;
    }

    @GetMapping("/{id}")
    public Status getStatusById(@PathVariable int id) {
        logger.info("Запит на отримання статусу з ID: {}", id);
        Status status = statusService.getStatusById(id);
        if (status == null) {
            logger.warn("Статус з ID {} не знайдено", id);
        }
        return status;
    }

    @PostMapping
    public String addStatus(@RequestBody Status status) {
        logger.info("Додавання нового статусу: {}", status.getName());
        statusService.addStatus(status);
        return "Статус успішно додано!";
    }

    @PutMapping("/{id}")
    public String updateStatus(@PathVariable int id, @RequestBody Status status) {
        logger.info("Оновлення статусу з ID: {}", id);
        status.setStatusId(id);
        statusService.updateStatus(status);
        return "Статус успішно оновлено!";
    }

    @DeleteMapping("/{id}")
    public String deleteStatus(@PathVariable int id) {
        logger.info("Видалення статусу з ID: {}", id);
        statusService.deleteStatus(id);
        return "Статус успішно видалено!";
    }
}
