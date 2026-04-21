package com.receiptmanagement;

import com.receiptmanagement.infrastructure.database.DatabaseAdapter;
import com.receiptmanagement.port.DatabaseInterface;
import com.receiptmanagement.ui.ReceiptManagementUI;

import javax.swing.SwingUtilities;

/**
 * Entry point for the Receipt Management System.
 * Launches the Swing-based GUI application.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseInterface database = new DatabaseAdapter();
            new ReceiptManagementUI(database);
        });
    }
}
