package com.interviewproject.service;

import com.interviewproject.model.Report;
import com.interviewproject.model.Transaction;
import com.interviewproject.model.TransactionReport;
import com.interviewproject.util.TransactionType;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;


@Service
public class PersistenceService {
    private static final String TRANSACTION_TYPE = "transactionType";
    private static final String TOTAL_SUM = "totalSum";
    private static final String TRANSACTION_COUNT = "transactionCount";
    private static final String SUM = "sum";
    private static final String ID_PREFIX = "$_id.";
    private static final String OPERATOR_PREFIX = "$";
    private static final String CNP = "cnp";
    private static final String NAME = "name";
    private static final String IBAN = "iban";
    private static final String DESCRIPTION = "description";
    public static final String TRANSACTIONS = "transactions";

    @Autowired
    private MongoTemplate mongoTemplate;

    public Transaction saveTransaction(Transaction transaction) {
        return mongoTemplate.insert(transaction);
    }

    public ResponseEntity<Report> generateReport(String cnp) {
        AggregationResults<Report> result = mongoTemplate.aggregate(
                newAggregation(
                       match(where(CNP).is(cnp)),
                        group(TRANSACTION_TYPE, NAME, IBAN).push(DESCRIPTION).as(DESCRIPTION).sum(SUM).as(TOTAL_SUM).count().as(TRANSACTION_COUNT),
                        group(ID_PREFIX + NAME, ID_PREFIX + IBAN).push(
                                new Document(TRANSACTION_TYPE, ID_PREFIX + TRANSACTION_TYPE)
                                        .append(TOTAL_SUM, OPERATOR_PREFIX + TOTAL_SUM)
                                        .append(TRANSACTION_COUNT, OPERATOR_PREFIX + TRANSACTION_COUNT)
                                        .append(DESCRIPTION, OPERATOR_PREFIX + DESCRIPTION)
                        ).as(TRANSACTIONS),
                        addFields().addFieldWithValue(NAME, ID_PREFIX + NAME).addFieldWithValue(IBAN, ID_PREFIX + IBAN).build(),
                        project(TRANSACTIONS, NAME, IBAN)
                ),
                Transaction.COLLECTION_NAME,
                Report.class
            );
        Report report = result.getUniqueMappedResult();

        if (report != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            report.setCnp(cnp);
            fillInMissingTransactionTypeReport(report);

            return new ResponseEntity<>(report, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
    }

    private void fillInMissingTransactionTypeReport(Report report) {
        for (TransactionType transactionType : TransactionType.values()) {
            if (report.getTransactions().stream().noneMatch(transactionReport -> transactionReport.getTransactionType().equals(transactionType))) {
                TransactionReport missingTransactionType = getEmptyReport(transactionType);

                report.getTransactions().add(missingTransactionType);
            }
        }
    }

    private TransactionReport getEmptyReport(TransactionType transactionType) {
        TransactionReport emptyReport = new TransactionReport();
        emptyReport.setTransactionType(transactionType);
        emptyReport.setTransactionCount(0);
        emptyReport.setTransactionCount(0);
        emptyReport.setTotalSum(0.);

        return emptyReport;
    }
}
