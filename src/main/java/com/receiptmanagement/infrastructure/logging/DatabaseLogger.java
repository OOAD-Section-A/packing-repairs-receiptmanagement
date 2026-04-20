package com.receiptmanagement.infrastructure.logging;

import com.receiptmanagement.application.Logger;
import com.receiptmanagement.domain.model.PaymentDetails;
import com.receiptmanagement.domain.model.ReceiptDocument;
import com.receiptmanagement.port.DatabaseInterface;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class DatabaseLogger implements Logger {

    private static final DateTimeFormatter LOG_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final DatabaseInterface database;

    public DatabaseLogger(DatabaseInterface database) {
        this.database = Objects.requireNonNull(database, "database cannot be null");
    }

    @Override
    public void logInfo(String message) {
        database.saveLog(timestamp() + " INFO  " + message);
    }

    @Override
    public void logReceiptCreated(ReceiptDocument receiptDocument) {
        database.saveLog(
                timestamp()
                        + " INFO  Receipt generated: "
                        + receiptDocument.getReceiptId()
                        + " for payment "
                        + receiptDocument.getPaymentId()
        );
    }

    @Override
    public void logValidationFailure(PaymentDetails paymentDetails, String reason) {
        database.saveLog(
                timestamp()
                        + " ERROR Validation failed for payment "
                        + paymentDetails.getPaymentId()
                        + ": "
                        + reason
        );
    }

    private String timestamp() {
        return LocalDateTime.now().format(LOG_FORMAT);
    }
}

