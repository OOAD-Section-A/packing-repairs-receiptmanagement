package com.repairs.external;

import com.repairs.subsystems.database.RepairFlatFileDatabaseAdapter;
import com.repairs.subsystems.database.IRepairDatabaseLayer;

/**
 * DefaultDatabaseSubsystem - compatibility adapter for the shared file-backed database subsystem.
 */
public class DefaultDatabaseSubsystem extends IRepairDatabaseLayer {
    public DefaultDatabaseSubsystem() {
        super(RepairFlatFileDatabaseAdapter.getDefaultDatabasePath());
    }
}
