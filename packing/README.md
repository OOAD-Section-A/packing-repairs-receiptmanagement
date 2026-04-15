# SCM Packing Subsystem

The Packing Subsystem for the Supply Chain Management (SCM) project. Built in **Java** following the **Model-View-Controller (MVC)** architecture with a **Swing-based dark-themed GUI**.

---

## Features

### Core Packing Workflow
- **Order Selection**: Browse all pending orders from the database (or seed data), select one or multiple orders, and submit them for packing in a single click.
- **Concurrent Packing**: Each selected order spawns its own background `SwingWorker` thread, allowing multiple orders to be packed simultaneously with live progress bars.
- **Strategy-Based Packing**: Fragile items automatically trigger the `FragilePackingStrategy` (extra cushioning/wrapping), while normal items use `StandardPackingStrategy`. Strategies are selected at runtime via the **Factory Method** pattern.

### Labeling & Traceability
- **Barcode Generation**: Every packed job receives a unique barcode label encoding `SCM|JobID|OrderID|Timestamp`.
- **Visual Barcode Rendering**: Barcodes are rendered graphically using Java2D and can be viewed in a dedicated dialog.

### Unitization
- **Case/Pallet Grouping**: Packed jobs can be grouped into secondary (Case) or tertiary (Pallet) shipping units for easier warehouse handling.
- **Capacity Management**: Each unit type has a default capacity (Case = 5, Pallet = 20) and tracks total weight.

### Integration
- **SCM Database Module**: Uses the shared `SupplyChainDatabaseFacade` when the `database-module` JAR is on the classpath.
- **SCM Exception Handler**: Fires structured exceptions (IDs 9, 157–159, 208–210, 359–363) via the SCM handler when available.
- **Automatic Fallback**: If external JARs are missing, the system automatically degrades to a flat-file database (session-only, wiped on exit) and a console-based exception logger.

### Software Licensing
- A separate **License Server** process must be running before the main app starts. The server validates an activation key and listens on TCP port `15151`.
- The main app performs a TCP handshake at startup to verify the license.

---

## Architecture & Design Patterns

| Category | Pattern | Where Used |
|---|---|---|
| **Creational** | Factory Method | `PackingStrategyFactory` selects Standard/Fragile strategy; `DatabaseLayerFactory` and `ExceptionDispatcherFactory` choose real vs. fallback adapters |
| **Structural** | Adapter | `SCMDatabaseAdapter` / `FlatFileDatabaseAdapter` adapt different persistence backends to `IDatabaseLayer`; `SCMExceptionAdapter` / `FallbackConsoleLogger` adapt error dispatching to `IExceptionDispatcher` |
| **Behavioral** | Observer | `PackingModel` (subject) notifies `PackingMainFrame` (observer) of all state changes |
| **Behavioral** | Strategy | `IPackingStrategy` with `StandardPackingStrategy` and `FragilePackingStrategy` implementations |

### SOLID Principles
- **SRP**: Each class has a single focused responsibility (model, view, controller, adapter, strategy, worker).
- **OCP**: New strategies, adapters, or observers can be added without modifying existing code.
- **LSP**: All adapter implementations are interchangeable with their interfaces.
- **ISP**: Interfaces are small and focused (`IDatabaseLayer`, `IExceptionDispatcher`, `IPackingStrategy`, `PackingObserver`).
- **DIP**: All dependencies are injected via constructors — no Singletons.

### GRASP Principles
- **Information Expert**: `PackingModel` manages job state; `PackingJob` knows its own items and progress.
- **Controller**: `PackingController` is the first non-UI object handling user requests.
- **Low Coupling**: Adapters and factories isolate external subsystem dependencies.
- **High Cohesion**: Each class has a well-defined, focused role.
- **Creator**: Factories create the objects they have the information to configure.
- **Pure Fabrication**: DAO/adapter classes are invented to support persistence without polluting domain models.

### Multithreading
- **`SwingWorker`**: Each packing job runs on a pooled background thread with EDT-safe progress updates via `publish()`/`process()`.
- **`ConcurrentHashMap`**: Thread-safe maps for jobs and orders — workers write, EDT reads.
- **`CopyOnWriteArrayList`**: Safe observer iteration during notification.
- **`AtomicInteger`**: Lock-free unique ID generation for jobs and units.
- **`volatile` fields**: Job status and progress are visible across threads without synchronization.
- **EDT discipline**: All Swing mutations in observer callbacks are wrapped in `SwingUtilities.invokeLater()`.

