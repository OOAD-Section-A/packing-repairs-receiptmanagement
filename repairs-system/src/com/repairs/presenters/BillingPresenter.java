package com.repairs.presenters;

import com.repairs.entities.CostEstimate;
import com.repairs.entities.Receipt;
import com.repairs.entities.RepairJob;
import com.repairs.enums.PaymentStatus;
import com.repairs.interfaces.model.*;
import com.repairs.interfaces.view.IBillingView;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * BillingPresenter - MVP Presenter for billing operations.
 * Controls billing flow, cost estimation, and payment processing.
 */
public class BillingPresenter {
    private final IBillingView view;
    private final IBillingService billingService;
    private final ICostEstimator estimator;
    private final IFinancialSystemConnector financial;
    private final IRepairLogger logger;
    private final IRepairRepository repository;

    public BillingPresenter(IBillingView view,
                           IBillingService billingService,
                           ICostEstimator estimator,
                           IFinancialSystemConnector financial,
                           IRepairLogger logger,
                           IRepairRepository repository) {
        this.view = Objects.requireNonNull(view, "View cannot be null");
        this.billingService = Objects.requireNonNull(billingService, "Billing service cannot be null");
        this.estimator = Objects.requireNonNull(estimator, "Estimator cannot be null");
        this.financial = Objects.requireNonNull(financial, "Financial connector cannot be null");
        this.logger = Objects.requireNonNull(logger, "Logger cannot be null");
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
    }

    /**
     * Generate cost estimate for a repair job
     */
    public void onEstimationRequested(String jobId) {
        try {
            view.showLoadingIndicator();

            var jobOptional = repository.findRepairJobById(jobId);
            if (jobOptional.isEmpty()) {
                view.hideLoadingIndicator();
                view.displayError("Repair job not found");
                return;
            }

            RepairJob job = jobOptional.get();

            // Generate cost estimate
            CostEstimate estimate = estimator.estimateCost(job);

            view.hideLoadingIndicator();
            view.displayCostEstimate(estimate);

            // Display cost breakdown
            view.displayCostBreakdown(
                estimate.getLaborCost(),
                estimate.getPartsCost(),
                estimate.getTaxAmount(),
                estimate.getTotalCost()
            );

            logger.log(jobId,
                      "Cost estimate generated",
                      "INFO",
                      "ESTIMATION");

        } catch (Exception e) {
            view.hideLoadingIndicator();
            view.displayError("Error generating estimate: " + e.getMessage());
            logger.log(jobId,
                      "Error generating estimate: " + e.getMessage(),
                      "ERROR",
                      "ESTIMATION");
        }
    }

    /**
     * Generate bill for a completed repair
     */
    public void onBillingRequested(String jobId) {
        try {
            view.showLoadingIndicator();

            var jobOptional = repository.findRepairJobById(jobId);
            if (jobOptional.isEmpty()) {
                view.hideLoadingIndicator();
                view.displayError("Repair job not found");
                return;
            }

            RepairJob job = jobOptional.get();

            // Generate bill
            Receipt receipt = billingService.generateBill(job);

            view.hideLoadingIndicator();
            view.displayReceipt(receipt);
            view.displaySuccess("Invoice generated: " + receipt.generateReceiptNumber());

            // Enable payment button
            view.setPayButtonEnabled(true);

            logger.log(jobId,
                      "Bill generated - Receipt: " + receipt.getReceiptId(),
                      "INFO",
                      "BILLING");

        } catch (Exception e) {
            view.hideLoadingIndicator();
            view.displayError("Error generating bill: " + e.getMessage());
            logger.log(jobId,
                      "Error generating bill: " + e.getMessage(),
                      "ERROR",
                      "BILLING");
        }
    }

    /**
     * Process payment for a receipt
     */
    public void onPaymentProcessed(String receiptId) {
        try {
            view.showLoadingIndicator();

            var receiptOptional = billingService.getReceipt(receiptId);
            if (receiptOptional.isEmpty()) {
                view.hideLoadingIndicator();
                view.displayError("Receipt not found");
                return;
            }

            Receipt receipt = receiptOptional.get();

            // Check if already paid
            if (receipt.isFullyPaid()) {
                view.hideLoadingIndicator();
                view.displayWarning("Receipt is already paid");
                return;
            }

            // Process payment through financial system
            PaymentStatus status = financial.processPayment(receipt);

            if (status == PaymentStatus.PROCESSED) {
                // Mark receipt as paid
                receipt.markAsPaid("Credit Card"); // Default, could show dialog
                repository.updateReceipt(receipt);

                view.hideLoadingIndicator();
                view.showPaymentStatus(PaymentStatus.PROCESSED);
                view.displaySuccess("Payment processed successfully");
                view.setPayButtonEnabled(false);
                view.setRefundButtonEnabled(true);

                logger.log(receipt.getRepairJob().getJobId(),
                          "Payment processed for receipt: " + receiptId,
                          "INFO",
                          "PAYMENT");

            } else {
                view.hideLoadingIndicator();
                view.showPaymentStatus(PaymentStatus.FAILED);
                view.displayError("Payment processing failed");

                logger.log(receipt.getRepairJob().getJobId(),
                          "Payment failed for receipt: " + receiptId,
                          "WARNING",
                          "PAYMENT");
            }

        } catch (Exception e) {
            view.hideLoadingIndicator();
            view.displayError("Error processing payment: " + e.getMessage());
            logger.log(receiptId,
                      "Error processing payment: " + e.getMessage(),
                      "ERROR",
                      "PAYMENT");
        }
    }

