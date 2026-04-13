package com.repairs.controllers;

import com.repairs.entities.RepairJob;
import com.repairs.entities.RepairLog;
import com.repairs.enums.RepairStatus;
import com.repairs.interfaces.model.*;
import com.repairs.interfaces.view.IRepairExecutionView;
import java.util.List;
import java.util.Objects;

/**
 * RepairExecutionController - MVC Controller for repair execution.
 * Controls execution lifecycle and status updates.
 */
public class RepairExecutionController {
    private final IRepairExecutionView view;
    private final IRepairExecutor executor;
    private final IStatusTracker statusTracker;
    private final IRepairLogger logger;
    private final IRepairRepository repository;

    public RepairExecutionController(IRepairExecutionView view,
                                   IRepairExecutor executor,
                                   IStatusTracker statusTracker,
                                   IRepairLogger logger,
                                   IRepairRepository repository) {
        this.view = Objects.requireNonNull(view, "View cannot be null");
        this.executor = Objects.requireNonNull(executor, "Executor cannot be null");
        this.statusTracker = Objects.requireNonNull(statusTracker, "Status tracker cannot be null");
        this.logger = Objects.requireNonNull(logger, "Logger cannot be null");
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
    }

    /**
     * Start repair execution
     */
    public void onExecutionStarted(String jobId) {
        try {
            view.showLoadingIndicator();

            var jobOptional = repository.findRepairJobById(jobId);
            if (jobOptional.isEmpty()) {
                view.hideLoadingIndicator();
                view.displayError("Repair job not found: " + jobId);
                return;
            }

            RepairJob job = jobOptional.get();

            // Check if job is scheduled
            if (job.getStatus() != RepairStatus.SCHEDULED) {
                view.hideLoadingIndicator();
                view.displayError("Job must be scheduled before execution");
                return;
            }

            // Start execution
            executor.executeRepair(job);

            // Display updated information
            view.hideLoadingIndicator();
            view.showExecutionStatus(RepairStatus.IN_PROGRESS);
            view.displayTechnician(job.getAssignedTechnician(), "Technician: " + job.getAssignedTechnician());

            if (job.getEstimatedDuration() != null) {
                long minutes = job.getEstimatedDuration().toMinutes();
                view.displayTimeRemaining(minutes);
            }

            // Disable start, enable pause/complete
            view.setStartButtonEnabled(false);
            view.setPauseButtonEnabled(true);
            view.setCompleteButtonEnabled(true);
            view.setFailButtonEnabled(true);

            logger.log(jobId,
                      "Execution started",
                      "INFO",
                      "EXECUTION_START");

        } catch (Exception e) {
            view.hideLoadingIndicator();
            view.displayError("Error starting execution: " + e.getMessage());
            logger.log(jobId,
                      "Error starting execution: " + e.getMessage(),
                      "ERROR",
                      "EXECUTION_START");
        }
    }

    /**
     * Pause repair execution
     */
    public void onExecutionPaused(String jobId) {
        try {
            boolean paused = executor.pauseRepair(jobId);

            if (paused) {
                view.displayWarning("Repair paused");
                view.setPauseButtonEnabled(false);

                logger.log(jobId,
                          "Execution paused",
                          "INFO",
                          "EXECUTION_PAUSE");
            } else {
                view.displayError("Failed to pause execution");
            }

        } catch (Exception e) {
            view.displayError("Error pausing execution: " + e.getMessage());
        }
    }

    /**
     * Resume repair execution
     */
    public void onExecutionResumed(String jobId) {
        try {
            boolean resumed = executor.resumeRepair(jobId);

            if (resumed) {
                view.displaySuccess("Repair resumed");
                view.setPauseButtonEnabled(true);

                logger.log(jobId,
                          "Execution resumed",
                          "INFO",
                          "EXECUTION_RESUME");
            } else {
                view.displayError("Failed to resume execution");
            }

        } catch (Exception e) {
            view.displayError("Error resuming execution: " + e.getMessage());
        }
    }

    /**
     * Complete repair execution
     */
    public void onExecutionCompleted(String jobId) {
        try {
            view.showLoadingIndicator();

            executor.completeRepair(jobId);

            view.hideLoadingIndicator();
            view.showExecutionStatus(RepairStatus.COMPLETED);
            view.displaySuccess("Repair completed successfully");

            // Disable execution buttons
            view.setStartButtonEnabled(false);
            view.setPauseButtonEnabled(false);
            view.setCompleteButtonEnabled(false);
            view.setFailButtonEnabled(false);

            logger.log(jobId,
                      "Repair completed",
                      "INFO",
                      "EXECUTION_COMPLETE");

        } catch (Exception e) {
            view.hideLoadingIndicator();
            view.displayError("Error completing repair: " + e.getMessage());
            logger.log(jobId,
                      "Error completing repair: " + e.getMessage(),
                      "ERROR",
                      "EXECUTION_COMPLETE");
        }
    }

