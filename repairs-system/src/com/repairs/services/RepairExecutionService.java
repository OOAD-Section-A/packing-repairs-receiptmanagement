package com.repairs.services;

import com.repairs.entities.RepairJob;
import com.repairs.enums.RepairStatus;
import com.repairs.interfaces.model.IRepairExecutor;
import com.repairs.interfaces.model.IRepairLogger;
import com.repairs.interfaces.model.IRepairRepository;
import com.repairs.interfaces.model.IStatusTracker;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RepairExecutionService - Concrete implementation of IRepairExecutor.
 * Manages the execution lifecycle of repair jobs.
 */
public class RepairExecutionService implements IRepairExecutor {
    private final IStatusTracker statusTracker;
    private final IRepairLogger logger;
    private final IRepairRepository repository;
    private final Map<String, Integer> jobProgress; // Track progress percentage
    private final Map<String, String> pausedReasons; // Track pause reasons

    public RepairExecutionService(IStatusTracker statusTracker, 
                                  IRepairLogger logger, 
                                  IRepairRepository repository) {
        this.statusTracker = Objects.requireNonNull(statusTracker, "Status tracker cannot be null");
        this.logger = Objects.requireNonNull(logger, "Logger cannot be null");
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
        this.jobProgress = new ConcurrentHashMap<>();
        this.pausedReasons = new ConcurrentHashMap<>();
    }

    @Override
    public void executeRepair(RepairJob job) {
        if (job == null) {
            throw new IllegalArgumentException("Repair job cannot be null");
        }

        if (job.getStatus() != RepairStatus.SCHEDULED) {
            throw new IllegalStateException("Only scheduled jobs can be executed");
        }

        if (job.getAssignedTechnician() == null) {
            throw new IllegalStateException("Technician must be assigned before execution");
        }

        try {
            // Start the repair
            job.startRepair();

            // Update status in tracker
            statusTracker.updateStatus(job.getJobId(), RepairStatus.IN_PROGRESS);

            // Save to repository
            repository.updateRepairJob(job);

            // Log execution start
            logger.log(job.getJobId(),
                      "Repair execution started by " + job.getAssignedTechnician(),
                      "INFO",
                      "EXECUTION_START");

            // Initialize progress
            jobProgress.put(job.getJobId(), 0);

        } catch (Exception e) {
            logger.log(job.getJobId(),
                      "Error starting repair: " + e.getMessage(),
                      "ERROR",
                      "EXECUTION_START");
            throw e;
        }
    }

    @Override
    public boolean pauseRepair(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return false;
        }

        Optional<RepairJob> jobOptional = repository.findRepairJobById(jobId);
        if (jobOptional.isEmpty()) {
            return false;
        }

        RepairJob job = jobOptional.get();

        if (job.getStatus() != RepairStatus.IN_PROGRESS) {
            return false;
        }

