package com.repairs.views;

import com.repairs.entities.RepairRequest;
import com.repairs.enums.RepairType;
import com.repairs.interfaces.view.IRepairRequestIntakeView;
import javax.swing.*;
import javax.swing.border.*;
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
    private JComboBox<String> requestSelector;
    private JTextArea descriptionArea;
    private JButton submitButton;
    private JButton backButton;
    private JButton resetButton;
    private JButton checkStatusButton;
    private JButton refreshRequestsButton;
    private JLabel statusLabel;
    private JPanel mainPanel;

    // Modern color palette (shared with other views)
    private static final Color BG_PRIMARY = new Color(25, 28, 36);
    private static final Color BG_CARD = new Color(35, 39, 50);
    private static final Color BG_INPUT = new Color(44, 49, 63);
    private static final Color ACCENT_BLUE = new Color(88, 136, 255);
    private static final Color ACCENT_GREEN = new Color(72, 199, 142);
    private static final Color ACCENT_RED = new Color(255, 89, 94);
    private static final Color TEXT_PRIMARY = new Color(230, 233, 240);
    private static final Color TEXT_SECONDARY = new Color(150, 158, 175);
    private static final Color BORDER_SUBTLE = new Color(55, 60, 75);

    public GUIRepairRequestIntakeView() {
        setTitle("Repair Request Intake Form");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(560, 640);
        setLocationRelativeTo(null);
        setResizable(false);

        initializeComponents();
        setVisible(false);
    }

    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BG_PRIMARY);

        // --- Top Bar ---
        JPanel topBar = new JPanel(new BorderLayout(12, 0));
        topBar.setBackground(BG_CARD);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        backButton = createStyledButton("← Dashboard", TEXT_SECONDARY, BG_INPUT);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        topBar.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("New Repair Request");
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 17));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(titleLabel, BorderLayout.CENTER);

        JLabel helperLabel = new JLabel("Use C10001 format for IDs  ");
        helperLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        helperLabel.setForeground(TEXT_SECONDARY);
        topBar.add(helperLabel, BorderLayout.EAST);
        mainPanel.add(topBar, BorderLayout.NORTH);

        // --- Center Content ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BG_PRIMARY);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));

        // Existing Requests Card
        JPanel existingCard = createCard("Existing Requests");
        existingCard.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0;
        existingCard.add(styledLabel("Request"), g);
        g.gridx = 1; g.weightx = 1.0;
        requestSelector = new JComboBox<>();
        styleComboBox(requestSelector);
        existingCard.add(requestSelector, g);
        g.gridx = 2; g.weightx = 0;
        refreshRequestsButton = createStyledButton("Refresh", TEXT_PRIMARY, ACCENT_BLUE.darker());
        existingCard.add(refreshRequestsButton, g);
        g.gridx = 3;
        checkStatusButton = createStyledButton("Check Status", TEXT_PRIMARY, BG_INPUT);
        existingCard.add(checkStatusButton, g);

        centerPanel.add(existingCard);
        centerPanel.add(Box.createVerticalStrut(10));

        // New Request Form Card
        JPanel formCard = createCard("Request Details");
        formCard.setLayout(new GridBagLayout());
        g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0;
        formCard.add(styledLabel("Request ID"), g);
        g.gridx = 1; g.weightx = 1.0;
        requestIdField = styledTextField(20);
        requestIdField.setText("REQ-" + System.currentTimeMillis() % 10000);
        requestIdField.setToolTipText("Auto-generated request identifier. You can edit if needed.");
        formCard.add(requestIdField, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        formCard.add(styledLabel("Customer ID"), g);
        g.gridx = 1; g.weightx = 1.0;
        customerIdField = styledTextField(20);
        customerIdField.setToolTipText("Enter customer id, for example C10001.");
        formCard.add(customerIdField, g);

        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        formCard.add(styledLabel("Repair Type"), g);
        g.gridx = 1; g.weightx = 1.0;
        repairTypeCombo = new JComboBox<>(RepairType.values());
        repairTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        repairTypeCombo.setBackground(BG_INPUT);
        repairTypeCombo.setForeground(TEXT_PRIMARY);
        repairTypeCombo.setBorder(new LineBorder(BORDER_SUBTLE, 1, true));
        formCard.add(repairTypeCombo, g);

        g.gridx = 0; g.gridy = 3; g.weightx = 0;
        g.anchor = GridBagConstraints.NORTHWEST;
        formCard.add(styledLabel("Description"), g);
        g.gridx = 1; g.weightx = 1.0;
        g.gridheight = 3;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descriptionArea.setBackground(BG_INPUT);
        descriptionArea.setForeground(TEXT_PRIMARY);
        descriptionArea.setCaretColor(TEXT_PRIMARY);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        descriptionArea.setToolTipText("Describe symptoms, location, and urgency.");
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBorder(new LineBorder(BORDER_SUBTLE, 1, true));
        formCard.add(scrollPane, g);

        centerPanel.add(formCard);
        centerPanel.add(Box.createVerticalStrut(10));

        // Status Card
        JPanel statusCard = createCard("Status");
        statusCard.setLayout(new BorderLayout(8, 4));
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_SECONDARY);
        statusCard.add(statusLabel, BorderLayout.CENTER);
        centerPanel.add(statusCard);
        centerPanel.add(Box.createVerticalStrut(6));

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- Bottom Action Bar ---
        JPanel actionBar = new JPanel(new GridLayout(1, 2, 12, 0));
        actionBar.setBackground(BG_CARD);
        actionBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        resetButton = createActionButton("Reset Form", new Color(127, 140, 151));
        resetButton.addActionListener(e -> clearForm());
        submitButton = createActionButton("Submit Request", ACCENT_BLUE);

        actionBar.add(resetButton);
        actionBar.add(submitButton);
        mainPanel.add(actionBar, BorderLayout.SOUTH);

        setContentPane(mainPanel);
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

    // =============== IRepairRequestIntakeView Implementation ===============

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
        statusLabel.setText("Validation Failed");
        statusLabel.setForeground(ACCENT_RED);
    }

    @Override
    public void displayValidationSuccess() {
        statusLabel.setText("✓ Validation Passed");
        statusLabel.setForeground(ACCENT_GREEN);
    }

    @Override
    public void displayScheduledDate(String scheduledDate) {
        JOptionPane.showMessageDialog(this, "Scheduled Date: " + scheduledDate, "Scheduling Success", JOptionPane.INFORMATION_MESSAGE);
        statusLabel.setText("Scheduled for: " + scheduledDate);
        statusLabel.setForeground(ACCENT_GREEN);
    }

    @Override
    public void clearForm() {
        requestIdField.setText("REQ-" + System.currentTimeMillis() % 10000);
        customerIdField.setText("");
        repairTypeCombo.setSelectedIndex(0);
        descriptionArea.setText("");
        statusLabel.setText(" ");
        statusLabel.setForeground(TEXT_SECONDARY);
    }

    @Override
    public void showLoadingIndicator(String message) {
        statusLabel.setText("⏳ " + message);
        statusLabel.setForeground(new Color(255, 171, 64));
        submitButton.setEnabled(false);
    }

    @Override
    public void hideLoadingIndicator() {
        submitButton.setEnabled(true);
    }

    @Override
    public void displayError(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
        statusLabel.setText("Error: " + errorMessage);
        statusLabel.setForeground(ACCENT_RED);
    }

    @Override
    public void displaySuccess(String successMessage) {
        JOptionPane.showMessageDialog(this, successMessage, "Success", JOptionPane.INFORMATION_MESSAGE);
        statusLabel.setText("✓ " + successMessage);
        statusLabel.setForeground(ACCENT_GREEN);
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

    public JButton getCheckStatusButton() {
        return checkStatusButton;
    }

    public JButton getRefreshRequestsButton() {
        return refreshRequestsButton;
    }

    public void setAvailableRequestIds(List<String> requestIds) {
        requestSelector.removeAllItems();
        if (requestIds == null || requestIds.isEmpty()) {
            requestSelector.addItem("No requests found");
            return;
        }
        for (String requestId : requestIds) {
            requestSelector.addItem(requestId);
        }
    }

    public String getSelectedRequestId() {
        Object selected = requestSelector.getSelectedItem();
        if (selected == null) {
            return null;
        }
        String value = selected.toString();
        return "No requests found".equals(value) ? null : value;
    }

    public void setBackAction(Runnable action) {
        backButton.addActionListener(e -> action.run());
    }

    public JButton getResetButton() {
        return resetButton;
    }
}