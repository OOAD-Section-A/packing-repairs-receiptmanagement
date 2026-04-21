package com.receiptmanagement.ui;

import com.jackfruit.scm.database.model.PackagingModels;

public class ReceiptDisplayWrapper {

    private final PackagingModels.ReceiptRecord receiptRecord;

    public ReceiptDisplayWrapper(PackagingModels.ReceiptRecord receiptRecord) {
        this.receiptRecord = receiptRecord;
    }

    public PackagingModels.ReceiptRecord getReceiptRecord() {
        return receiptRecord;
    }

    @Override
    public String toString() {
        return receiptRecord.receiptRecordId()
                + " | Order "
                + receiptRecord.orderId()
                + " | Amount "
                + receiptRecord.receivedAmount();
    }
}
