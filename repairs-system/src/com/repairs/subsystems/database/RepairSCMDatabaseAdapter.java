package com.repairs.subsystems.database;

import com.repairs.interfaces.model.IDatabaseSubsystem;

/**
 * Placeholder SCM adapter. Until the external database JAR is added and mapped,
 * it behaves like the fallback database while keeping the integration seam in
 * place.
 */
public class RepairSCMDatabaseAdapter extends RepairFlatFileDatabaseAdapter {

    public RepairSCMDatabaseAdapter() {
        System.out.println("[RepairSCMDatabaseAdapter] External SCM DB module detected.");
        System.out.println("[RepairSCMDatabaseAdapter] Using fallback in-memory mapping until facade wiring is added.");
    }

    public static IDatabaseSubsystem createDefaultSubsystem() {
        return new RepairSCMDatabaseAdapter();
    }
}
