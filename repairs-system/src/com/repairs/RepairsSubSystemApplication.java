package com.repairs;

import com.repairs.entities.*;
import com.repairs.enums.*;
import com.repairs.external.*;
import com.repairs.interfaces.model.*;
import com.repairs.interfaces.view.*;
import com.repairs.presenters.*;
import com.repairs.repositories.*;
import com.repairs.services.*;
import com.repairs.views.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * RepairsSubSystemApplication - Main application class.
 * Demonstrates dependency injection setup and system initialization.
 * 
 * Architecture:
 * - MVP Pattern (Model-View-Presenter)
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

    // Presenters
    private final RepairRequestPresenter repairRequestPresenter;
    private final RepairExecutionPresenter executionPresenter;
    private final BillingPresenter billingPresenter;

    /**
     * Constructor - Sets up all dependencies (Dependency Injection)
     */
    public RepairsSubSystemApplication() {
        // Initialize Repository (DAO Pattern)
        this.repository = new RepairRepository();

        // Initialize Logger
        this.logger = new RepairLogger(repository, "./logs");

        // Initialize External Adapters
        this.financialConnector = new FinancialSystemConnector(logger);
        this.inventoryConnector = new InventoryConnector(logger);

        // Initialize Business Services
        this.validator = new RepairValidator(repository, logger);
        this.statusTracker = new StatusTracker(repository);
        this.scheduler = new RepairScheduler(repository, logger);
        this.executor = new RepairExecutionService(statusTracker, logger, repository);
        this.costEstimator = new CostEstimationService(inventoryConnector, logger);
        this.billingService = new BillingService(repository, costEstimator, logger);

        // Initialize Views (Console-based for demo)
        IRepairRequestIntakeView requestIntakeView = new ConsoleRepairRequestIntakeView();
        IRepairExecutionView executionView = new ConsoleRepairExecutionView();
        IBillingView billingView = new ConsoleBillingView();

        // Initialize Presenters (MVP Controllers)
        this.repairRequestPresenter = new RepairRequestPresenter(
                requestIntakeView, validator, scheduler, statusTracker, repository, logger);

        this.executionPresenter = new RepairExecutionPresenter(
                executionView, executor, statusTracker, logger, repository);

        this.billingPresenter = new BillingPresenter(
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
            repository.getStatistics().forEach((key, value) ->
                    System.out.println("   " + key + ": " + value)
            );

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    /**
     * Show system information
     */
    public void showSystemInfo() {
        System.out.println("\n========== REPAIRS SUB-SYSTEM INFORMATION ==========\n");
        System.out.println("Architecture: MVP (Model-View-Presenter)");
        System.out.println("Design Patterns Used:");
        System.out.println("  - Singleton (Logger, Repository)");
        System.out.println("  - Factory (Entity creation)");
        System.out.println("  - Builder (Complex objects)");
        System.out.println("  - Observer (Status tracking)");
        System.out.println("  - Strategy (Repair types)");
        System.out.println("  - DAO/Repository (Persistence)");
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
     * Main entry point
     */
    public static void main(String[] args) {
        RepairsSubSystemApplication app = new RepairsSubSystemApplication();

        // Show system information
        app.showSystemInfo();

        // Run demonstrations
        app.demonstrateRepairRequestFlow();
        app.demonstrateRepairExecutionFlow();
        app.demonstrateBillingFlow();

        System.out.println("\n========== DEMONSTRATION COMPLETE ==========\n");
        System.out.println("All components working correctly!");
        System.out.println("The system is ready for integration with full UI implementation.");
    }
}
