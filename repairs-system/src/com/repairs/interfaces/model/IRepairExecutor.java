package com.repairs.interfaces.model;

import com.repairs.entities.RepairJob;

/**
 * IRepairExecutor - Interface for executing repair jobs.
 * Implements SRP - only handles repair execution logic.
 */
public interface IRepairExecutor {
    
    /**
     * Start executing a repair job
     * @param job The repair job to execute
     */
    void executeRepair(RepairJob job);

    /**
     * Pause an ongoing repair
     * @param jobId The job ID to pause
     * @return true if pause successful
     */
    boolean pauseRepair(String jobId);

    /**
     * Resume a paused repair
     * @param jobId The job ID to resume
     * @return true if resume successful
     */
    boolean resumeRepair(String jobId);

    /**
     * Complete a repair job
     * @param jobId The job ID to complete
     */
    void completeRepair(String jobId);

    /**
     * Mark a repair as failed
     * @param jobId The job ID to fail
     * @param reason The reason for failure
     */
    void failRepair(String jobId, String reason);

    /**
     * Update progress of an ongoing repair
     * @param jobId The job ID
     * @param progressPercentage The progress (0-100)
     */
    void updateProgress(String jobId, int progressPercentage);
}
