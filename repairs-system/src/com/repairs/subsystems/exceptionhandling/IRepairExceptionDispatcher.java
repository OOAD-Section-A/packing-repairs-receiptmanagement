package com.repairs.subsystems.exceptionhandling;

import com.repairs.interfaces.model.IExceptionHandler;

/**
 * Exception-dispatch contract for the Packing/Repairs/Receipt subsystem.
 *
 * <p>The shared SCM exception JAR exposes strongly typed methods such as
 * {@code onInvalidRepairRequest(...)} on
 * {@code PackingRepairsReceiptSubsystem.INSTANCE}. This interface keeps the
 * repair codebase decoupled from that external dependency while still exposing
 * the exact exception methods this subsystem is allowed to raise.</p>
 */
public interface IRepairExceptionDispatcher extends IExceptionHandler {

    String SUBSYSTEM_NAME = "PackingRepairsReceiptSubsystem";
    String SUBSYSTEM_LABEL = "Packing, Repairs, Receipt Management";

    void dispatch(int exceptionId, String severity, String subsystem, String detail);

    void dispatchUnregistered(String detail);

    void onInvalidRepairRequest(String repairId, String itemId);

    void onSparePartNotAvailable(String partId);

    void onInventoryReservationFailed(String itemId, String operation);

    void onItemNotAvailableForPacking(String itemId, String orderId);

    void onWarrantyValidationFailed(String productId, String warrantyId);

    void onRepairExecutionFailed(String repairId, String reason);

    void onRepairDelayDetected(String repairId, long elapsedMs, long slaMs);

    void onPackageCreationFailed(String orderId);

    void onReceiptStorageFailed(String receiptId);

    void onReceiptGenerationFailed(String orderId);

    void onPaymentProcessingFailed(String orderId, String reason);

    void onCostCalculationFailed(String orderId, String reason);

    @Override
    default void handleException(Exception exception, String context) {
        String message = exception == null ? "No exception details available." : exception.getMessage();
        dispatchUnregistered(context + " :: " + message);
    }

    static IRepairExceptionDispatcher createDefaultHandler() {
        return RepairExceptionDispatcherFactory.create();
    }

    static String getDefaultLogPath() {
        return "./logs/repair-exceptions.log";
    }
}
