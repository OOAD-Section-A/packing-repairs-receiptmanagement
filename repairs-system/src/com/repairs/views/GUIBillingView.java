package com.repairs.views;

import com.repairs.entities.CostEstimate;
import com.repairs.entities.Receipt;
import com.repairs.enums.PaymentStatus;
import com.repairs.interfaces.view.IBillingView;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * GUIBillingView - Swing GUI implementation of IBillingView.
 * Provides a professional GUI for billing and payment operations.
 */
public class GUIBillingView extends JFrame implements IBillingView {
    private JTextField receiptIdInputField;
    private JComboBox<String> receiptSelector;
    private JComboBox<String> completedJobSelector;
    private JLabel estimateIdLabel;
    private JLabel jobIdLabel;
    private JTextArea costBreakdownArea;
    private JLabel totalAmountLabel;
    private JLabel paymentStatusLabel;
    private JButton backButton;
    private JButton refreshBillsButton;
    private JButton refreshJobsButton;
    private JButton generateEstimateButton;
    private JButton generateBillButton;
    private JButton loadReceiptButton;
    private JButton payButton;
    private JButton refundButton;
    private JButton discountButton;
    private JTable billsTable;
    private DefaultTableModel tableModel;

    // Modern color palette (shared with other views)
    private static final Color BG_PRIMARY = new Color(25, 28, 36);
    private static final Color BG_CARD = new Color(35, 39, 50);
    private static final Color BG_INPUT = new Color(44, 49, 63);
    private static final Color ACCENT_BLUE = new Color(88, 136, 255);
    private static final Color ACCENT_GREEN = new Color(72, 199, 142);
    private static final Color ACCENT_ORANGE = new Color(255, 171, 64);
    private static final Color ACCENT_RED = new Color(255, 89, 94);
    private static final Color TEXT_PRIMARY = new Color(230, 233, 240);
    private static final Color TEXT_SECONDARY = new Color(150, 158, 175);
    private static final Color BORDER_SUBTLE = new Color(55, 60, 75);

    public GUIBillingView() {
        setTitle("Billing & Payment Management");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(860, 740);
        setLocationRelativeTo(null);
        setResizable(false);

        initializeComponents();
        setVisible(false);
    }

