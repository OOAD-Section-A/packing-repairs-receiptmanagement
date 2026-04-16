package com.scm.repair.integration.database;

import java.util.ArrayList;
import java.util.List;
import com.scm.repair.mvc.model.RepairJob;

/**
 * Fallback adapter (no database).
 */
public class RepairFlatFileDatabaseAdapter implements IRepairDatabaseLayer {

    private final List<RepairJob> jobs = new ArrayList<>();

    @Override
    public List<RepairJob> loadRepairJobs() {
        return jobs;
    }

    @Override
    public void saveRepairJob(RepairJob job) {
        jobs.add(job);
        System.out.println("[RepairFlatFileDB] Job saved: " + job.getJobId());
    }

    @Override
    public void updateRepairJob(RepairJob job) {
        System.out.println("[RepairFlatFileDB] Job updated: " + job.getJobId());
    }

    @Override
    public void clearAll() {
        jobs.clear();
        System.out.println("[RepairFlatFileDB] Cleared all jobs.");
    }
}