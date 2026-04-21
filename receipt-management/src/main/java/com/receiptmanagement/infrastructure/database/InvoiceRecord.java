package com.receiptmanagement.infrastructure.database;

import java.math.BigDecimal;

public final class InvoiceRecord {

    private final String invoiceId;
    private final String orderId;
    private final BigDecimal invoiceAmount;
    private final String invoiceStatus;

    public InvoiceRecord(
            String invoiceId,
            String orderId,
            BigDecimal invoiceAmount,
            String invoiceStatus
    ) {
        this.invoiceId = invoiceId;
        this.orderId = orderId;
        this.invoiceAmount = invoiceAmount;
        this.invoiceStatus = invoiceStatus;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    @Override
    public String toString() {
        return invoiceId
                + " | Order "
                + orderId
                + " | Amount "
                + invoiceAmount;
    }
}
