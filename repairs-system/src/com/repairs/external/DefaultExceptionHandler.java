package com.repairs.external;

import com.repairs.subsystems.exceptionhandling.IRepairExceptionDispatcher;
import com.repairs.subsystems.exceptionhandling.RepairExceptionDispatcherFactory;

/**
 * DefaultExceptionHandler - compatibility adapter for the shared exception subsystem.
 */
public class DefaultExceptionHandler extends RepairExceptionDispatcherFactory {
    public DefaultExceptionHandler() {
        super(IRepairExceptionDispatcher.getDefaultLogPath());
    }
}
