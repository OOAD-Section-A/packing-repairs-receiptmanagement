package com.repairs.services;

import com.repairs.entities.RepairRequest;
import com.repairs.interfaces.model.IRepairLogger;
import com.repairs.interfaces.model.IRepairRepository;
import com.repairs.interfaces.model.IRepairValidator;
import java.util.*;

/**
 * RepairValidator - Concrete implementation of IRepairValidator.
 * Implements SRP: Single responsibility is validation logic.
 */
public class RepairValidator implements IRepairValidator {
    private final IRepairRepository repository;
    private final IRepairLogger logger;
    private List<String> validationErrors;

    public RepairValidator(IRepairRepository repository, IRepairLogger logger) {
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
        this.logger = Objects.requireNonNull(logger, "Logger cannot be null");
        this.validationErrors = new ArrayList<>();
    }

    @Override
    public boolean validate(RepairRequest request) {
        validationErrors.clear();

        // Validate request is not null
        if (request == null) {
            validationErrors.add("Repair request cannot be null");
            return false;
        }

        // Validate customer ID
        if (!isCustomerEligible(request.getCustomerId())) {
            validationErrors.add("Customer ID is invalid or customer is not eligible for repairs");
        }

        // Validate repair type is supported
        if (!isRepairTypeSupported(request.getRepairType().toString())) {
            validationErrors.add("Repair type " + request.getRepairType() + " is not supported");
        }

        // Validate description
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            validationErrors.add("Repair description cannot be empty");
        }

        // Validate request ID uniqueness
        if (repository.findRepairRequestById(request.getRequestId()).isPresent()) {
            validationErrors.add("Request ID " + request.getRequestId() + " already exists");
        }

        // Log validation result
        String severity = validationErrors.isEmpty() ? "INFO" : "WARNING";
        logger.log(request.getRequestId(), 
                  "Validation: " + (validationErrors.isEmpty() ? "PASSED" : "FAILED"),
                  severity, 
                  "VALIDATION");

        return validationErrors.isEmpty();
    }

    @Override
    public List<String> getValidationErrors() {
        return Collections.unmodifiableList(validationErrors);
    }

    @Override
    public boolean isCustomerEligible(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return false;
        }

        // Check if customer ID format is valid (starts with C and has minimum length)
        if (!customerId.matches("C\\d{4,}")) {
            return false;
        }

        // Add more eligibility checks as needed
        // - Check customer payment history
        // - Check if customer has active account
        // - Check for blacklist status

        return true;
    }

    @Override
    public boolean isRepairTypeSupported(String repairType) {
        if (repairType == null || repairType.isBlank()) {
            return false;
        }

        try {
            com.repairs.enums.RepairType.valueOf(repairType.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
