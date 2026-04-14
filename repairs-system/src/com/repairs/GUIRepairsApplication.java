package com.repairs;

import com.repairs.views.*;
import com.repairs.controllers.*;
import com.repairs.interfaces.view.*;
import com.repairs.repositories.*;
import com.repairs.services.*;
import javax.swing.*;
import java.awt.*;

/**
 * GUIRepairsApplication - Main GUI application launcher.
 * Provides a menu-driven interface to access all repair system functions.
 */
public class GUIRepairsApplication extends JFrame {
    private GUIRepairRequestIntakeView requestView;
    private GUIRepairExecutionView executionView;
    private GUIBillingView billingView;

    private RepairRequestController requestController;
    private RepairExecutionController executionController;
    private BillingController billingController;

    public GUIRepairsApplication() {
        setTitle("Repairs Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        initializeServices();
        createMenuBar();
        createMainPanel();

        setVisible(true);
    }

    private void initializeServices() {
        // Initialize views
        requestView = new GUIRepairRequestIntakeView();
        executionView = new GUIRepairExecutionView();
        billingView = new GUIBillingView();

        // Initialize repository and services
        IRepairRepository repository = new RepairRepository();
        IRepairLogger logger = new RepairLogger(repository, "./logs");
        IRepairValidator validator = new RepairValidator(repository, logger);
        IRepairScheduler scheduler = new RepairScheduler(repository, logger);
        IStatusTracker statusTracker = new StatusTracker(repository);
        IRepairExecutor executor = new RepairExecutionService(statusTracker, logger, repository);
        ICostEstimator costEstimator = new CostEstimationService(new InventoryConnector(logger), logger);
        IBillingService billingService = new BillingService(repository, costEstimator, logger);
        IFinancialSystemConnector financialConnector = new FinancialSystemConnector(logger);

        // Initialize controllers (renamed from Presenters)
        requestController = new RepairRequestController(
                requestView, validator, scheduler, statusTracker, repository, logger
        );

        executionController = new RepairExecutionController(
                executionView, executor, statusTracker, logger, repository
        );

        billingController = new BillingController(
                billingView, billingService, costEstimator, financialConnector, logger, repository
        );

        // Wire button listeners
        wireControllers();
    }

    private void wireControllers() {
        // Request view button
        requestView.getSubmitButton().addActionListener(e -> {
            requestController.onRepairRequestSubmitted();
        });

        // Execution view buttons
        executionView.getStartButton().addActionListener(e -> {
            String jobId = executionView.jobIdLabel.getText();
            if (!jobId.equals("-")) {
                executionController.onExecutionStarted(jobId);
            }
        });

        executionView.getCompleteButton().addActionListener(e -> {
            String jobId = executionView.jobIdLabel.getText();
            if (!jobId.equals("-")) {
                executionController.onExecutionCompleted(jobId);
            }
        });

        executionView.getFailButton().addActionListener(e -> {
            String jobId = executionView.jobIdLabel.getText();
            if (!jobId.equals("-")) {
                String reason = JOptionPane.showInputDialog(executionView, "Enter failure reason:");
                if (reason != null && !reason.isEmpty()) {
                    executionController.onExecutionFailed(jobId, reason);
                }
            }
        });

        // Billing view buttons
        billingView.getPayButton().addActionListener(e -> {
            String receiptId = billingView.estimateIdLabel.getText();
            if (!receiptId.equals("-")) {
                billingController.onPaymentProcessed(receiptId);
            }
        });

        billingView.getDiscountButton().addActionListener(e -> {
            String receiptId = billingView.estimateIdLabel.getText();
            if (!receiptId.equals("-")) {
                String amountStr = JOptionPane.showInputDialog(billingView, "Enter discount amount:");
                if (amountStr != null && !amountStr.isEmpty()) {
                    try {
                        java.math.BigDecimal amount = new java.math.BigDecimal(amountStr);
                        billingController.onDiscountApplied(receiptId, amount);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(billingView, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // Operations Menu
        JMenu operationsMenu = new JMenu("Operations");

        JMenuItem requestItem = new JMenuItem("New Repair Request");
        requestItem.addActionListener(e -> requestView.showRepairRequestForm());
        operationsMenu.add(requestItem);

        JMenuItem executionItem = new JMenuItem("Monitor Execution");
        executionItem.addActionListener(e -> executionView.setVisible(true));
        operationsMenu.add(executionItem);

        JMenuItem billingItem = new JMenuItem("Manage Billing");
        billingItem.addActionListener(e -> billingView.setVisible(true));
        operationsMenu.add(billingItem);

        menuBar.add(operationsMenu);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // Title
        JLabel titleLabel = new JLabel("Repairs Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        // Description
        JLabel descLabel = new JLabel("Select an operation from the menu to begin");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 1;
        panel.add(descLabel, gbc);

        // Quick Access Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));

        JButton requestBtn = new JButton("📋 New Repair Request");
        requestBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        requestBtn.setPreferredSize(new Dimension(200, 40));
        requestBtn.addActionListener(e -> requestView.showRepairRequestForm());
        buttonPanel.add(requestBtn);

        JButton executionBtn = new JButton("⚙️  Monitor Execution");
        executionBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        executionBtn.setPreferredSize(new Dimension(200, 40));
        executionBtn.addActionListener(e -> executionView.setVisible(true));
        buttonPanel.add(executionBtn);

        JButton billingBtn = new JButton("💳 Manage Billing");
        billingBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        billingBtn.setPreferredSize(new Dimension(200, 40));
        billingBtn.addActionListener(e -> billingView.setVisible(true));
        buttonPanel.add(billingBtn);

        gbc.gridy = 2;
        gbc.insets = new Insets(30, 20, 20, 20);
        panel.add(buttonPanel, gbc);

        add(panel);
    }

    private void showAboutDialog() {
        String aboutText = "Repairs Management System\n\n" +
                "Version: 2.0 (GUI Edition)\n" +
                "Architecture: MVC with GUI\n" +
                "Platform: Java Swing\n\n" +
                "A complete repair management system with:\n" +
                "• Repair Request Intake\n" +
                "• Job Scheduling & Execution\n" +
                "• Cost Estimation & Billing\n" +
                "• Payment Processing";

        JOptionPane.showMessageDialog(this, aboutText, "About", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUIRepairsApplication());
    }
}