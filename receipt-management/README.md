# Receipt Management

This project demonstrates a receipt management system with both CLI and GUI interfaces.

## Overview

The Receipt Management System provides a complete solution for generating and managing payment receipts with validation, formatting, logging, and notification capabilities.

### Key Features

- **Payment Validation**: Validates payment details before generating receipts
- **Receipt Generation**: Creates formatted receipt documents
- **Logging**: Persists all operations to an in-memory database
- **Error Handling**: Gracefully handles validation failures
- **Multiple Interfaces**: Both CLI and modern Swing-based GUI available

## Design summary

The project follows clean architecture principles with these main responsibilities:

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
  - Dependency Inversion: the application depends on interfaces, with concrete adapters injected.
- GRASP:
  - Controller: `ReceiptGenerationService`
  - Information Expert: validation stays in `StandardPaymentValidation`, formatting in `PlainTextReceiptFormatter`
  - Low Coupling / High Cohesion: responsibilities are isolated
  - Indirection / Pure Fabrication: logger and external ports reduce direct coupling to infrastructure
- Patterns used where they add value:
  - Strategy via `PaymentValidation`, `ReceiptFormatter`, `NotificationSystemInterface`, and `ExceptionHandlerInterface`
  - Builder via `ReceiptDocument.Builder`
  - Dependency Injection through constructor wiring

## Class diagram

The generated class diagram is available at:

- `docs/receipt-management-class-diagram.puml`

## Run

From the project folder:

```powershell
cd receipt-management
```

Compile with Maven:

```powershell
mvn compile
```

Run the console demo:

```powershell
java -cp target\classes com.receiptmanagement.Main
```

Run the alternate CLI demo:

```powershell
java -cp target\classes com.receiptmanagement.CliDemo
```

Run the Swing GUI:

```powershell
java -cp target\classes com.receiptmanagement.ui.ReceiptManagementUI
```

You can also compile without Maven:

```powershell
$files = Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $files
java -cp out com.receiptmanagement.Main
```
