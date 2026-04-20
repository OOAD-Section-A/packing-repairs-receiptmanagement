package com.receiptmanagement.domain.model;

import java.util.Objects;

public final class CustomerInformation {

    private final String customerId;
    private final String fullName;
    private final String email;

    public CustomerInformation(String customerId, String fullName, String email) {
        this.customerId = requireText(customerId, "customerId");
        this.fullName = requireText(fullName, "fullName");
        this.email = requireText(email, "email");
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " cannot be null");
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return normalized;
    }
}

