package com.interviewproject.model;

import com.interviewproject.util.TransactionType;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = Transaction.COLLECTION_NAME)
public class Transaction {
    public static final String COLLECTION_NAME = "transactions";

    private TransactionType transactionType;
    private String iban;
    private String name;
    private String cnp;
    private String description;
    private Double sum;
}
