package com.receiptmanagement.infrastructure.logging;

import com.receiptmanagement.application.Logger;
import com.receiptmanagement.domain.model.PaymentDetails;
import com.receiptmanagement.domain.model.ReceiptDocument;
import com.receiptmanagement.port.DatabaseInterface;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatabaseLogger implements Logger {

    private final DatabaseInterface database;

    public DatabaseLogger(
            DatabaseInterface database
    ) {

        this.database = database;
    }

    @Override
    public void logInfo(String message) {

        database.saveLog(
                timestamp()
                        + " INFO "
                        + message
        );
    }

    @Override
    public void logReceiptCreated(
            ReceiptDocument receipt
    ) {

        database.saveLog(
                timestamp()
                        + " RECEIPT CREATED "
                        + receipt.getReceiptId()
        );
    }

    @Override
    public void logValidationFailure(
            PaymentDetails payment,
            String reason
    ) {

        database.saveLog(
                timestamp()
                        + " VALIDATION FAILED "
                        + payment.getPaymentId()
                        + " : "
                        + reason
        );
    }

    private String timestamp() {

        return LocalDateTime.now()
                .format(
                        DateTimeFormatter
                                .ofPattern(
                                        "yyyy-MM-dd HH:mm:ss"
                                )
                );
    }
}