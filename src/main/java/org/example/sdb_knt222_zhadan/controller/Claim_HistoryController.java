package org.example.sdb_knt222_zhadan.controller;

import org.example.sdb_knt222_zhadan.model.Claim_History;
import org.example.sdb_knt222_zhadan.service.Claim_HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class Claim_HistoryController {
    private final Claim_HistoryService claim_HistoryService;

    @Autowired
    public Claim_HistoryController(Claim_HistoryService claim_HistoryService) {
        this.claim_HistoryService = claim_HistoryService;
    }

    @GetMapping("/{claimId}/history")
    public ResponseEntity<List<Claim_History>> getClaimHistory(@PathVariable int claimId, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            List<Claim_History> history = claim_HistoryService.getClaimHistory(claimId);
            if (history != null && !history.isEmpty()) {
                return new ResponseEntity<>(history, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
