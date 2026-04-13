README.md - Repairs Sub-System

# Repairs Sub-System - Production-Quality Java Implementation

## Overview

This is a **complete, production-ready Java implementation** of a Repairs Sub-System based on the component diagram. The system manages the entire lifecycle of repair requests from intake through completion and billing.

### Key Characteristics
- **Architecture**: MVC (Model-View-Controller)
- **Principles**: SOLID + GRASP
- **Design Patterns**: 8+ enterprise patterns
- **Code Quality**: Production-level, fully documented
- **Extensibility**: Built for easy feature additions
- **Testability**: Highly testable with dependency injection
- **Integration**: External DB subsystem + fallback default DB and exception handler

---

## Quick Start (Windows PowerShell)

Run these commands from the `repairs-system` folder.

```powershell
# 1) Go to repairs-system
Set-Location .\repairs-system

# 2) Clean and create output folder
if (Test-Path .\bin) { Remove-Item .\bin -Recurse -Force }
New-Item -ItemType Directory -Path .\bin | Out-Null

# 3) Compile all Java sources
$sources = Get-ChildItem -Path .\src -Filter *.java -Recurse | ForEach-Object { $_.FullName }
javac -d .\bin $sources

# 4) Run the interactive console UI
java -cp .\bin com.repairs.RepairsSubSystemApplication

# Optional: run scripted demo mode
# java -cp .\bin com.repairs.RepairsSubSystemApplication demo
```

If `javac` or `java` is not recognized, install JDK 17+ and ensure it is on `PATH`.

---

## Architecture Overview

### 1. LAYERED ARCHITECTURE

```
┌─────────────────────────────────────────────────────┐
│                    CONTROLLERS (MVC)                │
│  RepairRequestController, RepairExecutionController,│
│  BillingController - Controller Logic               │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                   BUSINESS LAYER                    │
│  Services implementing business logic               │
│  - RepairValidator                                  │
│  - RepairScheduler                                  │
│  - RepairExecutionService                           │
│  - StatusTracker                                    │
│  - CostEstimationService                            │
│  - BillingService                                   │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│              DOMAIN LAYER (Entities)                │
│  - RepairRequest, RepairJob, SparePart              │
│  - CostEstimate, Receipt, RepairLog                 │
│  - Enums: RepairStatus, RepairType, etc.            │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│           PERSISTENCE & EXTERNAL SYSTEMS            │
│  Repository (DAO), Adapters                         │
│  - RepairRepository (in-memory, easily swappable)   │
│  - FinancialSystemConnector (Adapter)               │
│  - InventoryConnector (Adapter)                     │
└─────────────────────────────────────────────────────┘

```

### 2. MVC PATTERN IMPLEMENTATION

```
┌─────────────────────────────────────────────┐
│           USER INTERFACE (VIEW)             │
│   ConsoleRepairRequestIntakeView            │
│   ConsoleRepairExecutionView                │
│   ConsoleBillingView                        │
│   ← PASSIVE - No Business Logic             │
└─────────────┬───────────────────────────────┘
              │ user interaction
              ▼
┌─────────────────────────────────────────────┐
│    CONTROLLER                                │
│  RepairRequestController                     │
│  RepairExecutionController                   │
│  BillingController                           │
│  ← ORCHESTRATES all logic flow (MVC C)       │
└─────────────┬───────────────────────────────┘
              │ delegates to
              ▼
┌─────────────────────────────────────────────┐
│           MODEL (Business Logic + Data)     │
│  Services, Entities, Repositories            │
│  ← Contains ALL business rules               │
└─────────────────────────────────────────────┘
```

### 3. DEPENDENCY INJECTION & IoC

All dependencies are injected through constructors:

```java
// Services created with dependencies
IRepairRepository repository = new RepairRepository();
IRepairLogger logger = new RepairLogger(repository, logDir);
IRepairValidator validator = new RepairValidator(repository, logger);

// Controllers created with service dependencies
RepairRequestController controller = new RepairRequestController(
    view, validator, scheduler, statusTracker, repository, logger
);
```

---

## Design Patterns Used