    private void initializeComponents() {
        JPanel container = new JPanel(new BorderLayout(0, 0));
        container.setBackground(BG_PRIMARY);

        // --- Top Bar ---
        JPanel topBar = new JPanel(new BorderLayout(12, 0));
        topBar.setBackground(BG_CARD);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        backButton = createStyledButton("← Dashboard", TEXT_SECONDARY, BG_INPUT);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        topBar.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Billing & Payment Management");
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 17));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(titleLabel, BorderLayout.CENTER);

        JLabel tipLabel = new JLabel("Enter receipt ID for direct payment  ");
        tipLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        tipLabel.setForeground(TEXT_SECONDARY);
        topBar.add(tipLabel, BorderLayout.EAST);
        container.add(topBar, BorderLayout.NORTH);

        // --- Tabbed Pane ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        tabbedPane.setBackground(BG_PRIMARY);
        tabbedPane.setForeground(TEXT_PRIMARY);

        tabbedPane.addTab("  Cost Estimate  ", createEstimatePanel());
        tabbedPane.addTab("  Bills & Receipts  ", createBillingPanel());

        container.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(container);
    }

    private JPanel createEstimatePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        // Job Selection Card
        JPanel jobCard = createCard("Job & Receipt Selection");
        jobCard.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0;
        jobCard.add(styledLabel("Estimate ID"), g);
        g.gridx = 1;
        estimateIdLabel = new JLabel("-");
        estimateIdLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        estimateIdLabel.setForeground(ACCENT_BLUE);
        jobCard.add(estimateIdLabel, g);

        g.gridx = 0; g.gridy = 1;
        jobCard.add(styledLabel("Job ID"), g);
        g.gridx = 1;
        jobIdLabel = new JLabel("-");
        jobIdLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        jobIdLabel.setForeground(ACCENT_BLUE);
        jobCard.add(jobIdLabel, g);

        g.gridx = 0; g.gridy = 2;
        jobCard.add(styledLabel("Completed Job"), g);
        g.gridx = 1; g.weightx = 1.0;
        completedJobSelector = new JComboBox<>();
        styleComboBox(completedJobSelector);
        jobCard.add(completedJobSelector, g);

        g.gridx = 0; g.gridy = 3; g.weightx = 0;
        g.gridwidth = 2;
        JPanel jobActionPanel = new JPanel(new GridLayout(1, 3, 8, 8));
        jobActionPanel.setOpaque(false);
        refreshJobsButton = createStyledButton("Refresh Jobs", TEXT_PRIMARY, ACCENT_BLUE.darker());
        generateEstimateButton = createStyledButton("Generate Estimate", TEXT_PRIMARY, BG_INPUT);
        generateBillButton = createStyledButton("Generate Bill", TEXT_PRIMARY, BG_INPUT);
        jobActionPanel.add(refreshJobsButton);
        jobActionPanel.add(generateEstimateButton);
        jobActionPanel.add(generateBillButton);
        jobCard.add(jobActionPanel, g);
        g.gridwidth = 1;

        g.gridx = 0; g.gridy = 4; g.weightx = 0;
        jobCard.add(styledLabel("Receipt ID"), g);
        g.gridx = 1; g.weightx = 1.0;
        receiptIdInputField = styledTextField(20);
        receiptIdInputField.setToolTipText("Use this field to process payment or discount for a known receipt id.");
        jobCard.add(receiptIdInputField, g);

        g.gridx = 0; g.gridy = 5; g.weightx = 0;
        jobCard.add(styledLabel("Or Select"), g);
        g.gridx = 1; g.weightx = 1.0;
        receiptSelector = new JComboBox<>();
        styleComboBox(receiptSelector);
        jobCard.add(receiptSelector, g);

        g.gridx = 0; g.gridy = 6; g.gridwidth = 2; g.weightx = 0;
        loadReceiptButton = createStyledButton("Load Receipt Details", TEXT_PRIMARY, ACCENT_BLUE.darker());
        jobCard.add(loadReceiptButton, g);
        g.gridwidth = 1;

        panel.add(jobCard);
        panel.add(Box.createVerticalStrut(8));

        // Cost Breakdown Card
        JPanel costCard = createCard("Cost Breakdown");
        costCard.setLayout(new BorderLayout(6, 6));

        costBreakdownArea = new JTextArea(7, 50);
        costBreakdownArea.setEditable(false);
        costBreakdownArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        costBreakdownArea.setBackground(BG_INPUT);
        costBreakdownArea.setForeground(TEXT_PRIMARY);
        costBreakdownArea.setCaretColor(TEXT_PRIMARY);
        costBreakdownArea.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        JScrollPane scrollPane = new JScrollPane(costBreakdownArea);
        scrollPane.setBorder(new LineBorder(BORDER_SUBTLE, 1, true));
        costCard.add(scrollPane, BorderLayout.CENTER);

        JPanel summaryPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        summaryPanel.setOpaque(false);
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        totalPanel.setOpaque(false);
        totalPanel.add(styledLabel("Total Amount:"));
        totalAmountLabel = new JLabel("$0.00");
        totalAmountLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
        totalAmountLabel.setForeground(ACCENT_GREEN);
        totalPanel.add(totalAmountLabel);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        statusPanel.setOpaque(false);
        statusPanel.add(styledLabel("Payment Status:"));
        paymentStatusLabel = new JLabel("PENDING");
        paymentStatusLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        paymentStatusLabel.setForeground(ACCENT_ORANGE);
        statusPanel.add(paymentStatusLabel);

        summaryPanel.add(totalPanel);
        summaryPanel.add(statusPanel);
        costCard.add(summaryPanel, BorderLayout.SOUTH);

        panel.add(costCard);
        panel.add(Box.createVerticalStrut(8));

        // Action Buttons
        JPanel actionBar = new JPanel(new GridLayout(1, 3, 10, 0));
        actionBar.setOpaque(false);

        payButton = createActionButton("Process Payment", ACCENT_GREEN);
        refundButton = createActionButton("Refund", ACCENT_RED);
        refundButton.setEnabled(false);
        discountButton = createActionButton("Apply Discount", ACCENT_BLUE);

        actionBar.add(payButton);
        actionBar.add(refundButton);
        actionBar.add(discountButton);
        panel.add(actionBar);

        return panel;
    }

    private JPanel createBillingPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JPanel headerPanel = new JPanel(new BorderLayout(8, 0));
        headerPanel.setOpaque(false);
        JLabel tableTitle = new JLabel("Outstanding & Overdue Bills");
        tableTitle.setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
        tableTitle.setForeground(TEXT_PRIMARY);
        headerPanel.add(tableTitle, BorderLayout.WEST);
        refreshBillsButton = createStyledButton("Refresh Outstanding", TEXT_PRIMARY, ACCENT_BLUE.darker());
        headerPanel.add(refreshBillsButton, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Receipt ID", "Amount", "Status", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        billsTable = new JTable(tableModel);
        billsTable.setRowHeight(30);
        billsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        billsTable.setBackground(BG_CARD);
        billsTable.setForeground(TEXT_PRIMARY);
        billsTable.setGridColor(BORDER_SUBTLE);
        billsTable.setSelectionBackground(ACCENT_BLUE.darker());
        billsTable.setSelectionForeground(Color.WHITE);
        billsTable.setShowGrid(true);
        billsTable.setIntercellSpacing(new Dimension(1, 1));

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBackground(BG_CARD);
        cellRenderer.setForeground(TEXT_PRIMARY);
        cellRenderer.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        for (int i = 0; i < billsTable.getColumnCount(); i++) {
            billsTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JTableHeader header = billsTable.getTableHeader();
        header.setBackground(BG_INPUT);
        header.setForeground(ACCENT_BLUE);
        header.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
        header.setBorder(new LineBorder(BORDER_SUBTLE));

        JScrollPane scrollPane = new JScrollPane(billsTable);
        scrollPane.setBorder(new LineBorder(BORDER_SUBTLE, 1, true));
        scrollPane.getViewport().setBackground(BG_CARD);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // =============== Styling Helpers ===============

    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setBackground(BG_CARD);
        TitledBorder tb = BorderFactory.createTitledBorder(
                new LineBorder(BORDER_SUBTLE, 1, true), title);
        tb.setTitleColor(TEXT_SECONDARY);
        tb.setTitleFont(new Font("Segoe UI", Font.PLAIN, 11));
        card.setBorder(BorderFactory.createCompoundBorder(
                tb, BorderFactory.createEmptyBorder(6, 10, 8, 10)));
        return card;
    }

    private JLabel styledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    private JTextField styledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBackground(BG_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_SUBTLE, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return field;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBackground(BG_INPUT);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(new LineBorder(BORDER_SUBTLE, 1, true));
    }

    private JButton createStyledButton(String text, Color fg, Color bg) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(fg);
        button.setBackground(bg);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_SUBTLE, 1, true),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createActionButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bg);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    // =============== IBillingView Implementation ===============

    @Override
    public void displayCostEstimate(CostEstimate estimate) {
        estimateIdLabel.setText(estimate.getEstimateId());
        jobIdLabel.setText(estimate.getRepairJob().getJobId());
        totalAmountLabel.setText("$" + estimate.getTotalCost());
    }

    @Override
    public void displayReceipt(Receipt receipt) {
        estimateIdLabel.setText(receipt.getReceiptId());
        receiptIdInputField.setText(receipt.getReceiptId());
        jobIdLabel.setText(receipt.getRepairJob().getJobId());
        totalAmountLabel.setText("$" + receipt.getFinalAmount());
        paymentStatusLabel.setText(receipt.getPaymentStatus().toString());
    }

    @Override
    public void showPaymentStatus(PaymentStatus status) {
        paymentStatusLabel.setText(status.toString());
        Color statusColor = switch (status) {
            case PROCESSED -> ACCENT_GREEN;
            case FAILED -> ACCENT_RED;
            case PENDING -> ACCENT_ORANGE;
            case REFUNDED -> ACCENT_BLUE;
        };
        paymentStatusLabel.setForeground(statusColor);
    }

    @Override
    public void displayPaymentStatusMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Payment Status", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void displayOutstandingBills(List<Receipt> bills) {
        tableModel.setRowCount(0);
        for (Receipt bill : bills) {
            tableModel.addRow(new Object[]{
                    bill.generateReceiptNumber(),
                    "$" + bill.getFinalAmount(),
                    bill.getPaymentStatus(),
                    bill.getGeneratedDate()
            });
        }
    }

    @Override
    public void displayOverdueBills(List<Receipt> bills) {
        tableModel.setRowCount(0);
        for (Receipt bill : bills) {
            tableModel.addRow(new Object[]{
                    bill.generateReceiptNumber(),
                    "$" + bill.getFinalAmount(),
                    "OVERDUE",
                    bill.getGeneratedDate()
            });
        }
    }

    @Override
    public void displayCostBreakdown(BigDecimal laborCost, BigDecimal partsCost, BigDecimal taxAmount, BigDecimal total) {
        costBreakdownArea.setText("");
        costBreakdownArea.append("══════════════════════════════════════\n");
        costBreakdownArea.append("  COST BREAKDOWN\n");
        costBreakdownArea.append("══════════════════════════════════════\n\n");
        costBreakdownArea.append(String.format("  Labor Cost:        $%,10.2f\n", laborCost));
        costBreakdownArea.append(String.format("  Parts Cost:        $%,10.2f\n", partsCost));
        costBreakdownArea.append(String.format("  Subtotal:          $%,10.2f\n", laborCost.add(partsCost)));
        costBreakdownArea.append(String.format("  Tax (18%%):         $%,10.2f\n", taxAmount));
        costBreakdownArea.append("  ──────────────────────────────────\n");
        costBreakdownArea.append(String.format("  TOTAL:             $%,10.2f\n", total));
        costBreakdownArea.append("══════════════════════════════════════\n");
        totalAmountLabel.setText("$" + total);
    }

    @Override
    public void displayDiscountApplied(BigDecimal discountAmount, BigDecimal newTotal) {
        costBreakdownArea.append("\n  ✓ Discount Applied: -$" + discountAmount + "\n");
        costBreakdownArea.append("  New Total: $" + newTotal + "\n");
        totalAmountLabel.setText("$" + newTotal);
    }

    @Override
    public void displayError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void displayWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void displaySuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showLoadingIndicator() {
        payButton.setEnabled(false);
        discountButton.setEnabled(false);
    }

    @Override
    public void hideLoadingIndicator() {
        payButton.setEnabled(true);
        discountButton.setEnabled(true);
    }

    @Override
    public String showPaymentMethodDialog() {
        String[] methods = {"Credit Card", "Debit Card", "Bank Transfer"};
        String selected = (String) JOptionPane.showInputDialog(this, "Select Payment Method:", "Payment Method", JOptionPane.QUESTION_MESSAGE, null, methods, methods[0]);
        return selected != null ? selected : "Credit Card";
    }

    @Override
    public void setPayButtonEnabled(boolean enabled) {
        payButton.setEnabled(enabled);
    }

    @Override
    public void setRefundButtonEnabled(boolean enabled) {
        refundButton.setEnabled(enabled);
    }

    @Override
    public void setDiscountButtonEnabled(boolean enabled) {
        discountButton.setEnabled(enabled);
    }

    @Override
    public void clearDisplay() {
        estimateIdLabel.setText("-");
        receiptIdInputField.setText("");
        jobIdLabel.setText("-");
        costBreakdownArea.setText("");
        totalAmountLabel.setText("$0.00");
        paymentStatusLabel.setText("PENDING");
        paymentStatusLabel.setForeground(ACCENT_ORANGE);
        tableModel.setRowCount(0);
    }

    @Override
    public void displayEstimateValidity(boolean isValid, String expiryMessage) {
        if (isValid) {
            costBreakdownArea.append("\n  ✓ ESTIMATE IS VALID\n");
        } else {
            costBreakdownArea.append("\n  ✕ ESTIMATE HAS EXPIRED\n");
        }
        costBreakdownArea.append("  " + expiryMessage + "\n");
    }

    @Override
    public void displayPaymentHistory(List<Receipt> receipts) {
        tableModel.setRowCount(0);
        for (Receipt receipt : receipts) {
            tableModel.addRow(new Object[]{
                    receipt.generateReceiptNumber(),
                    "$" + receipt.getFinalAmount(),
                    receipt.getPaymentStatus(),
                    receipt.getGeneratedDate()
            });
        }
    }

    // =============== Accessors ===============

    public JButton getPayButton() {
        return payButton;
    }

    public JButton getRefundButton() {
        return refundButton;
    }

    public JButton getDiscountButton() {
        return discountButton;
    }

    public String getCurrentReceiptId() {
        String typedId = receiptIdInputField.getText().trim();
        if (!typedId.isEmpty()) {
            return typedId;
        }
        Object selected = receiptSelector.getSelectedItem();
        if (selected != null) {
            String selectedId = selected.toString();
            if (!"No receipts found".equals(selectedId)) {
                return selectedId;
            }
        }
        return estimateIdLabel.getText();
    }

    public void setAvailableReceiptIds(List<String> receiptIds) {
        receiptSelector.removeAllItems();
        if (receiptIds == null || receiptIds.isEmpty()) {
            receiptSelector.addItem("No receipts found");
            return;
        }
        for (String receiptId : receiptIds) {
            receiptSelector.addItem(receiptId);
        }
    }

    public JButton getRefreshBillsButton() {
        return refreshBillsButton;
    }

    public JButton getRefreshJobsButton() {
        return refreshJobsButton;
    }

    public JButton getGenerateEstimateButton() {
        return generateEstimateButton;
    }

    public JButton getGenerateBillButton() {
        return generateBillButton;
    }

    public JButton getLoadReceiptButton() {
        return loadReceiptButton;
    }

    public String getSelectedCompletedJobId() {
        Object selected = completedJobSelector.getSelectedItem();
        if (selected == null) {
            return null;
        }
        String value = selected.toString();
        return "No completed jobs found".equals(value) ? null : value;
    }

    public void setAvailableCompletedJobIds(List<String> jobIds) {
        completedJobSelector.removeAllItems();
        if (jobIds == null || jobIds.isEmpty()) {
            completedJobSelector.addItem("No completed jobs found");
            return;
        }
        for (String jobId : jobIds) {
            completedJobSelector.addItem(jobId);
        }
    }

    public void setBackAction(Runnable action) {
        backButton.addActionListener(e -> action.run());
    }
}