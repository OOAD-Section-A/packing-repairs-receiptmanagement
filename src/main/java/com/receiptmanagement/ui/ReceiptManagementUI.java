package com.receiptmanagement.ui;

import com.receiptmanagement.application.EnhancedReceiptGenerationService;
import com.receiptmanagement.application.Logger;
import com.receiptmanagement.application.PaymentValidation;
import com.receiptmanagement.application.ReceiptFormatter;
import com.receiptmanagement.application.ReceiptGenerationService;
import com.receiptmanagement.domain.model.CustomerInformation;
import com.receiptmanagement.domain.model.Invoice;
import com.receiptmanagement.domain.model.PaymentDetails;
import com.receiptmanagement.domain.model.PurchaseOrder;
import com.receiptmanagement.domain.model.ReceiptDocument;
import com.receiptmanagement.infrastructure.categorization.CategorizationService;
import com.receiptmanagement.infrastructure.database.InMemoryDatabase;
import com.receiptmanagement.infrastructure.exception.ConsoleExceptionHandler;
import com.receiptmanagement.infrastructure.formatter.PlainTextReceiptFormatter;
import com.receiptmanagement.infrastructure.logging.DatabaseLogger;
import com.receiptmanagement.infrastructure.matching.ThreeWayMatchingService;
import com.receiptmanagement.infrastructure.reimbursement.ReimbursementService;
import com.receiptmanagement.infrastructure.validation.StandardPaymentValidation;
import com.receiptmanagement.port.DatabaseInterface;
import com.receiptmanagement.port.ExceptionHandlerInterface;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReceiptManagementUI extends JFrame {
    private final ReceiptGenerationService receiptGenerationService;
    private final EnhancedReceiptGenerationService enhancedService;
    private final DatabaseInterface database;
    private final GuiNotificationSystem notificationSystem;
    private JTextField customerIdField;
    private JTextField customerNameField;
    private JTextField customerEmailField;
    private JTextField paymentIdField;
    private JTextField amountField;
    private JComboBox<String> currencyCombo;
    private JComboBox<String> paymentMethodCombo;
    private JCheckBox paymentCompletedCheckbox;
    private JTextArea receiptDisplayArea;
    private JTextArea logsDisplayArea;
    private JLabel statusLabel;

    public ReceiptManagementUI() {
        // Initialize services
        this.database = new InMemoryDatabase();
        Logger logger = new DatabaseLogger(database);
        PaymentValidation paymentValidation = new StandardPaymentValidation();
        ReceiptFormatter receiptFormatter = new PlainTextReceiptFormatter();
        this.notificationSystem = new GuiNotificationSystem();
        ExceptionHandlerInterface exceptionHandler = new ConsoleExceptionHandler();

        this.receiptGenerationService = new ReceiptGenerationService(
                paymentValidation,
                receiptFormatter,
                logger,
                notificationSystem,
                exceptionHandler
        );

        // Initialize enhanced service with remaining features
        this.enhancedService = new EnhancedReceiptGenerationService(
                paymentValidation,
                receiptFormatter,
                logger,
                notificationSystem,
                exceptionHandler,
                null,
                null,
                new ThreeWayMatchingService(),
                new CategorizationService(),
                null
        );

        // Setup UI
        setupUI();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupUI() {
        setTitle("Advanced Receipt Management System - Full Feature Set");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);

        // Create main panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Generate Receipt", createReceiptGenerationPanel());
        tabbedPane.addTab("Categorization", createCategorizationPanel());
        tabbedPane.addTab("3-Way Matching", createThreeWayMatchingPanel());
        tabbedPane.addTab("Reimbursement", createReimbursementPanel());
        tabbedPane.addTab("View Logs", createLogsPanel());

        add(tabbedPane, BorderLayout.CENTER);
        
        // Status bar at bottom
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        add(statusLabel, BorderLayout.SOUTH);
    }

    private JPanel createReceiptGenerationPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left panel for input
        JPanel inputPanel = createInputPanel();
        
        // Right panel for output
        JPanel outputPanel = createOutputPanel();

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputPanel, outputPanel);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.4);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Payment & Customer Information"));

        // Customer Information Group
        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(new GridLayout(3, 2, 10, 10));
        customerPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));

        customerPanel.add(new JLabel("Customer ID:"));
        customerIdField = new JTextField(15);
        customerIdField.setText("CUST-1001");
        customerPanel.add(customerIdField);

        customerPanel.add(new JLabel("Full Name:"));
        customerNameField = new JTextField(15);
        customerNameField.setText("Anirudh");
        customerPanel.add(customerNameField);

        customerPanel.add(new JLabel("Email:"));
        customerEmailField = new JTextField(15);
        customerEmailField.setText("anirudh@example.com");
        customerPanel.add(customerEmailField);

        panel.add(customerPanel);
        panel.add(Box.createVerticalStrut(15));

        // Payment Information Group
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new GridLayout(5, 2, 10, 10));
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment Details"));

        paymentPanel.add(new JLabel("Payment ID:"));
        paymentIdField = new JTextField(15);
        paymentIdField.setText("PAY-2001");
        paymentPanel.add(paymentIdField);

        paymentPanel.add(new JLabel("Amount:"));
        amountField = new JTextField(15);
        amountField.setText("2499.99");
        paymentPanel.add(amountField);

        paymentPanel.add(new JLabel("Currency:"));
        currencyCombo = new JComboBox<>(new String[]{"INR", "USD", "EUR", "GBP", "JPY"});
        currencyCombo.setSelectedItem("INR");
        paymentPanel.add(currencyCombo);

        paymentPanel.add(new JLabel("Payment Method:"));
        paymentMethodCombo = new JComboBox<>(new String[]{"UPI", "CHEQUE", "CREDIT_CARD", "DEBIT_CARD", "NET_BANKING"});
        paymentMethodCombo.setSelectedItem("UPI");
        paymentPanel.add(paymentMethodCombo);

        paymentPanel.add(new JLabel("Payment Completed:"));
        paymentCompletedCheckbox = new JCheckBox();
        paymentCompletedCheckbox.setSelected(true);
        paymentPanel.add(paymentCompletedCheckbox);

        panel.add(paymentPanel);
        panel.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton generateButton = new JButton("Generate Receipt");
        generateButton.setFont(new Font("Arial", Font.BOLD, 12));
        generateButton.setPreferredSize(new Dimension(150, 40));
        generateButton.addActionListener(e -> generateReceipt());
        buttonPanel.add(generateButton);

        JButton clearButton = new JButton("Clear Form");
        clearButton.setPreferredSize(new Dimension(120, 40));
        clearButton.addActionListener(e -> clearForm());
        buttonPanel.add(clearButton);

        panel.add(buttonPanel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createOutputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Receipt Output"));

        JLabel receiptLabel = new JLabel("Generated Receipt:");
        receiptLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(receiptLabel);

        receiptDisplayArea = new JTextArea(15, 40);
        receiptDisplayArea.setEditable(false);
        receiptDisplayArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        receiptDisplayArea.setBackground(Color.WHITE);
        receiptDisplayArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        JScrollPane scrollPane = new JScrollPane(receiptDisplayArea);
        panel.add(scrollPane);

        return panel;
    }

    private JPanel createCategorizationPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Receipt Info"));

        inputPanel.add(new JLabel("Payment ID:"));
        JTextField catPaymentIdField = new JTextField(15);
        catPaymentIdField.setText("PAY-2001");
        inputPanel.add(catPaymentIdField);

        inputPanel.add(new JLabel("Amount:"));
        JTextField catAmountField = new JTextField(15);
        catAmountField.setText("2499.99");
        inputPanel.add(catAmountField);

        inputPanel.add(new JLabel("Vendor/Description:"));
        JTextField catVendorField = new JTextField(15);
        catVendorField.setText("Office Supplies");
        inputPanel.add(catVendorField);

        JPanel buttonPanel = new JPanel();
        JButton categorizeButton = new JButton("Categorize Receipt");
        categorizeButton.addActionListener(e -> {
            String vendor = catVendorField.getText();
            String category = enhancedService.getCategorizationService().getCategory(vendor, "").getDisplayName();
            JOptionPane.showMessageDialog(this, "Categorized as: " + category, "Category", JOptionPane.INFORMATION_MESSAGE);
        });
        buttonPanel.add(categorizeButton);
        inputPanel.add(buttonPanel);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        JTextArea categoryOutputArea = new JTextArea();
        categoryOutputArea.setEditable(false);
        categoryOutputArea.setText("Available Categories:\n");
        for (var cat : enhancedService.getCategorizationService().getAllCategories()) {
            categoryOutputArea.append("  - " + cat.getDisplayName() + "\n");
        }
        mainPanel.add(new JScrollPane(categoryOutputArea), BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createThreeWayMatchingPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Match Documents"));

        inputPanel.add(new JLabel("Receipt Amount:"));
        JTextField receiptAmtField = new JTextField("5000");
        inputPanel.add(receiptAmtField);

        inputPanel.add(new JLabel("PO Amount:"));
        JTextField poAmtField = new JTextField("5000");
        inputPanel.add(poAmtField);

        inputPanel.add(new JLabel("Invoice Amount:"));
        JTextField invoiceAmtField = new JTextField("5000");
        inputPanel.add(invoiceAmtField);

        inputPanel.add(new JLabel("Vendor ID (PO/Invoice):"));
        JTextField vendorField = new JTextField("VENDOR-001");
        inputPanel.add(vendorField);

        inputPanel.add(new JLabel("Quantity:"));
        JTextField quantityField = new JTextField("10");
        inputPanel.add(quantityField);

        JButton matchButton = new JButton("Perform 3-Way Match");
        matchButton.addActionListener(e -> {
            try {
                ReceiptDocument receipt = new ReceiptDocument.Builder()
                        .withReceiptId("REC-001")
                        .withCustomerId("CUST-001")
                        .withCustomerName("Test Customer")
                        .withCustomerEmail("test@test.com")
                        .withPaymentId("PAY-001")
                        .withAmount(new BigDecimal(receiptAmtField.getText()))
                        .withCurrency("INR")
                        .withPaymentMethod("CHEQUE")
                        .withIssuedAt(LocalDateTime.now())
                        .withFormattedContent("Test Receipt")
                        .build();

                PurchaseOrder po = new PurchaseOrder.Builder()
                        .poId("PO-001")
                        .vendorId(vendorField.getText())
                        .vendorName("Test Vendor")
                        .itemDescription("Test Item")
                        .orderedAmount(new BigDecimal(poAmtField.getText()))
                        .currency("INR")
                        .orderedQuantity(Integer.parseInt(quantityField.getText()))
                        .createdAt(LocalDateTime.now())
                        .status("PENDING")
                        .build();

                Invoice invoice = new Invoice.Builder()
                        .invoiceId("INV-001")
                        .vendorId(vendorField.getText())
                        .vendorName("Test Vendor")
                        .itemDescription("Test Item")
                        .invoicedAmount(new BigDecimal(invoiceAmtField.getText()))
                        .currency("INR")
                        .receivedQuantity(Integer.parseInt(quantityField.getText()))
                        .invoiceDate(LocalDateTime.now())
                        .status("PENDING")
                        .build();

                var matchResult = enhancedService.performThreeWayMatch(receipt, po, invoice);
                if (matchResult.isPresent()) {
                    String result = "Match Status: " + matchResult.get().getMatchStatus() + "\n" +
                            "Amount Match: " + matchResult.get().isAmountMatches() + "\n" +
                            "Quantity Match: " + matchResult.get().isQuantityMatches() + "\n" +
                            "Vendor Match: " + matchResult.get().isVendorMatches();
                    JOptionPane.showMessageDialog(this, result, "Match Result", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        inputPanel.add(matchButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        return mainPanel;
    }

    private JPanel createReimbursementPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Expense Report"));

        inputPanel.add(new JLabel("Employee ID:"));
        JTextField empIdField = new JTextField("EMP-001");
        inputPanel.add(empIdField);

        inputPanel.add(new JLabel("Employee Name:"));
        JTextField empNameField = new JTextField("John Doe");
        inputPanel.add(empNameField);

        inputPanel.add(new JLabel("Total Amount (INR):"));
        JTextField totalAmtField = new JTextField("5000");
        inputPanel.add(totalAmtField);

        JPanel buttonPanel = new JPanel();
        JButton createReportButton = new JButton("Create Expense Report");
        createReportButton.addActionListener(e -> {
            ReimbursementService reimbService = new ReimbursementService();
            var report = reimbService.createExpenseReport(empIdField.getText(), empNameField.getText());
            JOptionPane.showMessageDialog(this, 
                    "Report Created:\n" +
                    "ID: " + report.getReportId() + "\n" +
                    "Status: " + report.getStatus(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        buttonPanel.add(createReportButton);
        inputPanel.add(buttonPanel);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        JTextArea reimbInfoArea = new JTextArea();
        reimbInfoArea.setEditable(false);
        reimbInfoArea.setText("Reimbursement Processing Features:\n\n" +
                "- Create expense reports\n" +
                "- Add receipts to reports\n" +
                "- Submit for approval\n" +
                "- Manager approval workflow\n" +
                "- Rejection handling\n" +
                "- Reimbursement processing\n" +
                "- Payment integration\n" +
                "- Mobile app support");
        mainPanel.add(new JScrollPane(reimbInfoArea), BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createLogsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel logsLabel = new JLabel("Application Logs:");
        logsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(logsLabel, BorderLayout.NORTH);

        logsDisplayArea = new JTextArea();
        logsDisplayArea.setEditable(false);
        logsDisplayArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        logsDisplayArea.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(logsDisplayArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh Logs");
        refreshButton.addActionListener(e -> refreshLogs());
        panel.add(refreshButton, BorderLayout.SOUTH);

        return panel;
    }

    private void generateReceipt() {
        try {
            // Validate inputs
            String customerId = customerIdField.getText().trim();
            String customerName = customerNameField.getText().trim();
            String email = customerEmailField.getText().trim();
            String paymentId = paymentIdField.getText().trim();
            String amountStr = amountField.getText().trim();
            String currency = (String) currencyCombo.getSelectedItem();
            String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
            boolean isCompleted = paymentCompletedCheckbox.isSelected();

            if (customerId.isEmpty() || customerName.isEmpty() || email.isEmpty() ||
                paymentId.isEmpty() || amountStr.isEmpty()) {
                statusLabel.setText("Error: All fields are required");
                statusLabel.setForeground(Color.RED);
                return;
            }

            BigDecimal amount = new BigDecimal(amountStr);

            // Create domain objects
            CustomerInformation customer = new CustomerInformation(customerId, customerName, email);
            PaymentDetails payment = new PaymentDetails(
                    paymentId,
                    amount,
                    currency,
                    paymentMethod,
                    isCompleted,
                    LocalDateTime.now()
            );

            // Generate receipt
            receiptGenerationService.generateReceipt(payment, customer)
                    .ifPresentOrElse(
                            receipt -> {
                                receiptDisplayArea.setText(receipt.getFormattedContent());
                                statusLabel.setText("Receipt generated successfully: " + receipt.getReceiptId());
                                statusLabel.setForeground(new Color(0, 128, 0));
                            },
                            () -> {
                                receiptDisplayArea.setText("Receipt generation failed. Please check the validation errors.");
                                statusLabel.setText("Receipt generation failed");
                                statusLabel.setForeground(Color.RED);
                            }
                    );

            refreshLogs();

        } catch (NumberFormatException e) {
            statusLabel.setText("Error: Invalid amount format");
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "Please enter a valid amount", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        customerIdField.setText("CUST-1001");
        customerNameField.setText("Anirudh");
        customerEmailField.setText("anirudh@example.com");
        paymentIdField.setText("PAY-2001");
        amountField.setText("2499.99");
        currencyCombo.setSelectedItem("INR");
        paymentMethodCombo.setSelectedItem("UPI");
        paymentCompletedCheckbox.setSelected(true);
        receiptDisplayArea.setText("");
        statusLabel.setText("Form cleared");
        statusLabel.setForeground(Color.BLACK);
    }

    private void refreshLogs() {
        StringBuilder logsText = new StringBuilder();
        database.readLogs().forEach(log -> {
            logsText.append(log).append("\n");
        });
        logsDisplayArea.setText(logsText.toString());
        logsDisplayArea.setCaretPosition(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReceiptManagementUI());
    }
}
