package com.repairs;

import com.repairs.controllers.*;
import com.repairs.entities.*;
import com.repairs.enums.*;
import com.repairs.external.*;
import com.repairs.interfaces.model.*;
import com.repairs.interfaces.view.*;
import com.repairs.repositories.*;
import com.repairs.services.*;
import com.repairs.views.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * RepairsSubSystemApplication - Main application class.
 * Demonstrates dependency injection setup and system initialization.
 * 
 * Architecture:
 * - MVC Pattern (Model-View-Controller)
 * - SOLID Principles
 * - GRASP Patterns
 * - Factory Pattern (for object creation)
 * - Singleton Pattern (for shared services)
 * - Observer Pattern (for status changes)
 * - DAO/Repository Pattern (for persistence)
 * - Adapter Pattern (for external system integration)
 */
public class RepairsSubSystemApplication {
    
    // Services and dependencies
    private final IDatabaseSubsystem databaseSubsystem;
    private final IExceptionHandler exceptionHandler;
    private final IRepairRepository repository;
    private final IRepairValidator validator;
    private final IRepairScheduler scheduler;
    private final IRepairExecutor executor;
    private final IStatusTracker statusTracker;
    private final ICostEstimator costEstimator;
    private final IBillingService billingService;
    private final IFinancialSystemConnector financialConnector;
    private final IInventoryConnector inventoryConnector;
    private final IRepairLogger logger;

    // MVC Controllers
    private final RepairRequestController repairRequestController;
    private final RepairExecutionController executionController;
    private final BillingController billingController;

    /**
     * Constructor - Sets up all dependencies (Dependency Injection)
     */
    public RepairsSubSystemApplication() {
        // Initialize integration adapters with defaults
        this.databaseSubsystem = new DefaultDatabaseSubsystem();
        this.exceptionHandler = new DefaultExceptionHandler();

        // Initialize Repository (DAO Pattern)
        this.repository = new RepairRepository(databaseSubsystem, exceptionHandler);

        // Initialize Logger
        this.logger = new RepairLogger(repository, "./logs", exceptionHandler);

        // Initialize External Adapters
        this.financialConnector = new FinancialSystemConnector(logger);
        this.inventoryConnector = new InventoryConnector(logger, databaseSubsystem, exceptionHandler);

        // Initialize Business Services
        this.validator = new RepairValidator(repository, logger);
        this.statusTracker = new StatusTracker(repository, exceptionHandler);
        this.scheduler = new RepairScheduler(repository, logger);
        this.executor = new RepairExecutionService(statusTracker, logger, repository);
        this.costEstimator = new CostEstimationService(inventoryConnector, logger);
        this.billingService = new BillingService(repository, costEstimator, logger);

        // Initialize Views (Console-based for demo)
        IRepairRequestIntakeView requestIntakeView = new ConsoleRepairRequestIntakeView();
        IRepairExecutionView executionView = new ConsoleRepairExecutionView();
        IBillingView billingView = new ConsoleBillingView();

        // Initialize MVC Controllers
        this.repairRequestController = new RepairRequestController(
                requestIntakeView, validator, scheduler, statusTracker, repository, logger);

        this.executionController = new RepairExecutionController(
                executionView, executor, statusTracker, logger, repository);

        this.billingController = new BillingController(
                billingView, billingService, costEstimator, financialConnector, logger, repository);
    }

