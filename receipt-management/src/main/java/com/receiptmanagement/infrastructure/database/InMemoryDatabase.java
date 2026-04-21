package com.receiptmanagement.infrastructure.database;

import com.receiptmanagement.port.DatabaseInterface;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class InMemoryDatabase implements DatabaseInterface {

    private final List<String> logs = new ArrayList<>();

    @Override
    public void saveLog(String entry) {
        logs.add(entry);
    }

    @Override
    public List<String> readLogs() {
        return List.copyOf(logs);
    }

    @Override
    public List<Object> getAllOrders() {
        return List.of();
    }

    @Override
    public List<Object> getAllReceipts() {
        return List.of();
    }

    @Override
    public List<Object> getAllInvoices() {
        return List.of();
    }

    @Override
    public boolean receiptExistsForOrder(String orderId) {
        return false;
    }

    @Override
    public void logSubsystemException(
            String exceptionName,
            String severity,
            String message
    ) {
        saveLog(severity + " " + exceptionName + ": " + message);
    }

    @Override
    public void saveReceiptRecord(
            String receiptId,
            String orderId,
            String packageId,
            BigDecimal amount,
            String status
    ) {
        saveLog("Receipt generated: " + receiptId + " for order " + orderId);
    }
}
