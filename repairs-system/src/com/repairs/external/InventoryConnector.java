package com.repairs.external;

import com.repairs.entities.SparePart;
import com.repairs.interfaces.model.IDatabaseSubsystem;
import com.repairs.interfaces.model.IExceptionHandler;
import com.repairs.interfaces.model.IInventoryConnector;
import com.repairs.interfaces.model.IRepairLogger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * InventoryConnector - Adapter between repair subsystem and inventory data in DB subsystem.
 */
public class InventoryConnector implements IInventoryConnector {
    private final IRepairLogger logger;
    private final IDatabaseSubsystem databaseSubsystem;
    private final IExceptionHandler exceptionHandler;
    private boolean isConnected;
    private final Map<String, Integer> partInventoryCache;
    private final Map<String, Long> cacheTimestamps;
    private static final long CACHE_VALIDITY_MS = 300000;

    public InventoryConnector(IRepairLogger logger) {
        this(logger, new DefaultDatabaseSubsystem(), new DefaultExceptionHandler());
    }

    public InventoryConnector(IRepairLogger logger,
                              IDatabaseSubsystem databaseSubsystem,
                              IExceptionHandler exceptionHandler) {
        this.logger = Objects.requireNonNull(logger, "Logger cannot be null");
        this.databaseSubsystem = Objects.requireNonNull(databaseSubsystem, "Database subsystem cannot be null");
        this.exceptionHandler = exceptionHandler != null ? exceptionHandler : new DefaultExceptionHandler();
        this.isConnected = databaseSubsystem.isConnected();
        this.partInventoryCache = new ConcurrentHashMap<>();
        this.cacheTimestamps = new ConcurrentHashMap<>();
    }

    @Override
    public boolean checkAvailability(String partId, int quantity) {
        if (partId == null || partId.isBlank() || quantity <= 0 || !isConnected) {
            return false;
        }

        try {
            Optional<Integer> stockLevel = getStockLevel(partId);
            return stockLevel.isPresent() && stockLevel.get() >= quantity;
        } catch (Exception e) {
            logger.log(partId, "Error checking availability: " + e.getMessage(), "ERROR", "INVENTORY_CHECK");
            exceptionHandler.handleException(e, "InventoryConnector.checkAvailability");
            return false;
        }
    }

    @Override
    public boolean reservePart(String partId, int quantity) {
        if (partId == null || partId.isBlank() || quantity <= 0 || !isConnected) {
            return false;
        }

        try {
            Optional<SparePart> partDetails = databaseSubsystem.findSparePartById(partId);
            if (partDetails.isEmpty() || partDetails.get().getQuantity() < quantity) {
                logger.log(partId, "Insufficient stock for reservation: " + quantity, "WARNING", "RESERVATION");
                return false;
            }

            SparePart existing = partDetails.get();
            int remainingQuantity = existing.getQuantity() - quantity;
            if (remainingQuantity < 0) {
                logger.log(partId, "Insufficient stock for reservation: " + quantity, "WARNING", "RESERVATION");
                return false;
            }

            if (remainingQuantity == 0) {
                partInventoryCache.put(partId, 0);
                cacheTimestamps.put(partId, System.currentTimeMillis());
                logger.log(partId, "Part reserved: " + quantity, "INFO", "RESERVATION");
                return true;
            }

            SparePart updated = new SparePart(
                    existing.getPartId(),
                    existing.getName(),
                    remainingQuantity,
                    existing.getUnitPrice(),
                    existing.getCategory());

            boolean saved = databaseSubsystem.saveOrUpdateSparePart(updated);
            if (saved) {
                partInventoryCache.put(partId, updated.getQuantity());
                cacheTimestamps.put(partId, System.currentTimeMillis());
                logger.log(partId, "Part reserved: " + quantity, "INFO", "RESERVATION");
            }
            return saved;

        } catch (Exception e) {
            logger.log(partId, "Error reserving part: " + e.getMessage(), "ERROR", "RESERVATION");
            exceptionHandler.handleException(e, "InventoryConnector.reservePart");
            return false;
        }
    }