| Pattern | Usage | Benefit |
|---------|-------|---------|
| **Singleton** | Database, Logger (single instance) | Resource efficiency |
| **Factory** | Entity creation methods | Encapsulation of creation logic |
| **Builder** | Complex objects (RepairRequest, CostEstimate) | Clean API, optional parameters |
| **Strategy** | Different repair types with different handlers | Extensibility |
| **Observer** | StatusTracker notifies observers | Loose coupling, event handling |
| **DAO/Repository** | RepairRepository abstracts persistence | Database independence |
| **Adapter** | FinancialSystemConnector, InventoryConnector | External system integration |
| **Dependency Injection** | All services injected | Testability, loose coupling |
| **MVC** | Controllers control Model-View interaction | Separation of concerns |

---

## SOLID Principles Implementation

### 1. Single Responsibility Principle (SRP)
Each class has ONE reason to change:
- `RepairValidator` - only validates repairs
- `RepairScheduler` - only schedules jobs
- `CostEstimationService` - only calculates costs
- `BillingService` - only handles billing

### 2. Open/Closed Principle (OCP)
Open for extension, closed for modification:
```java
// New repair types can be added WITHOUT modifying existing code
public enum RepairType {
    MECHANICAL, ELECTRICAL, PLUMBING, STRUCTURAL, OTHER
    // Add new types easily
}
```

### 3. Liskov Substitution Principle (LSP)
Subclasses can replace parent classes:
```java
IRepairValidator validator = new RepairValidator(...);
// Can substitute with different implementation without issues
```

### 4. Interface Segregation Principle (ISP)
Clients depend on small, specific interfaces:
```java
// Separate interfaces for different concerns
public interface IRepairValidator { }
public interface IRepairScheduler { }
public interface IStatusTracker { }
```

### 5. Dependency Inversion Principle (DIP)
Depend on abstractions, not concrete implementations:
```java
// Constructor takes interfaces, not concrete classes
public RepairValidator(IRepairRepository repository, IRepairLogger logger)
```

---

## GRASP Principles

### Information Expert
Classes know what they should know:
- `RepairValidator` knows validation rules
- `CostEstimationService` knows cost calculation
- `StatusTracker` knows status transitions

### Creator
Objects are created by classes with necessary knowledge:
- `RepairScheduler` creates RepairJob (knows scheduling)
- `BillingService` creates Receipt (knows billing)

### Controller
Controllers control the flow:
- `RepairRequestController` orchestrates request flow
- `RepairExecutionController` controls execution flow

### Low Coupling
Services depend on interfaces, not concrete classes:
```java
private final IRepairRepository repository;  // Interface
private final IRepairLogger logger;          // Interface
```

### High Cohesion
Related functionality grouped together:
- All validation in RepairValidator
- All scheduling in RepairScheduler
- All billing in BillingService

---

## Package Structure

```
com/repairs/
├── entities/              # Domain objects
│   ├── RepairRequest.java
│   ├── RepairJob.java
│   ├── SparePart.java
│   ├── CostEstimate.java
│   ├── Receipt.java
│   └── RepairLog.java
│
├── enums/                 # Enumerations
│   ├── RepairStatus.java
│   ├── RepairType.java
│   ├── BillingStatus.java
│   └── PaymentStatus.java
│
├── interfaces/
│   ├── model/             # Service interfaces
│   │   ├── IRepairValidator.java
│   │   ├── IRepairScheduler.java
│   │   ├── IRepairExecutor.java
│   │   ├── IStatusTracker.java
│   │   ├── IStatusObserver.java
│   │   ├── ICostEstimator.java
│   │   ├── IBillingService.java
│   │   ├── IFinancialSystemConnector.java
│   │   ├── IInventoryConnector.java
│   │   ├── IRepairLogger.java
│   │   └── IRepairRepository.java
│   │
│   └── view/              # View interfaces (passive)
│       ├── IRepairRequestIntakeView.java
│       ├── IRepairExecutionView.java
│       └── IBillingView.java
│
├── services/              # Business logic implementations
│   ├── RepairValidator.java
│   ├── RepairScheduler.java
│   ├── RepairExecutionService.java
│   ├── StatusTracker.java
│   ├── CostEstimationService.java
│   ├── BillingService.java
│   └── RepairLogger.java
│
├── repositories/          # Data access
│   └── RepairRepository.java (DAO pattern)
│
├── external/              # External system adapters
│   ├── FinancialSystemConnector.java
│   └── InventoryConnector.java
│
├── presenters/            # MVP controllers
│   ├── RepairRequestPresenter.java
│   ├── RepairExecutionPresenter.java
│   └── BillingPresenter.java
│
├── views/                 # View implementations
│   ├── ConsoleRepairRequestIntakeView.java
│   ├── ConsoleRepairExecutionView.java
│   └── ConsoleBillingView.java
│
└── RepairsSubSystemApplication.java  # Main entry point
```

