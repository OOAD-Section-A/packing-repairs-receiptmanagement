ARCHITECTURE.md - Detailed Architecture Documentation

# Repairs Sub-System Architecture

## Executive Summary

This document provides a comprehensive overview of the architecture, design decisions, and implementation patterns used in the Repairs Sub-System.

**Status**: Production-Ready
**Patterns**: 8+ enterprise patterns
**Principles**: SOLID + GRASP
**Architecture Style**: Layered + MVP

---

## 1. ARCHITECTURAL LAYERS

### Layer 1: Presentation Layer (View)
**Responsibility**: Display data to users and capture input
**Key Classes**:
- `IRepairRequestIntakeView` - Form for new repairs
- `IRepairExecutionView` - Execution monitoring
- `IBillingView` - Billing information

**Characteristics**:
- PASSIVE - no business logic
- Only methods for display and input capture
- Independent of model implementation
- Easy to replace with web/mobile UI

**Console Implementations**:
- `ConsoleRepairRequestIntakeView`
- `ConsoleRepairExecutionView`
- `ConsoleBillingView`

---

### Layer 2: Presentation Logic Layer (Presenter/Controller)
**Responsibility**: Orchestrate flow between View and Model
**Key Classes**:
- `RepairRequestPresenter` - Manages request flow
- `RepairExecutionPresenter` - Manages execution flow
- `BillingPresenter` - Manages billing flow

**Characteristics**:
- Contains ALL business logic flow
- Never updates view directly without data
- Handles all error cases
- Logs all operations
- Coordinates between services

**Example Flow**:
```
User Input → Presenter → Validation → Persistence → Status Update → View Display
```

---

### Layer 3: Business Logic Layer (Services)
**Responsibility**: Implement business rules and domain logic
**Key Services**:

| Service | Purpose |
|---------|---------|
| `RepairValidator` | Validate repair requests |
| `RepairScheduler` | Schedule repairs for execution |
| `RepairExecutionService` | Execute and track repairs |
| `StatusTracker` | Track status changes (Observer) |
| `CostEstimationService` | Calculate repair costs |
| `BillingService` | Generate invoices |
| `RepairLogger` | Audit logging |

**Characteristics**:
- Stateless operations
- Depend on interfaces, not concrete classes
- Single responsibility
- Highly testable
- No knowledge of views

---

### Layer 4: Domain Layer (Entities)
**Responsibility**: Represent core business concepts
**Key Entities**:
- `RepairRequest` - A customer's repair request
- `RepairJob` - Scheduled and executing repair
- `CostEstimate` - Cost breakdown
- `Receipt` - Invoice for completed repair
- `SparePart` - Parts used in repairs
- `RepairLog` - Audit log entry

**Characteristics**:
- Immutable or mostly immutable
- Contain only business-relevant data
- Validate their own state
- No dependencies on services
- Use Builder pattern for complex objects

---

### Layer 5: Persistence & Integration Layer
**Responsibility**: Abstract data access and external systems
**Key Components**:

**Repository (DAO Pattern)**:
- `IRepairRepository` - Interface
- `RepairRepository` - In-memory implementation
- Easily replaceable with database

**External System Adapters**:
- `IFinancialSystemConnector` - Payment processing
- `IInventoryConnector` - Inventory management

**Characteristics**:
- Hide database/external API details
- Provide simple CRUD operations
- Support transactions
- Error handling and retry logic

---

## 2. MVP PATTERN IMPLEMENTATION

### Model
**What**: Business logic and data
**Who**: Services + Entities
**Example**:
```java
// Model layer - NO UI knowledge
public class RepairValidator implements IRepairValidator {
    public boolean validate(RepairRequest request) {
        // Pure business logic
        return customerEligible && repairTypeSupported;
    }
}
```

### View
**What**: Display data, capture input
**Who**: Interfaces + Console implementations
**Example**:
```java
// View layer - NO business logic
public interface IRepairRequestIntakeView {
    void displayValidationErrors(List<String> errors);
    RepairRequest getRepairRequestInput();
}
```

### Presenter
**What**: Coordinate Model and View
**Who**: Presenter classes
**Example**:
```java
// Presenter - orchestrates everything
public class RepairRequestPresenter {
    public void onRepairRequestSubmitted() {
        // Step 1: Get input from view
        RepairRequest request = view.getRepairRequestInput();
        
        // Step 2: Validate with model
        if (!validator.validate(request)) {
            view.displayValidationErrors(validator.getValidationErrors());
            return;
        }
        
        // Step 3: Persist with model
        repository.saveRepairRequest(request);
        
        // Step 4: Update view
        view.displaySuccess("Request saved!");
    }
}
```

