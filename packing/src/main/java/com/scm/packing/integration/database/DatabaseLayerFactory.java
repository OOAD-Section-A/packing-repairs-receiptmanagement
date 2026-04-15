package com.scm.packing.integration.database;

/**
 * Factory that decides which {@link IDatabaseLayer} implementation to use.
 *
 * <p><b>Design Pattern – Factory Method (Creational):</b> The selection
 * between the real SCM adapter and the flat-file fallback is centralised
 * here.  The rest of the application simply asks for an
 * {@code IDatabaseLayer} and is unaware of the concrete class.</p>
 *
 * <p><b>Fallback strategy:</b> We first attempt to load the SCM database
 * adapter's dependency class via reflection.  If it is not on the
 * classpath (i.e. the database-module JAR is missing), we fall back to
 * the flat-file adapter automatically.</p>
 *
 * <p><b>GRASP – Creator:</b> This factory possesses the knowledge needed
 * to choose the correct implementation (classpath availability).</p>
 */
public class DatabaseLayerFactory {

    /**
     * Creates the most capable available {@link IDatabaseLayer}.
     *
     * @return an {@link SCMDatabaseAdapter} if the SCM database-module
     *         is available, or a {@link FlatFileDatabaseAdapter} otherwise
     */
    public static IDatabaseLayer create() {
        // -----------------------------------------------------------
        // FALLBACK LOGIC: Try to load the SCM facade class.  If it is
        // not on the classpath, Class.forName will throw, and we
        // gracefully fall back to the flat-file adapter.
        // -----------------------------------------------------------
        try {
            Class.forName("com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade");
            System.out.println("[DatabaseLayerFactory] SCM database-module found — using SCMDatabaseAdapter.");
            return new SCMDatabaseAdapter();
        } catch (ClassNotFoundException e) {
            System.out.println("[DatabaseLayerFactory] SCM database-module NOT found on classpath.");
            System.out.println("[DatabaseLayerFactory] Falling back to FlatFileDatabaseAdapter (session-only persistence).");
            return new FlatFileDatabaseAdapter();
        }
    }
}