---

## Entity Relationships

```
RepairRequest
    ├─ has RepairType
    ├─ has RepairStatus
    └─ creates → RepairJob
        ├─ has RepairStatus
        ├─ has assigned Technician
        ├─ uses → List<SparePart>
        ├─ has → CostEstimate
        │    ├─ has BillingStatus
        │    └─ calculates from parts & labor
        └─ generates → Receipt
             ├─ has PaymentStatus
             └─ links to → CostEstimate
```

---

## Key Features

### 1. Repair Request Lifecycle
- **REQUESTED** → Submit new repair
- **VALIDATED** → Check eligibility
- **SCHEDULED** → Assign date/time
- **IN_PROGRESS** → Technician executes
- **COMPLETED** → Repair done
- **FAILED/CANCELLED** → Terminal states

### 2. Status Tracking
- Observer pattern for real-time updates
- State transition validation
- Audit logging of all changes

### 3. Cost Estimation
- Labor cost based on repair type and duration
- Parts cost from inventory
- Tax calculation (configurable)
- Overhead markup

### 4. Billing & Payment
- Invoice generation
- Discount application
- Payment processing via financial connector
- Refund support
- Outstanding/overdue bill tracking

### 5. Inventory Integration
- Spare parts tracking
- Stock availability checking
- Reservation and release
- Caching for performance

### 6. Logging & Auditing
- File-based and database logging
- Severity levels (INFO, WARNING, ERROR, CRITICAL)
- Job-level log tracking
- Export to CSV

---

## Usage Examples

### Create and Process a Repair Request

```java
// Create repository and services
IRepairRepository repository = new RepairRepository();
IRepairLogger logger = new RepairLogger(repository, "./logs");
IRepairValidator validator = new RepairValidator(repository, logger);
IRepairScheduler scheduler = new RepairScheduler(repository, logger);

// Create repair request
RepairRequest request = new RepairRequest.Builder()
    .requestId("REQ-001")
    .customerId("C10001")
    .repairType(RepairType.ELECTRICAL)
    .description("Broken outlet")
    .createdDate(LocalDateTime.now())
    .build();

// Validate
if (validator.validate(request)) {
    // Save
    repository.saveRepairRequest(request);
    
    // Schedule
    LocalDateTime scheduledDate = scheduler.scheduleRepair(request);
    System.out.println("Scheduled for: " + scheduledDate);
}
```

### Execute a Repair

```java
// Get scheduled job
Optional<RepairJob> jobOpt = repository.findRepairJobById("JOB-001");

if (jobOpt.isPresent()) {
    RepairJob job = jobOpt.get();
    
    // Assign technician
    job.assignTechnician("TECH-001");
    
    // Add spare parts
    SparePart part = new SparePart("PART-001", "Outlet", 1, 
        new BigDecimal("25.50"), "Electrical");
    job.addSparePart(part);
    
    // Execute
    executor.executeRepair(job);
    
    // Update progress
    executor.updateProgress("JOB-001", 50);
    executor.updateProgress("JOB-001", 100);
    
    // Complete
    executor.completeRepair("JOB-001");
}
```

### Generate Bill and Process Payment

```java
// Get completed job
Optional<RepairJob> jobOpt = repository.findRepairJobById("JOB-001");

if (jobOpt.isPresent()) {
    RepairJob job = jobOpt.get();
    
    // Generate estimate
    CostEstimate estimate = costEstimator.estimateCost(job);
    System.out.println("Total: $" + estimate.getTotalCost());
    
    // Generate bill
    Receipt receipt = billingService.generateBill(job);
    
    // Apply discount (10%)
    BigDecimal discount = estimate.getTotalCost()
        .multiply(new BigDecimal("0.10"));
    receipt.applyDiscount(discount);
    
    // Process payment
    PaymentStatus status = financial.processPayment(receipt);
    if (status == PaymentStatus.PROCESSED) {
        receipt.markAsPaid("Credit Card");
        repository.updateReceipt(receipt);
    }
}
```

