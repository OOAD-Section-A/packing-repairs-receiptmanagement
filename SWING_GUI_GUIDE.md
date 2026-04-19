# Swing GUI Quick Start Guide

## Running the Application

The application now features a user-friendly Swing-based graphical interface. Here's how to get started:

### Launch the GUI

**Option 1: Using Maven**
```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.receiptmanagement.Main"
```

**Option 2: Direct Java Execution**
```bash
java -cp target/classes com.receiptmanagement.Main
```

**Option 3: Using the JAR file**
```bash
mvn package
java -jar target/receipt-management-1.0.0.jar
```

## User Interface Overview

### Main Window

The application opens with a tabbed interface containing two main sections:

#### Tab 1: Generate Receipt

This is the primary interface for creating receipts.

**Left Panel - Input Form:**

1. **Customer Details Section**
   - **Customer ID**: Unique identifier for the customer (e.g., `CUST-1001`)
   - **Full Name**: Customer's full name
   - **Email**: Customer's email address

2. **Payment Details Section**
   - **Payment ID**: Unique identifier for the payment (e.g., `PAY-2001`)
   - **Amount**: Payment amount (e.g., `2499.99`)
   - **Currency**: Select from dropdown (INR, USD, EUR, GBP, JPY)
   - **Payment Method**: Select from dropdown (UPI, CHEQUE, CREDIT_CARD, DEBIT_CARD, NET_BANKING)
   - **Payment Completed**: Checkbox to indicate if payment is completed

3. **Action Buttons**
   - **Generate Receipt**: Process the form and generate a receipt
   - **Clear Form**: Reset all fields to default values

**Right Panel - Receipt Output:**

After clicking "Generate Receipt", the formatted receipt content appears in this text area.

#### Tab 2: View Logs

Displays all transaction logs and system events:
- **Transaction Records**: Shows all successful and failed transactions
- **Timestamps**: Each log entry includes when the operation occurred
- **Refresh Button**: Click to reload the latest logs

### Status Bar

Located at the bottom of the window:
- Shows the result of your last action
- **Green text**: Success messages
- **Red text**: Error messages
- **Black text**: Informational messages

## Workflow Example

### Step 1: Enter Customer Information
```
Customer ID:    CUST-1002
Full Name:      John Doe
Email:          john.doe@example.com
```

### Step 2: Enter Payment Details
```
Payment ID:     PAY-2005
Amount:         1500.00
Currency:       USD
Payment Method: CREDIT_CARD
Payment Completed: ✓ (checked)
```

### Step 3: Generate Receipt
- Click the "Generate Receipt" button
- The receipt will appear in the right panel
- You'll see a success message in the status bar
- A notification dialog may appear

### Step 4: View Transaction Logs
- Switch to the "View Logs" tab
- Click "Refresh Logs" to see all transactions
- Each transaction shows the operation details and timestamp

## Validation Rules

The application validates payment information:

1. **All fields are required** - Don't leave any field blank
2. **Valid Amount** - Amount must be a positive number with up to 2 decimal places
3. **Currency** - Must be one of the predefined currencies
4. **Email Format** - Should be a valid email address (basic validation)
5. **Payment Status** - Unchecked status may result in validation failures

## Error Handling

If validation fails:
- An error message appears in the status bar (in red)
- An error dialog may pop up with details
- Review your input and try again

### Common Issues

| Issue | Solution |
|-------|----------|
| "All fields are required" | Check that no field is empty |
| "Invalid amount format" | Enter a numeric value (e.g., 100, 100.50) |
| "Receipt generation failed" | Check the error message and correct invalid fields |

## Default Values

The application comes pre-populated with sample data:
- Customer ID: `CUST-1001`
- Name: `Anirudh Sharma`
- Email: `anirudh.sharma@example.com`
- Payment ID: `PAY-2001`
- Amount: `2499.99`
- Currency: `INR`
- Payment Method: `UPI`
- Status: Completed ✓

You can modify any of these values before generating a receipt.

## Features

### Receipt Display
- Shows complete formatted receipt with:
  - Receipt ID
  - Customer information
  - Payment details
  - Amount and currency
  - Payment method
  - Issue date and time

### Transaction Logging
- All transactions are logged automatically
- View logs anytime in the "View Logs" tab
- Logs include successful and failed transactions

### Notifications
- Success notifications appear as dialog boxes
- Shows receipt ID and customer email
- Confirms that the receipt was generated

### Form Management
- Pre-filled sample data for easy testing
- Clear button to reset the form
- Input validation with helpful error messages

## Testing Scenarios

### Scenario 1: Valid Payment
1. Keep default values
2. Click "Generate Receipt"
3. Receipt displays successfully

### Scenario 2: Invalid Amount
1. Change amount to "0" or negative value
2. Click "Generate Receipt"
3. See validation error in red

### Scenario 3: Different Currency
1. Change currency to "USD"
2. Keep other fields as is
3. Generate and see USD receipt

## Tips & Tricks

- Use the "Clear Form" button to reset between different customers
- Check "View Logs" tab to audit all transactions
- The status bar shows the most recent action result
- Try different payment methods to test various scenarios

## Troubleshooting

### Application Won't Start
- Ensure Java 17 or higher is installed
- Verify Maven is configured correctly
- Check that the project compiled successfully with `mvn compile`

### GUI Elements Look Small/Large
- The application uses standard Swing defaults
- Consider adjusting your system's display scaling

### Receipt Not Showing
- Verify all input fields are filled
- Check the status bar for error messages
- Look at the logs tab to see what failed

## Architecture Notes

The Swing UI integrates with the existing domain architecture:
- Uses the same `ReceiptGenerationService`
- Implements `NotificationSystemInterface` for GUI dialogs
- All validation and formatting logic remains unchanged
- Maintains separation of concerns (UI layer, domain, infrastructure)

## Next Steps

Once comfortable with basic operation:
1. Try different customer data
2. Experiment with various payment methods
3. Monitor the logs to understand the system flow
4. Review the source code to understand the architecture