    @Override
    public boolean releasePart(String partId, int quantity) {
        if (partId == null || partId.isBlank() || quantity <= 0 || !isConnected) {
            return false;
        }

        try {
            Optional<SparePart> partDetails = databaseSubsystem.findSparePartById(partId);
            if (partDetails.isEmpty()) {
                return false;
            }

            SparePart existing = partDetails.get();
            SparePart updated = new SparePart(
                    existing.getPartId(),
                    existing.getName(),
                    existing.getQuantity() + quantity,
                    existing.getUnitPrice(),
                    existing.getCategory());

            boolean saved = databaseSubsystem.saveOrUpdateSparePart(updated);
            if (saved) {
                partInventoryCache.put(partId, updated.getQuantity());
                cacheTimestamps.put(partId, System.currentTimeMillis());
                logger.log(partId, "Part released: " + quantity, "INFO", "RELEASE");
            }
            return saved;

        } catch (Exception e) {
            logger.log(partId, "Error releasing part: " + e.getMessage(), "ERROR", "RELEASE");
            exceptionHandler.handleException(e, "InventoryConnector.releasePart");
            return false;
        }
    }

    @Override
    public boolean updateInventoryAfterUse(String partId, int quantityUsed) {
        return reservePart(partId, quantityUsed);
    }

    @Override
    public Optional<Integer> getStockLevel(String partId) {
        if (partId == null || partId.isBlank() || !isConnected) {
            return Optional.empty();
        }

        try {
            Long lastUpdate = cacheTimestamps.get(partId);
            Integer cachedStock = partInventoryCache.get(partId);

            if (lastUpdate != null
                    && cachedStock != null
                    && System.currentTimeMillis() - lastUpdate < CACHE_VALIDITY_MS) {
                return Optional.of(cachedStock);
            }

            Optional<SparePart> part = databaseSubsystem.findSparePartById(partId);
            if (part.isPresent()) {
                partInventoryCache.put(partId, part.get().getQuantity());
                cacheTimestamps.put(partId, System.currentTimeMillis());
                return Optional.of(part.get().getQuantity());
            }

            return Optional.empty();

        } catch (Exception e) {
            logger.log(partId, "Error fetching stock level: " + e.getMessage(), "ERROR", "STOCK_QUERY");
            exceptionHandler.handleException(e, "InventoryConnector.getStockLevel");
            return Optional.empty();
        }
    }

    @Override
    public Optional<SparePart> getPartDetails(String partId) {
        if (partId == null || partId.isBlank() || !isConnected) {
            return Optional.empty();
        }

        try {
            return databaseSubsystem.findSparePartById(partId);
        } catch (Exception e) {
            logger.log(partId, "Error fetching part details: " + e.getMessage(), "ERROR", "PART_QUERY");
            exceptionHandler.handleException(e, "InventoryConnector.getPartDetails");
            return Optional.empty();
        }
    }

    @Override
    public List<SparePart> getLowStockParts(int threshold) {
        if (!isConnected) {
            return Collections.emptyList();
        }

        try {
            return databaseSubsystem.findAllSpareParts().stream()
                    .filter(part -> part.getQuantity() <= threshold)
                    .toList();
        } catch (Exception e) {
            logger.log("SYSTEM", "Error fetching low stock parts: " + e.getMessage(), "ERROR", "LOW_STOCK_QUERY");
            exceptionHandler.handleException(e, "InventoryConnector.getLowStockParts");
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isConnected() {
        return isConnected && databaseSubsystem.isConnected();
    }

    public void refreshCache() {
        partInventoryCache.clear();
        cacheTimestamps.clear();
        logger.log("SYSTEM", "Inventory cache refreshed", "INFO", "CACHE_REFRESH");
    }

    public void setConnected(boolean connected) {
        this.isConnected = connected;
        String status = connected ? "connected" : "disconnected";
        logger.log("SYSTEM", "Inventory system " + status, "INFO", "CONNECTION");
    }
}
