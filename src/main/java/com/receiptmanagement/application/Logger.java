package com.receiptmanagement.application;

import com.receiptmanagement.domain.model.PaymentDetails;
import com.receiptmanagement.domain.model.ReceiptDocument;

public interface Logger {

    void logInfo(String message);

    void logReceiptCreated(ReceiptDocument receiptDocument);

    void logValidationFailure(PaymentDetails paymentDetails, String reason);
}

