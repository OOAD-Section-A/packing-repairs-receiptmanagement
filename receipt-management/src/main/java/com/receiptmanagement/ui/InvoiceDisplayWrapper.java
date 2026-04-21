package com.receiptmanagement.ui;

import com.receiptmanagement.infrastructure.database.InvoiceRecord;

public class InvoiceDisplayWrapper {

    private final InvoiceRecord invoiceRecord;

    public InvoiceDisplayWrapper(InvoiceRecord invoiceRecord) {
        this.invoiceRecord = invoiceRecord;
    }

    public InvoiceRecord getInvoiceRecord() {
        return invoiceRecord;
    }

    @Override
    public String toString() {
        return invoiceRecord.toString();
    }
}
