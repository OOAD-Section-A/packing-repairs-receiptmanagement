package com.repairs.subsystems.exceptionhandling;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Reflection-based bridge to the external SCM exception handler JAR.
 */
public class RepairSCMExceptionAdapter implements IRepairExceptionDispatcher {
    private final Object subsystemInstance;
    private final Method raiseMethod;

    public RepairSCMExceptionAdapter() {
        try {
            Class<?> subsystemClass = loadSubsystemClass();
            Field instanceField = subsystemClass.getField("INSTANCE");
            this.subsystemInstance = instanceField.get(null);
            this.raiseMethod = resolveRaiseMethod(subsystemClass);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "PackingRepairsReceiptSubsystem is not available on the classpath.", e);
        }
    }

    @Override
    public void dispatch(int exceptionId, String severity, String subsystem, String detail) {
        if (raiseMethod == null) {
            throw new IllegalStateException("No compatible raise(...) method found on SCM subsystem.");
        }

        try {
            Class<?> severityType = raiseMethod.getParameterTypes()[3];
            Object severityValue = Enum.valueOf(severityType.asSubclass(Enum.class), severity);
            raiseMethod.invoke(subsystemInstance, exceptionId, "UNREGISTERED_EXCEPTION", detail, severityValue);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to invoke SCM raise(...) method.", e);
        }
    }

    @Override
    public void dispatchUnregistered(String detail) {
        dispatch(0, "MINOR", IRepairExceptionDispatcher.SUBSYSTEM_LABEL, detail);
    }

    @Override
    public void onInvalidRepairRequest(String repairId, String itemId) {
        invoke("onInvalidRepairRequest", repairId, itemId);
    }

    @Override
    public void onSparePartNotAvailable(String partId) {
        invoke("onSparePartNotAvailable", partId);
    }

    @Override
    public void onInventoryReservationFailed(String itemId, String operation) {
        invoke("onInventoryReservationFailed", itemId, operation);
    }

    @Override
    public void onItemNotAvailableForPacking(String itemId, String orderId) {
        invoke("onItemNotAvailableForPacking", itemId, orderId);
    }

    @Override
    public void onWarrantyValidationFailed(String productId, String warrantyId) {
        invoke("onWarrantyValidationFailed", productId, warrantyId);
    }

    @Override
    public void onRepairExecutionFailed(String repairId, String reason) {
        invoke("onRepairExecutionFailed", repairId, reason);
    }

    @Override
    public void onRepairDelayDetected(String repairId, long elapsedMs, long slaMs) {
        invoke("onRepairDelayDetected", repairId, elapsedMs, slaMs);
    }

    @Override
    public void onPackageCreationFailed(String orderId) {
        invoke("onPackageCreationFailed", orderId);
    }

    @Override
    public void onReceiptStorageFailed(String receiptId) {
        invoke("onReceiptStorageFailed", receiptId);
    }

    @Override
    public void onReceiptGenerationFailed(String orderId) {
        invoke("onReceiptGenerationFailed", orderId);
    }

    @Override
    public void onPaymentProcessingFailed(String orderId, String reason) {
        invoke("onPaymentProcessingFailed", orderId, reason);
    }

    @Override
    public void onCostCalculationFailed(String orderId, String reason) {
        invoke("onCostCalculationFailed", orderId, reason);
    }

    private void invoke(String methodName, Object... args) {
        try {
            Method method = resolveMethod(methodName, args);
            method.invoke(subsystemInstance, args);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to invoke SCM exception method: " + methodName, e);
        }
    }

    private Method resolveMethod(String methodName, Object[] args) throws NoSuchMethodException {
        for (Method method : subsystemInstance.getClass().getMethods()) {
            if (!method.getName().equals(methodName) || method.getParameterCount() != args.length) {
                continue;
            }

            Class<?>[] parameterTypes = method.getParameterTypes();
            boolean matches = true;
            for (int i = 0; i < parameterTypes.length; i++) {
                if (!wrap(parameterTypes[i]).isInstance(args[i])) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                return method;
            }
        }

        throw new NoSuchMethodException(methodName);
    }

    private static Method resolveRaiseMethod(Class<?> subsystemClass) {
        for (Method method : subsystemClass.getMethods()) {
            if ("raise".equals(method.getName()) && method.getParameterCount() == 4) {
                return method;
            }
        }
        return null;
    }

    private static Class<?> loadSubsystemClass() throws ClassNotFoundException {
        String[] candidates = {
                "com.scm.exceptions.PackingRepairsReceiptSubsystem",
                "com.scm.exceptionhandler.PackingRepairsReceiptSubsystem",
                "PackingRepairsReceiptSubsystem"
        };

        for (String candidate : candidates) {
            try {
                return Class.forName(candidate);
            } catch (ClassNotFoundException ignored) {
                // Try next candidate.
            }
        }

        throw new ClassNotFoundException(IRepairExceptionDispatcher.SUBSYSTEM_NAME);
    }

    private static Class<?> wrap(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }
        if (type == long.class) {
            return Long.class;
        }
        if (type == int.class) {
            return Integer.class;
        }
        if (type == boolean.class) {
            return Boolean.class;
        }
        if (type == double.class) {
            return Double.class;
        }
        if (type == float.class) {
            return Float.class;
        }
        if (type == short.class) {
            return Short.class;
        }
        if (type == byte.class) {
            return Byte.class;
        }
        if (type == char.class) {
            return Character.class;
        }
        return type;
    }
}
