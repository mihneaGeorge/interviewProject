package com.interviewproject.service;

import com.interviewproject.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.validation.ValidationException;
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

    public void validateTransaction(Transaction transaction) throws ValidationException, InterruptedException, URISyntaxException {
        if (transaction.getIban() == null) {
            throw new ValidationException("IBAN cannot be null!");
        }
        if (transaction.getCnp() == null) {
            throw new ValidationException("User CNP cannot be null!");
        }
        if (transaction.getName() == null) {
            throw new ValidationException("User name cannot be null!");
        }
        if (transaction.getTransactionType() == null) {
            throw new ValidationException("Transaction type cannot be null!");
        }
        if (transaction.getSum() == null) {
            throw new ValidationException("Sum cannot be null!");
        }
        sendTransaction(transaction);
    }

    @Async("asyncExec")
    @Retryable(value = InterruptedException.class)
    public CompletableFuture<Transaction> sendTransaction(Transaction transaction) throws InterruptedException, URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        URI uri = new URI("http://localhost:8081/persist/saveTransaction");

        HttpEntity<Transaction> httpEntity = new HttpEntity<>(transaction, headers);

        Transaction result = restTemplate.postForObject(uri, httpEntity, Transaction.class);

        return CompletableFuture.completedFuture(result);
    }

}