---

## Extending the System

### Adding a New Repair Type

```java
public enum RepairType {
    MECHANICAL("...", new BigDecimal("50.00")),
    ELECTRICAL("...", new BigDecimal("75.00")),
    PLUMBING("...", new BigDecimal("60.00")),
    STRUCTURAL("...", new BigDecimal("100.00")),
    CUSTOM_NEW("Custom Repair", new BigDecimal("80.00"))  // ← Add here
}
```

### Custom Service Implementation

```java
// Implement the interface
public class CustomRepairValidator implements IRepairValidator {
    @Override
    public boolean validate(RepairRequest request) {
        // Custom validation logic
        return true;
    }
    
    // Implement other methods...
}

// Use it
IRepairValidator validator = new CustomRepairValidator(...);
```

### Adding Status Change Observers

```java
// Create observer
IStatusObserver emailNotifier = new EmailNotificationObserver();
IStatusObserver smsNotifier = new SMSNotificationObserver();

// Register with tracker
statusTracker.registerObserver(emailNotifier);
statusTracker.registerObserver(smsNotifier);

// Now observers are notified on status changes
```

---

## Testing

The system is designed for easy unit testing:

```java
// Mock dependencies
IRepairRepository mockRepo = mock(IRepairRepository.class);
IRepairLogger mockLogger = mock(IRepairLogger.class);

// Test service in isolation
IRepairValidator validator = new RepairValidator(mockRepo, mockLogger);
assertTrue(validator.validate(request));
```

---

## Running the Application

```powershell
# From repairs-system directory
if (Test-Path .\bin) { Remove-Item .\bin -Recurse -Force }
New-Item -ItemType Directory -Path .\bin | Out-Null
$sources = Get-ChildItem -Path .\src -Filter *.java -Recurse | ForEach-Object { $_.FullName }
javac -d .\bin $sources
# Interactive UI (recommended)
java -cp .\bin com.repairs.RepairsSubSystemApplication

# Scripted demo mode
# java -cp .\bin com.repairs.RepairsSubSystemApplication demo
```

Interactive mode shows a menu where you can:
- Submit new repair requests via the intake view
- Start and update repair execution
- Generate bills and process payments
- View outstanding bills

If you run demo mode (`demo` argument), it executes predefined flows and exits.

### Database and Exception Subsystem Integration

- The repository now depends on `IDatabaseSubsystem`, allowing external DB integration.
- If no external DB implementation is provided, `DefaultDatabaseSubsystem` is used as fallback data storage.
- Inventory reads/writes are done through the database subsystem contract.
- Logging is persisted via repository into database-backed storage.
- Exception handling is delegated through `IExceptionHandler`.
- If no external exception subsystem is provided, `DefaultExceptionHandler` handles exceptions safely.

---

## Performance Considerations

- **In-Memory Cache**: StatusTracker and InventoryConnector use caching
- **Lazy Loading**: Repository fetches on-demand
- **Concurrent Collections**: Thread-safe operations
- **Configurable Cache Validity**: Inventory cache refreshes every 5 minutes

---

## Security Considerations

- All monetary values use BigDecimal (no floating-point errors)
- Status transitions are validated (no invalid state changes)
- External system calls are wrapped in try-catch
- Logging includes audit trail for compliance

---

## Future Enhancements

1. **Database Integration**: Replace in-memory repo with JPA/Hibernate
2. **REST API**: Add Spring Boot REST endpoints
3. **Web UI**: Replace console views with Spring MVC
4. **Advanced Scheduling**: Integration with calendar systems
5. **Mobile App**: Technician mobile app for on-site updates
6. **Analytics**: Dashboard with repair metrics
7. **Notifications**: Email/SMS status updates
8. **Multi-tenant**: Support multiple locations/branches

---

## Conclusion

This implementation demonstrates **production-quality Java architecture** combining:
- Clean, testable code
- Enterprise design patterns
- SOLID and GRASP principles
- Extensible, maintainable structure
- Real-world business logic

Perfect as a foundation for a complete repairs management system.