### MVP Data Flow

```
┌──────────────┐
│              │ user clicks
│    VIEW      │◄──────────────────┐
│              │                   │
└──────┬───────┘                   │
       │ calls                      │ user
       │ presenter                  │ events
       │ methods                    │
       │                            │
┌──────▼─────────────────┐         │
│                        │         │
│   PRESENTER            │◄────────┘
│                        │
│  - Gets input from     │
│  - Calls services      │
│  - Updates view        │
│                        │
└──────┬─────────────────┘
       │
       │ calls
       │ model
       │ methods
       │
┌──────▼──────────────────┐
│                         │
│  MODEL (Services)       │
│                         │
│  - Business logic       │
│  - Data persistence     │
│  - External calls       │
│                         │
└────────────────────────┘
```

---

## 3. DESIGN PATTERNS DETAILED

### Pattern 1: Singleton
**Where**: Database, Logger (single instance)
**Why**: Ensure one instance, shared resource
**Example**:
```java
public class Database {
    private static Database instance;
    
    private Database() { }
    
    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
}
```
**Benefit**: Resource efficiency, global access point

---

### Pattern 2: Factory
**Where**: Entity creation in presenters and services
**Why**: Encapsulate object creation logic
**Example**:
```java
// Instead of: new RepairRequest(...)
// Use builder pattern (factory):
RepairRequest request = new RepairRequest.Builder()
    .requestId(id)
    .customerId(custId)
    .repairType(type)
    .build();
```
**Benefit**: Flexibility, parameter validation at creation

---

### Pattern 3: Builder
**Where**: Complex entities (RepairRequest, CostEstimate)
**Why**: Handle optional parameters cleanly
**Example**:
```java
public static class Builder {
    private String requestId;
    private String customerId;
    private RepairType repairType;
    
    public Builder requestId(String id) {
        this.requestId = id;
        return this;
    }
    
    public RepairRequest build() {
        return new RepairRequest(this);
    }
}
```
**Benefit**: Readable, fluent API, optional fields

---

### Pattern 4: Strategy
**Where**: Different repair types with different behaviors
**Why**: Switch algorithms at runtime
**Example**:
```java
// Different repair types have different costs
public enum RepairType {
    MECHANICAL(new BigDecimal("50")),   // $50/hr
    ELECTRICAL(new BigDecimal("75")),   // $75/hr
    PLUMBING(new BigDecimal("60"));     // $60/hr
}

// CostEstimationService uses repair type to calculate
BigDecimal cost = repairType.getHourlyRate() * hours;
```
**Benefit**: Easy to extend with new types

---

### Pattern 5: Observer
**Where**: StatusTracker notifies observers on status change
**Why**: Loose coupling between status changes and handlers
**Example**:
```java
public interface IStatusObserver {
    void onStatusChanged(String jobId, RepairStatus old, RepairStatus new);
}

public class StatusTracker {
    private List<IStatusObserver> observers = new ArrayList<>();
    
    public void updateStatus(String jobId, RepairStatus newStatus) {
        // ... update status ...
        
        // Notify all observers
        for (IStatusObserver observer : observers) {
            observer.onStatusChanged(jobId, oldStatus, newStatus);
        }
    }
}
```
**Benefit**: Decoupled event handling, multiple listeners

---

### Pattern 6: DAO/Repository
**Where**: RepairRepository abstracts data access
**Why**: Hide database details, enable easy testing
**Example**:
```java
// Interface - what we care about
public interface IRepairRepository {
    boolean saveRepairRequest(RepairRequest request);
    Optional<RepairRequest> findRepairRequestById(String id);
}

// Implementation - can be swapped
public class RepairRepository implements IRepairRepository {
    // Current: in-memory using Maps
    // Future: SQL database using JPA
    // Future: MongoDB using Spring Data
}
```
**Benefit**: Database independence, testability

---

