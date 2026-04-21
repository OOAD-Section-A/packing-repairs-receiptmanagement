package com.receiptmanagement.port;

import java.math.BigDecimal;
import java.util.List;

public interface DatabaseInterface {

    void saveLog(String entry);

    List<String> readLogs();

    List<Object> getAllOrders();

    List<Object> getAllReceipts();

    List<Object> getAllInvoices();

    boolean receiptExistsForOrder(String orderId);

    void logSubsystemException(String exceptionName,
                               String severity,
                               String message);

    void saveReceiptRecord(String receiptId,
                          String orderId,
                          String packageId,
                          BigDecimal amount,
                          String status);
}

