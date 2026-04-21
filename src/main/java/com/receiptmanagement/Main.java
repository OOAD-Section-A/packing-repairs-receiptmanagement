package com.receiptmanagement;

import com.receiptmanagement.infrastructure.database.DatabaseAdapter;
import com.receiptmanagement.port.DatabaseInterface;
import com.receiptmanagement.ui.ReceiptManagementUI;

public class Main {

    public static void main(String[] args) {

        try {

            DatabaseInterface database =
                    new DatabaseAdapter();

            System.out.println(
                    "SCM Receipt Management System Starting..."
            );

            database.saveLog(
                    "System initialized successfully."
            );

            database.readLogs()
                    .forEach(System.out::println);

            javax.swing.SwingUtilities.invokeLater(
                    ReceiptManagementUI::new
            );

        }

        catch (Exception e) {

            System.err.println(
                    "System failed to start: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }
}