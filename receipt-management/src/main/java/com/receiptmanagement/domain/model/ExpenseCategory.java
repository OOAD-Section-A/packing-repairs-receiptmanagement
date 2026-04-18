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
        String vendor = receiptVendor == null ? "" : receiptVendor.toLowerCase();
        String description = receiptDescription == null ? "" : receiptDescription.toLowerCase();
        String text = vendor + " " + description;

        if (text.isBlank()) {
            return OTHER;
        }

        if (containsAny(text, "hotel", "airline", "uber", "taxi", "train")) {
            return TRAVEL;
        }
        if (containsAny(text, "restaurant", "cafe", "pizza", "burger")) {
            return MEALS;
        }
        if (containsAny(text, "power", "water", "gas", "electric")) {
            return UTILITIES;
        }
        if (containsAny(text, "microsoft", "adobe", "salesforce", "software", "aws", "github")) {
            return SOFTWARE;
        }
        if (containsAny(text, "apple", "dell", "hp", "computer", "monitor")) {
            return EQUIPMENT;
        }
        if (containsAny(text, "plumber", "electrician", "repair", "maintenance")) {
            return MAINTENANCE;
        }
        if (containsAny(text, "consultant", "consulting", "analyst")) {
            return CONSULTING;
        }
        if (containsAny(text, "ads", "marketing", "advertising", "social")) {
            return MARKETING;
        }
        if (containsAny(text, "pen", "paper", "office", "staples")) {
            return OFFICE_SUPPLIES;
        }

        return OTHER;
    }

    private static boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
