package com.repairs.interfaces.model;

import com.repairs.entities.RepairJob;
import com.repairs.entities.RepairRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * IRepairScheduler - Interface for scheduling repair jobs.
 */
public interface IRepairScheduler {
    
    /**
     * Schedule a repair request for execution
     * @param request The repair request to schedule
     * @return The scheduled date/time
     */
    LocalDateTime scheduleRepair(RepairRequest request);

    /**
     * Reschedule an existing repair job
     * @param jobId The job ID to reschedule
     * @param newDate The new scheduled date
     * @return true if rescheduling successful
     */
    boolean rescheduleRepair(String jobId, LocalDateTime newDate);

    /**
     * Get all currently scheduled jobs
     * @return List of scheduled repair jobs
     */
    List<RepairJob> getScheduledJobs();

    /**
     * Get jobs scheduled for a specific date
     * @param date The date to query
     * @return List of jobs scheduled for that date
     */
    List<RepairJob> getJobsScheduledForDate(LocalDateTime date);

    /**
     * Find an available time slot for scheduling
     * @param preferredDate The preferred date
     * @return An available schedule time
     */
    Optional<LocalDateTime> findAvailableSlot(LocalDateTime preferredDate);

    /**
     * Cancel a scheduled repair
     * @param jobId The job to cancel
     * @return true if cancellation successful
     */
    boolean cancelScheduledRepair(String jobId);
}
