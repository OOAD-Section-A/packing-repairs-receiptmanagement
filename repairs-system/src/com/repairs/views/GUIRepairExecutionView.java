package com.repairs.views;

import com.repairs.enums.RepairStatus;
import com.repairs.interfaces.view.IRepairExecutionView;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

/**
 * GUIRepairExecutionView - Swing GUI implementation of IRepairExecutionView.
 * Provides a professional GUI for monitoring repair execution.
 */
public class GUIRepairExecutionView extends JFrame implements IRepairExecutionView {
    private JTextField jobIdField;
    private JTextField technicianIdField;
    private JSpinner progressSpinner;
    private JComboBox<String> jobSelector;
    private JLabel jobIdLabel;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JTextArea logsArea;
    private JLabel technicianLabel;
    private JLabel timeRemainingLabel;
    private JButton backButton;
    private JButton refreshJobsButton;
    private JButton checkStatusButton;
    private JButton assignTechnicianButton;
    private JButton updateProgressButton;
    private JButton startButton;
    private JButton pauseButton;
    private JButton completeButton;
    private JButton failButton;

    // Modern color palette
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

    public GUIRepairExecutionView() {
        setTitle("Repair Execution Monitor");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(780, 680);
        setLocationRelativeTo(null);
        setResizable(false);

        initializeComponents();
        setVisible(false);
    }

    private void initializeComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
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

