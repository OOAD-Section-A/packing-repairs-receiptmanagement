package com.receiptmanagement.ui;

import com.receiptmanagement.application.*;
import com.receiptmanagement.domain.model.*;
import com.receiptmanagement.infrastructure.accounting.QuickBooksIntegration;
import com.receiptmanagement.infrastructure.categorization.CategorizationService;
import com.receiptmanagement.infrastructure.cloudstorage.InMemoryCloudStorage;
import com.receiptmanagement.infrastructure.database.InMemoryDatabase;
import com.receiptmanagement.infrastructure.exception.ConsoleExceptionHandler;
import com.receiptmanagement.infrastructure.formatter.PlainTextReceiptFormatter;
import com.receiptmanagement.infrastructure.logging.DatabaseLogger;
import com.receiptmanagement.infrastructure.matching.ThreeWayMatchingService;
import com.receiptmanagement.infrastructure.notification.ConsoleNotificationSystem;
import com.receiptmanagement.infrastructure.ocr.MockOCRService;
import com.receiptmanagement.infrastructure.reimbursement.ReimbursementService;
import com.receiptmanagement.infrastructure.validation.StandardPaymentValidation;

import com.receiptmanagement.port.*;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

public class ReceiptManagementUI extends JFrame {

    private JTextArea receiptArea;

    private JComboBox<String> categoryReceiptBox;
    private JComboBox<String> matchReceiptBox;
    private JComboBox<String> reimbursementReceiptBox;

    private JTextArea reimbursementStatusArea;

    private final Map<String, ReceiptDocument> receiptStore =
            new LinkedHashMap<>();

    private final EnhancedReceiptGenerationService receiptService;
    private final CategorizationService categorizationService;
    private final ThreeWayMatchingService matcher;
    private final ReimbursementService reimbursementService;

    private String currentReportId = null;

    public ReceiptManagementUI() {

        setTitle("Receipt Management System");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        PaymentValidation validation =
                new StandardPaymentValidation();

        ReceiptFormatter formatter =
                new PlainTextReceiptFormatter();

        Logger logger =
                new DatabaseLogger(
                        new InMemoryDatabase()
                );

        NotificationSystemInterface notification =
                new ConsoleNotificationSystem();

        ExceptionHandlerInterface exceptionHandler =
                new ConsoleExceptionHandler();

        OCRServiceInterface ocr =
                new MockOCRService();

        CloudStorageInterface storage =
                new InMemoryCloudStorage();

        ThreeWayMatchingServiceInterface matching =
                new ThreeWayMatchingService();

        CategorizationServiceInterface categorization =
                new CategorizationService();

        AccountingIntegrationInterface accounting =
                new QuickBooksIntegration();

        receiptService =
                new EnhancedReceiptGenerationService(
                        validation,
                        formatter,
                        logger,
                        notification,
                        exceptionHandler,
                        ocr,
                        storage,
                        matching,
                        categorization,
                        accounting
                );

        categorizationService =
                new CategorizationService();

        matcher =
                new ThreeWayMatchingService();

        reimbursementService =
                new ReimbursementService();

        JTabbedPane tabs =
                new JTabbedPane();

        tabs.addTab("Generate Receipt", createGeneratePanel());
        tabs.addTab("Categorization", createCategorizationPanel());
        tabs.addTab("3-Way Matching", createMatchingPanel());
        tabs.addTab("Reimbursement", createReimbursementPanel());

        add(tabs);

        setVisible(true);
    }

