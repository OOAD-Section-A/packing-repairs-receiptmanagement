package com.receiptmanagement.domain.model;

/**
 * Enum for expense categorization.
 */
public enum ExpenseCategory {
    OFFICE_SUPPLIES("Office Supplies"),
    TRAVEL("Travel"),
    MEALS("Meals & Entertainment"),
    UTILITIES("Utilities"),
    SOFTWARE("Software & Licenses"),
    EQUIPMENT("Equipment"),
    MAINTENANCE("Maintenance & Repairs"),
    CONSULTING("Consulting Services"),
    MARKETING("Marketing"),
    OTHER("Other");

    private final String displayName;

    ExpenseCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ExpenseCategory categorize(String receiptVendor, String receiptDescription) {
        if (receiptVendor == null || receiptDescription == null) {
            return OTHER;
        }

        String vendor = receiptVendor.toLowerCase();
        String description = receiptDescription.toLowerCase();

        if (vendor.contains("hotel") || vendor.contains("airline") || vendor.contains("uber") || vendor.contains("taxi") || vendor.contains("train")) {
            return TRAVEL;
        }
        if (vendor.contains("restaurant") || vendor.contains("cafe") || vendor.contains("pizza") || vendor.contains("burger")) {
            return MEALS;
        }
        if (vendor.contains("power") || vendor.contains("water") || vendor.contains("gas") || vendor.contains("electric")) {
            return UTILITIES;
        }
        if (vendor.contains("microsoft") || vendor.contains("adobe") || vendor.contains("salesforce") || vendor.contains("software") || vendor.contains("aws") || vendor.contains("github")) {
            return SOFTWARE;
        }
        if (vendor.contains("apple") || vendor.contains("dell") || vendor.contains("hp") || vendor.contains("computer") || vendor.contains("monitor")) {
            return EQUIPMENT;
        }
        if (vendor.contains("plumber") || vendor.contains("electrician") || vendor.contains("repair") || vendor.contains("maintenance")) {
            return MAINTENANCE;
        }
        if (vendor.contains("consultant") || vendor.contains("consulting") || vendor.contains("analyst")) {
            return CONSULTING;
        }
        if (vendor.contains("ads") || vendor.contains("marketing") || vendor.contains("advertising") || vendor.contains("social")) {
            return MARKETING;
        }
        if (vendor.contains("pen") || vendor.contains("paper") || vendor.contains("office") || vendor.contains("staples")) {
            return OFFICE_SUPPLIES;
        }

        return OTHER;
    }
}
