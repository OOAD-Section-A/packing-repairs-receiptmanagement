package com.repairs.external;

import com.repairs.entities.SparePart;
import com.repairs.interfaces.model.IInventoryConnector;
import com.repairs.interfaces.model.IRepairLogger;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * InventoryConnector - Adapter for external Inventory Management System.
 * Implements Adapter pattern and caching for performance.
 */
public class InventoryConnector implements IInventoryConnector {
    private final IRepairLogger logger;
    private boolean isConnected;
    private final ExternalInventoryAPI externalAPI;
    private final Map<String, Integer> partInventoryCache; // Local cache
    private final Map<String, Long> cacheTimestamps; // Track cache freshness
    private static final long CACHE_VALIDITY_MS = 300000; // 5 minutes

    public InventoryConnector(IRepairLogger logger) {
        this.logger = Objects.requireNonNull(logger, "Logger cannot be null");
        this.externalAPI = new ExternalInventoryAPI();
        this.isConnected = true;
        this.partInventoryCache = new ConcurrentHashMap<>();
        this.cacheTimestamps = new ConcurrentHashMap<>();
    }

    @Override
    public boolean checkAvailability(String partId, int quantity) {
        if (partId == null || partId.isBlank() || quantity <= 0) {
            return false;
        }

        if (!isConnected) {
            return false;
        }

        try {
            Optional<Integer> stockLevel = getStockLevel(partId);
            return stockLevel.isPresent() && stockLevel.get() >= quantity;
        } catch (Exception e) {
            logger.log(partId,
                      "Error checking availability: " + e.getMessage(),
                      "ERROR",
                      "INVENTORY_CHECK");
            return false;
        }
    }

    @Override
    public boolean reservePart(String partId, int quantity) {
        if (partId == null || partId.isBlank() || quantity <= 0) {
            return false;
        }

        if (!isConnected) {
            logger.log(partId,
                      "Inventory system not connected",
                      "ERROR",
                      "RESERVATION");
            return false;
        }

        try {
            if (!checkAvailability(partId, quantity)) {
                logger.log(partId,
                          "Insufficient stock for reservation: " + quantity,
                          "WARNING",
                          "RESERVATION");
                return false;
            }

            // Call external API to reserve
            boolean reserved = externalAPI.reservePart(partId, quantity);

            if (reserved) {
                // Update cache
                Integer currentStock = partInventoryCache.get(partId);
                if (currentStock != null) {
                    partInventoryCache.put(partId, currentStock - quantity);
                }

                logger.log(partId,
                          "Part reserved: " + quantity,
                          "INFO",
                          "RESERVATION");
            }

            return reserved;

        } catch (Exception e) {
            logger.log(partId,
                      "Error reserving part: " + e.getMessage(),
                      "ERROR",
                      "RESERVATION");
            return false;
        }
    }

    @Override
    public boolean releasePart(String partId, int quantity) {
        if (partId == null || partId.isBlank() || quantity <= 0) {
            return false;
        }

        if (!isConnected) {
            return false;
        }

        try {
            boolean released = externalAPI.releasePart(partId, quantity);

            if (released) {
                // Update cache
                Integer currentStock = partInventoryCache.get(partId);
                if (currentStock != null) {
                    partInventoryCache.put(partId, currentStock + quantity);
                }

                logger.log(partId,
                          "Part released: " + quantity,
                          "INFO",
                          "RELEASE");
            }

            return released;

        } catch (Exception e) {
            logger.log(partId,
                      "Error releasing part: " + e.getMessage(),
                      "ERROR",
                      "RELEASE");
            return false;
        }
    }

