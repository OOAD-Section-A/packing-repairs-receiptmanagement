package com.repairs.interfaces.model;

import com.repairs.entities.RepairRequest;
import java.util.List;

/**
 * IRepairValidator - Interface for validating repair requests.
 * Implements ISP (Interface Segregation) - clients only depend on validation methods.
 */
public interface IRepairValidator {
    
    /**
     * Validate a repair request for eligibility and correctness
     * @param request The repair request to validate
     * @return true if valid, false otherwise
     */
    boolean validate(RepairRequest request);

    /**
     * Get validation error messages if validation fails
     * @return List of validation error messages
     */
    List<String> getValidationErrors();

    /**
     * Check if a customer is eligible for repairs
     * @param customerId The customer ID
     * @return true if eligible
     */
    boolean isCustomerEligible(String customerId);

    /**
     * Check if a repair type is supported
     * @param repairType The repair type name
     * @return true if supported
     */
    boolean isRepairTypeSupported(String repairType);
}
