package com.repairs.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * SparePart entity - represents a spare part with quantity and pricing.
 */
public class SparePart implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String partId;
    private final String name;
    private Integer quantity;
    private final BigDecimal unitPrice;
    private final String category;

    public SparePart(String partId, String name, Integer quantity, BigDecimal unitPrice, String category) {
        this.partId = Objects.requireNonNull(partId, "Part ID cannot be null");
        this.name = Objects.requireNonNull(name, "Part name cannot be null");
        this.quantity = Objects.requireNonNull(quantity, "Quantity cannot be null");
        this.unitPrice = Objects.requireNonNull(unitPrice, "Unit price cannot be null");
        this.category = Objects.requireNonNull(category, "Category cannot be null");

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }
    }

    // ============ Getters ============
    public String getPartId() {
        return partId;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public String getCategory() {
        return category;
    }

    // ============ Business Methods ============
    
    /**
     * Calculate total cost for this spare part (quantity × unitPrice)
     */
    public BigDecimal getTotalCost() {
        return unitPrice.multiply(new BigDecimal(quantity));
    }

    /**
     * Increase quantity of this spare part
     */
    public void increaseQuantity(Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.quantity += amount;
    }

    /**
     * Decrease quantity of this spare part
     */
    public void decreaseQuantity(Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (this.quantity < amount) {
            throw new IllegalArgumentException("Cannot decrease quantity below 0");
        }
        this.quantity -= amount;
    }

    /**
     * Set quantity (used primarily for inventory synchronization)
     */
    public void setQuantity(Integer quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SparePart)) return false;
        SparePart sparePart = (SparePart) o;
        return Objects.equals(partId, sparePart.partId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partId);
    }

    @Override
    public String toString() {
        return "SparePart{" +
                "partId='" + partId + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", category='" + category + '\'' +
                '}';
    }
}
