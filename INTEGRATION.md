# SCM Packing Subsystem — Integration Guide

This document provides **complete, step-by-step instructions** for integrating the two external subsystems into the Packing application:

1. **SCM Exception Handler** — the `.jar` provided by the Exception Handling team
2. **SCM Database Module** — the `database-module` provided by Team Jackfruit

Both integrations follow the **Adapter pattern**. Our codebase isolates all external dependencies behind internal interfaces (`IExceptionDispatcher`, `IDatabaseLayer`), so swapping a stub for a real implementation requires changes to **exactly one adapter class** plus **one factory class** each — the rest of the application is untouched.

---

## Table of Contents

- [Current Architecture](#current-architecture)
- [Part A: Exception Handler Integration](#part-a-exception-handler-integration)
  - [A1. Understanding the SCM Exception Contract](#a1-understanding-the-scm-exception-contract)
  - [A2. Which Interfaces Apply to Us](#a2-which-interfaces-apply-to-us)
  - [A3. Our Specific Exception IDs](#a3-our-specific-exception-ids)
  - [A4. Step-by-Step: Installing the JAR](#a4-step-by-step-installing-the-jar)
  - [A5. Step-by-Step: Updating `SCMExceptionAdapter.java`](#a5-step-by-step-updating-scmexceptionadapterjava)
  - [A6. Step-by-Step: Updating `ExceptionDispatcherFactory.java`](#a6-step-by-step-updating-exceptiondispatcherfactoryjava)
  - [A7. Complete Code for the Finished Adapter](#a7-complete-code-for-the-finished-adapter)
  - [A8. Verification Checklist](#a8-verification-checklist)
- [Part B: Database Module Integration](#part-b-database-module-integration)
  - [B1. Understanding the Database Flow](#b1-understanding-the-database-flow)
  - [B2. Step-by-Step: MySQL & Schema Setup](#b2-step-by-step-mysql--schema-setup)
  - [B3. Step-by-Step: Installing the Database JAR](#b3-step-by-step-installing-the-database-jar)
  - [B4. Step-by-Step: Updating `SCMDatabaseAdapter.java`](#b4-step-by-step-updating-scmdatabaseadapterjava)
  - [B5. Complete Code for the Finished Adapter](#b5-complete-code-for-the-finished-adapter)
  - [B6. Verification Checklist](#b6-verification-checklist)
- [Fallback Behaviour](#fallback-behaviour)
- [Design Pattern Summary](#design-pattern-summary)

---

## Current Architecture

```
                          ┌──────────────────────┐
                          │   PackingController   │
                          │   PackingWorker       │
                          │   PackingModel        │
                          └───────┬──────┬────────┘
                                  │      │
              ┌───────────────────┘      └───────────────────┐
              ▼                                              ▼
    ┌─────────────────────┐                       ┌──────────────────────────┐
    │  IExceptionDispatcher│                       │    IDatabaseLayer        │
    │    (our interface)   │                       │    (our interface)       │
    └────┬──────────┬──────┘                       └─────┬──────────┬────────┘
         │          │                                    │          │
         ▼          ▼                                    ▼          ▼
  ┌────────────┐ ┌─────────────┐                ┌────────────┐ ┌──────────────┐
  │ SCMException│ │ Fallback    │                │ SCMDatabase │ │ FlatFile     │
  │ Adapter     │ │ ConsoleLog  │                │ Adapter     │ │ Adapter      │
  │ (STUB)     │ │ (active)    │                │ (STUB)     │ │ (active)     │
  └────────────┘ └─────────────┘                └────────────┘ └──────────────┘
        │                                              │
        ▼                                              ▼
  ┌────────────┐                                ┌─────────────────────────┐
  │ SCM Handler│  ◄── EXTERNAL JAR              │ SupplyChainDatabase     │
  │ .jar       │                                │ Facade (.jar)           │
  └────────────┘                                └─────────────────────────┘
```

The **Factory** classes (`ExceptionDispatcherFactory`, `DatabaseLayerFactory`) use `Class.forName()` to detect whether each external JAR is on the classpath. If found, they return the real adapter; if not, the fallback.

---

## Part A: Exception Handler Integration

### A1. Understanding the SCM Exception Contract

The Exception Handler team provides:

| Artifact | Description |
|---|---|
| **`.jar` file** | Compiled classes in package `com.scm.exceptions` |
| **`SCMExceptionHandler`** | The central handler object — receives and processes all exceptions |
| **`Severity` enum** | `MINOR`, `MAJOR`, `WARNING` |
| **`SCMExceptionEvent`** | Data class holding exception details |
| **`I*ExceptionSource` interfaces** | 10 category-specific interfaces (one per exception category) |

**How it works at runtime:**

1. Our subsystem class implements the relevant `I*ExceptionSource` interfaces.
2. At startup, the central Exception Handler calls `registerHandler(SCMExceptionHandler h)` on our class — **we do NOT call this ourselves**.
3. We store that `handler` reference.
4. When an exception condition is detected in our code, we call the appropriate `fire*()` method.
5. The handler displays a **blocking modal popup** and writes a **Windows Event Viewer** entry via JNA/Win32.
6. **After calling `fire*()`, we must halt the failing operation immediately** — no further logging or UI calls.

### A2. Which Interfaces Apply to Us

Based on our exception IDs, the Packing subsystem must implement **three** `I*ExceptionSource` interfaces:

| Interface | Package | Exception IDs We Use |
|---|---|---|
| `IResourceAvailabilityExceptionSource` | `com.scm.exceptions` | 157, 158, 159 |
| `IStateWorkflowExceptionSource` | `com.scm.exceptions` | 208, 209, 210 |
| `ISystemInfrastructureExceptionSource` | `com.scm.exceptions` | 359, 360, 361, 362, 363 |

We also use:
- **ID 9** (`INVALID_REPAIR_REQUEST`) — this falls under `IInputValidationExceptionSource`, but since it maps to Repairs and not the Packing workflow itself, implement it only if Repairs is also being built in this codebase. If it is, add `IInputValidationExceptionSource` as well.

### A3. Our Specific Exception IDs

The complete list of exceptions assigned to **"Packing, Repairs, Receipt Management"** in the master register:

| ID | Name | Severity | Category | `fire*` Method |
|----|------|----------|----------|----------------|
| 9 | `INVALID_REPAIR_REQUEST` | MAJOR | Input/Validation | `fireReferenceNotFound(9, "RepairItem", itemId)` |
| 157 | `SPARE_PART_NOT_AVAILABLE` | MAJOR | Resource/Availability | `fireResourceExhausted(157, "SparePart", partId, requested, available)` |
| 158 | `INVENTORY_RESERVATION_FAILED` | MAJOR | Resource/Availability | `fireResourceBlocked(158, "InventoryReservation", reservationDetail)` |
| 159 | `ITEM_NOT_AVAILABLE_FOR_PACKING` | MAJOR | Resource/Availability | `fireResourceBlocked(159, "PackingItem", itemId)` |
| 208 | `WARRANTY_VALIDATION_FAILED` | MAJOR | State/Workflow | `fireInvalidState(208, "Warranty", productId, "VALID", "FAILED")` |
| 209 | `REPAIR_EXECUTION_FAILED` | MAJOR | State/Workflow | `fireInvalidState(209, "RepairJob", jobId, "IN_PROGRESS", "FAILED")` |
| 210 | `REPAIR_DELAY_DETECTED` | WARNING | State/Workflow | `fireSlaBreached(210, "RepairJob", jobId, slaDetail)` |
| 359 | `PACKAGE_CREATION_FAILED` | MINOR | System/Infrastructure | `fireOutputFormatError(359, "PackageLabel", jobId, errorDetail)` |
| 360 | `RECEIPT_STORAGE_FAILED` | MAJOR | System/Infrastructure | `fireSystemComponentFailure(360, "ReceiptStorage", errorDetail)` |
| 361 | `RECEIPT_GENERATION_FAILED` | MAJOR | System/Infrastructure | `fireSystemComponentFailure(361, "ReceiptPipeline", errorDetail)` |
| 362 | `PAYMENT_PROCESSING_FAILED` | MAJOR | System/Infrastructure | `fireProcessingPipelineError(362, "PaymentProcessor", errorDetail)` |
| 363 | `COST_CALCULATION_FAILED` | MAJOR | System/Infrastructure | `fireProcessingPipelineError(363, "CostCalculator", errorDetail)` |
| 0 | `UNREGISTERED_EXCEPTION` | MINOR | (catch-all) | `raise(0, Severity.MINOR, detail)` |

### A4. Step-by-Step: Installing the JAR

The exception team has provided a `.jar` file. To add it to our Maven project:

**Option 1: Install to Local Maven Repository (Recommended)**

```powershell
# Replace the path with the actual location of the received .jar file
mvn install:install-file `
  "-Dfile=path\to\scm-exception-handler.jar" `
  "-DgroupId=com.scm.exceptions" `
  "-DartifactId=scm-exception-handler" `
  "-Dversion=1.0" `
  "-Dpackaging=jar"
```

Then add the dependency to `packing/pom.xml`:

```xml
<!-- SCM Exception Handler (from Exception Handling team) -->
<dependency>
    <groupId>com.scm.exceptions</groupId>
    <artifactId>scm-exception-handler</artifactId>
    <version>1.0</version>
</dependency>
```

**Option 2: System-Scope JAR (Quick & Dirty)**

If you don't want to install to the local repo, place the `.jar` in a `lib/` folder and reference it directly:

```xml
<!-- SCM Exception Handler — local JAR -->
<dependency>
    <groupId>com.scm.exceptions</groupId>
    <artifactId>scm-exception-handler</artifactId>
    <version>1.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/scm-exception-handler.jar</systemPath>
</dependency>
```

> **Important**: After adding the dependency, run `mvn compile` to verify the JAR is correctly resolved. You should see the `ExceptionDispatcherFactory` log `SCM Exception Handler found` instead of the fallback message.

### A5. Step-by-Step: Updating `SCMExceptionAdapter.java`

This is the main file that needs to change. Here is exactly what to do:

**1. Add the SCM `implements` clauses:**

The class declaration changes from:

```java
public class SCMExceptionAdapter implements IExceptionDispatcher {
```

to:

```java
import com.scm.exceptions.*;

public class SCMExceptionAdapter implements IExceptionDispatcher,
        IResourceAvailabilityExceptionSource,
        IStateWorkflowExceptionSource,
        ISystemInfrastructureExceptionSource {
```

**2. Add the handler field and `registerHandler()` method:**

```java
// The central handler reference — injected at startup by the Exception
// Handler subsystem. We do NOT call registerHandler() ourselves.
private SCMExceptionHandler handler;

@Override
public void registerHandler(SCMExceptionHandler h) {
    this.handler = h;
    System.out.println("[SCMExceptionAdapter] SCM Exception Handler registered.");
}
```

**3. Add the null-safe `raise()` helper:**

Per the spec, every `fire*` call should be guarded against the handler being null (in case our code runs before registration is complete):

```java
/**
 * Internal helper — null-safe exception raise.
 * If the handler has not yet been registered, silently returns.
 */
private void raise(int id, Severity severity, String detail) {
    if (handler == null) {
        // Handler not registered yet — log to stderr as a safety net
        System.err.println("[SCMExceptionAdapter] Handler not registered. "
                + "Exception id=" + id + " detail=" + detail);
        return;
    }
    handler.raise(id, severity, "Packing, Repairs, Receipt Management", detail);
}
```

> **Note**: The exact API of `handler.raise(...)` depends on whatever the Exception team's JAR actually exposes. The spec document shows that each `I*ExceptionSource` interface has specific `fire*()` methods (e.g., `fireResourceBlocked()`, `fireInvalidState()`). Inspect the JAR's classes to determine the exact method signatures. The `raise()` helper above is a generalised wrapper — you may need to call specific `fire*()` methods instead.

**4. Rewrite the `dispatch()` method to route by exception ID:**

```java
@Override
public void dispatch(int exceptionId, String severity, String subsystem, String detail) {
    Severity sev = mapSeverity(severity);

    switch (exceptionId) {
        // --- Resource / Availability (Category 4) ---
        case 157: fireResourceExhausted(157, "SparePart", detail, 0, 0); break;
        case 158: fireResourceBlocked(158, "InventoryReservation", detail); break;
        case 159: fireResourceBlocked(159, "PackingItem", detail); break;

        // --- State / Workflow (Category 5) ---
        case 208: fireInvalidState(208, "Warranty", detail, "VALID", "FAILED"); break;
        case 209: fireInvalidState(209, "RepairJob", detail, "IN_PROGRESS", "FAILED"); break;
        case 210: fireSlaBreached(210, "RepairJob", detail, "SLA exceeded"); break;

        // --- System / Infrastructure (Category 8) ---
        case 359: fireOutputFormatError(359, "PackageLabel", detail, "creation failed"); break;
        case 360: fireSystemComponentFailure(360, "ReceiptStorage", detail); break;
        case 361: fireSystemComponentFailure(361, "ReceiptPipeline", detail); break;
        case 362: fireProcessingPipelineError(362, "PaymentProcessor", detail); break;
        case 363: fireProcessingPipelineError(363, "CostCalculator", detail); break;

        // --- Catch-all for unregistered exceptions ---
        default:
            raise(0, Severity.MINOR, "UNREGISTERED_EXCEPTION in Packing: " + detail);
            break;
    }
}
```

> **Critical**: The exact `fire*()` method signatures above are **illustrative** — they follow the naming convention from the spec document. When you receive the JAR, use your IDE's autocomplete or `javap -public` to list the actual method signatures on each interface. Adjust the parameter order/types to match.

**5. Implement the `I*ExceptionSource` interface methods:**

Each `I*ExceptionSource` interface defines specific `fire*()` methods. You must implement all abstract methods declared in the interfaces you claim to implement. Here is the pattern (adapt parameter types to match the JAR):

```java
// ---- IResourceAvailabilityExceptionSource ----

@Override
public void fireResourceNotFound(int id, String resourceType, String resourceId) {
    raise(id, Severity.MAJOR, resourceType + " not found: " + resourceId);
}

@Override
public void fireResourceExhausted(int id, String resourceType, String resourceId,
                                   int requested, int available) {
    raise(id, Severity.MAJOR, resourceType + " " + resourceId
            + " exhausted (requested=" + requested + ", available=" + available + ")");
}

@Override
public void fireResourceBlocked(int id, String resourceType, String resourceId) {
    raise(id, Severity.MAJOR, resourceType + " " + resourceId + " is blocked/unavailable");
}

@Override
public void fireCapacityExceeded(int id, String resourceType, String resourceId) {
    raise(id, Severity.MAJOR, resourceType + " " + resourceId + " exceeded capacity");
}

// ---- IStateWorkflowExceptionSource ----

@Override
public void fireInvalidState(int id, String entityType, String entityId,
                              String expectedState, String actualState) {
    raise(id, Severity.MAJOR, entityType + " " + entityId
            + " state conflict: expected=" + expectedState + ", actual=" + actualState);
}

@Override
public void fireTimeoutExceeded(int id, String entityType, String entityId, String detail) {
    raise(id, Severity.MAJOR, entityType + " " + entityId + " timeout: " + detail);
}

@Override
public void fireExpired(int id, String entityType, String entityId) {
    raise(id, Severity.MINOR, entityType + " " + entityId + " expired");
}

@Override
public void fireSlaBreached(int id, String entityType, String entityId, String slaDetail) {
    raise(id, Severity.WARNING, entityType + " " + entityId + " SLA breached: " + slaDetail);
}

// ---- ISystemInfrastructureExceptionSource ----

@Override
public void fireSystemComponentFailure(int id, String component, String detail) {
    raise(id, Severity.MAJOR, "System component failure [" + component + "]: " + detail);
}

@Override
public void fireProcessingPipelineError(int id, String pipeline, String detail) {
    raise(id, Severity.MAJOR, "Pipeline error [" + pipeline + "]: " + detail);
}

@Override
public void firePerformanceDegraded(int id, String component, String metric) {
    raise(id, Severity.WARNING, "Performance degraded [" + component + "]: " + metric);
}

@Override
public void fireOutputFormatError(int id, String outputType, String detail, String format) {
    raise(id, Severity.MINOR, "Output format error [" + outputType + "]: " + detail);
}
```

**6. Add a severity mapping helper:**

```java
private Severity mapSeverity(String severityString) {
    switch (severityString.toUpperCase()) {
        case "MAJOR":   return Severity.MAJOR;
        case "WARNING": return Severity.WARNING;
        case "MINOR":
        default:        return Severity.MINOR;
    }
}
```

### A6. Step-by-Step: Updating `ExceptionDispatcherFactory.java`

The factory already checks for the right class. Once the JAR is on the classpath, it will automatically switch from `FallbackConsoleLogger` to `SCMExceptionAdapter`. **No changes are needed** to the factory — the existing `Class.forName("com.scm.exceptions.SCMExceptionHandler")` check is sufficient.

If the Exception team's handler class is named differently, update the string in `Class.forName()` to match.

### A7. Complete Code for the Finished Adapter

See [A5 above](#a5-step-by-step-updating-scmexceptionadapterjava) for the full listing of every method. After integration, `SCMExceptionAdapter.java` will be approximately 150–180 lines.

### A8. Verification Checklist

- [ ] JAR installed to local Maven repo or `lib/`
- [ ] `pom.xml` has the dependency entry
- [ ] `mvn compile` succeeds
- [ ] Startup log shows `SCM Exception Handler found — using SCMExceptionAdapter`
- [ ] `registerHandler()` is called by the Exception Handler subsystem at boot
- [ ] Triggering a packing failure (e.g., with a bad item) shows a **modal popup** with the exception details
- [ ] Windows Event Viewer entry is created for each fired exception
- [ ] No `fire*()` call is followed by further logging or recovery in the same method — the operation halts immediately

---

## Part B: Database Module Integration

### B1. Understanding the Database Flow

The database team (Team Jackfruit) provides a layered persistence module:

```
Your code
    → SupplyChainDatabaseFacade          (our entry point)
        → Subsystem-specific facade      (e.g. facade.orders())
            → Service layer              (validation)
                → DAO interface + impl   (SQL via JDBC)
                    → MySQL
```

**Golden Rule:** Never write raw SQL in our own code. Always go through the facade.

Key classes in the `database-module` JAR:

| Class | Package | Purpose |
|---|---|---|
| `SupplyChainDatabaseFacade` | `com.jackfruit.scm.database.facade` | Single entry point to all subsystem data |
| `facade.orders()` | — | Returns the Orders subsystem facade |
| `facade.exceptions()` | — | Returns the Exceptions subsystem facade |
| Various model classes | `com.jackfruit.scm.database.model` | Data transfer objects (Order, OrderItem, etc.) |

### B2. Step-by-Step: MySQL & Schema Setup

Before the database adapter can work, MySQL must be running with the correct schema:

```sql
-- Step 1: Run the main schema
SOURCE path/to/schema.sql;

-- Step 2: Run the extension schema (includes packing/receipt tables)
USE OOAD;
SOURCE path/to/schema-extension.sql;

-- Step 3: Load sample data
USE OOAD;
SOURCE path/to/sample-data.sql;

-- Step 4: Verify
USE OOAD;
SHOW TABLES;
SELECT * FROM orders;
```

Then edit `database-module/src/main/resources/database.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/OOAD
db.username=root
db.password=your_password_here
db.pool.size=5
```

### B3. Step-by-Step: Installing the Database JAR

Build the database module JAR:

```powershell
cd path\to\database-module
mvn package          # produces target/database-module-1.0-SNAPSHOT.jar (or similar)
```

Install it to your local Maven repo:

```powershell
mvn install:install-file `
  "-Dfile=target\database-module-1.0-SNAPSHOT.jar" `
  "-DgroupId=com.jackfruit.scm" `
  "-DartifactId=database-module" `
  "-Dversion=1.0" `
  "-Dpackaging=jar"
```

Add it to `packing/pom.xml`:

```xml
<!-- SCM Database Module (from Team Jackfruit) -->
<dependency>
    <groupId>com.jackfruit.scm</groupId>
    <artifactId>database-module</artifactId>
    <version>1.0</version>
</dependency>

<!-- MySQL Connector — required at runtime for the database module -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

### B4. Step-by-Step: Updating `SCMDatabaseAdapter.java`

**1. Add the import and facade field:**

```java
import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
// import any model classes as needed, e.g.:
// import com.jackfruit.scm.database.model.Order as DbOrder;
// import com.jackfruit.scm.database.model.OrderItem as DbOrderItem;
```

**2. Instantiate the facade in the constructor:**

```java
private final SupplyChainDatabaseFacade facade;

public SCMDatabaseAdapter() {
    this.facade = new SupplyChainDatabaseFacade();
    System.out.println("[SCMDatabaseAdapter] Connected to SCM database via SupplyChainDatabaseFacade.");
}
```

**3. Implement `loadOrders()`:**

This is the most complex method. The database module stores orders and items separately, so we need to:
1. Load all orders via `facade.orders().listOrders()` (or similar)
2. For each order, load its items via `facade.orders().listOrderItems(orderId)`
3. Map the database model objects to our Packing domain objects (`Order`, `PackingItem`)

```java
@Override
public List<Order> loadOrders() {
    List<Order> result = new ArrayList<>();

    try {
        // Step 1: Get all orders from the database
        // (Adapt method names to match the actual database-module API)
        List<DbOrder> dbOrders = facade.orders().listOrders();

        for (DbOrder dbOrder : dbOrders) {
            // Step 2: Load items for this order
            List<DbOrderItem> dbItems = facade.orders().listOrderItems(dbOrder.getOrderId());

            // Step 3: Map to our domain model
            List<PackingItem> packingItems = new ArrayList<>();
            for (DbOrderItem dbItem : dbItems) {
                packingItems.add(new PackingItem(
                    dbItem.getProductId(),           // SKU
                    dbItem.getProductName(),          // description
                    dbItem.getWeight(),               // weight in kg
                    dbItem.isFragile()                // fragility flag
                ));
            }

            Order order = new Order(
                dbOrder.getOrderId(),
                dbOrder.getCustomerId(),
                dbOrder.getCustomerName(),
                packingItems
            );
            result.add(order);
        }
    } catch (Exception e) {
        System.err.println("[SCMDatabaseAdapter] Failed to load orders: " + e.getMessage());
    }

    return result;
}
```

> **Important**: The exact method and field names (`getOrderId()`, `getProductName()`, `isFragile()`) depend on the database-module's model classes. Use your IDE to inspect the JAR or check the database module documentation. The pattern above shows the required data mapping.

**4. Implement `updateOrder()`:**

```java
@Override
public void updateOrder(Order order) {
    try {
        // Update order status in the database to reflect packing completion
        facade.orders().updateOrderStatus(order.getOrderId(), "PACKED");
    } catch (Exception e) {
        System.err.println("[SCMDatabaseAdapter] Failed to update order: " + e.getMessage());
    }
}
```

**5. Implement job persistence methods:**

If the database extension schema includes a `packing_jobs` table (check `schema-extension.sql`), implement accordingly:

```java
@Override
public void saveJob(PackingJob job) {
    try {
        // The facade may have a packing-specific subsystem facade.
        // If not, you can use a generic approach or log the job via
        // the exceptions/barcode facade.
        // Example (adapt to actual API):
        // facade.packing().createPackingJob(job.getJobId(), job.getOrderId(), ...);

        System.out.println("[SCMDatabaseAdapter] Saved job: " + job.getJobId());
    } catch (Exception e) {
        System.err.println("[SCMDatabaseAdapter] Failed to save job: " + e.getMessage());
    }
}

@Override
public void updateJob(PackingJob job) {
    try {
        // facade.packing().updateJobStatus(job.getJobId(), job.getStatus().name());
        System.out.println("[SCMDatabaseAdapter] Updated job: " + job.getJobId());
    } catch (Exception e) {
        System.err.println("[SCMDatabaseAdapter] Failed to update job: " + e.getMessage());
    }
}

@Override
public List<PackingJob> loadAllJobs() {
    // If the database has a packing_jobs table, query it here.
    // Otherwise, return empty and let the UI state be session-only.
    return new ArrayList<>();
}

@Override
public void clearAll() {
    // Only relevant for flat-file mode. In database mode, data persists
    // across sessions — so this can be a no-op.
    System.out.println("[SCMDatabaseAdapter] clearAll is a no-op in database mode.");
}
```

### B5. Complete Code for the Finished Adapter

After integration, `SCMDatabaseAdapter.java` should look approximately like this (full skeleton):

```java
package com.scm.packing.integration.database;

import com.scm.packing.mvc.model.Order;
import com.scm.packing.mvc.model.PackingItem;
import com.scm.packing.mvc.model.PackingJob;

import com.jackfruit.scm.database.facade.SupplyChainDatabaseFacade;
// import com.jackfruit.scm.database.model.*;  // adapt to actual model package

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter connecting packed-subsystem persistence to the SCM database
 * via {@link SupplyChainDatabaseFacade}.
 *
 * <b>Design Pattern - Adapter (Structural)</b>
 */
public class SCMDatabaseAdapter implements IDatabaseLayer {

    private final SupplyChainDatabaseFacade facade;

    public SCMDatabaseAdapter() {
        this.facade = new SupplyChainDatabaseFacade();
        System.out.println("[SCMDatabaseAdapter] Connected to SCM database.");
    }

    @Override
    public List<Order> loadOrders() {
        List<Order> result = new ArrayList<>();
        try {
            // adapt to actual API: facade.orders().listOrders() etc.
            // map DB model -> our Order/PackingItem domain objects
        } catch (Exception e) {
            System.err.println("[SCMDatabaseAdapter] loadOrders failed: " + e.getMessage());
        }
        return result;
    }

    @Override
    public void updateOrder(Order order) {
        try {
            facade.orders().updateOrderStatus(order.getOrderId(), "PACKED");
        } catch (Exception e) {
            System.err.println("[SCMDatabaseAdapter] updateOrder failed: " + e.getMessage());
        }
    }

    @Override
    public void saveJob(PackingJob job) {
        // Persist to packing_jobs table if available
    }

    @Override
    public void updateJob(PackingJob job) {
        // Update job status in packing_jobs table
    }

    @Override
    public List<PackingJob> loadAllJobs() {
        return new ArrayList<>(); // or query packing_jobs table
    }

    @Override
    public void clearAll() {
        // No-op in database mode — data persists across sessions
    }
}
```

### B6. Verification Checklist

- [ ] MySQL is running with `OOAD` database
- [ ] `schema.sql` and `schema-extension.sql` have been executed
- [ ] `database.properties` has correct credentials
- [ ] `database-module` JAR is installed to local Maven repo
- [ ] `pom.xml` has both `database-module` and `mysql-connector-java` dependencies
- [ ] `mvn compile` succeeds
- [ ] Startup log shows `SCM database-module found — using SCMDatabaseAdapter`
- [ ] Orders loaded from MySQL appear in the UI order table
- [ ] Packing a job updates the order status in MySQL
- [ ] Application still works if MySQL is down (falls back to `FlatFileDatabaseAdapter`)

---

## Fallback Behaviour

Both integrations are designed to degrade gracefully. The fallback chain is:

| Component | External JAR Present | External JAR Missing |
|---|---|---|
| **Exception Handler** | `SCMExceptionAdapter` — modal popups + Event Viewer | `FallbackConsoleLogger` — prints to `System.err` |
| **Database** | `SCMDatabaseAdapter` — real MySQL via facade | `FlatFileDatabaseAdapter` — in-memory seed data, cleared on exit |

The fallback is selected automatically at startup by the Factory classes. **No configuration flags or environment variables** are needed — the system simply checks whether the external classes exist on the classpath via `Class.forName()`.

This means:
- A developer without MySQL can still run and test the Packing UI using seed data.
- A developer without the exception JAR still sees exception details in the console log.
- The full production system uses both real integrations transparently.

---

## Design Pattern Summary

| Pattern | Role in Integration |
|---|---|
| **Adapter** (Structural) | `SCMExceptionAdapter` and `SCMDatabaseAdapter` bridge external APIs to our internal interfaces |
| **Factory Method** (Creational) | `ExceptionDispatcherFactory` and `DatabaseLayerFactory` select the correct adapter at startup |
| **Observer** (Behavioral) | Exception events flow from workers → model → observers (view) |
| **Dependency Inversion** (SOLID) | Controllers and workers depend on `IExceptionDispatcher` and `IDatabaseLayer`, never on concrete adapters |
| **Liskov Substitution** (SOLID) | Real adapters and fallbacks are completely interchangeable behind the interface |

---

## Files to Modify Checklist

When integrating each subsystem, only these files need to change:

### Exception Handler
| File | Action |
|---|---|
| `pom.xml` | Add JAR dependency |
| `SCMExceptionAdapter.java` | Implement SCM interfaces + routing logic |
| **All other files** | **No changes** |

### Database Module
| File | Action |
|---|---|
| `pom.xml` | Add database-module + MySQL connector dependencies |
| `database.properties` | Create/edit with MySQL credentials |
| `SCMDatabaseAdapter.java` | Implement facade calls + model mapping |
| **All other files** | **No changes** |

> That is the power of the Adapter pattern — the entire rest of the application (Model, View, Controller, Worker, Strategy) is **completely decoupled** from both external subsystems.
