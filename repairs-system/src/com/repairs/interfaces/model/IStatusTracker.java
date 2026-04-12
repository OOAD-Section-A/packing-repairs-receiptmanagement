package com.repairs.interfaces.model;

import com.repairs.enums.RepairStatus;
import java.util.Optional;

/**
 * IStatusTracker - Interface for tracking repair status.
 * Implements Observer pattern for status change notifications.
 */
public interface IStatusTracker {
    
    /**
     * Update status of a repair job
     * @param jobId The job ID
     * @param newStatus The new status
     */
    void updateStatus(String jobId, RepairStatus newStatus);

    /**
     * Get current status of a repair job
     * @param jobId The job ID
     * @return The current status
     */
    Optional<RepairStatus> getStatus(String jobId);

    /**
     * Register an observer for status change notifications
     * @param observer The observer to register
     */
    void registerObserver(IStatusObserver observer);

    /**
     * Remove an observer from status notifications
     * @param observer The observer to remove
     */
    void removeObserver(IStatusObserver observer);

    /**
     * Notify all observers of a status change
     * @param jobId The job ID that changed
     * @param oldStatus The previous status
     * @param newStatus The new status
     */
    void notifyStatusChange(String jobId, RepairStatus oldStatus, RepairStatus newStatus);

    /**
     * Get the timestamp of the last status update
     * @param jobId The job ID
     * @return The last update time
     */
    Optional<java.time.LocalDateTime> getLastStatusUpdateTime(String jobId);
}