### Pattern 7: Adapter
**Where**: FinancialSystemConnector, InventoryConnector
**Why**: Bridge to external APIs with different interfaces
**Example**:
```java
// External API has different interface
public class ExternalFinancialAPI {
    public PaymentResponse submitPayment(PaymentRequest req) { }
}

// We adapt it to our interface
public class FinancialSystemConnector implements IFinancialSystemConnector {
    private ExternalFinancialAPI externalAPI;
    
    @Override
    public PaymentStatus processPayment(Receipt receipt) {
        // Translate our Receipt to their PaymentRequest
        PaymentRequest req = new PaymentRequest(...);
        
        // Call external API
        PaymentResponse response = externalAPI.submitPayment(req);
        
        // Translate response back to our PaymentStatus
        return handlePaymentResponse(response);
    }
}
```
**Benefit**: Decouple from external APIs, easy to swap implementations

---

### Pattern 8: Dependency Injection
**Where**: All constructors
**Why**: Enable testing, loose coupling
**Example**:
```java
// Bad: Creating dependencies inside class
public class RepairValidator {
    private Database db = new Database(); // ← Tight coupling
}

// Good: Receive dependencies as parameters
public class RepairValidator {
    private IRepairRepository repository;
    
    public RepairValidator(IRepairRepository repository) {
        this.repository = repository; // ← Injected
    }
}
```
**Benefit**: Testability (can inject mocks), loose coupling

---

### Pattern 9: MVP
**Where**: Entire architecture
**Why**: Separate concerns, passive view, testable logic
**Example**: See MVP Pattern Implementation section above

---

## 4. SOLID PRINCIPLES DETAILED

### S - Single Responsibility Principle

**Definition**: A class should have only one reason to change

**RepairValidator**:
```java
public class RepairValidator implements IRepairValidator {
    // ONLY validates repairs
    // Changes ONLY when validation rules change
    public boolean validate(RepairRequest request) { }
}
```
- Change reason: Validation rules

**RepairScheduler**:
```java
public class RepairScheduler implements IRepairScheduler {
    // ONLY schedules repairs
    // Changes ONLY when scheduling logic changes
    public LocalDateTime scheduleRepair(RepairRequest request) { }
}
```
- Change reason: Scheduling algorithm

**Benefit**: Easy to modify, test, and understand

---

### O - Open/Closed Principle

**Definition**: Open for extension, closed for modification

**Example**:
```java
// Open for extension: Add new repair type
public enum RepairType {
    MECHANICAL, ELECTRICAL, PLUMBING,
    STRUCTURAL,
    NEW_TYPE  // ← Can add without changing code
}

// Closed for modification: Validation logic unchanged
public class RepairValidator {
    public boolean isRepairTypeSupported(String type) {
        try {
            RepairType.valueOf(type); // ← Works for any type
            return true;
        } catch (IllegalArgumentException) {
            return false;
        }
    }
}
```

**Benefit**: Extensibility without breaking existing code

---

### L - Liskov Substitution Principle

**Definition**: Subtypes must be substitutable for base types

**Example**:
```java
// Both implementations work identically
IRepairValidator validator1 = new RepairValidator(repo, logger);
IRepairValidator validator2 = new StrictRepairValidator(repo, logger);

// Can use either without changing code
boolean valid = validator1.validate(request); // Works
boolean valid = validator2.validate(request); // Also works
```

**Benefit**: Can swap implementations without breaking code

---

### I - Interface Segregation Principle

**Definition**: Clients should not depend on interfaces they don't use

**Example**:
```java
// BAD: One fat interface
public interface IRepairService {
    boolean validate(RepairRequest r);
    LocalDateTime schedule(RepairRequest r);
    void execute(RepairJob j);
    CostEstimate estimate(RepairJob j);
    Receipt bill(RepairJob j);
}

// GOOD: Segregated interfaces
public interface IRepairValidator { boolean validate(...); }
public interface IRepairScheduler { LocalDateTime schedule(...); }
public interface IRepairExecutor { void execute(...); }
public interface ICostEstimator { CostEstimate estimate(...); }
public interface IBillingService { Receipt bill(...); }
```

**Benefit**: Classes depend on interfaces they actually use

---

### D - Dependency Inversion Principle

**Definition**: Depend on abstractions, not concrete implementations

**Example**:
```java
// Bad: Depends on concrete class
public class RepairValidator {
    private Database database = new Database(); // ← Concrete
}

// Good: Depends on interface
public class RepairValidator {
    private IRepairRepository repository; // ← Abstract
    
    public RepairValidator(IRepairRepository repository) {
        this.repository = repository;
    }
}
```

**Benefit**: Can swap implementations (e.g., in-memory → database)

---

## 5. GRASP PRINCIPLES DETAILED

