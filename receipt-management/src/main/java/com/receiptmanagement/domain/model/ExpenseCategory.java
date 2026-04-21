package com.receiptmanagement.domain.model;

/**
 * Enum for expense categorization.
 */
public enum ExpenseCategory {
    PACKAGING("Packaging"),
    REPAIR("Repair"),
    PROCUREMENT("Procurement"),
    WAREHOUSE("Warehouse"),
    LOGISTICS("Logistics"),
    INVENTORY("Inventory"),
    RETURNS("Returns"),
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

        if (containsAny(text, "package", "packaging", "packing", "box", "bundle")) {
            return PACKAGING;
        }
        if (containsAny(text, "repair", "maintenance", "damage", "defect", "fix")) {
            return REPAIR;
        }
        if (containsAny(text, "purchase", "supplier", "procurement", "invoice", "po")) {
            return PROCUREMENT;
        }
        if (containsAny(text, "warehouse", "storage", "bin", "zone")) {
            return WAREHOUSE;
        }
        if (containsAny(text, "delivery", "shipment", "logistics", "dispatch", "route")) {
            return LOGISTICS;
        }
        if (containsAny(text, "inventory", "stock", "goods", "item", "sku")) {
            return INVENTORY;
        }
        if (containsAny(text, "return", "refund", "replacement")) {
            return RETURNS;
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

    @Override
    public String toString() {
        return displayName;
    }
}
