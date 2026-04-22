package com.repairs.external;

import com.repairs.interfaces.model.IExceptionHandler;
import com.repairs.subsystems.exceptionhandling.IRepairExceptionDispatcher;

/**
 * Compatibility adapter used by the repair codebase anywhere an
 * {@link IExceptionHandler} is required.
 */
public class DefaultExceptionHandler implements IExceptionHandler {
    private final IRepairExceptionDispatcher dispatcher;

    public DefaultExceptionHandler() {
        this.dispatcher = IRepairExceptionDispatcher.createDefaultHandler();
    }

    @Override
    public void handleException(Exception exception, String context) {
        dispatcher.handleException(exception, context);
    }

    public IRepairExceptionDispatcher getDispatcher() {
        return dispatcher;
    }
}