    /**
     * Fail repair execution
     */
    public void onExecutionFailed(String jobId, String reason) {
        try {
            view.showLoadingIndicator();

            executor.failRepair(jobId, reason);

            view.hideLoadingIndicator();
            view.showExecutionStatus(RepairStatus.FAILED);
            view.displayError("Repair failed: " + reason);

            // Disable execution buttons
            view.setStartButtonEnabled(false);
            view.setPauseButtonEnabled(false);
            view.setCompleteButtonEnabled(false);
            view.setFailButtonEnabled(false);

            logger.log(jobId,
                      "Repair failed: " + reason,
                      "ERROR",
                      "EXECUTION_FAIL");

        } catch (Exception e) {
            view.hideLoadingIndicator();
            view.displayError("Error marking repair as failed: " + e.getMessage());
        }
    }

    /**
     * Update repair progress
     */
    public void onProgressUpdated(String jobId, int progressPercentage) {
        try {
            if (progressPercentage < 0 || progressPercentage > 100) {
                view.displayWarning("Invalid progress percentage");
                return;
            }

            executor.updateProgress(jobId, progressPercentage);
            view.displayJobProgress(jobId, progressPercentage);

            if (progressPercentage % 25 == 0) {
                logger.log(jobId,
                          "Progress: " + progressPercentage + "%",
                          "INFO",
                          "PROGRESS_UPDATE");
            }

        } catch (Exception e) {
            view.displayError("Error updating progress: " + e.getMessage());
        }
    }

    /**
     * Display execution progress
     */
    public void displayExecutionProgress(String jobId) {
        try {
            var jobOptional = repository.findRepairJobById(jobId);
            if (jobOptional.isEmpty()) {
                view.displayError("Repair job not found");
                return;
            }

            RepairJob job = jobOptional.get();

            // Display current status
            view.showExecutionStatus(job.getStatus());

            // Get and display logs
            List<RepairLog> logs = repository.findLogsByJobId(jobId);
            List<String> logMessages = logs.stream()
                    .map(RepairLog::getSummary)
                    .toList();

            view.displayLogs(logMessages);

            // Display technician information
            if (job.getAssignedTechnician() != null) {
                view.displayTechnician(job.getAssignedTechnician(), 
                                      "Technician: " + job.getAssignedTechnician());
            }

        } catch (Exception e) {
            view.displayError("Error displaying progress: " + e.getMessage());
        }
    }

    /**
     * Assign technician to repair job
     */
    public void assignTechnician(String jobId, String technicianId) {
        try {
            var jobOptional = repository.findRepairJobById(jobId);
            if (jobOptional.isEmpty()) {
                view.displayError("Repair job not found");
                return;
            }

            RepairJob job = jobOptional.get();
            job.assignTechnician(technicianId);

            repository.updateRepairJob(job);

            view.displayTechnician(technicianId, "Technician assigned: " + technicianId);
            view.setStartButtonEnabled(true); // Now can start execution

            logger.log(jobId,
                      "Technician assigned: " + technicianId,
                      "INFO",
                      "ASSIGNMENT");

        } catch (Exception e) {
            view.displayError("Error assigning technician: " + e.getMessage());
        }
    }

    /**
     * Display repair details
     */
    public void displayRepairDetails(String jobId) {
        try {
            var jobOptional = repository.findRepairJobById(jobId);
            if (jobOptional.isEmpty()) {
                view.displayError("Repair job not found");
                return;
            }

            RepairJob job = jobOptional.get();

            // Build details string
            StringBuilder details = new StringBuilder();
            details.append("Job ID: ").append(jobId).append("\n");
            details.append("Status: ").append(job.getStatus()).append("\n");
            details.append("Repair Type: ").append(job.getRepairRequest().getRepairType()).append("\n");

            if (job.getAssignedTechnician() != null) {
                details.append("Technician: ").append(job.getAssignedTechnician()).append("\n");
            }

            if (job.getEstimatedDuration() != null) {
                details.append("Estimated Duration: ").append(job.getEstimatedDuration()).append("\n");
            }

            view.displaySuccess(details.toString());

        } catch (Exception e) {
            view.displayError("Error displaying repair details: " + e.getMessage());
        }
    }
}

