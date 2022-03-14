package com.interviewproject.model;

import com.interviewproject.util.TransactionType;
import lombok.Data;

import java.util.List;

@Data
public class TransactionReport {
    private TransactionType transactionType;
    private Double totalSum;
    private int transactionCount;
    private List<String> description;
}
