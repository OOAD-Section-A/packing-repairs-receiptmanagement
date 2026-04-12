package com.repairs.services;

import com.repairs.entities.RepairJob;
import com.repairs.entities.RepairRequest;
import com.repairs.enums.RepairStatus;
import com.repairs.interfaces.model.IRepairLogger;
import com.repairs.interfaces.model.IRepairRepository;
import com.repairs.interfaces.model.IRepairScheduler;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * RepairScheduler - Concrete implementation of IRepairScheduler.
 * Handles scheduling repair jobs and finding available time slots.
 */
public class RepairScheduler implements IRepairScheduler {
    private final IRepairRepository repository;
    private final IRepairLogger logger;
    private final Map<String, LocalDateTime> scheduledSlots; // Track booked slots
    private final int SLOT_DURATION_MINUTES = 120; // 2-hour slots
    private final int WORKING_HOURS_START = 8;
    private final int WORKING_HOURS_END = 18;

    public RepairScheduler(IRepairRepository repository, IRepairLogger logger) {
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
        this.logger = Objects.requireNonNull(logger, "Logger cannot be null");
        this.scheduledSlots = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public LocalDateTime scheduleRepair(RepairRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (request.getStatus() != RepairStatus.VALIDATED) {
            throw new IllegalStateException("Only validated requests can be scheduled");
        }

        // Find first available slot starting from tomorrow
        LocalDateTime startDate = LocalDateTime.now()
                .truncatedTo(ChronoUnit.DAYS)
                .plusDays(1)
                .withHour(WORKING_HOURS_START);

        Optional<LocalDateTime> availableSlot = findAvailableSlot(startDate);

        if (availableSlot.isEmpty()) {
            // Try next week if no availability this week
            startDate = startDate.plusWeeks(1);
            availableSlot = findAvailableSlot(startDate);
        }

        if (availableSlot.isEmpty()) {
            throw new RuntimeException("No available scheduling slots found");
        }

        LocalDateTime scheduledDate = availableSlot.get();

        // Create repair job
        String jobId = generateJobId(request.getRequestId());
        RepairJob job = new RepairJob(jobId, request);

        // Schedule the job
        request.scheduleForDate(scheduledDate);
        request.updateStatus(RepairStatus.SCHEDULED);

        // Update repository
        repository.updateRepairRequest(request);
        repository.saveRepairJob(job);

        // Record the scheduled slot
        scheduledSlots.put(jobId, scheduledDate);

        // Log the scheduling
        logger.log(jobId, 
                  "Repair scheduled for " + scheduledDate,
                  "INFO",
                  "SCHEDULING");

        return scheduledDate;
    }

    @Override
    public boolean rescheduleRepair(String jobId, LocalDateTime newDate) {
        if (jobId == null || jobId.isBlank()) {
            throw new IllegalArgumentException("Job ID cannot be null");
        }

        Optional<RepairJob> jobOptional = repository.findRepairJobById(jobId);
        if (jobOptional.isEmpty()) {
            return false;
        }

        RepairJob job = jobOptional.get();

        // Can only reschedule if not in progress or completed
        if (job.getStatus() != RepairStatus.SCHEDULED) {
            throw new IllegalStateException("Cannot reschedule job with status: " + job.getStatus());
        }

        // Check if new slot is available
        if (isSlotBooked(newDate)) {
            return false;
        }

        // Update scheduled date
        job.getRepairRequest().scheduleForDate(newDate);

        // Update repository
        repository.updateRepairJob(job);
        repository.updateRepairRequest(job.getRepairRequest());

        // Update scheduled slots
        scheduledSlots.remove(jobId);
        scheduledSlots.put(jobId, newDate);

        // Log the rescheduling
        logger.log(jobId,
                  "Repair rescheduled to " + newDate,
                  "INFO",
                  "RESCHEDULING");

        return true;
    }

    @Override
    public List<RepairJob> getScheduledJobs() {
        return repository.findRepairJobsByStatus(RepairStatus.SCHEDULED);
    }

    @Override
    public List<RepairJob> getJobsScheduledForDate(LocalDateTime date) {
        return scheduledSlots.entrySet().stream()
                .filter(entry -> entry.getValue().toLocalDate().equals(date.toLocalDate()))
                .map(entry -> repository.findRepairJobById(entry.getKey()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public Optional<LocalDateTime> findAvailableSlot(LocalDateTime preferredDate) {
        if (preferredDate == null) {
            preferredDate = LocalDateTime.now().plusDays(1);
        }

        LocalDateTime checkDate = preferredDate
                .truncatedTo(ChronoUnit.DAYS)
                .withHour(WORKING_HOURS_START);

        // Check next 14 days
        for (int day = 0; day < 14; day++) {
            for (int hour = WORKING_HOURS_START; hour < WORKING_HOURS_END; hour++) {
                LocalDateTime slotTime = checkDate.plusDays(day).withHour(hour);

                // Skip weekends
                if (isWeekend(slotTime)) {
                    continue;
                }

                // Check if slot is available
                if (!isSlotBooked(slotTime)) {
                    return Optional.of(slotTime);
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean cancelScheduledRepair(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return false;
        }

        Optional<RepairJob> jobOptional = repository.findRepairJobById(jobId);
        if (jobOptional.isEmpty()) {
            return false;
        }

        RepairJob job = jobOptional.get();

        if (job.getStatus() != RepairStatus.SCHEDULED) {
            return false;
        }

        // Update status to cancelled
        job.getRepairRequest().updateStatus(RepairStatus.CANCELLED);

        // Update repository
        repository.updateRepairRequest(job.getRepairRequest());
        repository.updateRepairJob(job);

        // Remove scheduled slot
        scheduledSlots.remove(jobId);

        // Log cancellation
        logger.log(jobId,
                  "Repair cancelled",
                  "INFO",
                  "CANCELLATION");

        return true;
    }

    // ============ Helper Methods ============

    private boolean isSlotBooked(LocalDateTime slotTime) {
        return scheduledSlots.values().stream()
                .anyMatch(bookedTime -> 
                    Math.abs(ChronoUnit.MINUTES.between(bookedTime, slotTime)) < SLOT_DURATION_MINUTES);
    }

    private boolean isWeekend(LocalDateTime dateTime) {
        java.time.DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        return dayOfWeek == java.time.DayOfWeek.SATURDAY || 
               dayOfWeek == java.time.DayOfWeek.SUNDAY;
    }

    private String generateJobId(String requestId) {
        return "JOB-" + requestId.substring(4) + "-" + System.currentTimeMillis() % 10000;
    }
}