        try {
            // Note: We don't have a PAUSED status in the enum
            // So we store pause reason and keep status as IN_PROGRESS
            // This could be extended with additional pause state
            
            pausedReasons.put(jobId, "Repair paused - waiting for parts or other reason");

            logger.log(jobId,
                      "Repair paused",
                      "INFO",
                      "EXECUTION_PAUSE");

            return true;
        } catch (Exception e) {
            logger.log(jobId,
                      "Error pausing repair: " + e.getMessage(),
                      "ERROR",
                      "EXECUTION_PAUSE");
            return false;
        }
    }

    @Override
    public boolean resumeRepair(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return false;
        }

        if (!pausedReasons.containsKey(jobId)) {
            return false; // Not paused
        }

        try {
            pausedReasons.remove(jobId);

            logger.log(jobId,
                      "Repair resumed",
                      "INFO",
                      "EXECUTION_RESUME");

            return true;
        } catch (Exception e) {
            logger.log(jobId,
                      "Error resuming repair: " + e.getMessage(),
                      "ERROR",
                      "EXECUTION_RESUME");
            return false;
        }
    }

    @Override
    public void completeRepair(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            throw new IllegalArgumentException("Job ID cannot be null");
        }

        Optional<RepairJob> jobOptional = repository.findRepairJobById(jobId);
        if (jobOptional.isEmpty()) {
            throw new IllegalArgumentException("Job not found: " + jobId);
        }

        RepairJob job = jobOptional.get();

        if (job.getStatus() != RepairStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only in-progress jobs can be completed");
        }

        try {
            // Complete the job
            job.completeRepair();

            // Update status in tracker
            statusTracker.updateStatus(jobId, RepairStatus.COMPLETED);

            // Save to repository
            repository.updateRepairJob(job);

            // Update request status
            job.getRepairRequest().markCompleted();
            repository.updateRepairRequest(job.getRepairRequest());

            // Set progress to 100%
            jobProgress.put(jobId, 100);

            // Clean up
            pausedReasons.remove(jobId);

            // Log completion
            logger.log(jobId,
                      "Repair completed successfully",
                      "INFO",
                      "EXECUTION_COMPLETE");

        } catch (Exception e) {
            logger.log(jobId,
                      "Error completing repair: " + e.getMessage(),
                      "ERROR",
                      "EXECUTION_COMPLETE");
            throw e;
        }
    }

    @Override
    public void failRepair(String jobId, String reason) {
        if (jobId == null || jobId.isBlank()) {
            throw new IllegalArgumentException("Job ID cannot be null");
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Failure reason cannot be empty");
        }

        Optional<RepairJob> jobOptional = repository.findRepairJobById(jobId);
        if (jobOptional.isEmpty()) {
            throw new IllegalArgumentException("Job not found: " + jobId);
        }

        RepairJob job = jobOptional.get();

        try {
            // Fail the job
            job.failRepair(reason);

            // Update status in tracker
            statusTracker.updateStatus(jobId, RepairStatus.FAILED);

            // Save to repository
            repository.updateRepairJob(job);

            // Clean up
            pausedReasons.remove(jobId);
            jobProgress.remove(jobId);

            // Log failure
            logger.log(jobId,
                      "Repair failed - Reason: " + reason,
                      "ERROR",
                      "EXECUTION_FAIL");

        } catch (Exception e) {
            logger.log(jobId,
                      "Error failing repair: " + e.getMessage(),
                      "CRITICAL",
                      "EXECUTION_FAIL");
            throw e;
        }
    }

    @Override
    public void updateProgress(String jobId, int progressPercentage) {
        if (jobId == null || jobId.isBlank()) {
            return;
        }

        if (progressPercentage < 0 || progressPercentage > 100) {
            throw new IllegalArgumentException("Progress must be between 0 and 100");
        }

        Optional<RepairJob> jobOptional = repository.findRepairJobById(jobId);
        if (jobOptional.isEmpty()) {
            return;
        }

        RepairJob job = jobOptional.get();

        if (job.getStatus() != RepairStatus.IN_PROGRESS) {
            return;
        }

        int previousProgress = jobProgress.getOrDefault(jobId, 0);
        jobProgress.put(jobId, progressPercentage);

        // Log significant progress changes
        if (progressPercentage % 25 == 0 && progressPercentage != previousProgress) {
            logger.log(jobId,
                      "Progress: " + progressPercentage + "%",
                      "INFO",
                      "PROGRESS_UPDATE");
        }
    }

    /**
     * Get current progress of a repair
     */
    public int getJobProgress(String jobId) {
        return jobProgress.getOrDefault(jobId, 0);
    }

    /**
     * Check if job is paused
     */
    public boolean isJobPaused(String jobId) {
        return pausedReasons.containsKey(jobId);
    }

    /**
     * Get pause reason if job is paused
     */
    public Optional<String> getPauseReason(String jobId) {
        return Optional.ofNullable(pausedReasons.get(jobId));
    }
}
