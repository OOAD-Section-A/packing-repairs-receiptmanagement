package com.repairs;

import com.repairs.views.*;
import com.repairs.controllers.*;
import com.repairs.enums.RepairStatus;
import com.repairs.external.*;
import com.repairs.interfaces.model.*;
import com.repairs.interfaces.view.*;
import com.repairs.repositories.*;
import com.repairs.services.*;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    private IRepairRepository repository;
    private IBillingService billingService;
    private JLabel statusBarLabel;

    public GUIRepairsApplication() {
        setTitle("Repairs Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 620);
        setMinimumSize(new Dimension(860, 560));
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

        // Initialize repository and services using shared integration adapters.
        IDatabaseSubsystem databaseSubsystem = new DefaultDatabaseSubsystem();
        IExceptionHandler exceptionHandler = new DefaultExceptionHandler();

        repository = new RepairRepository(databaseSubsystem, exceptionHandler);
        IRepairLogger logger = new RepairLogger(repository, "./logs", exceptionHandler);
        IRepairValidator validator = new RepairValidator(repository, logger);
        IRepairScheduler scheduler = new RepairScheduler(repository, logger);
        IStatusTracker statusTracker = new StatusTracker(repository, exceptionHandler);
        IRepairExecutor executor = new RepairExecutionService(statusTracker, logger, repository);
        IInventoryConnector inventoryConnector = new InventoryConnector(logger, databaseSubsystem, exceptionHandler);
        ICostEstimator costEstimator = new CostEstimationService(inventoryConnector, logger);
        billingService = new BillingService(repository, costEstimator, logger);
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

        requestView.setBackAction(this::showDashboard);
        executionView.setBackAction(this::showDashboard);
        billingView.setBackAction(this::showDashboard);

        refreshRequestSelector();
        refreshJobSelector();
        refreshCompletedJobSelector();
        refreshReceiptSelector();
    }

    private void wireControllers() {
        // Request view button
        requestView.getSubmitButton().addActionListener(e -> {
            requestController.onRepairRequestSubmitted();
            refreshRequestSelector();
            refreshJobSelector();
            refreshCompletedJobSelector();
            statusBarLabel.setText("Request submission attempted. Check request window status for details.");
        });

        requestView.getRefreshRequestsButton().addActionListener(e -> {
            refreshRequestSelector();
            statusBarLabel.setText("Request list refreshed.");
        });

        requestView.getCheckStatusButton().addActionListener(e -> {
            String requestId = requestView.getSelectedRequestId();
            if (isValidSelection(requestId)) {
                requestController.displayRequestStatus(requestId);
                statusBarLabel.setText("Status checked for request: " + requestId);
            } else {
                JOptionPane.showMessageDialog(requestView,
                        "Select a request from the dropdown first.",
                        "No Request Selected",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        // Execution view buttons
        executionView.getStartButton().addActionListener(e -> {
            String jobId = executionView.getCurrentJobId();
            if (isValidSelection(jobId)) {
                executionController.onExecutionStarted(jobId);
                refreshJobSelector();
                refreshCompletedJobSelector();
                statusBarLabel.setText("Started execution flow for job: " + jobId);
            } else {
                JOptionPane.showMessageDialog(executionView,
                        "Enter a valid job id before starting.",
                        "Missing Job ID",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        executionView.getCompleteButton().addActionListener(e -> {
            String jobId = executionView.getCurrentJobId();
            if (isValidSelection(jobId)) {
                executionController.onExecutionCompleted(jobId);
                refreshJobSelector();
                refreshCompletedJobSelector();
                refreshReceiptSelector();
                statusBarLabel.setText("Completion requested for job: " + jobId);
            } else {
                JOptionPane.showMessageDialog(executionView,
                        "Enter a valid job id before completing.",
                        "Missing Job ID",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        executionView.getFailButton().addActionListener(e -> {
            String jobId = executionView.getCurrentJobId();
            if (isValidSelection(jobId)) {
                String reason = JOptionPane.showInputDialog(executionView, "Enter failure reason:");
                if (reason != null && !reason.isEmpty()) {
                    executionController.onExecutionFailed(jobId, reason);
                    refreshJobSelector();
                    refreshCompletedJobSelector();
                    statusBarLabel.setText("Failure recorded for job: " + jobId);
                }
            } else {
                JOptionPane.showMessageDialog(executionView,
                        "Enter a valid job id before failing a repair.",
                        "Missing Job ID",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        executionView.getRefreshJobsButton().addActionListener(e -> {
            refreshJobSelector();
            statusBarLabel.setText("Job list refreshed.");
        });

        executionView.getCheckStatusButton().addActionListener(e -> {
            String jobId = executionView.getCurrentJobId();
            if (isValidSelection(jobId)) {
                executionController.displayExecutionProgress(jobId);
                statusBarLabel.setText("Status checked for job: " + jobId);
            } else {
                JOptionPane.showMessageDialog(executionView,
                        "Select a job from the dropdown or enter a job ID.",
                        "No Job Selected",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        executionView.getAssignTechnicianButton().addActionListener(e -> {
            String jobId = executionView.getCurrentJobId();
            String technicianId = executionView.getTechnicianIdInput();
            if (!isValidSelection(jobId)) {
                JOptionPane.showMessageDialog(executionView,
                        "Select a job first.",
                        "No Job Selected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!isValidSelection(technicianId)) {
                JOptionPane.showMessageDialog(executionView,
                        "Enter a technician id.",
                        "Missing Technician ID",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            executionController.assignTechnician(jobId, technicianId);
            statusBarLabel.setText("Technician " + technicianId + " assigned to " + jobId);
        });

        executionView.getUpdateProgressButton().addActionListener(e -> {
            String jobId = executionView.getCurrentJobId();
            if (!isValidSelection(jobId)) {
                JOptionPane.showMessageDialog(executionView,
                        "Select a job first.",
                        "No Job Selected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            executionController.onProgressUpdated(jobId, executionView.getProgressInput());
            statusBarLabel.setText("Progress updated for job: " + jobId);
        });

        executionView.getPauseButton().addActionListener(e -> {
            String jobId = executionView.getCurrentJobId();
            if (!isValidSelection(jobId)) {
                JOptionPane.showMessageDialog(executionView,
                        "Select a job first.",
                        "No Job Selected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            JButton pauseButton = executionView.getPauseButton();
            if ("Pause".equalsIgnoreCase(pauseButton.getText())) {
                executionController.onExecutionPaused(jobId);
                pauseButton.setText("Resume");
                statusBarLabel.setText("Execution paused for job: " + jobId);
            } else {
                executionController.onExecutionResumed(jobId);
                pauseButton.setText("Pause");
                statusBarLabel.setText("Execution resumed for job: " + jobId);
            }
        });

        // Billing view buttons
        billingView.getPayButton().addActionListener(e -> {
            String receiptId = billingView.getCurrentReceiptId();
            if (isValidSelection(receiptId)) {
                billingController.onPaymentProcessed(receiptId);
                refreshReceiptSelector();
                statusBarLabel.setText("Payment attempted for receipt: " + receiptId);
            } else {
                JOptionPane.showMessageDialog(billingView,
                        "Enter a valid receipt id before processing payment.",
                        "Missing Receipt ID",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        billingView.getDiscountButton().addActionListener(e -> {
            String receiptId = billingView.getCurrentReceiptId();
            if (isValidSelection(receiptId)) {
                String amountStr = JOptionPane.showInputDialog(billingView, "Enter discount amount:");
                if (amountStr != null && !amountStr.isEmpty()) {
                    try {
                        BigDecimal amount = new BigDecimal(amountStr);
                        billingController.onDiscountApplied(receiptId, amount);
                        refreshReceiptSelector();
                        statusBarLabel.setText("Discount attempted for receipt: " + receiptId);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(billingView, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(billingView,
                        "Enter a valid receipt id before applying discount.",
                        "Missing Receipt ID",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        billingView.getRefreshBillsButton().addActionListener(e -> {
            billingController.displayOutstandingBills();
            refreshReceiptSelector();
            statusBarLabel.setText("Outstanding bills refreshed.");
        });

        billingView.getRefreshJobsButton().addActionListener(e -> {
            refreshCompletedJobSelector();
            statusBarLabel.setText("Completed jobs refreshed for billing.");
        });

        billingView.getGenerateEstimateButton().addActionListener(e -> {
            String jobId = billingView.getSelectedCompletedJobId();
            if (!isValidSelection(jobId)) {
                JOptionPane.showMessageDialog(billingView,
                        "Select a completed job first.",
                        "No Job Selected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            billingController.onEstimationRequested(jobId);
            statusBarLabel.setText("Estimate generated for job: " + jobId);
        });

        billingView.getGenerateBillButton().addActionListener(e -> {
            String jobId = billingView.getSelectedCompletedJobId();
            if (!isValidSelection(jobId)) {
                JOptionPane.showMessageDialog(billingView,
                        "Select a completed job first.",
                        "No Job Selected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            billingController.onBillingRequested(jobId);
            refreshReceiptSelector();
            statusBarLabel.setText("Bill generated for job: " + jobId);
        });

        billingView.getLoadReceiptButton().addActionListener(e -> {
            String receiptId = billingView.getCurrentReceiptId();
            if (!isValidSelection(receiptId)) {
                JOptionPane.showMessageDialog(billingView,
                        "Select or enter a receipt id first.",
                        "No Receipt Selected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            billingController.displayReceipt(receiptId);
            statusBarLabel.setText("Loaded receipt details for: " + receiptId);
        });

        billingView.getRefundButton().addActionListener(e -> {
            String receiptId = billingView.getCurrentReceiptId();
            if (!isValidSelection(receiptId)) {
                JOptionPane.showMessageDialog(billingView,
                        "Select or enter a receipt id first.",
                        "No Receipt Selected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            billingController.onRefundRequested(receiptId);
            refreshReceiptSelector();
            statusBarLabel.setText("Refund attempted for receipt: " + receiptId);
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
        requestItem.addActionListener(e -> openRequestView());
        operationsMenu.add(requestItem);

        JMenuItem executionItem = new JMenuItem("Monitor Execution");
        executionItem.addActionListener(e -> openExecutionView());
        operationsMenu.add(executionItem);

        JMenuItem billingItem = new JMenuItem("Manage Billing");
        billingItem.addActionListener(e -> openBillingView());
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
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        panel.setBackground(new Color(236, 240, 241));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Repairs Management Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        JLabel descLabel = new JLabel("Pick a module to start work. You can always return here using Back to Dashboard.");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(90, 90, 90));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descLabel, BorderLayout.SOUTH);
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 14, 14));
        cardsPanel.setOpaque(false);
        cardsPanel.add(createModuleCard(
                "Repair Intake",
                "Create and validate new repair requests.",
                "Open Intake",
                this::openRequestView,
                new Color(52, 152, 219)));

        cardsPanel.add(createModuleCard(
                "Execution",
                "Track job progress and mark completion/failure.",
                "Open Execution",
                this::openExecutionView,
                new Color(46, 204, 113)));

        cardsPanel.add(createModuleCard(
                "Billing",
                "Handle estimates, payments, and bill tracking.",
                "Open Billing",
                this::openBillingView,
                new Color(230, 126, 34)));
        panel.add(cardsPanel, BorderLayout.CENTER);

        statusBarLabel = new JLabel("Ready.");
        statusBarLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        statusBarLabel.setOpaque(true);
        statusBarLabel.setBackground(Color.WHITE);
        panel.add(statusBarLabel, BorderLayout.SOUTH);

        setContentPane(panel);
    }

    private JPanel createModuleCard(String title,
                                    String description,
                                    String buttonText,
                                    Runnable action,
                                    Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(accentColor);
        card.add(titleLabel, BorderLayout.NORTH);

        JTextArea descriptionArea = new JTextArea(description);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setFocusable(false);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionArea.setForeground(new Color(70, 70, 70));
        card.add(descriptionArea, BorderLayout.CENTER);

        JButton openButton = new JButton(buttonText);
        openButton.setBackground(accentColor);
        openButton.setForeground(Color.WHITE);
        openButton.setFocusPainted(false);
        openButton.addActionListener(e -> action.run());
        card.add(openButton, BorderLayout.SOUTH);

        return card;
    }

    private void openRequestView() {
        setVisible(false);
        refreshRequestSelector();
        requestView.showRepairRequestForm();
        statusBarLabel.setText("Request intake opened.");
    }

    private void openExecutionView() {
        setVisible(false);
        refreshJobSelector();
        refreshCompletedJobSelector();
        executionView.setVisible(true);
        statusBarLabel.setText("Execution monitor opened.");
    }

    private void openBillingView() {
        setVisible(false);
        refreshCompletedJobSelector();
        refreshReceiptSelector();
        billingView.setVisible(true);
        billingController.displayOutstandingBills();
        statusBarLabel.setText("Billing workspace opened.");
    }

    private void showDashboard() {
        requestView.setVisible(false);
        executionView.setVisible(false);
        billingView.setVisible(false);
        setVisible(true);
        toFront();
        requestFocus();
    }

    private boolean isValidSelection(String id) {
        return id != null && !id.isBlank() && !"-".equals(id.trim());
    }

    private void refreshRequestSelector() {
        List<String> requestIds = repository.findAllRepairRequests().stream()
                .map(request -> request.getRequestId())
                .sorted(Comparator.naturalOrder())
                .toList();
        requestView.setAvailableRequestIds(requestIds);
    }

    private void refreshJobSelector() {
        List<String> jobIds = repository.findAllRepairJobs().stream()
                .map(job -> job.getJobId())
                .sorted(Comparator.naturalOrder())
                .toList();
        executionView.setAvailableJobIds(jobIds);
    }

    private void refreshReceiptSelector() {
        List<String> receiptIds = repository.findAllRepairJobs().stream()
            .flatMap(job -> repository.findReceiptsByJobId(job.getJobId()).stream())
            .map(receipt -> receipt.getReceiptId())
            .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();
        billingView.setAvailableReceiptIds(receiptIds);
    }

        private void refreshCompletedJobSelector() {
        List<String> completedJobIds = repository.findRepairJobsByStatus(RepairStatus.COMPLETED).stream()
            .map(job -> job.getJobId())
            .collect(Collectors.toList());
        completedJobIds.sort(Comparator.naturalOrder());
        billingView.setAvailableCompletedJobIds(completedJobIds);
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