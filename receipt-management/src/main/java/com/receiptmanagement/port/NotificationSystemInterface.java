package com.receiptmanagement.port;

import com.receiptmanagement.domain.model.CustomerInformation;
import com.receiptmanagement.domain.model.ReceiptDocument;

public interface NotificationSystemInterface {

    void sendReceipt(ReceiptDocument receiptDocument, CustomerInformation customerInformation);
}