---

## Requirements
- **Java Development Kit (JDK)** 11 or higher (tested with JDK 21)
- **Apache Maven** 3.9+

---

## Setup and Running

### 1. Compile
```powershell
cd "c:\Users\Advaith\Documents\PES\Sem 6\OOAD\Project\packing-repairs-receiptmanagement\packing"
mvn compile
```

### 2. Start the License Server (Terminal 1)
```powershell
java -cp "target\classes" com.scm.packing.licensing.LicenseServer
```
When prompted, enter the activation key: **`SCM-PACK-2026-XRAY`**

Leave this terminal **open** — the server must be running.

### 3. Start the Application (Terminal 2)
```powershell
mvn exec:java "-Dexec.mainClass=com.scm.packing.Main"
```

### Using the Application
1. **Select Orders**: Tick the checkboxes next to orders you want to pack on the left panel.
2. **Pack Selected**: Click the green **"▶ Pack Selected"** button. Each order becomes a packing job with a live progress bar.
3. **View Barcode**: Select a packed job row in the job table, then click **"🏷 View Barcode"** to see the generated barcode.
4. **Unitize**: Select multiple packed job rows, then click **"📦 Unitize as Case"** or **"📦 Unitize as Pallet"** to group them for shipping.

---

## Folder Structure
```
packing/
├── pom.xml
├── README.md
├── .gitignore
└── src/main/java/com/scm/packing/
    ├── Main.java                              # Entry point
    ├── licensing/
    │   ├── LicenseServer.java                 # Standalone license server
    │   └── LicenseChecker.java                # Client-side license check
    ├── mvc/
    │   ├── model/
    │   │   ├── PackingJobStatus.java          # Status enum
    │   │   ├── PackingItem.java               # Item domain model
    │   │   ├── PackingJob.java                # Job domain model
    │   │   ├── Order.java                     # Order domain model
    │   │   ├── BarcodeLabel.java              # Barcode for traceability
    │   │   ├── PackingUnit.java               # Unitization model
    │   │   └── PackingModel.java              # MVC Model (Observer subject)
    │   ├── view/
    │   │   └── PackingMainFrame.java          # Swing dashboard (Observer)
    │   └── controller/
    │       └── PackingController.java         # MVC Controller (GRASP)
    ├── worker/
    │   └── PackingWorker.java                 # SwingWorker for background packing
    ├── strategy/
    │   ├── IPackingStrategy.java              # Strategy interface
    │   ├── StandardPackingStrategy.java       # Standard packing algorithm
    │   ├── FragilePackingStrategy.java        # Fragile packing algorithm
    │   └── PackingStrategyFactory.java        # Factory Method
    ├── observer/
    │   ├── PackingObserver.java               # Observer interface
    │   └── PackingEventType.java              # Event type enum
    └── integration/
        ├── database/
        │   ├── IDatabaseLayer.java            # Adapter target interface
        │   ├── FlatFileDatabaseAdapter.java   # Fallback (flat file + seed data)
        │   ├── SCMDatabaseAdapter.java        # Real SCM DB adapter (stub)
        │   └── DatabaseLayerFactory.java      # Factory with auto-detection
        └── exceptions/
            ├── IExceptionDispatcher.java      # Adapter target interface
            ├── FallbackConsoleLogger.java      # Fallback (console)
            ├── SCMExceptionAdapter.java        # Real SCM handler (stub)
            └── ExceptionDispatcherFactory.java # Factory with auto-detection
```

---

## SCM Exception IDs Used

| ID | Name | Severity | When Fired |
|----|------|----------|------------|
| 159 | ITEM_NOT_AVAILABLE_FOR_PACKING | MAJOR | Item fails during packing |
| 209 | REPAIR_EXECUTION_FAILED | MAJOR | Unexpected packing job failure |
| 359 | PACKAGE_CREATION_FAILED | MINOR | Cannot persist a new packing job |
| 0 | UNREGISTERED_EXCEPTION | MINOR | Any unmatched exception condition |
