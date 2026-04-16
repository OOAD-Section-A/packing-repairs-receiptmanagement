package com.repairs.entities;

import com.repairs.enums.RepairStatus;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * RepairJob entity - represents the execution plan for a RepairRequest.
 * Links request to technician, parts, and execution timeline.
 */
public class RepairJob {
    private final String jobId;
    private final RepairRequest repairRequest;
    private final LocalDateTime createdDate;
    
    private String assignedTechnician;
    private Duration estimatedDuration;
    private Duration actualDuration;
    private List<SparePart> usedParts;
    private RepairStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public RepairJob(String jobId, RepairRequest repairRequest) {
        this.jobId = jobId;
        this.repairRequest = repairRequest;
        this.createdDate = LocalDateTime.now();
        this.usedParts = new ArrayList<>();
        this.status = RepairStatus.SCHEDULED;
    }

    // ============ Getters ============
    public String getJobId() {
        return jobId;
    }

    public RepairRequest getRepairRequest() {
        return repairRequest;
    }

    public String getAssignedTechnician() {
        return assignedTechnician;
    }

    public Duration getEstimatedDuration() {
        return estimatedDuration;
    }

    public Duration getActualDuration() {
        return actualDuration;
    }

    public List<SparePart> getUsedParts() {
        return Collections.unmodifiableList(usedParts);
    }

    public RepairStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    // ============ Business Methods ============
    
    /**
     * Assign a technician to this job
     */
    public void assignTechnician(String technicianId) {
        if (technicianId == null || technicianId.isBlank()) {
            throw new IllegalArgumentException("Technician ID cannot be null or empty");
        }
        this.assignedTechnician = technicianId;
    }

    /**
     * Add a spare part used in this repair
     */
    public void addSparePart(SparePart part) {
        if (part == null) {
            throw new IllegalArgumentException("Spare part cannot be null");
        }
        // Check for duplicate parts and update quantity if exists
        for (SparePart existingPart : usedParts) {
            if (existingPart.getPartId().equals(part.getPartId())) {
                existingPart.increaseQuantity(part.getQuantity());
                return;
            }
        }
        usedParts.add(part);
    }

    /**
     * Set estimated duration for the repair
     */
    public void setEstimatedDuration(Duration duration) {
        if (duration == null || duration.isNegative()) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        this.estimatedDuration = duration;
    }

    /**
     * Start the repair job
     */
    public void startRepair() {
        if (status != RepairStatus.SCHEDULED) {
            throw new IllegalStateException("Only scheduled jobs can be started");
        }
        if (assignedTechnician == null) {
            throw new IllegalStateException("Technician must be assigned before starting");
        }
        this.status = RepairStatus.IN_PROGRESS;
        this.startDate = LocalDateTime.now();
    }

    /**
     * Complete the repair job
     */
    public void completeRepair() {
        if (status != RepairStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only in-progress jobs can be completed");
        }
        this.status = RepairStatus.COMPLETED;
        this.endDate = LocalDateTime.now();
        calculateActualDuration();
    }

    /**
     * Pause the repair job
     */
    public void pauseRepair() {
        if (status != RepairStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only in-progress jobs can be paused");
        }
        this.status = RepairStatus.PAUSED;
    }

    /**
     * Resume the repair job
     */
    public void resumeRepair() {
        if (status != RepairStatus.PAUSED) {
            throw new IllegalStateException("Only paused jobs can be resumed");
        }
        this.status = RepairStatus.IN_PROGRESS;
    }

    /**
     * Mark job as failed
     */
    public void failRepair(String reason) {
        if (status == RepairStatus.COMPLETED || status == RepairStatus.FAILED) {
            throw new IllegalStateException("Cannot fail a completed or already failed job");
        }
        this.status = RepairStatus.FAILED;
        this.endDate = LocalDateTime.now();
        if (startDate != null) {
            calculateActualDuration();
        }
    }

    /**
     * Calculate actual duration based on start and end dates
     */
    private void calculateActualDuration() {
        if (startDate != null && endDate != null) {
            this.actualDuration = Duration.between(startDate, endDate);
        }
    }

    /**
     * Calculate total parts cost for this job
     */
    public java.math.BigDecimal getTotalPartsCost() {
        return usedParts.stream()
                .map(SparePart::getTotalCost)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    /**
     * Check if job is overdue (estimated time exceeded)
     */
    public boolean isOverdue() {
        if (startDate == null || estimatedDuration == null) {
            return false;
        }
        LocalDateTime estimatedEndTime = startDate.plus(estimatedDuration);
        return LocalDateTime.now().isAfter(estimatedEndTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RepairJob)) return false;
        RepairJob repairJob = (RepairJob) o;
        return Objects.equals(jobId, repairJob.jobId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId);
    }

    @Override
    public String toString() {
        return "RepairJob{" +
                "jobId='" + jobId + '\'' +
                ", status=" + status +
                ", assignedTechnician='" + assignedTechnician + '\'' +
                ", estimatedDuration=" + estimatedDuration +
                '}';
    }
}
