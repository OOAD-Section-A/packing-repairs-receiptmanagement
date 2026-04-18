package com.repairs.subsystems.exceptionhandling;

/**
 * Factory that selects the SCM-backed exception adapter when the external JARs
 * are present and otherwise falls back to structured console logging.
 */
public final class RepairExceptionDispatcherFactory {

    private RepairExceptionDispatcherFactory() {
    }

    public static IRepairExceptionDispatcher create() {
        try {
            return new RepairSCMExceptionAdapter();
        } catch (RuntimeException ex) {
            System.out.println("[RepairExceptionDispatcherFactory] SCM exception handler not available.");
            System.out.println("[RepairExceptionDispatcherFactory] Falling back to RepairFallbackConsoleLogger.");
            return new RepairFallbackConsoleLogger();
        }
    }
}
