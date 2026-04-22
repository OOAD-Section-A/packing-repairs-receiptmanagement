package com.repairs.interfaces.model;

import com.repairs.enums.RepairStatus;

/**
 * IStatusObserver - Observer interface for repair status changes.
 * Implements Observer pattern (Gang of Four).
 */
public interface IStatusObserver {
    
    /**
     * Called when a repair job's status changes
     * @param jobId The job ID that changed status
     * @param oldStatus The previous status
     * @param newStatus The new status
     */
    void onStatusChanged(String jobId, RepairStatus oldStatus, RepairStatus newStatus);

    /**
     * Get the observer's unique identifier
     * @return Observer ID
     */
    String getObserverId();
}
