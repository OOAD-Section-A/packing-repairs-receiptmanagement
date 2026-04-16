# Receipt Management

This project converts the supplied component diagram into a concrete class design and a working Java implementation.

## Design summary

The image was translated into an object-oriented design with these main responsibilities:

- `ReceiptGenerationService` acts as the controller/facade for the receipt flow.
- `PaymentValidation`, `ReceiptFormatter`, and `Logger` are abstractions so the service depends on interfaces rather than concrete classes.
- `NotificationSystemInterface`, `ExceptionHandlerInterface`, and `DatabaseInterface` represent external systems.
- `ReceiptDocument`, `PaymentDetails`, and `CustomerInformation` are domain models.
- `InvalidPaymentException` models validation failures explicitly.

## Principles applied

- SOLID:
  - Single Responsibility: validation, formatting, logging, notification, and exception handling are separated.
  - Open/Closed: new validators, formatters, or notification channels can be added without changing the service.
  - Liskov Substitution: all implementations respect their interface contracts.
  - Interface Segregation: each port exposes a focused API.
  - Dependency Inversion: the application depends on interfaces, with concrete adapters injected in `Main`.
- GRASP:
  - Controller: `ReceiptGenerationService`
  - Information Expert: validation stays in `StandardPaymentValidation`, formatting in `PlainTextReceiptFormatter`
  - Low Coupling / High Cohesion: responsibilities are isolated
  - Indirection / Pure Fabrication: logger and external ports reduce direct coupling to infrastructure
- Patterns used where they add value:
  - Strategy via `PaymentValidation`, `ReceiptFormatter`, `NotificationSystemInterface`, and `ExceptionHandlerInterface`
  - Builder via `ReceiptDocument.Builder`
  - Dependency Injection through constructor wiring in `Main`

## Class diagram

The generated class diagram is available at:

- `docs/receipt-management-class-diagram.puml`

## Run

Compile with `javac`:

```powershell
$files = Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $files
java -cp out com.receiptmanagement.Main
```

You can also use Maven if your environment already has the standard compiler plugin available:

```powershell
mvn compile
java -cp target/classes com.receiptmanagement.Main
```

