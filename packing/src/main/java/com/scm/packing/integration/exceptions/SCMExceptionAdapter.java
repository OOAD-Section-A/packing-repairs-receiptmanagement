package com.scm.packing.integration.exceptions;

/**
 * Adapter that connects to the real SCM Exception Handler subsystem.
 *
 * <p><b>Design Pattern – Adapter (Structural):</b> Bridges the
 * {@link IExceptionDispatcher} interface expected by the Packing
 * subsystem with the fire* methods defined in the SCM Interface
 * Specification (see {@code SCM_Interface_Specification.docx}).</p>
 *
 * <p><b>Integration note:</b> This class depends on the SCM exception
 * handler JARs being on the classpath (package
 * {@code com.scm.exceptions}).  If they are absent, the
 * {@link ExceptionDispatcherFactory} falls back to
 * {@link FallbackConsoleLogger}.</p>
 *
 * <p>The SCM spec requires:</p>
 * <ol>
 *   <li>Implement the relevant {@code I*ExceptionSource} interfaces
 *       (e.g. {@code IResourceAvailabilityExceptionSource}).</li>
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
public class SCMExceptionAdapter implements IExceptionDispatcher {

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

    public SCMExceptionAdapter() {
        // -----------------------------------------------------------
        // INTEGRATION PLACEHOLDER
        // When the SCM exception JARs are on the classpath, implement
        // registerHandler() and delegate dispatch() to the appropriate
        // fire* methods.
        //
        // Example:
        //   handler.fireResourceNotAvailable(159, "Item", itemId);
        //
        // A null-check on handler is recommended as a safeguard:
        //   if (handler == null) return;
        // -----------------------------------------------------------
        System.out.println("[SCMExceptionAdapter] Initialised (stub — real handler not yet wired).");
    }

    @Override
    public void dispatch(int exceptionId, String severity, String subsystem, String detail) {
        // -----------------------------------------------------------
        // STUB: In production this would call the appropriate fire*
        // method on the SCM handler.
        //
        // Example for exception 159 (ITEM_NOT_AVAILABLE_FOR_PACKING):
        //   if (handler == null) return;
        //   handler.fireResourceNotAvailable(159, "PackingItem", itemId);
        // -----------------------------------------------------------
        System.out.println(String.format(
                "[SCMExceptionAdapter] DISPATCH id=%d sev=%s sub=%s detail=%s",
                exceptionId, severity, subsystem, detail));
    }

    @Override
    public void dispatchUnregistered(String detail) {
        dispatch(0, "MINOR", "Packing", "UNREGISTERED_EXCEPTION — " + detail);
    }
}
