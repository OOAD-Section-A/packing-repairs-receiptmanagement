package com.repairs.enums;

/**
 * Represents the lifecycle status of a repair request.
 * Follows a strict state transition flow:
 * REQUESTED → VALIDATED → SCHEDULED → IN_PROGRESS → COMPLETED
 *                              ↓
 *                         FAILED/CANCELLED (at any point)
 */
public enum RepairStatus {
    REQUESTED("Request submitted"),
    VALIDATED("Request validated and eligible"),
    SCHEDULED("Repair scheduled for execution"),
    IN_PROGRESS("Repair currently being executed"),
    COMPLETED("Repair successfully completed"),
    FAILED("Repair failed"),
    CANCELLED("Repair cancelled by user or system");

    private final String description;

    RepairStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if transition from current status to target status is valid
     */
    public boolean canTransitionTo(RepairStatus targetStatus) {
        switch (this) {
            case REQUESTED:
                return targetStatus == VALIDATED || targetStatus == CANCELLED;
            case VALIDATED:
                return targetStatus == SCHEDULED || targetStatus == CANCELLED;
            case SCHEDULED:
                return targetStatus == IN_PROGRESS || targetStatus == CANCELLED;
            case IN_PROGRESS:
                return targetStatus == COMPLETED || targetStatus == FAILED || targetStatus == CANCELLED;
            case COMPLETED:
            case FAILED:
            case CANCELLED:
                return false; // Terminal states
            default:
                return false;
        }
    }
}
