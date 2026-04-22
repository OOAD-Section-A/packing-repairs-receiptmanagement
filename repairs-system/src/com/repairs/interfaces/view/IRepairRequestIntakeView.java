package com.repairs.interfaces.view;

import com.repairs.entities.RepairRequest;
import java.util.List;

/**
 * IRepairRequestIntakeView - View interface for repair request intake form.
 * This is a PASSIVE view - contains NO business logic.
 * Only responsible for displaying data and capturing user input.
 */
public interface IRepairRequestIntakeView {
    
    /**
     * Display the repair request intake form to user
     */
    void showRepairRequestForm();

    /**
     * Display validation errors to user
     * @param errors List of error messages
     */
    void displayValidationErrors(List<String> errors);

    /**
     * Display successful validation message
     */
    void displayValidationSuccess();

    /**
     * Display scheduled date to user
     * @param scheduledDate The scheduled date as string
     */
    void displayScheduledDate(String scheduledDate);

    /**
     * Clear the form inputs
     */
    void clearForm();

    /**
     * Show a loading indicator
     * @param message The loading message
     */
    void showLoadingIndicator(String message);

    /**
     * Hide the loading indicator
     */
    void hideLoadingIndicator();

    /**
     * Display an error message
     * @param errorMessage The error message to display
     */
    void displayError(String errorMessage);

    /**
     * Display a success message
     * @param successMessage The success message
     */
    void displaySuccess(String successMessage);

    /**
     * Get the repair request data entered by user
     * @return The repair request entered
     */
    RepairRequest getRepairRequestInput();

    /**
     * Enable/disable the submit button
     * @param enabled true to enable, false to disable
     */
    void setSubmitButtonEnabled(boolean enabled);

    /**
     * Request focus on form (for accessibility)
     */
    void requestFocus();

    /**
     * Check if view is visible
     * @return true if visible
     */
    boolean isVisible();

    /**
     * Show confirmation dialog
     * @param message The confirmation message
     * @return true if user confirmed, false otherwise
     */
    boolean showConfirmDialog(String message);
}
