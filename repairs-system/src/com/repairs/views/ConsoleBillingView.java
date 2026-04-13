package com.repairs.views;

import com.repairs.entities.CostEstimate;
import com.repairs.entities.Receipt;
import com.repairs.enums.PaymentStatus;
import com.repairs.interfaces.view.IBillingView;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * ConsoleBillingView - Console-based implementation of IBillingView.
 * PASSIVE view - displays billing information only, no business logic.
 */
public class ConsoleBillingView implements IBillingView {
    private final Scanner scanner;
    private boolean visible;

    public ConsoleBillingView() {
        this.scanner = new Scanner(System.in);
        this.visible = false;
    }

    @Override
    public void displayCostEstimate(CostEstimate estimate) {
        System.out.println("\n=== COST ESTIMATE ===");
        System.out.println("Estimate ID: " + estimate.getEstimateId());
        System.out.println("Labor Cost: $" + estimate.getLaborCost());
        System.out.println("Parts Cost: $" + estimate.getPartsCost());
        System.out.println("Tax (18%): $" + estimate.getTaxAmount());
        System.out.println("TOTAL: $" + estimate.getTotalCost());
    }

    @Override
    public void displayReceipt(Receipt receipt) {
        System.out.println("\n=== RECEIPT/INVOICE ===");
        System.out.println("Receipt Number: " + receipt.generateReceiptNumber());
        System.out.println("Receipt ID: " + receipt.getReceiptId());
        System.out.println("Generated: " + receipt.getGeneratedDate());
        System.out.println("Job ID: " + receipt.getRepairJob().getJobId());
        System.out.println("Amount: $" + receipt.getFinalAmount());
        System.out.println("Payment Status: " + receipt.getPaymentStatus());
        if (receipt.getPaidDate() != null) {
            System.out.println("Paid Date: " + receipt.getPaidDate());
        }
    }

    @Override
    public void showPaymentStatus(PaymentStatus status) {
        String statusIcon = switch (status) {
            case PROCESSED -> "✓";
            case FAILED -> "❌";
            case PENDING -> "⏳";
            case REFUNDED -> "↩️";
        };
        System.out.println("\n💳 Payment Status: " + statusIcon + " " + status);
    }

    @Override
    public void displayPaymentStatusMessage(String message) {
        System.out.println("\n" + message);
    }

    @Override
    public void displayOutstandingBills(List<Receipt> bills) {
        System.out.println("\n=== OUTSTANDING BILLS ===");
        System.out.println("Count: " + bills.size());
        BigDecimal total = BigDecimal.ZERO;
        for (Receipt bill : bills) {
            System.out.println("  - " + bill.generateReceiptNumber() + 
                             " ($" + bill.getFinalAmount() + ")");
            total = total.add(bill.getFinalAmount());
        }
        System.out.println("TOTAL OUTSTANDING: $" + total);
    }

    @Override
    public void displayOverdueBills(List<Receipt> bills) {
        System.out.println("\n=== OVERDUE BILLS ===");
        System.out.println("Count: " + bills.size());
        BigDecimal total = BigDecimal.ZERO;
        for (Receipt bill : bills) {
            System.out.println("  - " + bill.generateReceiptNumber() + 
                             " ($" + bill.getFinalAmount() + ")");
            total = total.add(bill.getFinalAmount());
        }
        System.out.println("TOTAL OVERDUE: $" + total);
    }

    @Override
    public void displayCostBreakdown(BigDecimal laborCost, BigDecimal partsCost,
                                    BigDecimal taxAmount, BigDecimal total) {
        System.out.println("\n=== COST BREAKDOWN ===");
        System.out.println("Labor: $" + laborCost);
        System.out.println("Parts: $" + partsCost);
        System.out.println("Subtotal: $" + laborCost.add(partsCost));
        System.out.println("Tax: $" + taxAmount);
        System.out.println("────────────");
        System.out.println("TOTAL: $" + total);
    }

    @Override
    public void displayDiscountApplied(BigDecimal discountAmount, BigDecimal newTotal) {
        System.out.println("\n=== DISCOUNT APPLIED ===");
        System.out.println("Discount: -$" + discountAmount);
        System.out.println("New Total: $" + newTotal);
    }

    @Override
    public void displayError(String message) {
        System.out.println("\n❌ ERROR: " + message);
    }

    @Override
    public void displayWarning(String message) {
        System.out.println("\n⚠️  WARNING: " + message);
    }

    @Override
    public void displaySuccess(String message) {
        System.out.println("\n✓ SUCCESS: " + message);
    }

    @Override
    public void showLoadingIndicator() {
        System.out.println("⏳ Processing...");
    }

    @Override
    public void hideLoadingIndicator() {
        // Console doesn't hide indicators
    }

    @Override
    public String showPaymentMethodDialog() {
        System.out.println("\nSelect Payment Method:");
        System.out.println("1. Credit Card");
        System.out.println("2. Debit Card");
        System.out.println("3. Bank Transfer");
        System.out.print("Enter choice (1-3): ");
        String choice = scanner.nextLine().trim();
        
        return switch (choice) {
            case "1" -> "Credit Card";
            case "2" -> "Debit Card";
            case "3" -> "Bank Transfer";
            default -> "Credit Card";
        };
    }

    @Override
    public void setPayButtonEnabled(boolean enabled) {
        System.out.println("PAY button: " + (enabled ? "✓ ENABLED" : "✗ DISABLED"));
    }

    @Override
    public void setRefundButtonEnabled(boolean enabled) {
        System.out.println("REFUND button: " + (enabled ? "✓ ENABLED" : "✗ DISABLED"));
    }

    @Override
    public void setDiscountButtonEnabled(boolean enabled) {
        System.out.println("DISCOUNT button: " + (enabled ? "✓ ENABLED" : "✗ DISABLED"));
    }

    @Override
    public void clearDisplay() {
        System.out.println("\n[Display cleared]");
    }

    @Override
    public void displayEstimateValidity(boolean isValid, String expiryMessage) {
        String validity = isValid ? "✓ VALID" : "❌ EXPIRED";
        System.out.println("\nEstimate: " + validity);
        System.out.println(expiryMessage);
    }

    @Override
    public void displayPaymentHistory(List<Receipt> receipts) {
        System.out.println("\n=== PAYMENT HISTORY ===");
        System.out.println("Total Receipts: " + receipts.size());
        for (Receipt receipt : receipts) {
            System.out.println("\n  " + receipt.generateReceiptNumber());
            System.out.println("  Amount: $" + receipt.getFinalAmount());
            System.out.println("  Status: " + receipt.getPaymentStatus());
            System.out.println("  Date: " + receipt.getGeneratedDate());
        }
    }
}
