package com.repairs.views;

import com.repairs.entities.CostEstimate;
import com.repairs.entities.Receipt;
import com.repairs.enums.PaymentStatus;
import com.repairs.interfaces.view.IBillingView;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

    public GUIBillingView() {
        setTitle("Billing & Payment Management");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        initializeComponents();
        setVisible(false);
    }

    private void initializeComponents() {
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel topBar = new JPanel(new BorderLayout(10, 10));
        topBar.setBackground(new Color(240, 240, 240));
        backButton = new JButton("Back to Dashboard");
        backButton.setFocusable(false);
        JLabel tipLabel = new JLabel("Tip: Enter a receipt ID directly when processing payment.");
        tipLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        tipLabel.setForeground(new Color(80, 80, 80));
        topBar.add(backButton, BorderLayout.WEST);
        topBar.add(tipLabel, BorderLayout.CENTER);
        container.add(topBar, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Estimate Tab
        tabbedPane.addTab("Cost Estimate", createEstimatePanel());

        // Billing Tab
        tabbedPane.addTab("Bills & Receipts", createBillingPanel());

        container.add(tabbedPane, BorderLayout.CENTER);
        add(container);
    }

    private JPanel createEstimatePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Cost Estimate");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Estimate ID
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Estimate ID:"), gbc);
        gbc.gridx = 1;
        estimateIdLabel = new JLabel("-");
        panel.add(estimateIdLabel, gbc);

        // Job ID
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Job ID:"), gbc);
        gbc.gridx = 1;
        jobIdLabel = new JLabel("-");
        panel.add(jobIdLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Completed Job:"), gbc);
        gbc.gridx = 1;
        completedJobSelector = new JComboBox<>();
        completedJobSelector.setPrototypeDisplayValue("JOB-0000");
        panel.add(completedJobSelector, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JPanel jobActionPanel = new JPanel(new GridLayout(1, 3, 8, 8));
        jobActionPanel.setBackground(new Color(240, 240, 240));
        refreshJobsButton = new JButton("Refresh Jobs");
        generateEstimateButton = new JButton("Generate Estimate");
        generateBillButton = new JButton("Generate Bill");
        jobActionPanel.add(refreshJobsButton);
        jobActionPanel.add(generateEstimateButton);
        jobActionPanel.add(generateBillButton);
        gbc.gridwidth = 2;
        panel.add(jobActionPanel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Receipt ID Input:"), gbc);
        gbc.gridx = 1;
        receiptIdInputField = new JTextField(20);
        receiptIdInputField.setToolTipText("Use this field to process payment or discount for a known receipt id.");
        panel.add(receiptIdInputField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Or Select Receipt:"), gbc);
        gbc.gridx = 1;
        receiptSelector = new JComboBox<>();
        receiptSelector.setPrototypeDisplayValue("RCT-0000");
        panel.add(receiptSelector, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        loadReceiptButton = new JButton("Load Receipt Details");
        panel.add(loadReceiptButton, gbc);
        gbc.gridwidth = 1;

        // Cost Breakdown
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Cost Breakdown:"), gbc);
        gbc.gridy = 9;
        gbc.gridheight = 3;
        costBreakdownArea = new JTextArea(8, 50);
        costBreakdownArea.setEditable(false);
        costBreakdownArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(costBreakdownArea);
        panel.add(scrollPane, gbc);

        // Total Amount
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        panel.add(new JLabel("Total Amount:"), gbc);
        gbc.gridx = 1;
        totalAmountLabel = new JLabel("$0.00");
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalAmountLabel.setForeground(new Color(39, 174, 96));
        panel.add(totalAmountLabel, gbc);

        // Payment Status
        gbc.gridx = 0;
        gbc.gridy = 13;
        panel.add(new JLabel("Payment Status:"), gbc);
        gbc.gridx = 1;
        paymentStatusLabel = new JLabel("PENDING");
        paymentStatusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        paymentStatusLabel.setForeground(new Color(231, 76, 60));
        panel.add(paymentStatusLabel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setLayout(new GridLayout(1, 4, 10, 10));

        payButton = new JButton("Process Payment");
        payButton.setBackground(new Color(39, 174, 96));
        payButton.setForeground(Color.WHITE);
        buttonPanel.add(payButton);

        refundButton = new JButton("Refund");
        refundButton.setBackground(new Color(231, 76, 60));
        refundButton.setForeground(Color.WHITE);
        refundButton.setEnabled(false);
        buttonPanel.add(refundButton);

        discountButton = new JButton("Apply Discount");
        discountButton.setBackground(new Color(52, 152, 219));
        discountButton.setForeground(Color.WHITE);
        buttonPanel.add(discountButton);

        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createBillingPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Outstanding & Overdue Bills");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel tableHeader = new JPanel(new BorderLayout(8, 8));
        tableHeader.setBackground(new Color(240, 240, 240));
        tableHeader.add(titleLabel, BorderLayout.WEST);
        refreshBillsButton = new JButton("Refresh Outstanding");
        tableHeader.add(refreshBillsButton, BorderLayout.EAST);
        panel.add(tableHeader, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Receipt ID", "Amount", "Status", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        billsTable = new JTable(tableModel);
        billsTable.setRowHeight(25);
        billsTable.getTableHeader().setBackground(new Color(52, 152, 219));
        billsTable.getTableHeader().setForeground(Color.WHITE);
        billsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(billsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

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
            case PROCESSED -> new Color(39, 174, 96);
            case FAILED -> new Color(231, 76, 60);
            case PENDING -> new Color(230, 126, 34);
            case REFUNDED -> new Color(52, 152, 219);
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
        costBreakdownArea.append("═══════════════════════════════════\n");
        costBreakdownArea.append("COST BREAKDOWN\n");
        costBreakdownArea.append("═══════════════════════════════════\n\n");
        costBreakdownArea.append(String.format("Labor Cost:        $%,10.2f\n", laborCost));
        costBreakdownArea.append(String.format("Parts Cost:        $%,10.2f\n", partsCost));
        costBreakdownArea.append(String.format("Subtotal:          $%,10.2f\n", laborCost.add(partsCost)));
        costBreakdownArea.append(String.format("Tax (18%%):         $%,10.2f\n", taxAmount));
        costBreakdownArea.append("───────────────────────────────────\n");
        costBreakdownArea.append(String.format("TOTAL:             $%,10.2f\n", total));
        costBreakdownArea.append("═══════════════════════════════════\n");
        totalAmountLabel.setText("$" + total);
    }

    @Override
    public void displayDiscountApplied(BigDecimal discountAmount, BigDecimal newTotal) {
        costBreakdownArea.append("\n✓ Discount Applied: -$" + discountAmount + "\n");
        costBreakdownArea.append("New Total: $" + newTotal + "\n");
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
        tableModel.setRowCount(0);
    }

    @Override
    public void displayEstimateValidity(boolean isValid, String expiryMessage) {
        if (isValid) {
            costBreakdownArea.append("\n✓ ESTIMATE IS VALID\n");
        } else {
            costBreakdownArea.append("\n❌ ESTIMATE HAS EXPIRED\n");
        }
        costBreakdownArea.append(expiryMessage + "\n");
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