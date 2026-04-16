package com.scm.repair.integration.exceptions;

/**
 * Adapter that connects to the real SCM Exception Handler subsystem.
 *
 * <p><b>Design Pattern – Adapter (Structural):</b> Bridges the
 * {@link IRepairExceptionDispatcher} interface expected by the Repair
 * subsystem with the fire* methods defined in the SCM Interface
 * Specification.</p>
 *
 * <p><b>Integration note:</b> This class depends on the SCM exception
 * handler JARs being on the classpath (package
 * {@code com.scm.exceptions}). If they are absent, the
 * {@link RepairExceptionDispatcherFactory} falls back to
 * {@link RepairFallbackConsoleLogger}.</p>
 *
 * <p>The SCM spec requires:</p>
 * <ol>
 *   <li>Implement the relevant {@code I*ExceptionSource} interfaces.</li>
 *   <li>Store the handler reference passed via
 *       {@code registerHandler(SCMExceptionHandler h)}.</li>
 *   <li>Call the appropriate {@code fire*} method when an exception is
 *       detected.</li>
 *   <li>Halt the failing operation immediately after firing.</li>
 * </ol>
 *
 * <p><b>SOLID – Single Responsibility:</b> Exception forwarding only —
 * no business logic.</p>
 */
public class RepairSCMExceptionAdapter implements IRepairExceptionDispatcher {

    /*
     * When the SCM exception JARs are added to the classpath, this class
     * should implement the relevant SCM interfaces:
     *
     *   implements IResourceAvailabilityExceptionSource,
     *              IStateWorkflowExceptionSource,
     *              ISystemInfrastructureExceptionSource
     *
     * and store the handler reference:
     *
     *   private SCMExceptionHandler handler;
     *
     *   @Override
     *   public void registerHandler(SCMExceptionHandler h) {
     *       this.handler = h;
     *   }
     */

    public RepairSCMExceptionAdapter() {
        // -----------------------------------------------------------
        // INTEGRATION PLACEHOLDER
        // When the SCM exception JARs are on the classpath, implement
        // registerHandler() and delegate dispatch() to the appropriate
        // fire* methods.
        //
        // Example:
        //   handler.fireInvalidState(209, "RepairJob", jobId, "IN_PROGRESS", "FAILED");
        //
        // A null-check on handler is recommended:
        //   if (handler == null) return;
        // -----------------------------------------------------------
        System.out.println("[RepairSCMExceptionAdapter] Initialised (stub — real handler not yet wired).");
    }

    @Override
    public void dispatch(int exceptionId, String severity, String subsystem, String detail) {

        // -----------------------------------------------------------
        // STUB: In production this would call SCM fire* methods.
        //
        // Example:
        //   handler.fireInvalidState(209, "RepairJob", jobId, ...);
        // -----------------------------------------------------------
        System.out.println(String.format(
                "[RepairSCMExceptionAdapter] DISPATCH id=%d sev=%s sub=%s detail=%s",
                exceptionId, severity, subsystem, detail));
    }

    @Override
    public void dispatchUnregistered(String detail) {
        dispatch(0, "MINOR", "Repair", "UNREGISTERED_EXCEPTION — " + detail);
    }
}