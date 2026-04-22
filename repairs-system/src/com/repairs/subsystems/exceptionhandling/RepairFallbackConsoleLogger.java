package com.repairs.subsystems.exceptionhandling;

import java.time.Instant;

/**
 * Console fallback used when the SCM exception handler JARs are not available.
 */
public class RepairFallbackConsoleLogger implements IRepairExceptionDispatcher {

    @Override
    public void dispatch(int exceptionId, String severity, String subsystem, String detail) {
        System.err.printf(
                "[REPAIR FALLBACK EXCEPTION] %s | ID: %d | Severity: %s | Subsystem: %s | Detail: %s%n",
                Instant.now(), exceptionId, severity, subsystem, detail);
    }

    @Override
    public void dispatchUnregistered(String detail) {
        dispatch(0, "MINOR", IRepairExceptionDispatcher.SUBSYSTEM_LABEL,
                "UNREGISTERED_EXCEPTION - " + detail);
    }

    @Override
    public void onInvalidRepairRequest(String repairId, String itemId) {
        dispatch(9, "MAJOR", IRepairExceptionDispatcher.SUBSYSTEM_LABEL,
                "Invalid repair request. repairId=" + repairId + ", itemId=" + itemId);
    }

    @Override
    public void onSparePartNotAvailable(String partId) {
        dispatch(157, "MAJOR", IRepairExceptionDispatcher.SUBSYSTEM_LABEL,
                "Spare part not available. partId=" + partId);
    }

    @Override
    public void onInventoryReservationFailed(String itemId, String operation) {
        dispatch(158, "MAJOR", IRepairExceptionDispatcher.SUBSYSTEM_LABEL,
                "Inventory reservation failed. itemId=" + itemId + ", operation=" + operation);
    }

    @Override
    public void onItemNotAvailableForPacking(String itemId, String orderId) {
        dispatch(159, "MAJOR", IRepairExceptionDispatcher.SUBSYSTEM_LABEL,
                "Item not available for packing. itemId=" + itemId + ", orderId=" + orderId);
    }

    @Override
    public void onWarrantyValidationFailed(String productId, String warrantyId) {
        dispatch(208, "MAJOR", IRepairExceptionDispatcher.SUBSYSTEM_LABEL,
                "Warranty validation failed. productId=" + productId + ", warrantyId=" + warrantyId);
    }

    @Override
    public void onRepairExecutionFailed(String repairId, String reason) {
        dispatch(209, "MAJOR", IRepairExceptionDispatcher.SUBSYSTEM_LABEL,
                "Repair execution failed. repairId=" + repairId + ", reason=" + reason);
    }

    @Override
    public void onRepairDelayDetected(String repairId, long elapsedMs, long slaMs) {
        dispatch(210, "WARNING", IRepairExceptionDispatcher.SUBSYSTEM_LABEL,
                "Repair delay detected. repairId=" + repairId + ", elapsedMs=" + elapsedMs + ", slaMs=" + slaMs);
    }

    @Override
    public void onPackageCreationFailed(String orderId) {
        dispatch(359, "MINOR", IRepairExceptionDispatcher.SUBSYSTEM_LABEL,
                "Package creation failed. orderId=" + orderId);
    }

    @Override
    public void onReceiptStorageFailed(String receiptId) {
        dispatch(360, "MAJOR", IRepairExceptionDispatcher.SUBSYSTEM_LABEL,
                "Receipt storage failed. receiptId=" + receiptId);
    }

    @Override
    public void onReceiptGenerationFailed(String orderId) {
        dispatch(361, "MAJOR", IRepairExceptionDispatcher.SUBSYSTEM_LABEL,
                "Receipt generation failed. orderId=" + orderId);
    }

    @Override
    public void onPaymentProcessingFailed(String orderId, String reason) {
        dispatch(362, "MAJOR", IRepairExceptionDispatcher.SUBSYSTEM_LABEL,
                "Payment processing failed. orderId=" + orderId + ", reason=" + reason);
    }

    @Override
    public void onCostCalculationFailed(String orderId, String reason) {
        dispatch(363, "MAJOR", IRepairExceptionDispatcher.SUBSYSTEM_LABEL,
                "Cost calculation failed. orderId=" + orderId + ", reason=" + reason);
    }
}
