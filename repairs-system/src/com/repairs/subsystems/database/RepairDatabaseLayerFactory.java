package com.scm.repair.integration.database;

/**
 * Factory that decides which {@link IRepairDatabaseLayer} implementation to use.
 *
 * <p><b>Design Pattern – Factory Method (Creational):</b>
 * Centralises selection between SCM adapter and flat-file fallback.</p>
 *
 * <p><b>Fallback strategy:</b>
 * Uses Class.forName() to detect SCM database-module at runtime.</p>
 */
public class RepairDatabaseLayerFactory {

    /**
     * Creates the best available database layer.
     */
    public static IRepairDatabaseLayer create() {

        try {
            // SAME check as packing (shared backend)
            Class.forName("com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade");

            System.out.println("[RepairDatabaseLayerFactory] SCM DB found — using RepairSCMDatabaseAdapter.");
            return new RepairSCMDatabaseAdapter();

        } catch (ClassNotFoundException e) {

            System.out.println("[RepairDatabaseLayerFactory] SCM DB NOT found.");
            System.out.println("[RepairDatabaseLayerFactory] Falling back to RepairFlatFileDatabaseAdapter.");

            return new RepairFlatFileDatabaseAdapter();
        }
    }
}