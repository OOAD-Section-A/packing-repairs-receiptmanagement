package com.repairs.interfaces.model;

import com.repairs.entities.SparePart;
import java.util.List;
import java.util.Optional;

/**
 * IInventoryConnector - Interface for integrating with external Inventory Management System.
 * Implements Adapter pattern to abstract external API calls.
 */
public interface IInventoryConnector {
    
    /**
     * Check if spare part is available in inventory
     * @param partId The part ID
     * @param quantity The required quantity
     * @return true if available
     */
    boolean checkAvailability(String partId, int quantity);

    /**
     * Reserve spare parts from inventory
     * @param partId The part ID
     * @param quantity The quantity to reserve
     * @return true if reservation successful
     */
    boolean reservePart(String partId, int quantity);

    /**
     * Release reserved parts back to inventory
     * @param partId The part ID
     * @param quantity The quantity to release
     * @return true if release successful
     */
    boolean releasePart(String partId, int quantity);

    /**
     * Update inventory quantity after consumption
     * @param partId The part ID
     * @param quantityUsed The quantity consumed
     * @return true if update successful
     */
    boolean updateInventoryAfterUse(String partId, int quantityUsed);

    /**
     * Get current stock level for a part
     * @param partId The part ID
     * @return Current quantity in stock
     */
    Optional<Integer> getStockLevel(String partId);

    /**
     * Get spare part details
     * @param partId The part ID
     * @return The spare part details if found
     */
    Optional<SparePart> getPartDetails(String partId);

    /**
     * Get list of low-stock parts
     * @param threshold The quantity threshold
     * @return List of parts below threshold
     */
    List<SparePart> getLowStockParts(int threshold);

    /**
     * Check if inventory system is connected
     * @return true if connected and available
     */
    boolean isConnected();
}
