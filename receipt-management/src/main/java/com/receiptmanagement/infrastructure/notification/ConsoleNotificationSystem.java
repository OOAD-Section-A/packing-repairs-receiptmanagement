package com.receiptmanagement.infrastructure.notification;

import com.receiptmanagement.domain.model.CustomerInformation;
import com.receiptmanagement.domain.model.ReceiptDocument;
import com.receiptmanagement.port.NotificationSystemInterface;

public final class ConsoleNotificationSystem implements NotificationSystemInterface {

    @Override
    public void sendReceipt(ReceiptDocument receiptDocument, CustomerInformation customerInformation) {
        System.out.println("NOTIFICATION SENT TO: " + customerInformation.getEmail());
        System.out.println(receiptDocument.getFormattedContent());
    }
}

