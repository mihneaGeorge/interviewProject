package com.interviewproject.controller;

import com.interviewproject.model.Transaction;
import com.interviewproject.service.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    private Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private ValidationService validationService;

    @PostMapping(value = "/transaction/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public void validateTransaction(@RequestBody Transaction transaction) throws Exception {
        validationService.validateTransaction(transaction);
    }
}