        JLabel titleLabel = new JLabel("Repair Execution Monitor");
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 17));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(titleLabel, BorderLayout.CENTER);

        JLabel helperLabel = new JLabel("Select or enter a Job ID to get started  ");
        helperLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        helperLabel.setForeground(TEXT_SECONDARY);
        topBar.add(helperLabel, BorderLayout.EAST);
        mainPanel.add(topBar, BorderLayout.NORTH);

        // --- Center Content ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BG_PRIMARY);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));

        // Job Selection Card
        JPanel selectionCard = createCard("Job Selection");
        selectionCard.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0;
        selectionCard.add(styledLabel("Available Jobs"), g);
        g.gridx = 1; g.weightx = 1.0;
        jobSelector = new JComboBox<>();
        styleComboBox(jobSelector);
        selectionCard.add(jobSelector, g);
        g.gridx = 2; g.weightx = 0;
        refreshJobsButton = createStyledButton("Refresh", TEXT_PRIMARY, ACCENT_BLUE.darker());
        selectionCard.add(refreshJobsButton, g);
        g.gridx = 3;
        checkStatusButton = createStyledButton("Check Status", TEXT_PRIMARY, BG_INPUT);
        selectionCard.add(checkStatusButton, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        selectionCard.add(styledLabel("Job ID Input"), g);
        g.gridx = 1; g.weightx = 1.0;
        jobIdField = styledTextField(16);
        jobIdField.setToolTipText("Enter job id, for example JOB-1234.");
        selectionCard.add(jobIdField, g);
        g.gridx = 2;
        selectionCard.add(styledLabel("Active Job"), g);
        g.gridx = 3;
        jobIdLabel = new JLabel("-");
        jobIdLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        jobIdLabel.setForeground(ACCENT_BLUE);
        selectionCard.add(jobIdLabel, g);

        centerPanel.add(selectionCard);
        centerPanel.add(Box.createVerticalStrut(8));

        // Info & Controls Card
        JPanel infoCard = createCard("Status & Controls");
        infoCard.setLayout(new GridBagLayout());
        g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0;
        infoCard.add(styledLabel("Status"), g);
        g.gridx = 1;
        statusLabel = new JLabel("PENDING");
        statusLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        statusLabel.setForeground(ACCENT_BLUE);
        infoCard.add(statusLabel, g);

        g.gridx = 2;
        infoCard.add(styledLabel("Technician"), g);
        g.gridx = 3;
        technicianLabel = new JLabel("Not assigned");
        technicianLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        technicianLabel.setForeground(TEXT_SECONDARY);
        infoCard.add(technicianLabel, g);

        g.gridx = 0; g.gridy = 1;
        infoCard.add(styledLabel("Assign Tech"), g);
        g.gridx = 1;
        technicianIdField = new JTextField("TECH-001", 12);
        styleTextField(technicianIdField);
        technicianIdField.setToolTipText("Assign a technician before starting execution.");
        infoCard.add(technicianIdField, g);
        g.gridx = 2;
        assignTechnicianButton = createStyledButton("Assign", TEXT_PRIMARY, ACCENT_BLUE);
        infoCard.add(assignTechnicianButton, g);
        g.gridx = 3;
        infoCard.add(styledLabel("Time Left"), g);

        g.gridx = 0; g.gridy = 2;
        infoCard.add(styledLabel("Progress"), g);
        g.gridx = 1; g.gridwidth = 2;
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        progressBar.setForeground(ACCENT_BLUE);
        progressBar.setBackground(BG_INPUT);
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        progressBar.setPreferredSize(new Dimension(200, 22));
        infoCard.add(progressBar, g);
        g.gridwidth = 1;
        g.gridx = 3;
        timeRemainingLabel = new JLabel("-");
        timeRemainingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeRemainingLabel.setForeground(ACCENT_ORANGE);
        infoCard.add(timeRemainingLabel, g);

        g.gridx = 0; g.gridy = 3;
        SpinnerNumberModel progressModel = new SpinnerNumberModel(0, 0, 100, 5);
        progressSpinner = new JSpinner(progressModel);
        styleSpinner(progressSpinner);
        progressSpinner.setToolTipText("Set progress percentage and click Update Progress.");
        infoCard.add(styledLabel("Set %"), g);
        g.gridx = 1;
        infoCard.add(progressSpinner, g);
        g.gridx = 2;
        updateProgressButton = createStyledButton("Update", TEXT_PRIMARY, BG_INPUT);
        infoCard.add(updateProgressButton, g);

        centerPanel.add(infoCard);
        centerPanel.add(Box.createVerticalStrut(8));

        // Logs Card
        JPanel logsCard = createCard("Repair Logs");
        logsCard.setLayout(new BorderLayout(6, 6));
        logsArea = new JTextArea(7, 50);
        logsArea.setEditable(false);
        logsArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        logsArea.setBackground(BG_INPUT);
        logsArea.setForeground(TEXT_PRIMARY);
        logsArea.setCaretColor(TEXT_PRIMARY);
        logsArea.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        JScrollPane scrollPane = new JScrollPane(logsArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_SUBTLE));
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        logsCard.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(logsCard);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- Bottom Action Bar ---
        JPanel actionBar = new JPanel(new GridLayout(1, 4, 10, 0));
        actionBar.setBackground(BG_CARD);
        actionBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_SUBTLE),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        startButton = createActionButton("▶ Start", ACCENT_GREEN);
        pauseButton = createActionButton("⏸ Pause", ACCENT_ORANGE);
        pauseButton.setEnabled(false);
        completeButton = createActionButton("✓ Complete", ACCENT_BLUE);
        completeButton.setEnabled(false);
        failButton = createActionButton("✕ Fail", ACCENT_RED);
        failButton.setEnabled(false);

        actionBar.add(startButton);
        actionBar.add(pauseButton);
        actionBar.add(completeButton);
        actionBar.add(failButton);
        mainPanel.add(actionBar, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    // =============== Styling Helpers ===============

    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_SUBTLE, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
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
        styleTextField(field);
        return field;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBackground(BG_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_SUBTLE, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBackground(BG_INPUT);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(new LineBorder(BORDER_SUBTLE, 1, true));
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        spinner.getEditor().getComponent(0).setBackground(BG_INPUT);
        spinner.getEditor().getComponent(0).setForeground(TEXT_PRIMARY);
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

    // =============== IRepairExecutionView Implementation ===============

    @Override
    public void displayJobProgress(String jobId, int progress) {
        jobIdField.setText(jobId);
        jobIdLabel.setText(jobId);
        progressBar.setValue(progress);
    }

    @Override
    public void showExecutionStatus(RepairStatus status) {
        statusLabel.setText(status.toString());
        Color statusColor = switch (status) {
            case IN_PROGRESS -> ACCENT_BLUE;
            case COMPLETED -> ACCENT_GREEN;
            case FAILED -> ACCENT_RED;
            case SCHEDULED -> ACCENT_ORANGE;
            default -> TEXT_SECONDARY;
        };
        statusLabel.setForeground(statusColor);
    }

    @Override
    public void displayError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        logsArea.append("  ERROR: " + message + "\n");
    }

    @Override
    public void displayWarning(String message) {
        logsArea.append("  WARNING: " + message + "\n");
    }

    @Override
    public void displaySuccess(String message) {
        logsArea.append("  SUCCESS: " + message + "\n");
    }

    @Override
    public void displayLogs(List<String> logs) {
        logsArea.setText("");
        for (String log : logs) {
            logsArea.append(log + "\n");
        }
    }

    @Override
    public void addLogMessage(String logMessage) {
        logsArea.append("  " + logMessage + "\n");
        logsArea.setCaretPosition(logsArea.getDocument().getLength());
    }

    @Override
    public void displayTechnician(String technicianId, String technicianName) {
        technicianLabel.setText(technicianName);
        technicianLabel.setForeground(ACCENT_GREEN);
    }

    @Override
    public void displayTimeRemaining(long minutesRemaining) {
        long hours = minutesRemaining / 60;
        long mins = minutesRemaining % 60;
        timeRemainingLabel.setText(hours + "h " + mins + "m");
    }

    @Override
    public void setStartButtonEnabled(boolean enabled) {
        startButton.setEnabled(enabled);
    }

    @Override
    public void setPauseButtonEnabled(boolean enabled) {
        pauseButton.setEnabled(enabled);
    }

    @Override
    public void setCompleteButtonEnabled(boolean enabled) {
        completeButton.setEnabled(enabled);
    }

    @Override
    public void setFailButtonEnabled(boolean enabled) {
        failButton.setEnabled(enabled);
    }

    @Override
    public void clearDisplay() {
        jobIdField.setText("");
        technicianIdField.setText("TECH-001");
        progressSpinner.setValue(0);
        jobIdLabel.setText("-");
        statusLabel.setText("PENDING");
        statusLabel.setForeground(ACCENT_BLUE);
        progressBar.setValue(0);
        logsArea.setText("");
        technicianLabel.setText("Not assigned");
        technicianLabel.setForeground(TEXT_SECONDARY);
        timeRemainingLabel.setText("-");
    }

    @Override
    public void showLoadingIndicator() {
        startButton.setEnabled(false);
    }

    @Override
    public void hideLoadingIndicator() {
        startButton.setEnabled(true);
    }

    @Override
    public void refresh() {
        repaint();
        revalidate();
    }

    // =============== Accessors ===============

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getPauseButton() {
        return pauseButton;
    }

    public JButton getCompleteButton() {
        return completeButton;
    }

    public JButton getFailButton() {
        return failButton;
    }

    public JButton getAssignTechnicianButton() {
        return assignTechnicianButton;
    }

    public JButton getUpdateProgressButton() {
        return updateProgressButton;
    }

    public String getTechnicianIdInput() {
        return technicianIdField.getText().trim();
    }

    public int getProgressInput() {
        return ((Number) progressSpinner.getValue()).intValue();
    }

    public String getCurrentJobId() {
        String typedId = jobIdField.getText().trim();
        if (!typedId.isEmpty()) {
            jobIdLabel.setText(typedId);
            return typedId;
        }
        Object selected = jobSelector.getSelectedItem();
        if (selected != null) {
            String selectedId = selected.toString();
            if (!"No jobs found".equals(selectedId)) {
                jobIdLabel.setText(selectedId);
                return selectedId;
            }
        }
        return jobIdLabel.getText();
    }

    public void setAvailableJobIds(List<String> jobIds) {
        jobSelector.removeAllItems();
        if (jobIds == null || jobIds.isEmpty()) {
            jobSelector.addItem("No jobs found");
            return;
        }
        for (String jobId : jobIds) {
            jobSelector.addItem(jobId);
        }
    }

    public JButton getRefreshJobsButton() {
        return refreshJobsButton;
    }

    public JButton getCheckStatusButton() {
        return checkStatusButton;
    }

    public void setBackAction(Runnable action) {
        backButton.addActionListener(e -> action.run());
    }
}