package com.repairs.services;

import com.repairs.enums.RepairStatus;
import com.repairs.interfaces.model.IRepairRepository;
import com.repairs.interfaces.model.IStatusObserver;
import com.repairs.interfaces.model.IStatusTracker;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * StatusTracker - Concrete implementation of IStatusTracker.
 * Implements Observer pattern for status change notifications.
 */
public class StatusTracker implements IStatusTracker {
    private final IRepairRepository repository;
    private final List<IStatusObserver> observers;
    private final Map<String, RepairStatus> statusCache; // Cache for performance
    private final Map<String, LocalDateTime> lastUpdateCache; // Track last update times

    public StatusTracker(IRepairRepository repository) {
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
        this.observers = Collections.synchronizedList(new ArrayList<>());
        this.statusCache = new ConcurrentHashMap<>();
        this.lastUpdateCache = new ConcurrentHashMap<>();
    }

    @Override
    public void updateStatus(String jobId, RepairStatus newStatus) {
        if (jobId == null || jobId.isBlank()) {
            throw new IllegalArgumentException("Job ID cannot be null");
        }

        if (newStatus == null) {
            throw new IllegalArgumentException("New status cannot be null");
        }

        // Get current status
        RepairStatus oldStatus = statusCache.get(jobId);
        if (oldStatus == null) {
            // Try to fetch from repository
            var jobOptional = repository.findRepairJobById(jobId);
            if (jobOptional.isPresent()) {
                oldStatus = jobOptional.get().getStatus();
            } else {
                throw new IllegalArgumentException("Job not found: " + jobId);
            }
        }

        // Validate state transition
        if (!oldStatus.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", oldStatus, newStatus)
            );
        }

        // Update cache
        statusCache.put(jobId, newStatus);
        lastUpdateCache.put(jobId, LocalDateTime.now());

        // Notify all observers
        notifyStatusChange(jobId, oldStatus, newStatus);
    }

    @Override
    public Optional<RepairStatus> getStatus(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return Optional.empty();
        }

        // Check cache first
        RepairStatus status = statusCache.get(jobId);
        if (status != null) {
            return Optional.of(status);
        }

        // Fetch from repository
        var jobOptional = repository.findRepairJobById(jobId);
        if (jobOptional.isPresent()) {
            status = jobOptional.get().getStatus();
            statusCache.put(jobId, status);
            return Optional.of(status);
        }

        return Optional.empty();
    }

    @Override
    public void registerObserver(IStatusObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }

        // Check if observer already registered
        if (observers.stream()
                .anyMatch(o -> o.getObserverId().equals(observer.getObserverId()))) {
            return; // Already registered
        }

        observers.add(observer);
    }

    @Override
    public void removeObserver(IStatusObserver observer) {
        if (observer == null) {
            return;
        }

        observers.removeIf(o -> o.getObserverId().equals(observer.getObserverId()));
    }

    @Override
    public void notifyStatusChange(String jobId, RepairStatus oldStatus, RepairStatus newStatus) {
        if (jobId == null || oldStatus == null || newStatus == null) {
            return;
        }

        // Notify all registered observers
        for (IStatusObserver observer : new ArrayList<>(observers)) {
            try {
                observer.onStatusChanged(jobId, oldStatus, newStatus);
            } catch (Exception e) {
                // Log observer notification errors but don't fail the status update
                System.err.println("Error notifying observer " + observer.getObserverId() + ": " + e.getMessage());
            }
        }
    }

    @Override
    public Optional<LocalDateTime> getLastStatusUpdateTime(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return Optional.empty();
        }

        return Optional.ofNullable(lastUpdateCache.get(jobId));
    }

    /**
     * Get list of all registered observers
     */
    public List<IStatusObserver> getObservers() {
        return Collections.unmodifiableList(observers);
    }

    /**
     * Clear cache (useful for testing)
     */
    public void clearCache() {
        statusCache.clear();
        lastUpdateCache.clear();
    }

    /**
     * Clear cache for specific job
     */
    public void clearJobCache(String jobId) {
        statusCache.remove(jobId);
        lastUpdateCache.remove(jobId);
    }
}
