package com.repairs.views;

import com.repairs.entities.RepairRequest;
import com.repairs.enums.RepairType;
import com.repairs.interfaces.view.IRepairRequestIntakeView;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

/**
 * ConsoleRepairRequestIntakeView - Console-based implementation of IRepairRequestIntakeView.
 * PASSIVE view - no business logic, only UI presentation and input capture.
 */
public class ConsoleRepairRequestIntakeView implements IRepairRequestIntakeView {
    private final Scanner scanner;
    private boolean visible;
    private String lastInput;

    public ConsoleRepairRequestIntakeView() {
        this.scanner = new Scanner(System.in);
        this.visible = false;
    }

    @Override
    public void showRepairRequestForm() {
        visible = true;
        System.out.println("\n=== REPAIR REQUEST INTAKE FORM ===");
        System.out.println("Please provide the following information:");
    }

    @Override
    public void displayValidationErrors(List<String> errors) {
        System.out.println("\n❌ VALIDATION ERRORS:");
        for (String error : errors) {
            System.out.println("  - " + error);
        }
    }

    @Override
    public void displayValidationSuccess() {
        System.out.println("\n✓ VALIDATION PASSED");
    }

    @Override
    public void displayScheduledDate(String scheduledDate) {
        System.out.println("\n📅 SCHEDULED DATE: " + scheduledDate);
    }

    @Override
    public void clearForm() {
        System.out.println("\nForm cleared.");
    }

    @Override
    public void showLoadingIndicator(String message) {
        System.out.println("⏳ " + message);
    }

    @Override
    public void hideLoadingIndicator() {
        // Console doesn't really hide indicators
    }

    @Override
    public void displayError(String errorMessage) {
        System.out.println("\n❌ ERROR: " + errorMessage);
    }

    @Override
    public void displaySuccess(String successMessage) {
        System.out.println("\n✓ SUCCESS: " + successMessage);
    }

    @Override
    public RepairRequest getRepairRequestInput() {
        System.out.print("\nEnter Request ID (auto-generated if empty): ");
        String requestId = scanner.nextLine().trim();
        if (requestId.isEmpty()) {
            requestId = "REQ-" + System.currentTimeMillis() % 10000;
        }

        System.out.print("Enter Customer ID (e.g., C10001): ");
        String customerId = scanner.nextLine().trim();

        System.out.println("\nAvailable Repair Types:");
        for (RepairType type : RepairType.values()) {
            System.out.println("  - " + type.name());
        }
        System.out.print("Enter Repair Type: ");
        String repairTypeStr = scanner.nextLine().trim().toUpperCase();
        RepairType repairType = RepairType.valueOf(repairTypeStr);

        System.out.print("Enter Description: ");
        String description = scanner.nextLine().trim();

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
        System.out.println("Submit button: " + (enabled ? "ENABLED" : "DISABLED"));
    }

    @Override
    public void requestFocus() {
        // Not applicable to console
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public boolean showConfirmDialog(String message) {
        System.out.print("\n" + message + " (Yes/No): ");
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("yes") || response.equals("y");
    }
}
