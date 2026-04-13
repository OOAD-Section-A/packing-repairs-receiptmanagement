package com.repairs.views;

import com.repairs.enums.RepairStatus;
import com.repairs.interfaces.view.IRepairExecutionView;
import java.util.List;

/**
 * ConsoleRepairExecutionView - Console-based implementation of IRepairExecutionView.
 * PASSIVE view - displays execution status only, no business logic.
 */
public class ConsoleRepairExecutionView implements IRepairExecutionView {
    private boolean visible;
    private int currentProgress;

    public ConsoleRepairExecutionView() {
        this.visible = false;
        this.currentProgress = 0;
    }

    @Override
    public void displayJobProgress(String jobId, int progress) {
        this.currentProgress = progress;
        System.out.println("\n📊 Job: " + jobId + " | Progress: " + progress + "%");
        displayProgressBar(progress);
    }

    @Override
    public void showExecutionStatus(RepairStatus status) {
        System.out.println("\n📋 REPAIR STATUS: " + status);
    }

    @Override
    public void displayError(String message) {
        System.out.println("\n❌ ERROR: " + message);
    }

    @Override
    public void displayWarning(String message) {
        System.out.println("\n⚠️  WARNING: " + message);
    }

    @Override
    public void displaySuccess(String message) {
        System.out.println("\n✓ SUCCESS: " + message);
    }

    @Override
    public void displayLogs(List<String> logs) {
        System.out.println("\n📝 REPAIR LOGS:");
        if (logs.isEmpty()) {
            System.out.println("  (No logs available)");
        } else {
            for (String log : logs) {
                System.out.println("  " + log);
            }
        }
    }

    @Override
    public void addLogMessage(String logMessage) {
        System.out.println("  📌 " + logMessage);
    }

    @Override
    public void displayTechnician(String technicianId, String technicianName) {
        System.out.println("\n👨‍🔧 " + technicianName);
    }

    @Override
    public void displayTimeRemaining(long minutesRemaining) {
        long hours = minutesRemaining / 60;
        long mins = minutesRemaining % 60;
        System.out.println("⏱️  Estimated Time Remaining: " + hours + "h " + mins + "m");
    }

    @Override
    public void setStartButtonEnabled(boolean enabled) {
        System.out.println("START button: " + (enabled ? "✓ ENABLED" : "✗ DISABLED"));
    }

    @Override
    public void setPauseButtonEnabled(boolean enabled) {
        System.out.println("PAUSE button: " + (enabled ? "✓ ENABLED" : "✗ DISABLED"));
    }

    @Override
    public void setCompleteButtonEnabled(boolean enabled) {
        System.out.println("COMPLETE button: " + (enabled ? "✓ ENABLED" : "✗ DISABLED"));
    }

    @Override
    public void setFailButtonEnabled(boolean enabled) {
        System.out.println("FAIL button: " + (enabled ? "✓ ENABLED" : "✗ DISABLED"));
    }

    @Override
    public void clearDisplay() {
        System.out.println("\n[Display cleared]");
    }

    @Override
    public void showLoadingIndicator() {
        System.out.println("⏳ Loading...");
    }

    @Override
    public void hideLoadingIndicator() {
        // Console doesn't hide indicators
    }

    @Override
    public void refresh() {
        System.out.println("\n[Refreshing display...]");
    }

    private void displayProgressBar(int progress) {
        StringBuilder bar = new StringBuilder("[");
        int filled = (progress / 5); // 20 chars = 5% each
        for (int i = 0; i < 20; i++) {
            if (i < filled) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        bar.append("] ").append(progress).append("%");
        System.out.println(bar.toString());
    }
}
