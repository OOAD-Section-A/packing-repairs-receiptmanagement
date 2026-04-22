package com.repairs.presenters;

import com.repairs.entities.RepairRequest;
import com.repairs.enums.RepairStatus;
import com.repairs.interfaces.model.*;
import com.repairs.interfaces.view.IRepairRequestIntakeView;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * RepairRequestPresenter - MVP Presenter for repair request intake.
 * Controls all logic flow between View and Model.
 * View is completely passive - no business logic.
 */
public class RepairRequestPresenter {
    private final IRepairRequestIntakeView view;
    private final IRepairValidator validator;
    private final IRepairScheduler scheduler;
    private final IStatusTracker statusTracker;
    private final IRepairRepository repository;
    private final IRepairLogger logger;

    public RepairRequestPresenter(IRepairRequestIntakeView view,
                                 IRepairValidator validator,
                                 IRepairScheduler scheduler,
                                 IStatusTracker statusTracker,
                                 IRepairRepository repository,
                                 IRepairLogger logger) {
        this.view = Objects.requireNonNull(view, "View cannot be null");
        this.validator = Objects.requireNonNull(validator, "Validator cannot be null");
        this.scheduler = Objects.requireNonNull(scheduler, "Scheduler cannot be null");
        this.statusTracker = Objects.requireNonNull(statusTracker, "Status tracker cannot be null");
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
        this.logger = Objects.requireNonNull(logger, "Logger cannot be null");
    }

    /**
     * Handle repair request submission from view
     * Flow: Get Input → Validate → Save → Schedule → Update Status
     */
    public void onRepairRequestSubmitted() {
        try {
            view.showLoadingIndicator("Processing repair request...");

            // Step 1: Get input from view (view is passive - only provides data)
            RepairRequest request = view.getRepairRequestInput();

            // Step 2: Validate request
            if (!validator.validate(request)) {
                List<String> errors = validator.getValidationErrors();
                view.hideLoadingIndicator();
                view.displayValidationErrors(errors);
                logger.log(request.getRequestId(),
                          "Validation failed with " + errors.size() + " errors",
                          "WARNING",
                          "SUBMISSION");
                return;
            }

            // Step 3: Save request to repository
            boolean saved = repository.saveRepairRequest(request);
            if (!saved) {
                view.hideLoadingIndicator();
                view.displayError("Failed to save repair request. Please try again.");
                logger.log(request.getRequestId(),
                          "Failed to save repair request",
                          "ERROR",
                          "SUBMISSION");
                return;
            }

            // Step 4: Update status to VALIDATED
            request.updateStatus(RepairStatus.VALIDATED);
            repository.updateRepairRequest(request);

            // Step 5: Schedule the repair
            LocalDateTime scheduledDate = scheduler.scheduleRepair(request);

            // Step 6: Update view with results
            view.hideLoadingIndicator();
            view.displayValidationSuccess();
            view.displayScheduledDate(scheduledDate.toString());
            view.clearForm();

            // Log successful submission
            logger.log(request.getRequestId(),
                      "Repair request submitted and scheduled for " + scheduledDate,
                      "INFO",
                      "SUBMISSION");

        } catch (Exception e) {
            view.hideLoadingIndicator();
            view.displayError("Error processing request: " + e.getMessage());
            logger.log("UNKNOWN",
                      "Error in repair request submission: " + e.getMessage(),
                      "ERROR",
                      "SUBMISSION");
        }
    }

    /**
     * Handle validation request from view
     */
    public void onValidationRequested(String requestId) {
        try {
            view.showLoadingIndicator("Validating request...");

            var requestOptional = repository.findRepairRequestById(requestId);
            if (requestOptional.isEmpty()) {
                view.hideLoadingIndicator();
                view.displayError("Request not found");
                return;
            }

            RepairRequest request = requestOptional.get();

            if (validator.validate(request)) {
                view.hideLoadingIndicator();
                view.displayValidationSuccess();
            } else {
                view.hideLoadingIndicator();
                view.displayValidationErrors(validator.getValidationErrors());
            }

        } catch (Exception e) {
            view.hideLoadingIndicator();
            view.displayError("Error validating request: " + e.getMessage());
        }
    }

    /**
     * Handle scheduling request from view
     */
    public void onSchedulingRequested(String requestId) {
        try {
            view.showLoadingIndicator("Scheduling repair...");

            var requestOptional = repository.findRepairRequestById(requestId);
            if (requestOptional.isEmpty()) {
                view.hideLoadingIndicator();
                view.displayError("Request not found");
                return;
            }

            RepairRequest request = requestOptional.get();

            // Check if already validated
            if (request.getStatus() != RepairStatus.VALIDATED) {
                // Try to validate first
                if (!validator.validate(request)) {
                    view.hideLoadingIndicator();
                    view.displayValidationErrors(validator.getValidationErrors());
                    return;
                }
                request.updateStatus(RepairStatus.VALIDATED);
                repository.updateRepairRequest(request);
                statusTracker.updateStatus(requestId, RepairStatus.VALIDATED);
            }

            // Schedule the repair
            LocalDateTime scheduledDate = scheduler.scheduleRepair(request);

            view.hideLoadingIndicator();
            view.displayScheduledDate(scheduledDate.toString());

            logger.log(requestId,
                      "Repair scheduled for " + scheduledDate,
                      "INFO",
                      "SCHEDULING");

        } catch (Exception e) {
            view.hideLoadingIndicator();
            view.displayError("Error scheduling repair: " + e.getMessage());
            logger.log(requestId,
                      "Error scheduling: " + e.getMessage(),
                      "ERROR",
                      "SCHEDULING");
        }
    }

    /**
     * Display request status
     */
    public void displayRequestStatus(String requestId) {
        try {
            var requestOptional = repository.findRepairRequestById(requestId);
            if (requestOptional.isEmpty()) {
                view.displayError("Request not found");
                return;
            }

            RepairRequest request = requestOptional.get();
            String statusMessage = String.format("Request %s is in status: %s. Created: %s",
                    request.getRequestId(),
                    request.getStatus(),
                    request.getCreatedDate());

            view.displaySuccess(statusMessage);

        } catch (Exception e) {
            view.displayError("Error retrieving request status: " + e.getMessage());
        }
    }

    /**
     * Show repair form in view
     */
    public void showRepairForm() {
        view.showRepairRequestForm();
    }

    /**
     * Check eligibility and provide customer feedback
     */
    public void checkCustomerEligibility(String customerId) {
        try {
            if (validator.isCustomerEligible(customerId)) {
                view.displaySuccess("Customer is eligible for repairs");
            } else {
                view.displayError("Customer is not eligible for repairs");
            }
        } catch (Exception e) {
            view.displayError("Error checking eligibility: " + e.getMessage());
        }
    }

    /**
     * Get all pending requests
     */
    public void displayPendingRequests() {
        try {
            List<RepairRequest> pending = repository.findRepairRequestsByStatus(RepairStatus.REQUESTED);

            if (pending.isEmpty()) {
                view.displaySuccess("No pending repair requests");
            } else {
                StringBuilder sb = new StringBuilder("Pending Requests:\n");
                for (RepairRequest req : pending) {
                    sb.append("- ").append(req.getRequestId()).append(": ")
                      .append(req.getRepairType()).append("\n");
                }
                view.displaySuccess(sb.toString());
            }

        } catch (Exception e) {
            view.displayError("Error retrieving pending requests: " + e.getMessage());
        }
    }
}
