package com.receiptmanagement.ui;

import com.receiptmanagement.domain.model.CustomerInformation;
import com.receiptmanagement.domain.model.ReceiptDocument;
import com.receiptmanagement.port.NotificationSystemInterface;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI-based notification system that displays receipts in dialog windows
 * instead of printing to console.
 */
public class GuiNotificationSystem implements NotificationSystemInterface {

    private final List<String> notificationHistory = new ArrayList<>();

    @Override
    public void sendReceipt(ReceiptDocument receiptDocument, CustomerInformation customerInformation) {
        String notification = "NOTIFICATION SENT TO: " + customerInformation.getEmail();
        notificationHistory.add(notification);

        // Create and display notification dialog
        String message = "Receipt sent to: " + customerInformation.getEmail() + 
                        "\n\nReceipt ID: " + receiptDocument.getReceiptId() +
                        "\nPayment ID: " + receiptDocument.getPaymentId() +
                        "\nAmount: " + receiptDocument.getAmount() + " " + receiptDocument.getCurrency();
        
        JOptionPane.showMessageDialog(
                null,
                message,
                "Receipt Notification",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public List<String> getNotificationHistory() {
        return new ArrayList<>(notificationHistory);
    }
}
