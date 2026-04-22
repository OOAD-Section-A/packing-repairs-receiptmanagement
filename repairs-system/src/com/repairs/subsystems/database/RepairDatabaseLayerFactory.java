package com.repairs.subsystems.database;

import com.repairs.interfaces.model.IDatabaseSubsystem;

/**
 * Selects the external SCM database adapter when the DB module is present,
 * otherwise uses the in-memory fallback.
 */
public final class RepairDatabaseLayerFactory {

    private RepairDatabaseLayerFactory() {
    }

    public static IDatabaseSubsystem create() {
        try {
            Class.forName("com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade");
            System.out.println("[RepairDatabaseLayerFactory] SCM DB found - using RepairSCMDatabaseAdapter.");
            return new RepairSCMDatabaseAdapter();
        } catch (ClassNotFoundException e) {
            System.out.println("[RepairDatabaseLayerFactory] SCM DB not found.");
            System.out.println("[RepairDatabaseLayerFactory] Falling back to RepairFlatFileDatabaseAdapter.");
            return new RepairFlatFileDatabaseAdapter();
        }
    }
}
