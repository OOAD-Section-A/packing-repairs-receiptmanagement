package com.scm.packing.integration.exceptions;

/**
 * Factory that decides which {@link IExceptionDispatcher} to use.
 *
 * <p><b>Design Pattern – Factory Method (Creational):</b> The selection
 * between the real SCM exception adapter and the console fallback is
 * centralised here.</p>
 *
 * <p><b>Fallback strategy:</b> We try to load the SCM exception handler
 * class via reflection.  If it is not on the classpath, we return the
 * console logger instead.</p>
 */
public class ExceptionDispatcherFactory {

    /**
     * Creates the most capable available {@link IExceptionDispatcher}.
     *
     * @return an {@link SCMExceptionAdapter} if the SCM exception handler
     *         JARs are available, or a {@link FallbackConsoleLogger} otherwise
     */
    public static IExceptionDispatcher create() {
        try {
            Class.forName("com.scm.exceptions.SCMExceptionHandler");
            System.out.println("[ExceptionDispatcherFactory] SCM Exception Handler found — using SCMExceptionAdapter.");
            return new SCMExceptionAdapter();
        } catch (ClassNotFoundException e) {
            System.out.println("[ExceptionDispatcherFactory] SCM Exception Handler NOT found on classpath.");
            System.out.println("[ExceptionDispatcherFactory] Falling back to FallbackConsoleLogger.");
            return new FallbackConsoleLogger();
        }
    }
}
