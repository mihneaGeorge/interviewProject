package com.interviewproject.model;

import lombok.Data;

import java.util.List;

@Data
public class Report {
    private String name;
    private String iban;
    private String cnp;
    private List<TransactionReport> transactions;
}
