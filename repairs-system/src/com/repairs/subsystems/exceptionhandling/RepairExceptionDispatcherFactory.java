package com.scm.repair.integration.exceptions;

/**
 * Factory that decides which {@link IRepairExceptionDispatcher} to use.
 *
 * <p><b>Design Pattern – Factory Method (Creational):</b>
 * The selection between the real SCM exception adapter and the console
 * fallback is centralised here.</p>
 *
 * <p><b>Fallback strategy:</b>
 * We try to load the SCM exception handler class via reflection.
 * If it is not on the classpath, we return the console logger instead.</p>
 */
public class RepairExceptionDispatcherFactory {

    /**
     * Creates the most capable available {@link IRepairExceptionDispatcher}.
     *
     * @return a {@link FallbackConsoleLogger} if the SCM exception handler
     *         JAR is available, or a {@link RepairFallbackConsoleLogger} otherwise
     */
    public static IRepairExceptionDispatcher create() {

        try {
            // SAME external dependency check (shared exception system)
            Class.forName("com.scm.exceptions.SCMExceptionHandler");

            System.out.println("[RepairExceptionDispatcherFactory] SCM Exception Handler found — using RepairExceptionAdapter.");
            return new FallbackConsoleLogger();

        } catch (ClassNotFoundException e) {

            System.out.println("[RepairExceptionDispatcherFactory] SCM Exception Handler NOT found on classpath.");
            System.out.println("[RepairExceptionDispatcherFactory] Falling back to RepairFallbackConsoleLogger.");

            return new RepairFallbackConsoleLogger();
        }
    }
}