    /**
     * Apply discount to a receipt
     */
    public void onDiscountApplied(String receiptId, BigDecimal discountAmount) {
        try {
            if (billingService.applyDiscount(receiptId, discountAmount)) {
                var receiptOptional = billingService.getReceipt(receiptId);
                if (receiptOptional.isPresent()) {
                    Receipt receipt = receiptOptional.get();
                    view.displayDiscountApplied(discountAmount, receipt.getFinalAmount());
                    view.displaySuccess("Discount applied");

                    logger.log(receipt.getRepairJob().getJobId(),
                              "Discount applied: " + discountAmount,
                              "INFO",
                              "DISCOUNT");
                }
            } else {
                view.displayError("Failed to apply discount");
            }

        } catch (Exception e) {
            view.displayError("Error applying discount: " + e.getMessage());
        }
    }

    /**
     * Process refund for a paid receipt
     */
    public void onRefundRequested(String receiptId) {
        try {
            view.showLoadingIndicator();

            var receiptOptional = billingService.getReceipt(receiptId);
            if (receiptOptional.isEmpty()) {
                view.hideLoadingIndicator();
                view.displayError("Receipt not found");
                return;
            }

            Receipt receipt = receiptOptional.get();

            // Process refund through financial system
            boolean refundSuccess = financial.processRefund(receipt);

            if (refundSuccess) {
                repository.updateReceipt(receipt);

                view.hideLoadingIndicator();
                view.showPaymentStatus(PaymentStatus.REFUNDED);
                view.displaySuccess("Refund processed successfully");
                view.setRefundButtonEnabled(false);
                view.setPayButtonEnabled(true);

                logger.log(receipt.getRepairJob().getJobId(),
                          "Refund processed for receipt: " + receiptId,
                          "INFO",
                          "REFUND");

            } else {
                view.hideLoadingIndicator();
                view.displayError("Refund processing failed");

                logger.log(receipt.getRepairJob().getJobId(),
                          "Refund failed for receipt: " + receiptId,
                          "WARNING",
                          "REFUND");
            }

        } catch (Exception e) {
            view.hideLoadingIndicator();
            view.displayError("Error processing refund: " + e.getMessage());
            logger.log(receiptId,
                      "Error processing refund: " + e.getMessage(),
                      "ERROR",
                      "REFUND");
        }
    }

    /**
     * Display outstanding bills
     */
    public void displayOutstandingBills() {
        try {
            List<Receipt> bills = billingService.getOutstandingBills();

            if (bills.isEmpty()) {
                view.displaySuccess("No outstanding bills");
            } else {
                view.displayOutstandingBills(bills);
            }

        } catch (Exception e) {
            view.displayError("Error retrieving outstanding bills: " + e.getMessage());
        }
    }

    /**
     * Display overdue bills
     */
    public void displayOverdueBills() {
        try {
            List<Receipt> bills = billingService.getOverdueBills();

            if (bills.isEmpty()) {
                view.displaySuccess("No overdue bills");
            } else {
                view.displayOverdueBills(bills);
            }

        } catch (Exception e) {
            view.displayError("Error retrieving overdue bills: " + e.getMessage());
        }
    }

    /**
     * Display cost estimate
     */
    public void displayCostEstimate(String estimateId) {
        try {
            var estimateOptional = repository.findCostEstimateById(estimateId);
            if (estimateOptional.isEmpty()) {
                view.displayError("Cost estimate not found");
                return;
            }

            CostEstimate estimate = estimateOptional.get();
            view.displayCostEstimate(estimate);

            // Check if estimate is still valid
            boolean isValid = estimate.isValidEstimate();
            String validityMsg = isValid ? "Estimate is valid" : "Estimate has expired";
            view.displayEstimateValidity(isValid, validityMsg);

        } catch (Exception e) {
            view.displayError("Error displaying estimate: " + e.getMessage());
        }
    }

    /**
     * Display receipt details
     */
    public void displayReceipt(String receiptId) {
        try {
            var receiptOptional = billingService.getReceipt(receiptId);
            if (receiptOptional.isEmpty()) {
                view.displayError("Receipt not found");
                return;
            }

            Receipt receipt = receiptOptional.get();
            view.displayReceipt(receipt);
            view.showPaymentStatus(receipt.getPaymentStatus());

        } catch (Exception e) {
            view.displayError("Error displaying receipt: " + e.getMessage());
        }
    }

    /**
     * Display customer payment history
     */
    public void displayCustomerPaymentHistory(String customerId) {
        try {
            List<Receipt> history = billingService.getBillsForCustomer(customerId);

            if (history.isEmpty()) {
                view.displaySuccess("No payment history for customer");
            } else {
                view.displayPaymentHistory(history);
            }

        } catch (Exception e) {
            view.displayError("Error retrieving payment history: " + e.getMessage());
        }
    }
}