    /**
     * Demo: Create and process a repair request
     */
    public void demonstrateRepairRequestFlow() {
        System.out.println("\n========== DEMONSTRATION: Repair Request Flow ==========\n");

        try {
            // Create a sample repair request
            String requestId = "REQ-" + System.currentTimeMillis() % 10000;
            RepairRequest request = new RepairRequest.Builder()
                    .requestId(requestId)
                    .customerId("C10001")
                    .repairType(RepairType.ELECTRICAL)
                    .description("Kitchen electrical outlet not working")
                    .createdDate(LocalDateTime.now())
                    .build();

            System.out.println("1. Created Repair Request: " + requestId);
            System.out.println("   Customer: C10001, Type: ELECTRICAL");

            // Validate request
            boolean isValid = validator.validate(request);
            System.out.println("\n2. Validation: " + (isValid ? "PASSED" : "FAILED"));

            if (!isValid) {
                System.out.println("   Errors: " + validator.getValidationErrors());
                return;
            }

            // Save request
            repository.saveRepairRequest(request);
            System.out.println("\n3. Request saved to repository");

            // Update request status to validated
            request.updateStatus(RepairStatus.VALIDATED);
            repository.updateRepairRequest(request);
            System.out.println("\n4. Request status updated to: VALIDATED");

            // Schedule repair
            LocalDateTime scheduledDate = scheduler.scheduleRepair(request);
            System.out.println("\n5. Repair scheduled for: " + scheduledDate);
            System.out.println("   Status: SCHEDULED");

            // Display repository statistics
            System.out.println("\n6. Repository Statistics:");
            if (repository instanceof RepairRepository concreteRepository) {
                concreteRepository.getStatistics().forEach((key, value) ->
                        System.out.println("   " + key + ": " + value)
                );
            }

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            exceptionHandler.handleException(e, "demonstrateRepairRequestFlow");
        }
    }

    /**
     * Demo: Execute and complete a repair
     */
    public void demonstrateRepairExecutionFlow() {
        System.out.println("\n========== DEMONSTRATION: Repair Execution Flow ==========\n");

        try {
            // Get a scheduled job (from previous demo)
            var scheduledJobs = repository.findRepairJobsByStatus(RepairStatus.SCHEDULED);
            
            if (scheduledJobs.isEmpty()) {
                System.out.println("No scheduled jobs found. Run demonstrateRepairRequestFlow first.");
                return;
            }

            RepairJob job = scheduledJobs.get(0);
            String jobId = job.getJobId();

            System.out.println("1. Found scheduled job: " + jobId);

            // Assign technician
            job.assignTechnician("TECH-001");
            repository.updateRepairJob(job);
            System.out.println("\n2. Assigned technician: TECH-001");

            // Set estimated duration
            job.setEstimatedDuration(Duration.ofHours(2));
            System.out.println("\n3. Estimated duration: 2 hours");

            // Add spare parts
            SparePart part = new SparePart("PART-001", "Electrical Outlet", 2, 
                    new BigDecimal("25.50"), "Electrical");
            job.addSparePart(part);
            repository.updateRepairJob(job);
            System.out.println("\n4. Added spare part: Electrical Outlet (Qty: 2, Price: $25.50)");

            // Execute repair
            executor.executeRepair(job);
            System.out.println("\n5. Repair execution started");
            System.out.println("   Status: IN_PROGRESS");

            // Update progress
            executor.updateProgress(jobId, 50);
            System.out.println("\n6. Progress: 50%");

            executor.updateProgress(jobId, 100);
            System.out.println("\n7. Progress: 100%");

            // Complete repair
            executor.completeRepair(jobId);
            System.out.println("\n8. Repair completed successfully");
            System.out.println("   Status: COMPLETED");

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            exceptionHandler.handleException(e, "demonstrateRepairExecutionFlow");
        }
    }

