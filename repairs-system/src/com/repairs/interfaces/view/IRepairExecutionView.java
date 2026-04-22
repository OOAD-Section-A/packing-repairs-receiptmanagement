package com.repairs.interfaces.view;

import com.repairs.enums.RepairStatus;
import java.util.List;

/**
 * IRepairExecutionView - View interface for monitoring repair execution.
 * PASSIVE view - NO business logic, only displays data.
 */
public interface IRepairExecutionView {
    
    /**
     * Display job progress percentage
     * @param jobId The job ID
     * @param progress The progress percentage (0-100)
     */
    void displayJobProgress(String jobId, int progress);

    /**
     * Update the status display
     * @param status The current repair status
     */
    void showExecutionStatus(RepairStatus status);

    /**
     * Display error message
     * @param message The error message
     */
    void displayError(String message);

    /**
     * Display warning message
     * @param message The warning message
     */
    void displayWarning(String message);

    /**
     * Display success message
     * @param message The success message
     */
    void displaySuccess(String message);

    /**
     * Show repair logs for current job
     * @param logs The log messages to display
     */
    void displayLogs(List<String> logs);

    /**
     * Add a new log message to display
     * @param logMessage The log message to add
     */
    void addLogMessage(String logMessage);

    /**
     * Display assigned technician information
     * @param technicianId The technician ID
     * @param technicianName The technician name
     */
    void displayTechnician(String technicianId, String technicianName);

    /**
     * Display estimated time remaining
     * @param minutesRemaining Minutes until estimated completion
     */
    void displayTimeRemaining(long minutesRemaining);

    /**
     * Show start button is enabled/disabled
     * @param enabled true to enable
     */
    void setStartButtonEnabled(boolean enabled);

    /**
     * Show pause button is enabled/disabled
     * @param enabled true to enable
     */
    void setPauseButtonEnabled(boolean enabled);

    /**
     * Show complete button is enabled/disabled
     * @param enabled true to enable
     */
    void setCompleteButtonEnabled(boolean enabled);

    /**
     * Show fail button is enabled/disabled
     * @param enabled true to enable
     */
    void setFailButtonEnabled(boolean enabled);

    /**
     * Clear all displays
     */
    void clearDisplay();

    /**
     * Show loading indicator
     */
    void showLoadingIndicator();

    /**
     * Hide loading indicator
     */
    void hideLoadingIndicator();

    /**
     * Refresh the entire view
     */
    void refresh();
}
