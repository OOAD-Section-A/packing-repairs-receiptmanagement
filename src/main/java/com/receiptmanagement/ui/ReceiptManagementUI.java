package com.receiptmanagement.ui;

import com.receiptmanagement.application.EnhancedReceiptGenerationService;
import com.receiptmanagement.application.Logger;
import com.receiptmanagement.application.PaymentValidation;
import com.receiptmanagement.application.ReceiptFormatter;
import com.receiptmanagement.application.ReceiptGenerationService;

import com.receiptmanagement.infrastructure.database.DatabaseAdapter;
import com.receiptmanagement.infrastructure.exception.ConsoleExceptionHandler;
import com.receiptmanagement.infrastructure.formatter.PlainTextReceiptFormatter;
import com.receiptmanagement.infrastructure.logging.DatabaseLogger;
import com.receiptmanagement.infrastructure.matching.ThreeWayMatchingService;
import com.receiptmanagement.infrastructure.ocr.SimpleOCRService;
import com.receiptmanagement.infrastructure.storage.InMemoryCloudStorage;
import com.receiptmanagement.infrastructure.validation.StandardPaymentValidation;
import com.receiptmanagement.infrastructure.categorization.CategorizationService;
import com.receiptmanagement.infrastructure.accounting.NoOpAccountingIntegration;

import com.receiptmanagement.port.DatabaseInterface;
import com.receiptmanagement.port.ExceptionHandlerInterface;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class ReceiptManagementUI extends JFrame {

    private final DatabaseInterface database;

    private JComboBox<String> orderDropdown;
    private JTextField amountField;

    private JTextArea receiptDisplayArea;
    private JTextArea logsDisplayArea;

    private JLabel statusLabel;

    private final EnhancedReceiptGenerationService enhancedService;

    public ReceiptManagementUI() {

        this.database = new DatabaseAdapter();

        Logger logger =
                new DatabaseLogger(database);

        PaymentValidation paymentValidation =
                new StandardPaymentValidation();

        ReceiptFormatter receiptFormatter =
                new PlainTextReceiptFormatter();

        ExceptionHandlerInterface exceptionHandler =
                new ConsoleExceptionHandler();

        this.enhancedService =
                new EnhancedReceiptGenerationService(
                        database
                );

        setupUI();

        setLocationRelativeTo(null);

        setVisible(true);
    }

    private void setupUI() {

        setTitle("SCM Receipt Management System");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1200, 800);

        JTabbedPane tabbedPane =
                new JTabbedPane();

        tabbedPane.addTab(
                "Generate Receipt",
                createReceiptGenerationPanel()
        );

        tabbedPane.addTab(
                "Categorization",
                createCategorizationPanel()
        );

        tabbedPane.addTab(
                "3-Way Matching",
                createThreeWayMatchingPanel()
        );

        tabbedPane.addTab(
                "Logs",
                createLogsPanel()
        );

        add(tabbedPane, BorderLayout.CENTER);

        statusLabel =
                new JLabel("Ready");

        statusLabel.setBorder(
                BorderFactory.createEtchedBorder()
        );

        add(statusLabel, BorderLayout.SOUTH);
    }

    private JPanel createReceiptGenerationPanel() {

        JPanel panel =
                new JPanel();

        panel.setLayout(
                new GridLayout(5, 2, 10, 10)
        );

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        "Generate Receipt From Order"
                )
        );

        panel.add(
                new JLabel("Select Order:")
        );

        orderDropdown =
                new JComboBox<>();

        database.getAllOrders()
                .forEach(order ->
                        orderDropdown.addItem(
                                order.toString()
                        )
                );

        panel.add(orderDropdown);

        panel.add(
                new JLabel("Received Amount:")
        );

        amountField =
                new JTextField("1000");

        panel.add(amountField);

        JButton generateButton =
                new JButton("Generate Receipt");

        generateButton.addActionListener(
                e -> generateReceipt()
        );

        panel.add(generateButton);

        receiptDisplayArea =
                new JTextArea(10, 40);

        receiptDisplayArea.setEditable(false);

        JScrollPane scrollPane =
                new JScrollPane(receiptDisplayArea);

        panel.add(scrollPane);

        return panel;
    }

    private JPanel createCategorizationPanel() {

        JPanel panel =
                new JPanel();

        panel.setLayout(
                new BorderLayout()
        );

        JTextArea textArea =
                new JTextArea();

        textArea.setEditable(false);

        textArea.setText(
                "Categorization will load receipts from DB."
        );

        panel.add(
                new JScrollPane(textArea),
                BorderLayout.CENTER
        );

        return panel;
    }

    private JPanel createThreeWayMatchingPanel() {

        JPanel panel =
                new JPanel();

        panel.setLayout(
                new BorderLayout()
        );

        JTextArea textArea =
                new JTextArea();

        textArea.setEditable(false);

        textArea.setText(
                "3-Way Matching will compare PO, Invoice and Receipt."
        );

        panel.add(
                new JScrollPane(textArea),
                BorderLayout.CENTER
        );

        return panel;
    }

    private JPanel createLogsPanel() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        logsDisplayArea =
                new JTextArea();

        logsDisplayArea.setEditable(false);

        JButton refreshButton =
                new JButton("Refresh Logs");

        refreshButton.addActionListener(
                e -> refreshLogs()
        );

        panel.add(
                new JScrollPane(logsDisplayArea),
                BorderLayout.CENTER
        );

        panel.add(
                refreshButton,
                BorderLayout.SOUTH
        );

        return panel;
    }

    private void generateReceipt() {

        try {

            String selectedOrder =
                    (String) orderDropdown
                            .getSelectedItem();

            BigDecimal amount =
                    new BigDecimal(
                            amountField.getText()
                    );

            String receiptId =
                    enhancedService
                            .generateReceiptForOrder(
                                    selectedOrder,
                                    amount
                            );

            receiptDisplayArea.setText(
                    "Receipt Created Successfully\n\n"
                            + "Receipt ID: "
                            + receiptId
            );

            statusLabel.setText(
                    "Receipt Generated"
            );

        }

        catch (Exception e) {

            statusLabel.setText(
                    "Error: "
                            + e.getMessage()
            );

        }
    }

    private void refreshLogs() {

        StringBuilder logs =
                new StringBuilder();

        database.readLogs()
                .forEach(log ->
                        logs.append(log)
                                .append("\n")
                );

        logsDisplayArea.setText(
                logs.toString()
        );
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                ReceiptManagementUI::new
        );
    }
}