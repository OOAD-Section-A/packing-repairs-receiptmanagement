package com.repairs.external;

import com.repairs.interfaces.model.IExceptionHandler;
import java.time.LocalDateTime;

/**
 * DefaultExceptionHandler - fallback handler when no external exception subsystem is provided.
 */
public class DefaultExceptionHandler implements IExceptionHandler {
    @Override
    public void handleException(Exception exception, String context) {
        String message = String.format("[%s] %s: %s",
                LocalDateTime.now(),
                context,
                exception != null ? exception.getMessage() : "Unknown exception");
        System.err.println(message);
    }
}