### Information Expert
"Assign responsibility to the class that has the information needed"

**Example**:
```java
// RepairRequest HAS the data needed to validate state transitions
public class RepairRequest {
    private RepairStatus status;
    
    public boolean canTransition(RepairStatus target) {
        return status.canTransitionTo(target); // ← Knows its own rules
    }
}

// StatusTracker KNOWS the current status of all jobs
public class StatusTracker {
    public Optional<RepairStatus> getStatus(String jobId) {
        return statusCache.containsKey(jobId) ? ... : repository.find(...);
    }
}
```

---

### Creator
"Create objects where the creator knows how"

**Example**:
```java
// RepairScheduler CREATES RepairJob (knows scheduling)
public class RepairScheduler {
    public LocalDateTime scheduleRepair(RepairRequest request) {
        // RepairScheduler knows WHEN to create a job
        RepairJob job = new RepairJob(...);
        // ...
        return scheduledDate;
    }
}

// BillingService CREATES Receipt (knows billing)
public class BillingService {
    public Receipt generateBill(RepairJob job) {
        // BillingService knows WHEN to create a receipt
        Receipt receipt = new Receipt(...);
        return receipt;
    }
}
```

---

### Controller
"Assign responsibility to coordinate/control operations"

**Example**:
```java
// RepairRequestPresenter CONTROLS the request flow
public class RepairRequestPresenter {
    public void onRepairRequestSubmitted() {
        RepairRequest request = view.getRepairRequestInput();        // Step 1
        if (!validator.validate(request)) return;                     // Step 2
        repository.saveRepairRequest(request);                        // Step 3
        LocalDateTime date = scheduler.scheduleRepair(request);       // Step 4
        view.displayScheduledDate(date.toString());                   // Step 5
    }
}
```

---

### Low Coupling
"Minimize dependencies between objects"

**Example**:
```java
// Service depends on interface, not concrete class
public class RepairValidator {
    private IRepairRepository repository; // ← Interface
    private IRepairLogger logger;         // ← Interface
}

// Can swap implementations without changing RepairValidator
IRepairRepository repo = new RepairRepository();      // Version 1
IRepairRepository repo = new DatabaseRepository();    // Version 2
IRepairRepository repo = new MockRepository();        // For tests
```

---

### High Cohesion
"Keep related functionality together"

**Example**:
```java
// All billing-related methods in one service
public class BillingService {
    public Receipt generateBill(RepairJob job) { }
    public boolean applyDiscount(String id, BigDecimal amount) { }
    public List<Receipt> getOutstandingBills() { }
    public List<Receipt> getOverdueBills() { }
}

// All execution-related methods in one service
public class RepairExecutionService {
    public void executeRepair(RepairJob job) { }
    public boolean pauseRepair(String jobId) { }
    public void completeRepair(String jobId) { }
    public void failRepair(String jobId, String reason) { }
}
```

---

## 6. STATE TRANSITIONS

### Repair Request State Machine

```
REQUESTED
    │
    ▼
VALIDATED
    │
    ▼
SCHEDULED
    │
    ▼
IN_PROGRESS ──────┐
    │             │
    ├─► COMPLETED │
    │             │
    ├─► FAILED    │
    │             │
    └─► CANCELLED ◄─┘
```

**Validation**: Checked in `RepairStatus.canTransitionTo()`

```java
public boolean canTransitionTo(RepairStatus targetStatus) {
    switch (this) {
        case REQUESTED:
            return targetStatus == VALIDATED || targetStatus == CANCELLED;
        case VALIDATED:
            return targetStatus == SCHEDULED || targetStatus == CANCELLED;
        case SCHEDULED:
            return targetStatus == IN_PROGRESS || targetStatus == CANCELLED;
        case IN_PROGRESS:
            return targetStatus == COMPLETED || targetStatus == FAILED 
                || targetStatus == CANCELLED;
        // Terminal states cannot transition
        default:
            return false;
    }
}
```

---

## 7. ERROR HANDLING STRATEGY

### Validation Errors
```java
// Collect all validation errors
public boolean validate(RepairRequest request) {
    validationErrors.clear();
    
    if (!isCustomerEligible(request.getCustomerId())) {
        validationErrors.add("Customer not eligible");
    }
    
    if (!isRepairTypeSupported(request.getRepairType())) {
        validationErrors.add("Repair type not supported");
    }
    
    return validationErrors.isEmpty();
}

// Presenter displays all errors
if (!validator.validate(request)) {
    view.displayValidationErrors(validator.getValidationErrors());
}
```

