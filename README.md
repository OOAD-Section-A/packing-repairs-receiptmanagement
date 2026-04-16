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

## User Interfaces

### Swing GUI (Recommended)

The application now features a modern Swing-based graphical user interface:

**Main Window Features:**
- **Generate Receipt Tab**: Input customer and payment information, generate receipts with real-time feedback
- **View Logs Tab**: Browse all transaction logs and system events

**Customer Information Section:**
- Customer ID
- Full Name
- Email Address

**Payment Details Section:**
- Payment ID
- Amount (with decimal precision)
- Currency (dropdown: INR, USD, EUR, GBP, JPY)
- Payment Method (dropdown: UPI, CHEQUE, CREDIT_CARD, DEBIT_CARD, NET_BANKING)
- Payment Status (completed checkbox)

**Features:**
- Real-time receipt preview
- Status bar showing operation results
- Form validation with error messages
- Transaction log viewer
- Clear form functionality
- Notification dialogs for sent receipts

### CLI Interface

For testing and automated workflows, the original CLI interface is preserved in `CliDemo.java`.

## Run the Application

### Option 1: Run the Swing GUI (Default)

Using Maven:
```powershell
mvn compile
mvn exec:java -Dexec.mainClass="com.receiptmanagement.Main"
```

Or directly with Java:
```powershell
java -cp target/classes com.receiptmanagement.Main
```

### Option 2: Run the CLI Demo

For testing with predefined payment scenarios:
```powershell
java -cp target/classes com.receiptmanagement.CliDemo
```

### Compilation

Compile with Maven (recommended):
```powershell
mvn compile
```

Or with javac:
```powershell
$files = Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $files
java -cp out com.receiptmanagement.Main
```

## Project Structure

```
├── src/main/java/com/receiptmanagement/
│   ├── Main.java                          # Entry point (launches GUI)
│   ├── CliDemo.java                       # CLI test scenarios
│   ├── application/                       # Business logic layer
│   │   ├── Logger.java                    # Logging interface
│   │   ├── PaymentValidation.java         # Validation interface
│   │   ├── ReceiptFormatter.java          # Formatting interface
│   │   └── ReceiptGenerationService.java  # Main service
│   ├── domain/                            # Domain models and exceptions
│   │   ├── exception/
│   │   │   └── InvalidPaymentException.java
│   │   └── model/
│   │       ├── CustomerInformation.java
│   │       ├── PaymentDetails.java
│   │       └── ReceiptDocument.java
│   ├── infrastructure/                    # Implementation layer
│   │   ├── database/
│   │   │   └── InMemoryDatabase.java
│   │   ├── exception/
│   │   │   └── ConsoleExceptionHandler.java
│   │   ├── formatter/
│   │   │   └── PlainTextReceiptFormatter.java
│   │   ├── logging/
│   │   │   └── DatabaseLogger.java
│   │   ├── notification/
│   │   │   └── ConsoleNotificationSystem.java
│   │   └── validation/
│   │       └── StandardPaymentValidation.java
│   ├── port/                              # Interface contracts
│   │   ├── DatabaseInterface.java
│   │   ├── ExceptionHandlerInterface.java
│   │   └── NotificationSystemInterface.java
│   └── ui/                                # Swing GUI components
│       ├── ReceiptManagementUI.java       # Main GUI window
│       └── GuiNotificationSystem.java     # GUI notification adapter
└── pom.xml
```

## Technology Stack

- **Language**: Java 17
- **UI Framework**: Swing
- **Build Tool**: Maven
- **Architecture**: Hexagonal Architecture (Ports & Adapters)


