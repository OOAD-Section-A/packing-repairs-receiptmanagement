package com.receiptmanagement.ui;

import com.receiptmanagement.application.EnhancedReceiptGenerationService;
import com.receiptmanagement.domain.model.ExpenseCategory;
import com.receiptmanagement.infrastructure.categorization.CategorizationService;
import com.receiptmanagement.infrastructure.database.DatabaseAdapter;
import com.receiptmanagement.infrastructure.database.InvoiceRecord;
import com.receiptmanagement.port.DatabaseInterface;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ReceiptManagementUI extends JFrame {

    private final DatabaseInterface database;
    private final EnhancedReceiptGenerationService receiptService;
    private final CategorizationService categorizationService;

    private JComboBox<Object> orderComboBox;
    private JTextField amountField;
    private JComboBox<Object> categoryReceiptComboBox;
    private JComboBox<ExpenseCategory> categoryTypeComboBox;
    private JTextArea categoryResultArea;
    private JComboBox<Object> matchPoComboBox;
    private JComboBox<Object> matchReceiptComboBox;
    private JComboBox<Object> matchInvoiceComboBox;
    private JTextArea matchResultArea;
    private JTextArea logArea;

    public ReceiptManagementUI(DatabaseInterface database) {

        this.database = database;
        this.receiptService = new EnhancedReceiptGenerationService(database);
        this.categorizationService = new CategorizationService();

        setTitle("SCM Receipt Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initUI();

        loadOrders();
        loadMatchingData();
        loadCategorizationReceipts();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initUI() {

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab(
                "Generate Receipt",
                createGeneratePanel()
        );

        tabs.addTab(
                "Categorization",
                createCategorizationPanel()
        );

        tabs.addTab(
                "3-Way Matching",
                createMatchingPanel()
        );

        tabs.addTab(
                "Logs",
                createLogPanel()
        );

        add(tabs, BorderLayout.CENTER);

    }

    private JPanel createGeneratePanel() {

        JPanel panel = new JPanel();

        panel.setLayout(new GridLayout(3, 2));

        JLabel orderLabel =
                new JLabel("Select Order:");

        orderComboBox =
                new JComboBox<>();

        JLabel amountLabel =
                new JLabel("Received Amount:");

        amountField =
                new JTextField();
        amountField.setEditable(false);

        orderComboBox.addActionListener(
                e -> updateAmountFromSelectedOrder()
        );

        JButton generateButton =
                new JButton("Generate Receipt");

        generateButton.addActionListener(
                e -> generateReceipt()
        );

        panel.add(orderLabel);
        panel.add(orderComboBox);

        panel.add(amountLabel);
        panel.add(amountField);

        panel.add(generateButton);

        return panel;

    }

    private JPanel createLogPanel() {

        JPanel panel =
                new JPanel(new BorderLayout());

        logArea =
                new JTextArea();

        JScrollPane scroll =
                new JScrollPane(logArea);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;

    }

    private void loadOrders() {

        try {

            orderComboBox.removeAllItems();

            java.util.List<Object> orders =
                    database.getAllOrders();

            for (Object obj : orders) {

                if (obj instanceof com.jackfruit.scm.database.model.Order) {

                    com.jackfruit.scm.database.model.Order order =
                            (com.jackfruit.scm.database.model.Order) obj;

                    orderComboBox.addItem(
                            new OrderDisplayWrapper(order)
                    );

                }

            }

            appendLog(
                    "Orders loaded: "
                            + orderComboBox.getItemCount()
            );

            updateAmountFromSelectedOrder();

        } catch (Exception e) {

            appendError("Failed to load orders", e);

        }
    }

    private JPanel createCategorizationPanel() {

        JPanel panel =
                new JPanel(new BorderLayout());

        JPanel form =
                new JPanel(new GridLayout(4, 2));

        categoryReceiptComboBox =
                new JComboBox<>();

        categoryTypeComboBox =
                new JComboBox<>(ExpenseCategory.values());

        JButton categorizeButton =
                new JButton("Categorize");

        categorizeButton.addActionListener(
                e -> categorizeReceipt()
        );

        JButton refreshButton =
                new JButton("Refresh Receipts");

        refreshButton.addActionListener(
                e -> loadCategorizationReceipts()
        );

        form.add(new JLabel("Receipt:"));
        form.add(categoryReceiptComboBox);

        form.add(new JLabel("Receipt Type:"));
        form.add(categoryTypeComboBox);

        form.add(refreshButton);
        form.add(categorizeButton);

        categoryResultArea =
                new JTextArea();

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(categoryResultArea), BorderLayout.CENTER);

        return panel;

    }

    private void loadCategorizationReceipts() {

        try {

            categoryReceiptComboBox.removeAllItems();

            java.util.List<Object> receipts =
                    database.getAllReceipts();

            for (Object obj : receipts) {

                if (obj instanceof com.jackfruit.scm.database.model.PackagingModels.ReceiptRecord) {

                    categoryReceiptComboBox.addItem(
                            new ReceiptDisplayWrapper(
                                    (com.jackfruit.scm.database.model.PackagingModels.ReceiptRecord) obj
                            )
                    );

                }

            }

            appendLog("Categorization receipts loaded: "
                    + categoryReceiptComboBox.getItemCount());

        } catch (Exception e) {

            appendError("Failed to load categorization receipts", e);

        }

    }

    private JPanel createMatchingPanel() {

        JPanel panel =
                new JPanel(new BorderLayout());

        JPanel form =
                new JPanel(new GridLayout(5, 2));

        matchPoComboBox =
                new JComboBox<>();
        matchReceiptComboBox =
                new JComboBox<>();
        matchInvoiceComboBox =
                new JComboBox<>();

        JButton refreshButton =
                new JButton("Refresh Data");

        refreshButton.addActionListener(
                e -> loadMatchingData()
        );

        JButton matchButton =
                new JButton("Run Match");

        matchButton.addActionListener(
                e -> runThreeWayMatch()
        );

        form.add(new JLabel("Purchase Order:"));
        form.add(matchPoComboBox);

        form.add(new JLabel("Receipt:"));
        form.add(matchReceiptComboBox);

        form.add(new JLabel("Invoice:"));
        form.add(matchInvoiceComboBox);

        form.add(refreshButton);
        form.add(matchButton);

        matchResultArea =
                new JTextArea();

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(matchResultArea), BorderLayout.CENTER);

        return panel;

    }

    private void loadMatchingData() {

        try {

            matchPoComboBox.removeAllItems();
            matchReceiptComboBox.removeAllItems();
            matchInvoiceComboBox.removeAllItems();

            java.util.List<Object> orders =
                    database.getAllOrders();

            for (Object obj : orders) {

                if (obj instanceof com.jackfruit.scm.database.model.Order) {

                    matchPoComboBox.addItem(
                            new OrderDisplayWrapper(
                                    (com.jackfruit.scm.database.model.Order) obj
                            )
                    );

                }

            }

            java.util.List<Object> receipts =
                    database.getAllReceipts();

            for (Object obj : receipts) {

                if (obj instanceof com.jackfruit.scm.database.model.PackagingModels.ReceiptRecord) {

                    matchReceiptComboBox.addItem(
                            new ReceiptDisplayWrapper(
                                    (com.jackfruit.scm.database.model.PackagingModels.ReceiptRecord) obj
                            )
                    );

                }

            }

            java.util.List<Object> invoices =
                    database.getAllInvoices();

            for (Object obj : invoices) {

                if (obj instanceof InvoiceRecord) {

                    matchInvoiceComboBox.addItem(
                            new InvoiceDisplayWrapper((InvoiceRecord) obj)
                    );

                }

            }

            appendLog("3-way data loaded: POs="
                    + matchPoComboBox.getItemCount()
                    + ", Receipts="
                    + matchReceiptComboBox.getItemCount()
                    + ", Invoices="
                    + matchInvoiceComboBox.getItemCount());

        } catch (Exception e) {

            appendError("Failed to load 3-way matching data", e);

        }

    }

    private void selectMatchingRows(
            String orderId,
            String receiptId
    ) {
        selectOrderForMatch(orderId);
        selectReceiptForMatch(receiptId);
        selectInvoiceForMatch(orderId);
    }

    private void selectOrderForMatch(String orderId) {
        for (int i = 0; i < matchPoComboBox.getItemCount(); i++) {
            Object item =
                    matchPoComboBox.getItemAt(i);

            if (item instanceof OrderDisplayWrapper) {
                OrderDisplayWrapper wrapper =
                        (OrderDisplayWrapper) item;

                if (wrapper.getOrder()
                        .getOrderId()
                        .equalsIgnoreCase(orderId)) {
                    matchPoComboBox.setSelectedIndex(i);
                    return;
                }
            }
        }
    }

    private void selectReceiptForMatch(String receiptId) {
        for (int i = 0; i < matchReceiptComboBox.getItemCount(); i++) {
            Object item =
                    matchReceiptComboBox.getItemAt(i);

            if (item instanceof ReceiptDisplayWrapper) {
                ReceiptDisplayWrapper wrapper =
                        (ReceiptDisplayWrapper) item;

                if (wrapper.getReceiptRecord()
                        .receiptRecordId()
                        .equalsIgnoreCase(receiptId)) {
                    matchReceiptComboBox.setSelectedIndex(i);
                    return;
                }
            }
        }
    }

    private void selectReceiptByOrderForMatch(String orderId) {
        for (int i = 0; i < matchReceiptComboBox.getItemCount(); i++) {
            Object item =
                    matchReceiptComboBox.getItemAt(i);

            if (item instanceof ReceiptDisplayWrapper) {
                ReceiptDisplayWrapper wrapper =
                        (ReceiptDisplayWrapper) item;

                if (wrapper.getReceiptRecord()
                        .orderId()
                        .equalsIgnoreCase(orderId)) {
                    matchReceiptComboBox.setSelectedIndex(i);
                    return;
                }
            }
        }
    }

    private void selectInvoiceForMatch(String orderId) {
        for (int i = 0; i < matchInvoiceComboBox.getItemCount(); i++) {
            Object item =
                    matchInvoiceComboBox.getItemAt(i);

            if (item instanceof InvoiceDisplayWrapper) {
                InvoiceDisplayWrapper wrapper =
                        (InvoiceDisplayWrapper) item;

                if (wrapper.getInvoiceRecord()
                        .getOrderId()
                        .equalsIgnoreCase(orderId)) {
                    matchInvoiceComboBox.setSelectedIndex(i);
                    return;
                }
            }
        }
    }

    private void generateReceipt() {

        try {

            Object selected =
                    orderComboBox.getSelectedItem();

            if (selected == null) {

                appendLog("Select an order first");

                return;

            }

            OrderDisplayWrapper wrappedOrder =
                    (OrderDisplayWrapper) selected;

            com.jackfruit.scm.database.model.Order order =
                    wrappedOrder.getOrder();

            String orderId =
                    order.getOrderId();

            appendLog("Generate clicked for Order " + orderId);

            if (database.receiptExistsForOrder(orderId)) {

                appendLog("Receipt already exists for Order " + orderId);
                loadMatchingData();
                loadCategorizationReceipts();
                selectOrderForMatch(orderId);
                selectReceiptByOrderForMatch(orderId);
                selectInvoiceForMatch(orderId);

                return;

            }

            BigDecimal amount =
                    order.getTotalAmount();

            if (amount == null) {

                appendLog("Selected order has no total amount");

                return;

            }

            appendLog("Saving receipt for Order "
                    + orderId
                    + " amount "
                    + amount);

            String receiptId =
                    receiptService
                            .generateReceiptForOrder(

                                    orderId,
                                    amount

                            );

            appendLog(
                    "Receipt Created: "
                            + receiptId
                            + " for Order "
                            + orderId
            );

            loadMatchingData();
            loadCategorizationReceipts();
            selectMatchingRows(orderId, receiptId);

        }

        catch (Exception e) {

            appendError("Generate receipt failed", e);

        }

    }

    private void categorizeReceipt() {

        try {

            ReceiptDisplayWrapper selectedReceipt =
                    (ReceiptDisplayWrapper) categoryReceiptComboBox.getSelectedItem();

            if (selectedReceipt == null) {

                appendLog("Select a receipt before categorizing");

                return;

            }

            com.jackfruit.scm.database.model.PackagingModels.ReceiptRecord receipt =
                    selectedReceipt.getReceiptRecord();

            ExpenseCategory category =
                    (ExpenseCategory) categoryTypeComboBox.getSelectedItem();

            if (category == null) {

                appendLog("Select an expense type before categorizing");

                return;

            }

            String result =
                    "Receipt: "
                            + receipt.receiptRecordId()
                            + "\nExpense Type: "
                            + category.getDisplayName();

            appendLog("Expense type categorization result: " + result);
            categoryResultArea.append(result + "\n");

        } catch (Exception e) {

            appendError("Categorization failed", e);

        }

    }

    private void updateAmountFromSelectedOrder() {
        Object selected =
                orderComboBox.getSelectedItem();

        if (selected instanceof OrderDisplayWrapper) {
            OrderDisplayWrapper wrapper =
                    (OrderDisplayWrapper) selected;

            BigDecimal amount =
                    wrapper.getOrder().getTotalAmount();

            amountField.setText(amount == null ? "" : amount.toPlainString());
        }
    }

    private void runThreeWayMatch() {

        try {

            OrderDisplayWrapper selectedPo =
                    (OrderDisplayWrapper) matchPoComboBox.getSelectedItem();

            ReceiptDisplayWrapper selectedReceipt =
                    (ReceiptDisplayWrapper) matchReceiptComboBox.getSelectedItem();

            InvoiceDisplayWrapper selectedInvoice =
                    (InvoiceDisplayWrapper) matchInvoiceComboBox.getSelectedItem();

            if (selectedPo == null
                    || selectedReceipt == null
                    || selectedInvoice == null) {

                appendLog("Select PO, Receipt, and Invoice before matching");

                return;

            }

            com.jackfruit.scm.database.model.Order po =
                    selectedPo.getOrder();

            com.jackfruit.scm.database.model.PackagingModels.ReceiptRecord receipt =
                    selectedReceipt.getReceiptRecord();

            InvoiceRecord invoice =
                    selectedInvoice.getInvoiceRecord();

            boolean poInvoiceOrderMatches =
                    po.getOrderId().equalsIgnoreCase(invoice.getOrderId());

            boolean poReceiptOrderMatches =
                    po.getOrderId().equalsIgnoreCase(receipt.orderId());

            boolean poInvoiceAmountMatches =
                    po.getTotalAmount().compareTo(invoice.getInvoiceAmount()) == 0;

            boolean poReceiptAmountMatches =
                    po.getTotalAmount().compareTo(receipt.receivedAmount()) == 0;

            boolean matched =
                    poInvoiceOrderMatches
                            && poReceiptOrderMatches
                            && poInvoiceAmountMatches
                            && poReceiptAmountMatches;

            String status =
                    matched ? "MATCH" : "DISCREPANCY";

            String discrepancies =
                    buildDiscrepancyText(
                            poInvoiceOrderMatches,
                            poReceiptOrderMatches,
                            poInvoiceAmountMatches,
                            poReceiptAmountMatches
                    );

            String message =
                    "Status: "
                            + status
                            + "\nPO Order ID: "
                            + po.getOrderId()
                            + "\nReceipt Order ID: "
                            + receipt.orderId()
                            + "\nInvoice Order ID: "
                            + invoice.getOrderId()
                            + "\nPO Amount: "
                            + po.getTotalAmount()
                            + "\nReceipt Amount: "
                            + receipt.receivedAmount()
                            + "\nInvoice Amount: "
                            + invoice.getInvoiceAmount()
                            + "\nDiscrepancies: "
                            + discrepancies
                            + "\n";

            appendLog("3-way database match result: " + status);
            matchResultArea.append(message + "\n");

            if (!matched) {

                database.logSubsystemException(
                        "ThreeWayMatchingDiscrepancy",
                        "ERROR",
                        message.replace("\n", " | ")
                );

            }

        } catch (Exception e) {

            appendError("3-way matching failed", e);

        }

    }

    private String buildDiscrepancyText(
            boolean poInvoiceOrderMatches,
            boolean poReceiptOrderMatches,
            boolean poInvoiceAmountMatches,
            boolean poReceiptAmountMatches
    ) {
        StringBuilder builder =
                new StringBuilder();

        if (!poInvoiceOrderMatches) {
            builder.append("PO order_id does not match invoice order_id; ");
        }

        if (!poReceiptOrderMatches) {
            builder.append("PO order_id does not match receipt order_id; ");
        }

        if (!poInvoiceAmountMatches) {
            builder.append("PO total_amount does not match invoice amount; ");
        }

        if (!poReceiptAmountMatches) {
            builder.append("PO total_amount does not match receipt received_amount; ");
        }

        if (builder.length() == 0) {
            return "None";
        }

        return builder.toString().trim();
    }

    private void appendLog(String message) {
        String line = "[ReceiptManagementUI] " + message;
        System.out.println(line);
        logArea.append(line + "\n");
    }

    private void appendError(String message, Exception e) {
        String line = "[ReceiptManagementUI] ERROR: "
                + message
                + " - "
                + e.getMessage();
        System.err.println(line);
        e.printStackTrace(System.err);
        logArea.append(line + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new ReceiptManagementUI(new DatabaseAdapter())
        );
    }
}
