package com.repairs.services;

import com.repairs.entities.CostEstimate;
import com.repairs.entities.RepairJob;
import com.repairs.enums.BillingStatus;
import com.repairs.interfaces.model.ICostEstimator;
import com.repairs.interfaces.model.IInventoryConnector;
import com.repairs.interfaces.model.IRepairLogger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

/**
 * CostEstimationService - Concrete implementation of ICostEstimator.
 * Calculates labor and parts costs for repairs.
 */
public class CostEstimationService implements ICostEstimator {
    private final IInventoryConnector inventory;
    private final IRepairLogger logger;
    private final BigDecimal TAX_RATE = new BigDecimal("0.18"); // 18% tax
    private final BigDecimal OVERHEAD_MULTIPLIER = new BigDecimal("1.25"); // 25% overhead

    public CostEstimationService(IInventoryConnector inventory, IRepairLogger logger) {
        this.inventory = Objects.requireNonNull(inventory, "Inventory connector cannot be null");
        this.logger = Objects.requireNonNull(logger, "Logger cannot be null");
    }

    @Override
    public CostEstimate estimateCost(RepairJob job) {
        if (job == null) {
            throw new IllegalArgumentException("Repair job cannot be null");
        }

        try {
            // Calculate labor cost
            BigDecimal laborCost = calculateLaborCost(job);

            // Calculate parts cost
            BigDecimal partsCost = calculatePartsCost(job);

            // Calculate subtotal
            BigDecimal subtotal = laborCost.add(partsCost);

            // Calculate tax
            BigDecimal taxAmount = calculateTax(subtotal);

            // Calculate total
            BigDecimal totalCost = subtotal.add(taxAmount);

            // Generate estimate ID
            String estimateId = generateEstimateId(job.getJobId());

            // Create and return estimate
            CostEstimate estimate = new CostEstimate.Builder()
                    .estimateId(estimateId)
                    .repairJob(job)
                    .laborCost(laborCost)
                    .partsCost(partsCost)
                    .taxAmount(taxAmount)
                    .totalCost(totalCost)
                    .status(BillingStatus.ESTIMATED)
                    .notes("Cost estimate for " + job.getRepairRequest().getRepairType())
                    .build();

            // Log estimation
            logger.log(job.getJobId(),
                      "Cost estimated: Labor=" + laborCost + ", Parts=" + partsCost + 
                      ", Total=" + totalCost,
                      "INFO",
                      "COST_ESTIMATION");

            return estimate;

        } catch (Exception e) {
            logger.log(job.getJobId(),
                      "Error estimating cost: " + e.getMessage(),
                      "ERROR",
                      "COST_ESTIMATION");
            throw new RuntimeException("Failed to estimate cost: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<CostEstimate> recalculateEstimate(String estimateId) {
        // In a real implementation, we would fetch the estimate from repository
        // and recalculate based on current prices
        // For now, returning empty as we don't have estimate repository access
        return Optional.empty();
    }

    @Override
    public boolean applyDiscount(String estimateId, BigDecimal discountPercent) {
        if (estimateId == null || estimateId.isBlank()) {
            return false;
        }

        if (discountPercent.compareTo(BigDecimal.ZERO) < 0 || 
            discountPercent.compareTo(new BigDecimal("100")) > 0) {
            return false;
        }

        // In a real implementation, we would fetch and update the estimate
        return true;
    }

    @Override
    public boolean isEstimateValid(String estimateId) {
        // Check if estimate is not older than 30 days
        // In a real implementation, we would fetch from repository
        return true;
    }

    // ============ Helper Methods ============

    /**
     * Calculate labor cost based on repair type and estimated duration
     */
    private BigDecimal calculateLaborCost(RepairJob job) {
        // Get hourly rate from repair type
        BigDecimal hourlyRate = job.getRepairRequest().getRepairType().getHourlyRate();

        // Get estimated duration (default to 4 hours if not set)
        Duration estimatedDuration = job.getEstimatedDuration();
        if (estimatedDuration == null) {
            estimatedDuration = Duration.ofHours(4);
            job.setEstimatedDuration(estimatedDuration);
        }

        // Calculate hours (including partial hours)
        long minutes = estimatedDuration.toMinutes();
        BigDecimal hours = new BigDecimal(minutes).divide(
                new BigDecimal(60), 2, RoundingMode.HALF_UP);

        // Base labor cost
        BigDecimal baseCost = hourlyRate.multiply(hours);

        // Apply overhead multiplier
        BigDecimal laborCost = baseCost.multiply(OVERHEAD_MULTIPLIER);

        return laborCost.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate total parts cost from spare parts list
     */
    private BigDecimal calculatePartsCost(RepairJob job) {
        BigDecimal totalPartsCost = job.getUsedParts().stream()
                .map(part -> {
                    // Get current price from inventory if available
                    var partDetails = inventory.getPartDetails(part.getPartId());
                    if (partDetails.isPresent()) {
                        BigDecimal currentPrice = partDetails.get().getUnitPrice();
                        return currentPrice.multiply(new BigDecimal(part.getQuantity()));
                    } else {
                        return part.getTotalCost();
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalPartsCost.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate tax amount
     */
    private BigDecimal calculateTax(BigDecimal subtotal) {
        return subtotal.multiply(TAX_RATE)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Generate unique estimate ID
     */
    private String generateEstimateId(String jobId) {
        return "EST-" + jobId.substring(4) + "-" + System.currentTimeMillis() % 10000;
    }

    /**
     * Get tax rate
     */
    public BigDecimal getTaxRate() {
        return TAX_RATE;
    }

    /**
     * Set custom tax rate (for different regions)
     */
    public void setTaxRate(BigDecimal taxRate) {
        if (taxRate == null || taxRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tax rate must be positive");
        }
        // Note: In real implementation, would update the field
        // This shows how to extend the service
    }
}