    private JPanel createGeneratePanel() {

        JPanel panel = new JPanel(new BorderLayout());

        JPanel form =
                new JPanel(new GridLayout(7, 2, 5, 5));

        JTextField customerIdField =
                new JTextField("CUST-001");

        JTextField nameField =
                new JTextField("Rahul");

        JTextField emailField =
                new JTextField("rahul@mail.com");

        JTextField paymentIdField =
                new JTextField("PAY-001");

        JTextField amountField =
                new JTextField("1500");

        JComboBox<String> currencyBox =
                new JComboBox<>(new String[]{"INR", "USD"});

        JComboBox<String> methodBox =
                new JComboBox<>(new String[]{
                        "UPI",
                        "CARD",
                        "CASH",
                        "BANK_TRANSFER"
                });

        form.add(new JLabel("Customer ID"));
        form.add(customerIdField);

        form.add(new JLabel("Customer Name"));
        form.add(nameField);

        form.add(new JLabel("Email"));
        form.add(emailField);

        form.add(new JLabel("Payment ID"));
        form.add(paymentIdField);

        form.add(new JLabel("Amount"));
        form.add(amountField);

        form.add(new JLabel("Currency"));
        form.add(currencyBox);

        form.add(new JLabel("Payment Method"));
        form.add(methodBox);

        panel.add(form, BorderLayout.NORTH);

        receiptArea = new JTextArea();

        panel.add(
                new JScrollPane(receiptArea),
                BorderLayout.CENTER
        );

        JButton generateBtn =
                new JButton("Generate Receipt");

        generateBtn.addActionListener(e -> {

            try {

                CustomerInformation customer =
                        new CustomerInformation(
                                customerIdField.getText(),
                                nameField.getText(),
                                emailField.getText()
                        );

                PaymentDetails payment =
                        new PaymentDetails(
                                paymentIdField.getText(),
                                new BigDecimal(amountField.getText()),
                                (String) currencyBox.getSelectedItem(),
                                (String) methodBox.getSelectedItem(),
                                true,
                                LocalDateTime.now()
                        );

                receiptService
                        .generateReceipt(payment, customer)
                        .ifPresent(receipt -> {

                            receiptArea.setText(
                                    receipt.getFormattedContent()
                            );

                            receiptStore.put(
                                    receipt.getReceiptId(),
                                    receipt
                            );

                            updateDropdowns();

                        });

            }
            catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage()
                );

            }

        });

        panel.add(generateBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMatchingPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        matchReceiptBox = new JComboBox<>();

        JButton matchBtn =
                new JButton("Run 3-Way Match");

        JTextArea output = new JTextArea();

        matchBtn.addActionListener(e -> {

            String id =
                    (String) matchReceiptBox.getSelectedItem();

            if (id == null) return;

            ReceiptDocument receipt =
                    receiptStore.get(id);

            matcher
                    .findMatchingPO(receipt)
                    .flatMap(po ->
                            matcher
                                    .findMatchingInvoice(receipt)
                                    .map(inv ->
                                            matcher.performThreeWayMatch(
                                                    receipt,
                                                    po,
                                                    inv
                                            )
                                    )
                    )
                    .ifPresent(result -> {

                        java.util.List<String> discrepancies =
                                matcher.getDiscrepancies(result);

                        StringBuilder sb =
                                new StringBuilder();

                        sb.append("Receipt ID: ")
                          .append(id)
                          .append("\n\n");

                        sb.append("Vendor Match: ")
                          .append(result.isVendorMatches())
                          .append("\n");

                        sb.append("Amount Match: ")
                          .append(result.isAmountMatches())
                          .append("\n");

                        sb.append("Quantity Match: ")
                          .append(result.isQuantityMatches())
                          .append("\n\n");

                        if (!discrepancies.isEmpty()) {

                            sb.append("Discrepancies:\n");

                            for (String d : discrepancies) {

                                sb.append("- ")
                                  .append(d)
                                  .append("\n");

                            }

                        }

                        sb.append("\nFinal Status: ")
                          .append(result.getMatchStatus());

                        output.setText(sb.toString());

                    });

        });

        JPanel top = new JPanel();

        top.add(matchReceiptBox);
        top.add(matchBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(output));

        return panel;
    }

    private JPanel createCategorizationPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        categoryReceiptBox = new JComboBox<>();

        JButton categorizeBtn =
                new JButton("Categorize");

        JTextArea output = new JTextArea();

        categorizeBtn.addActionListener(e -> {

            String id =
                    (String) categoryReceiptBox.getSelectedItem();

            if (id == null) return;

            ReceiptDocument receipt =
                    receiptStore.get(id);

            ExpenseCategory category =
                    categorizationService
                            .categorizeReceipt(receipt);

            output.setText(
                    "Receipt ID: " + id +
                    "\nCategory: " + category
            );

        });

        JPanel top = new JPanel();

        top.add(categoryReceiptBox);
        top.add(categorizeBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(output));

        return panel;
    }

    private JPanel createReimbursementPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        reimbursementReceiptBox =
                new JComboBox<>();

        JButton createBtn =
                new JButton("Create Expense Report");

        JButton submitBtn =
                new JButton("Submit Report");

        JButton approveBtn =
                new JButton("Approve Report");

        reimbursementStatusArea =
                new JTextArea();

        createBtn.addActionListener(e -> {

            String receiptId =
                    (String) reimbursementReceiptBox.getSelectedItem();

            if (receiptId == null) return;

            ReceiptDocument receipt =
                    receiptStore.get(receiptId);

            ExpenseReport report =
                    reimbursementService.createExpenseReport(
                            "EMP-001",
                            "Service Engineer",
                            receiptId,
                            receipt.getAmount(),
                            receipt.getCurrency()
                    );

            currentReportId =
                    report.getReportId();

            reimbursementStatusArea.setText(
                    "Report ID: " +
                    report.getReportId() +
                    "\nStatus: " +
                    report.getStatus()
            );

        });

        submitBtn.addActionListener(e -> {

            if (currentReportId == null) return;

            ExpenseReport report =
                    reimbursementService.submitReport(
                            currentReportId
                    );

            reimbursementStatusArea.setText(
                    "Report ID: " +
                    report.getReportId() +
                    "\nStatus: " +
                    report.getStatus()
            );

        });

        approveBtn.addActionListener(e -> {

            if (currentReportId == null) return;

            ExpenseReport report =
                    reimbursementService.approveReport(
                            currentReportId
                    );

            reimbursementStatusArea.setText(
                    "Report ID: " +
                    report.getReportId() +
                    "\nStatus: " +
                    report.getStatus()
            );

        });

        JPanel top = new JPanel();

        top.add(reimbursementReceiptBox);
        top.add(createBtn);
        top.add(submitBtn);
        top.add(approveBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(reimbursementStatusArea));

        return panel;
    }

    private void updateDropdowns() {

        categoryReceiptBox.removeAllItems();
        matchReceiptBox.removeAllItems();
        reimbursementReceiptBox.removeAllItems();

        for (String id : receiptStore.keySet()) {

            categoryReceiptBox.addItem(id);
            matchReceiptBox.addItem(id);
            reimbursementReceiptBox.addItem(id);

        }

    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                ReceiptManagementUI::new
        );

    }

}