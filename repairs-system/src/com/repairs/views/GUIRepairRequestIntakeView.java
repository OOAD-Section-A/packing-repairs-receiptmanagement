package com.repairs.views;

import com.repairs.entities.RepairRequest;
import com.repairs.enums.RepairType;
import com.repairs.interfaces.view.IRepairRequestIntakeView;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * GUIRepairRequestIntakeView - Swing GUI implementation of IRepairRequestIntakeView.
 * Provides a professional GUI for repair request intake.
 */
public class GUIRepairRequestIntakeView extends JFrame implements IRepairRequestIntakeView {
    private JTextField requestIdField;
    private JTextField customerIdField;
    private JComboBox<RepairType> repairTypeCombo;
    private JTextArea descriptionArea;
    private JButton submitButton;
    private JLabel statusLabel;
    private JPanel mainPanel;

    public GUIRepairRequestIntakeView() {
        setTitle("Repair Request Intake Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        initializeComponents();
        setVisible(false);
    }

    private void initializeComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Repair Request Intake Form");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Request ID
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Request ID:"), gbc);
        gbc.gridx = 1;
        requestIdField = new JTextField(20);
        requestIdField.setText("REQ-" + System.currentTimeMillis() % 10000);
        mainPanel.add(requestIdField, gbc);

        // Customer ID
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Customer ID:"), gbc);
        gbc.gridx = 1;
        customerIdField = new JTextField(20);
        mainPanel.add(customerIdField, gbc);

        // Repair Type
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Repair Type:"), gbc);
        gbc.gridx = 1;
        repairTypeCombo = new JComboBox<>(RepairType.values());
        mainPanel.add(repairTypeCombo, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.gridheight = 3;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        mainPanel.add(scrollPane, gbc);

        // Status Label
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.BLACK);
        mainPanel.add(statusLabel, gbc);

        // Submit Button
        gbc.gridy = 8;
        submitButton = new JButton("Submit Request");
        submitButton.setBackground(new Color(52, 152, 219));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Arial", Font.BOLD, 12));
        mainPanel.add(submitButton, gbc);

        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(mainScrollPane);
    }

    @Override
    public void showRepairRequestForm() {
        setVisible(true);
    }

    @Override
    public void displayValidationErrors(List<String> errors) {
        StringBuilder errorMsg = new StringBuilder("Validation Errors:\n");
        for (String error : errors) {
            errorMsg.append("• ").append(error).append("\n");
        }
        JOptionPane.showMessageDialog(this, errorMsg.toString(), "Validation Errors", JOptionPane.ERROR_MESSAGE);
        statusLabel.setText("❌ Validation Failed");
        statusLabel.setForeground(Color.RED);
    }

    @Override
    public void displayValidationSuccess() {
        statusLabel.setText("✓ Validation Passed");
        statusLabel.setForeground(new Color(39, 174, 96));
    }

    @Override
    public void displayScheduledDate(String scheduledDate) {
        JOptionPane.showMessageDialog(this, "Scheduled Date: " + scheduledDate, "Scheduling Success", JOptionPane.INFORMATION_MESSAGE);
        statusLabel.setText("📅 Scheduled for: " + scheduledDate);
        statusLabel.setForeground(new Color(39, 174, 96));
    }

    @Override
    public void clearForm() {
        requestIdField.setText("REQ-" + System.currentTimeMillis() % 10000);
        customerIdField.setText("");
        repairTypeCombo.setSelectedIndex(0);
        descriptionArea.setText("");
        statusLabel.setText(" ");
    }

    @Override
    public void showLoadingIndicator(String message) {
        statusLabel.setText("⏳ " + message);
        statusLabel.setForeground(Color.ORANGE);
        submitButton.setEnabled(false);
    }

    @Override
    public void hideLoadingIndicator() {
        submitButton.setEnabled(true);
    }

    @Override
    public void displayError(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
        statusLabel.setText("❌ " + errorMessage);
        statusLabel.setForeground(Color.RED);
    }

    @Override
    public void displaySuccess(String successMessage) {
        JOptionPane.showMessageDialog(this, successMessage, "Success", JOptionPane.INFORMATION_MESSAGE);
        statusLabel.setText("✓ " + successMessage);
        statusLabel.setForeground(new Color(39, 174, 96));
    }

    @Override
    public RepairRequest getRepairRequestInput() {
        String requestId = requestIdField.getText().trim();
        String customerId = customerIdField.getText().trim();
        RepairType repairType = (RepairType) repairTypeCombo.getSelectedItem();
        String description = descriptionArea.getText().trim();

        return new RepairRequest.Builder()
                .requestId(requestId)
                .customerId(customerId)
                .repairType(repairType)
                .description(description)
                .createdDate(LocalDateTime.now())
                .build();
    }

    @Override
    public void setSubmitButtonEnabled(boolean enabled) {
        submitButton.setEnabled(enabled);
    }

    @Override
    public void requestFocus() {
        setFocusable(true);
    }

    @Override
    public boolean isVisible() {
        return super.isVisible();
    }

    @Override
    public boolean showConfirmDialog(String message) {
        int result = JOptionPane.showConfirmDialog(this, message, "Confirm", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    public JButton getSubmitButton() {
        return submitButton;
    }
}