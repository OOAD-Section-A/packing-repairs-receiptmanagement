package com.repairs.views;

import com.repairs.enums.RepairStatus;
import com.repairs.interfaces.view.IRepairExecutionView;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * GUIRepairExecutionView - Swing GUI implementation of IRepairExecutionView.
 * Provides a professional GUI for monitoring repair execution.
 */
public class GUIRepairExecutionView extends JFrame implements IRepairExecutionView {
    private JTextField jobIdField;
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
    private JButton startButton;
    private JButton pauseButton;
    private JButton completeButton;
    private JButton failButton;

    public GUIRepairExecutionView() {
        setTitle("Repair Execution Monitor");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        initializeComponents();
        setVisible(false);
    }

    private void initializeComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(new Color(240, 240, 240));
        backButton = new JButton("Back to Dashboard");
        backButton.setFocusable(false);
        JLabel helperLabel = new JLabel("Enter or paste a job ID, then use actions below.");
        helperLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        helperLabel.setForeground(new Color(80, 80, 80));
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(helperLabel, BorderLayout.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        mainPanel.add(headerPanel, gbc);

        // Title
        JLabel titleLabel = new JLabel("Repair Execution Monitor");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        mainPanel.add(titleLabel, gbc);

        JPanel selectionPanel = new JPanel(new GridBagLayout());
        selectionPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints sgbc = new GridBagConstraints();
        sgbc.insets = new Insets(4, 4, 4, 4);
        sgbc.fill = GridBagConstraints.HORIZONTAL;

        sgbc.gridx = 0;
        sgbc.gridy = 0;
        selectionPanel.add(new JLabel("Available Jobs:"), sgbc);

        sgbc.gridx = 1;
        sgbc.weightx = 1.0;
        jobSelector = new JComboBox<>();
        jobSelector.setPrototypeDisplayValue("JOB-0000");
        selectionPanel.add(jobSelector, sgbc);

        sgbc.gridx = 2;
        sgbc.weightx = 0;
        refreshJobsButton = new JButton("Refresh");
        selectionPanel.add(refreshJobsButton, sgbc);

        sgbc.gridx = 3;
        checkStatusButton = new JButton("Check Status");
        selectionPanel.add(checkStatusButton, sgbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        mainPanel.add(selectionPanel, gbc);

        // Job ID input
        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Job ID Input:"), gbc);
        gbc.gridx = 1;
        jobIdField = new JTextField(16);
        jobIdField.setToolTipText("Enter job id, for example JOB-1234.");
        mainPanel.add(jobIdField, gbc);

        gbc.gridx = 2;
        mainPanel.add(new JLabel("Selected Job:"), gbc);
        gbc.gridx = 3;
        jobIdLabel = new JLabel("-");
        mainPanel.add(jobIdLabel, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusLabel = new JLabel("PENDING");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(new Color(52, 152, 219));
        mainPanel.add(statusLabel, gbc);

        // Progress Bar
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        mainPanel.add(new JLabel("Progress:"), gbc);
        gbc.gridy = 6;
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        mainPanel.add(progressBar, gbc);

        // Technician
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Technician:"), gbc);
        gbc.gridx = 1;
        technicianLabel = new JLabel("Not assigned");
        mainPanel.add(technicianLabel, gbc);

        // Time Remaining
        gbc.gridx = 2;
        mainPanel.add(new JLabel("Time Remaining:"), gbc);
        gbc.gridx = 3;
        timeRemainingLabel = new JLabel("-");
        mainPanel.add(timeRemainingLabel, gbc);

        // Logs Area
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 4;
        mainPanel.add(new JLabel("Repair Logs:"), gbc);
        gbc.gridy = 9;
        gbc.gridheight = 2;
        logsArea = new JTextArea(8, 50);
        logsArea.setEditable(false);
        logsArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(logsArea);
        mainPanel.add(scrollPane, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setLayout(new GridLayout(1, 4, 10, 10));

        startButton = new JButton("Start");
        startButton.setBackground(new Color(39, 174, 96));
        startButton.setForeground(Color.WHITE);
        buttonPanel.add(startButton);

        pauseButton = new JButton("Pause");
        pauseButton.setBackground(new Color(230, 126, 34));
        pauseButton.setForeground(Color.WHITE);
        pauseButton.setEnabled(false);
        buttonPanel.add(pauseButton);

        completeButton = new JButton("Complete");
        completeButton.setBackground(new Color(52, 152, 219));
        completeButton.setForeground(Color.WHITE);
        completeButton.setEnabled(false);
        buttonPanel.add(completeButton);

        failButton = new JButton("Fail");
        failButton.setBackground(new Color(231, 76, 60));
        failButton.setForeground(Color.WHITE);
        failButton.setEnabled(false);
        buttonPanel.add(failButton);

        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 4;
        gbc.gridheight = 1;
        mainPanel.add(buttonPanel, gbc);

        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        add(mainScrollPane);
    }

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
            case IN_PROGRESS -> new Color(52, 152, 219);
            case COMPLETED -> new Color(39, 174, 96);
            case FAILED -> new Color(231, 76, 60);
            default -> Color.BLACK;
        };
        statusLabel.setForeground(statusColor);
    }

    @Override
    public void displayError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        logsArea.append("❌ ERROR: " + message + "\n");
    }

    @Override
    public void displayWarning(String message) {
        logsArea.append("⚠️  WARNING: " + message + "\n");
    }

    @Override
    public void displaySuccess(String message) {
        logsArea.append("✓ SUCCESS: " + message + "\n");
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
        logsArea.append("📌 " + logMessage + "\n");
        logsArea.setCaretPosition(logsArea.getDocument().getLength());
    }

    @Override
    public void displayTechnician(String technicianId, String technicianName) {
        technicianLabel.setText(technicianName);
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
        jobIdLabel.setText("-");
        statusLabel.setText("PENDING");
        progressBar.setValue(0);
        logsArea.setText("");
        technicianLabel.setText("Not assigned");
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