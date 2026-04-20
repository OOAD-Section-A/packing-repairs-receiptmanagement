package com.receiptmanagement.infrastructure.accounting;

import com.receiptmanagement.domain.model.ReceiptDocument;
import com.receiptmanagement.port.AccountingIntegrationInterface;
import java.math.BigDecimal;
import java.util.Objects;

public final class NoOpAccountingIntegration implements AccountingIntegrationInterface {

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean validateReceiptForAccounting(ReceiptDocument receiptDocument) {
        Objects.requireNonNull(receiptDocument, "receiptDocument cannot be null");
        return receiptDocument.getAmount().compareTo(BigDecimal.ZERO) > 0
                && !receiptDocument.getCurrency().isBlank()
                && !receiptDocument.getPaymentId().isBlank();
    }

    @Override
    public String syncReceiptToAccounting(ReceiptDocument receiptDocument) {
        Objects.requireNonNull(receiptDocument, "receiptDocument cannot be null");
        return "NOT-CONNECTED";
    }
}