    /**
     * Demo: Generate bill and process payment
     */
    public void demonstrateBillingFlow() {
        System.out.println("\n========== DEMONSTRATION: Billing & Payment Flow ==========\n");

        try {
            // Get a completed job
            var completedJobs = repository.findRepairJobsByStatus(RepairStatus.COMPLETED);
            
            if (completedJobs.isEmpty()) {
                System.out.println("No completed jobs found. Run demonstrateRepairExecutionFlow first.");
                return;
            }

            RepairJob job = completedJobs.get(0);
            String jobId = job.getJobId();

            System.out.println("1. Found completed job: " + jobId);

            // Generate cost estimate
            CostEstimate estimate = costEstimator.estimateCost(job);
            System.out.println("\n2. Cost Estimate Generated:");
            System.out.println("   Labor Cost: $" + estimate.getLaborCost());
            System.out.println("   Parts Cost: $" + estimate.getPartsCost());
            System.out.println("   Tax: $" + estimate.getTaxAmount());
            System.out.println("   Total: $" + estimate.getTotalCost());

            // Generate bill
            Receipt receipt = billingService.generateBill(job);
            String receiptId = receipt.getReceiptId();
            System.out.println("\n3. Invoice Generated: " + receipt.generateReceiptNumber());
            System.out.println("   Receipt ID: " + receiptId);
            System.out.println("   Final Amount: $" + receipt.getFinalAmount());

            // Apply discount (10%)
            BigDecimal discountAmount = estimate.getTotalCost().multiply(new BigDecimal("0.10"));
            receipt.applyDiscount(discountAmount);
            repository.updateReceipt(receipt);
            System.out.println("\n4. Discount Applied: $" + discountAmount);
            System.out.println("   Final Amount after Discount: $" + receipt.getFinalAmount());

            // Process payment
            System.out.println("\n5. Processing Payment...");
            PaymentStatus paymentStatus = financialConnector.processPayment(receipt);
            System.out.println("   Payment Status: " + paymentStatus);

            if (paymentStatus == PaymentStatus.PROCESSED) {
                receipt.markAsPaid("Credit Card");
                repository.updateReceipt(receipt);
                System.out.println("   Payment Method: Credit Card");
                System.out.println("   Paid Date: " + receipt.getPaidDate());
                System.out.println("\n6. Payment Processed Successfully!");
            }

            // Show outstanding bills
            System.out.println("\n7. Outstanding Bills: " + billingService.getOutstandingBills().size());

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            exceptionHandler.handleException(e, "demonstrateBillingFlow");
        }
    }

    /**
     * Show system information
     */
    public void showSystemInfo() {
        System.out.println("\n========== REPAIRS SUB-SYSTEM INFORMATION ==========\n");
        System.out.println("Architecture: MVC (Model-View-Controller)");
        System.out.println("Design Patterns Used:");
        System.out.println("  - Singleton (Logger, Repository)");
        System.out.println("  - Factory (Entity creation)");
        System.out.println("  - Builder (Complex objects)");
        System.out.println("  - Observer (Status tracking)");
        System.out.println("  - Strategy (Repair types)");
        System.out.println("  - DAO/Repository (Persistence)");
        System.out.println("  - External DB Interface + Fallback Data Subsystem");
        System.out.println("  - Adapter (External systems)");
        System.out.println("  - Dependency Injection (Service assembly)");
        System.out.println("\nPrinciples Followed:");
        System.out.println("  - SOLID Principles");
        System.out.println("  - GRASP Patterns");
        System.out.println("  - Clean Code Practices");
        System.out.println("\nKey Components:");
        System.out.println("  - Repair Request Intake");
        System.out.println("  - Validation & Eligibility Check");
        System.out.println("  - Job Scheduling");
        System.out.println("  - Repair Execution");
        System.out.println("  - Status Tracking");
        System.out.println("  - Cost Estimation & Billing");
        System.out.println("  - Payment Processing");
        System.out.println("  - Inventory Integration");
        System.out.println("  - Logging & Auditing");
    }

