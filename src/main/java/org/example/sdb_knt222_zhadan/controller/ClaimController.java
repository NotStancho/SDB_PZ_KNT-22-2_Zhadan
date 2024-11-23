package org.example.sdb_knt222_zhadan.controller;

import org.example.sdb_knt222_zhadan.model.Claim;
import org.example.sdb_knt222_zhadan.model.User;
import org.example.sdb_knt222_zhadan.service.ClaimService;
import org.example.sdb_knt222_zhadan.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private static final Logger logger = LoggerFactory.getLogger(ClaimController.class);
    private final ClaimService claimService;
    private final UserService userService;

    @Autowired
    public ClaimController(ClaimService claimService, UserService userService) {
        this.claimService = claimService;
        this.userService = userService;
    }

    @GetMapping("/client")
    public ResponseEntity<List<Claim>> getClientClaims(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            logger.info("Отримання заявок для клієнта: {}", authentication.getName());
            int clientId = userService.getUserByEmail(authentication.getName()).getUserId();
            List<Claim> claims = claimService.getClientClaims(clientId);
            return new ResponseEntity<>(claims, HttpStatus.OK);
        } else {
            logger.warn("Клієнт не авторизований");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Claim>> getAllClaims(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            logger.info("Отримання всіх заявок");
            List<Claim> claims = claimService.getAllClaims();
            return new ResponseEntity<>(claims, HttpStatus.OK);
        } else {
            logger.warn("Користувач не авторизований");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/add-claim")
    public ResponseEntity<String> addClaim(@RequestBody Claim claim, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            logger.info("Додавання нової заявки для користувача: {}", authentication.getName());
            User client = userService.getUserByEmail(authentication.getName());
            claim.setClient(client);

            int employeeId = client.getUserId();
            claimService.addClaim(claim, employeeId);

            return new ResponseEntity<>("Заявку додано успішно", HttpStatus.CREATED);
        } else {
            logger.warn("Користувач не авторизований для додавання заявки");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{claimId}")
    public ResponseEntity<Claim> getClaimById(@PathVariable int claimId, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            logger.info("Отримання деталей заявки з ID: {}", claimId);
            Claim claim = claimService.getClaimById(claimId);
            if (claim != null) {
                return new ResponseEntity<>(claim, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            logger.warn("Користувач не авторизований для перегляду деталей заявки");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/{claimId}")
    public ResponseEntity<String> updateClaim(@PathVariable int claimId, @RequestBody Claim claim,
                                              @RequestParam String actionDescription, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            logger.info("Оновлення заявки з ID: {}", claimId);
            User user = userService.getUserByEmail(authentication.getName());
            int employeeId = user.getUserId();

            claim.setClaimId(claimId);

            logger.info("Отримано actionDescription: {}", actionDescription);
            claimService.updateClaim(claim, employeeId, actionDescription);
            return new ResponseEntity<>("Заявка успішно оновлена", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Користувач не авторизований для оновлення заявки", HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/{claimId}")
    public ResponseEntity<String> deleteClaim(@PathVariable int claimId, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            logger.info("Видалення заявки з ID: {}", claimId);
            claimService.deleteClaim(claimId);
            return new ResponseEntity<>("Заявка успішно видалена", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Користувач не авторизований", HttpStatus.UNAUTHORIZED);
        }
    }
}