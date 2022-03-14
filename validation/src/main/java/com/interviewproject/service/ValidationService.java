package com.interviewproject.service;

import com.interviewproject.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

@Service
public class ValidationService {

    private static Logger logger = LoggerFactory.getLogger(ValidationService.class);

    private final RestTemplate restTemplate;

    public ValidationService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public void validateTransaction(Transaction transaction) throws Exception, InterruptedException {
        if (transaction.getIban() == null) {
            throw new Exception("IBAN cannot be null!");
        }
        if (transaction.getCnp() == null) {
            throw new Exception("User CNP cannot be null!");
        }
        if (transaction.getName() == null) {
            throw new Exception("User name cannot be null!");
        }
        if (transaction.getTransactionType() == null) {
            throw new Exception("Transaction type cannot be null!");
        }
        if (transaction.getSum() == null) {
            throw new Exception("Sum cannot be null!");
        }
        sendTransaction(transaction);
    }

    @Async("asyncExec")
    @Retryable(value = InterruptedException.class)
    public CompletableFuture<Transaction> sendTransaction(Transaction transaction) throws InterruptedException, URISyntaxException {
        logger.info("Sending: " + transaction);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        URI uri = new URI("http://localhost:8081/persist/saveTransaction");

        HttpEntity<Transaction> httpEntity = new HttpEntity<>(transaction, headers);

        Transaction result = restTemplate.postForObject(uri, httpEntity, Transaction.class);
        logger.info("Got result: " + result);

        return CompletableFuture.completedFuture(result);
    }

}
