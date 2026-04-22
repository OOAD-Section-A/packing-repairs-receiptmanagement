package com.repairs.interfaces.model;

import com.repairs.entities.CostEstimate;
import com.repairs.entities.RepairJob;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * ICostEstimator - Interface for estimating repair costs.
 */
public interface ICostEstimator {
    
    /**
     * Estimate cost for a repair job
     * @param job The repair job to estimate
     * @return The cost estimate
     */
    CostEstimate estimateCost(RepairJob job);

    /**
     * Recalculate existing estimate (when parts or labor changes)
     * @param estimateId The estimate ID to recalculate
     * @return The updated cost estimate
     */
    Optional<CostEstimate> recalculateEstimate(String estimateId);

    /**
     * Apply a discount to a cost estimate
     * @param estimateId The estimate ID
     * @param discountPercent The discount percentage (0-100)
     * @return true if discount applied successfully
     */
    boolean applyDiscount(String estimateId, BigDecimal discountPercent);

    /**
     * Validate if estimate is still valid for the job
     * @param estimateId The estimate ID
     * @return true if estimate is current and valid
     */
    boolean isEstimateValid(String estimateId);
}
