package com.scm.repair.integration.exceptions;

/**
 * Abstraction for exception dispatching in the Repair subsystem.
 *
 * <p><b>Design Pattern – Adapter (Structural):</b> This interface is the
 * "target" that the Repair subsystem codes against. Concrete adapters
 * bridge either the real SCM Exception Handler subsystem or a simple
 * console-based fallback.</p>
 *
 * <p><b>SOLID – Dependency Inversion:</b> Controllers and workers depend
 * on this interface, never on a concrete exception handler class.</p>
 *
 * <p><b>SOLID – Interface Segregation:</b> Only repair-relevant exception
 * methods are declared — the full SCM exception surface is not exposed
 * unnecessarily.</p>
 *
 * <p>Relevant SCM Exception IDs for the Repair subsystem:</p>
 * <ul>
 *   <li>9   — INVALID_REPAIR_REQUEST</li>
 *   <li>209 — REPAIR_EXECUTION_FAILED</li>
 *   <li>210 — REPAIR_DELAY_DETECTED</li>
 * </ul>
 */
public interface IRepairExceptionDispatcher {

    /**
     * Dispatches an exception event.
     *
     * @param exceptionId  the numeric ID from the SCM master register
     * @param severity     "MINOR", "MAJOR", or "WARNING"
     * @param subsystem    subsystem name (always "Repair" here)
     * @param detail       human-readable context about the failure
     */
    void dispatch(int exceptionId, String severity, String subsystem, String detail);

    /**
     * Dispatches an unregistered exception (ID 0).
     *
     * @param detail context about the unregistered exception
     */
    void dispatchUnregistered(String detail);
}