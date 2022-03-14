package com.interviewproject.controller;

import com.interviewproject.model.Report;
import com.interviewproject.model.Transaction;
import com.interviewproject.service.PersistenceService;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class PersistenceController {
    private static Logger logger = LoggerFactory.getLogger(PersistenceController.class);

    @Autowired
    private final PersistenceService persistenceService;

    public PersistenceController(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping(value = "/persist/saveTransaction", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Transaction> saveTransaction(@RequestBody Transaction transaction) {
        Transaction resultTransaction = persistenceService.saveTransaction(transaction);

        return new ResponseEntity<>(resultTransaction, HttpStatus.CREATED);
    }

    @GetMapping(value = "/report/{cnp}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Report> getReportForUser(@PathVariable String cnp) {
        return persistenceService.generateReport(cnp);
    }
}
