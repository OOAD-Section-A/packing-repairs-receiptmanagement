package com.receiptmanagement.port;

import java.math.BigDecimal;
import java.util.List;

public interface DatabaseInterface {

    // -------- Logging --------

    void saveLog(String entry);

    List<String> readLogs();

    // -------- Orders --------

    List<Object> getAllOrders();

    // -------- Receipts --------

    List<Object> getAllReceipts();

    void saveReceiptRecord(
            String receiptId,
            String orderId,
            String packageId,
            BigDecimal amount,
            String status
    );
}