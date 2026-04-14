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
    private JLabel estimateIdLabel;
    private JLabel jobIdLabel;
    private JTextArea costBreakdownArea;
    private JLabel totalAmountLabel;
    private JLabel paymentStatusLabel;
    private JButton payButton;
    private JButton refundButton;
    private JButton discountButton;
    private JTable billsTable;
    private DefaultTableModel tableModel;

    public GUIBillingView() {
        setTitle("Billing & Payment Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        initializeComponents();
        setVisible(false);
    }

    private void initializeComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Estimate Tab
        tabbedPane.addTab("Cost Estimate", createEstimatePanel());

        // Billing Tab
        tabbedPane.addTab("Bills & Receipts", createBillingPanel());

        add(tabbedPane);
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

        // Cost Breakdown
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Cost Breakdown:"), gbc);
        gbc.gridy = 4;
        gbc.gridheight = 3;
        costBreakdownArea = new JTextArea(8, 50);
        costBreakdownArea.setEditable(false);
        costBreakdownArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(costBreakdownArea);
        panel.add(scrollPane, gbc);

        // Total Amount
        gbc.gridx = 0;
        gbc.gridy = 7;
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
        gbc.gridy = 8;
        panel.add(new JLabel("Payment Status:"), gbc);
        gbc.gridx = 1;
        paymentStatusLabel = new JLabel("PENDING");
        paymentStatusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        paymentStatusLabel.setForeground(new Color(231, 76, 60));
        panel.add(paymentStatusLabel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setLayout(new GridLayout(1, 3, 10, 10));

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
        gbc.gridy = 9;
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
        panel.add(titleLabel, BorderLayout.NORTH);

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
} 