    /**
     * Interactive console UI loop.
     */
    public void runInteractiveConsole() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n========== REPAIRS SYSTEM MENU ==========");
            System.out.println("1. Submit new repair request");
            System.out.println("2. View pending requests");
            System.out.println("3. View request status");
            System.out.println("4. Start scheduled repair execution");
            System.out.println("5. Update repair progress");
            System.out.println("6. Complete repair");
            System.out.println("7. Generate bill for completed repair");
            System.out.println("8. Process payment for outstanding receipt");
            System.out.println("9. Show outstanding bills");
            System.out.println("10. Exit");
            System.out.print("Select option: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        repairRequestController.showRepairForm();
                        repairRequestController.onRepairRequestSubmitted();
                        break;
                    case "2":
                        repairRequestController.displayPendingRequests();
                        break;
                    case "3":
                        System.out.print("Enter Request ID: ");
                        repairRequestController.displayRequestStatus(scanner.nextLine().trim());
                        break;
                    case "4": {
                        String jobId = selectJobIdByStatus(scanner, RepairStatus.SCHEDULED);
                        if (jobId == null) {
                            break;
                        }
                        System.out.print("Enter Technician ID (default TECH-001): ");
                        String technicianId = scanner.nextLine().trim();
                        if (technicianId.isBlank()) {
                            technicianId = "TECH-001";
                        }
                        executionController.assignTechnician(jobId, technicianId);
                        executionController.onExecutionStarted(jobId);
                        break;
                    }
                    case "5": {
                        String jobId = selectJobIdByStatus(scanner, RepairStatus.IN_PROGRESS);
                        if (jobId == null) {
                            break;
                        }
                        System.out.print("Enter progress percentage (0-100): ");
                        int progress = Integer.parseInt(scanner.nextLine().trim());
                        executionController.onProgressUpdated(jobId, progress);
                        break;
                    }
                    case "6": {
                        String jobId = selectJobIdByStatus(scanner, RepairStatus.IN_PROGRESS);
                        if (jobId == null) {
                            break;
                        }
                        executionController.onExecutionCompleted(jobId);
                        break;
                    }
                    case "7": {
                        String jobId = selectJobIdByStatus(scanner, RepairStatus.COMPLETED);
                        if (jobId == null) {
                            break;
                        }
                        billingController.onBillingRequested(jobId);
                        break;
                    }
                    case "8": {
                        String receiptId = selectOutstandingReceiptId(scanner);
                        if (receiptId == null) {
                            break;
                        }
                        billingController.onPaymentProcessed(receiptId);
                        break;
                    }
                    case "9":
                        billingController.displayOutstandingBills();
                        break;
                    case "10":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please choose 1-10.");
                }
            } catch (Exception e) {
                exceptionHandler.handleException(e, "runInteractiveConsole");
                System.out.println("Operation failed: " + e.getMessage());
            }
        }

        System.out.println("Exiting Repairs Sub-System.");
    }

    private String selectJobIdByStatus(Scanner scanner, RepairStatus status) {
        List<RepairJob> jobs = repository.findRepairJobsByStatus(status);
        if (jobs.isEmpty()) {
            System.out.println("No jobs found with status: " + status);
            return null;
        }

        System.out.println("Available jobs:");
        for (int i = 0; i < jobs.size(); i++) {
            System.out.println((i + 1) + ". " + jobs.get(i).getJobId());
        }

        System.out.print("Select job number: ");
        int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (index < 0 || index >= jobs.size()) {
            System.out.println("Invalid selection.");
            return null;
        }
        return jobs.get(index).getJobId();
    }

    private String selectOutstandingReceiptId(Scanner scanner) {
        List<Receipt> receipts = billingService.getOutstandingBills();
        if (receipts.isEmpty()) {
            System.out.println("No outstanding receipts found.");
            return null;
        }

        System.out.println("Outstanding receipts:");
        for (int i = 0; i < receipts.size(); i++) {
            Receipt receipt = receipts.get(i);
            System.out.println((i + 1) + ". " + receipt.getReceiptId() + " (Amount: " + receipt.getFinalAmount() + ")");
        }

        System.out.print("Select receipt number: ");
        int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (index < 0 || index >= receipts.size()) {
            System.out.println("Invalid selection.");
            return null;
        }
        return receipts.get(index).getReceiptId();
    }

    /**
     * Main entry point
     */
    public static void main(String[] args) {
        RepairsSubSystemApplication app = new RepairsSubSystemApplication();

        // Show system information
        app.showSystemInfo();

        // Run demo mode only when explicitly requested.
        if (args.length > 0 && "demo".equalsIgnoreCase(args[0])) {
            app.demonstrateRepairRequestFlow();
            app.demonstrateRepairExecutionFlow();
            app.demonstrateBillingFlow();
            System.out.println("\n========== DEMONSTRATION COMPLETE ==========\n");
        } else {
            app.runInteractiveConsole();
        }
    }
}
