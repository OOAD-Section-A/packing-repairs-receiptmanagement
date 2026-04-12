package com.repairs.enums;

import java.math.BigDecimal;

/**
 * Represents different types of repairs with their associated base labor costs.
 * Can be extended with new types without modifying existing code (OCP).
 */
public enum RepairType {
    MECHANICAL("Mechanical repair", new BigDecimal("50.00")),
    ELECTRICAL("Electrical repair", new BigDecimal("75.00")),
    PLUMBING("Plumbing repair", new BigDecimal("60.00")),
    STRUCTURAL("Structural repair", new BigDecimal("100.00")),
    OTHER("Other repair", new BigDecimal("40.00"));

    private final String displayName;
    private final BigDecimal baseLaborCost;

    RepairType(String displayName, BigDecimal baseLaborCost) {
        this.displayName = displayName;
        this.baseLaborCost = baseLaborCost;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getBaseLaborCost() {
        return baseLaborCost;
    }

    /**
     * Get hourly rate for this repair type
     */
    public BigDecimal getHourlyRate() {
        return baseLaborCost;
    }
}
