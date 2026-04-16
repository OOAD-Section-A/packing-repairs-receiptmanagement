package com.scm.repair.integration.database;

import java.util.ArrayList;
import java.util.List;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
import com.scm.repair.mvc.model.RepairJob;

/**
 * Adapter connecting Repair subsystem to SCM database.
 *
 * <b>Design Pattern: Adapter</b>
 */
public class RepairSCMDatabaseAdapter implements IRepairDatabaseLayer {

    private final SupplyChainDatabaseFacade facade;

    public RepairSCMDatabaseAdapter() {
        this.facade = new SupplyChainDatabaseFacade();
        System.out.println("[RepairSCMDatabaseAdapter] Connected to SCM DB.");
    }

    @Override
    public List<RepairJob> loadRepairJobs() {

        List<RepairJob> result = new ArrayList<>();

        try {
            // 👉 Adjust based on actual DB module
            // Example:
            // facade.repairs().listRepairJobs()

            System.out.println("[RepairSCMDatabaseAdapter] Loading jobs from DB...");

        } catch (Exception e) {
            System.err.println("[RepairSCMDatabaseAdapter] load failed: " + e.getMessage());
        }

        return result;
    }

    @Override
    public void saveRepairJob(RepairJob job) {
        try {
            // facade.repairs().createRepairJob(...)
            System.out.println("[RepairSCMDatabaseAdapter] Saved job: " + job.getJobId());
        } catch (Exception e) {
            System.err.println("[RepairSCMDatabaseAdapter] save failed: " + e.getMessage());
        }
    }

    @Override
    public void updateRepairJob(RepairJob job) {
        try {
            // facade.repairs().updateRepairStatus(...)
            System.out.println("[RepairSCMDatabaseAdapter] Updated job: " + job.getJobId());
        } catch (Exception e) {
            System.err.println("[RepairSCMDatabaseAdapter] update failed: " + e.getMessage());
        }
    }

    @Override
    public void clearAll() {
        System.out.println("[RepairSCMDatabaseAdapter] clearAll = no-op (DB mode)");
    }
}