### State Transition Errors
```java
// Prevent invalid transitions
public void updateStatus(RepairStatus newStatus) {
    if (!status.canTransitionTo(newStatus)) {
        throw new IllegalStateException(
            String.format("Cannot transition from %s to %s", 
                status, newStatus)
        );
    }
    this.status = newStatus;
}
```

### External System Errors
```java
// Handle with graceful degradation
try {
    PaymentStatus status = financial.processPayment(receipt);
} catch (Exception e) {
    logger.log(jobId, "Payment failed: " + e.getMessage(), 
              "ERROR", "PAYMENT");
    view.displayError("Payment processing failed");
    return;
}
```

---

## 8. CONCURRENCY CONSIDERATIONS

**Thread-Safe Collections**:
```java
private final Map<String, RepairStatus> statusCache 
    = new ConcurrentHashMap<>();

private final List<IStatusObserver> observers 
    = Collections.synchronizedList(new ArrayList<>());
```

**Synchronized Methods**:
```java
// For critical operations
public synchronized void registerObserver(IStatusObserver observer) {
    observers.add(observer);
}
```

**No Shared Mutable State**:
- Services are stateless
- State stored in Repository/Database
- Multiple threads can safely access different jobs

---

## 9. TESTING STRATEGY

### Unit Testing Services
```java
@Test
public void testValidateValidRequest() {
    // Arrange
    IRepairRepository mockRepo = mock(IRepairRepository.class);
    IRepairLogger mockLogger = mock(IRepairLogger.class);
    RepairValidator validator = new RepairValidator(mockRepo, mockLogger);
    
    RepairRequest request = new RepairRequest.Builder()
        .requestId("REQ-001")
        .customerId("C10001")
        .repairType(RepairType.ELECTRICAL)
        .build();
    
    // Act
    boolean valid = validator.validate(request);
    
    // Assert
    assertTrue(valid);
}
```

### Integration Testing Presenters
```java
@Test
public void testRepairRequestFlow() {
    // Arrange
    IRepairRepository repo = new RepairRepository();
    RepairRequestPresenter presenter = new RepairRequestPresenter(
        mockView, validator, scheduler, statusTracker, repo, logger
    );
    
    // Act
    presenter.onRepairRequestSubmitted();
    
    // Assert
    verify(mockView).displaySuccess(contains("scheduled"));
}
```

---

## 10. PERFORMANCE CHARACTERISTICS

| Operation | Time Complexity | Space Complexity |
|-----------|-----------------|------------------|
| Find request by ID | O(1) | O(1) |
| Find requests by status | O(n) | O(n) |
| Schedule repair | O(n) for slot search | O(1) |
| Track status | O(m) for observer notify | O(m) |
| Generate estimate | O(p) for parts | O(1) |
| Find unpaid receipts | O(r) for filter | O(r) |

where: n=requests, m=observers, p=parts, r=receipts

---

## 11. SCALABILITY RECOMMENDATIONS

### For Large-Scale Deployment

1. **Database**: Replace RepairRepository with JPA/Hibernate
2. **Caching**: Add Redis for status/inventory cache
3. **Message Queue**: Use Kafka for async notifications
4. **API Gateway**: Add Spring Cloud Gateway
5. **Load Balancing**: Horizontal scaling with Kubernetes
6. **Monitoring**: Add ELK stack for logging
7. **Circuit Breaker**: Add Resilience4j for external calls

### Deployment Architecture
```
┌─────────────────────────────────────┐
│      API Gateway (LoadBalancer)     │
└──────────────────┬──────────────────┘
                   │
       ┌───────────┼───────────┐
       │           │           │
   ┌───▼──┐    ┌───▼──┐   ┌───▼──┐
   │Node1 │    │Node2 │   │Node3 │
   │(Pod) │    │(Pod) │   │(Pod) │
   └───┬──┘    └───┬──┘   └───┬──┘
       │           │           │
       └───────────┼───────────┘
                   │
            ┌──────▼──────┐
            │ PostgreSQL  │
            │ Database    │
            └─────────────┘
```

---

## Conclusion

This architecture provides a solid foundation for a production repairs management system, combining:
- Clear separation of concerns (MVP)
- Enterprise design patterns
- SOLID and GRASP principles
- High testability
- Easy extensibility
- Performance and scalability

The system is ready for immediate deployment with UI replacement and database integration as needed.
