package com.repairs.enums;

/**
 * Represents the billing status of a repair job.
 */
public enum BillingStatus {
    PENDING("Waiting for estimation"),
    ESTIMATED("Cost estimate provided"),
    INVOICED("Invoice generated"),
    PAID("Invoice fully paid"),
    CANCELLED("Billing cancelled");

    private final String description;

    BillingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
