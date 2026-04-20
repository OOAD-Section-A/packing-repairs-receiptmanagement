package com.receiptmanagement.port;

import com.receiptmanagement.domain.model.ReceiptDocument;

public interface AccountingIntegrationInterface {

    boolean isConnected();

    boolean validateReceiptForAccounting(ReceiptDocument receiptDocument);

    String syncReceiptToAccounting(ReceiptDocument receiptDocument);
}