    @Override
    public boolean updateInventoryAfterUse(String partId, int quantityUsed) {
        if (partId == null || partId.isBlank() || quantityUsed <= 0) {
            return false;
        }

        if (!isConnected) {
            return false;
        }

        try {
            boolean updated = externalAPI.updateInventory(partId, quantityUsed);

            if (updated) {
                // Update cache
                Integer currentStock = partInventoryCache.get(partId);
                if (currentStock != null && currentStock >= quantityUsed) {
                    partInventoryCache.put(partId, currentStock - quantityUsed);
                }

                logger.log(partId,
                          "Inventory updated after use: " + quantityUsed,
                          "INFO",
                          "INVENTORY_UPDATE");
            }

            return updated;

        } catch (Exception e) {
            logger.log(partId,
                      "Error updating inventory: " + e.getMessage(),
                      "ERROR",
                      "INVENTORY_UPDATE");
            return false;
        }
    }

    @Override
    public Optional<Integer> getStockLevel(String partId) {
        if (partId == null || partId.isBlank()) {
            return Optional.empty();
        }

        if (!isConnected) {
            return Optional.empty();
        }

        try {
            // Check cache validity
            Long lastUpdate = cacheTimestamps.get(partId);
            Integer cachedStock = partInventoryCache.get(partId);

            if (lastUpdate != null && 
                cachedStock != null && 
                System.currentTimeMillis() - lastUpdate < CACHE_VALIDITY_MS) {
                return Optional.of(cachedStock);
            }

            // Fetch from API
            Integer stock = externalAPI.getStockLevel(partId);
            if (stock != null) {
                partInventoryCache.put(partId, stock);
                cacheTimestamps.put(partId, System.currentTimeMillis());
                return Optional.of(stock);
            }

            return Optional.empty();

        } catch (Exception e) {
            logger.log(partId,
                      "Error fetching stock level: " + e.getMessage(),
                      "ERROR",
                      "STOCK_QUERY");
            return Optional.empty();
        }
    }

    @Override
    public Optional<SparePart> getPartDetails(String partId) {
        if (partId == null || partId.isBlank()) {
            return Optional.empty();
        }

        if (!isConnected) {
            return Optional.empty();
        }

        try {
            // This would typically fetch from external API
            // For now, returning empty
            return Optional.empty();

        } catch (Exception e) {
            logger.log(partId,
                      "Error fetching part details: " + e.getMessage(),
                      "ERROR",
                      "PART_QUERY");
            return Optional.empty();
        }
    }

    @Override
    public List<SparePart> getLowStockParts(int threshold) {
        if (!isConnected) {
            return Collections.emptyList();
        }

        try {
            return partInventoryCache.entrySet().stream()
                    .filter(entry -> entry.getValue() <= threshold)
                    .map(entry -> new SparePart(
                        entry.getKey(),
                        "Part " + entry.getKey(),
                        entry.getValue(),
                        BigDecimal.ZERO,
                        "Unknown"
                    ))
                    .toList();

        } catch (Exception e) {
            logger.log("SYSTEM",
                      "Error fetching low stock parts: " + e.getMessage(),
                      "ERROR",
                      "LOW_STOCK_QUERY");
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Manually refresh cache
     */
    public void refreshCache() {
        try {
            partInventoryCache.clear();
            cacheTimestamps.clear();

            logger.log("SYSTEM",
                      "Inventory cache refreshed",
                      "INFO",
                      "CACHE_REFRESH");

        } catch (Exception e) {
            logger.log("SYSTEM",
                      "Error refreshing cache: " + e.getMessage(),
                      "ERROR",
                      "CACHE_REFRESH");
        }
    }

    /**
     * Set connection status
     */
    public void setConnected(boolean connected) {
        this.isConnected = connected;
        String status = connected ? "connected" : "disconnected";
        logger.log("SYSTEM",
                  "Inventory system " + status,
                  "INFO",
                  "CONNECTION");
    }

    /**
     * Stub for external Inventory System API
     */
    public static class ExternalInventoryAPI {
        public boolean reservePart(String partId, int quantity) {
            // Simulate API call
            return true;
        }

        public boolean releasePart(String partId, int quantity) {
            // Simulate API call
            return true;
        }

        public boolean updateInventory(String partId, int quantityUsed) {
            // Simulate API call
            return true;
        }

        public Integer getStockLevel(String partId) {
            // Simulate API call with mock data
            return Math.random() > 0.5 ? 10 : 5;
        }
    }
}
