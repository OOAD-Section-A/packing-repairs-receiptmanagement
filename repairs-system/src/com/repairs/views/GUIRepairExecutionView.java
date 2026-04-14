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
    private JLabel jobIdLabel;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private JTextArea logsArea;
    private JLabel technicianLabel;
    private JLabel timeRemainingLabel;
    private JButton startButton;
    private JButton pauseButton;
    private JButton completeButton;
    private JButton failButton;

    public GUIRepairExecutionView() {
        setTitle("Repair Execution Monitor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        // Title
        JLabel titleLabel = new JLabel("Repair Execution Monitor");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        mainPanel.add(titleLabel, gbc);

        // Job ID
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Job ID:"), gbc);
        gbc.gridx = 1;
        jobIdLabel = new JLabel("-");
        mainPanel.add(jobIdLabel, gbc);

        // Status
        gbc.gridx = 2;
        mainPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3;
        statusLabel = new JLabel("PENDING");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(new Color(52, 152, 219));
        mainPanel.add(statusLabel, gbc);

        // Progress Bar
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        mainPanel.add(new JLabel("Progress:"), gbc);
        gbc.gridy = 3;
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        mainPanel.add(progressBar, gbc);

        // Technician
        gbc.gridx = 0;
        gbc.gridy = 4;
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
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        mainPanel.add(new JLabel("Repair Logs:"), gbc);
        gbc.gridy = 6;
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
        gbc.gridy = 8;
        gbc.gridwidth = 4;
        gbc.gridheight = 1;
        mainPanel.add(buttonPanel, gbc);

        JScrollPane mainScrollPane = new JScrollPane(mainPanel);
        add(mainScrollPane);
    }

    @Override
    public void displayJobProgress(String jobId, int progress) {
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
}