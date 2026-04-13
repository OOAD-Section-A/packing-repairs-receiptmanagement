package com.repairs.interfaces.model;

/**
 * IExceptionHandler - Interface for external exception handling subsystem.
 */
public interface IExceptionHandler {

    /**
     * Handle an exception with execution context.
     * @param exception The caught exception
     * @param context The operation context where it happened
     */
    void handleException(Exception exception, String context);
}
