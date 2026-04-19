# Receipt Management System - Swing GUI Layout

## Main Window Layout

```
┌────────────────────────────────────────────────────────────────────────────────────┐
│  Receipt Management System                                            [_][□][X]    │
├────────────────────────────────────────────────────────────────────────────────────┤
│  ┌─ Generate Receipt ─┬─ View Logs ─┐                                            │
│  │                    │              │                                            │
│  │ ┌──────────────────┬──────────────────────────────────────────────────────┐   │
│  │ │  Customer Details│  Generated Receipt:                                  │   │
│  │ ├──────────────────┤                                                      │   │
│  │ │ Customer ID:     │ ┌────────────────────────────────────────────────┐  │   │
│  │ │ [CUST-1001....] │ │ ════════════════════════════════════════════  │  │   │
│  │ │                  │ │ RECEIPT DOCUMENT                             │  │   │
│  │ │ Full Name:       │ │ ════════════════════════════════════════════  │  │   │
│  │ │ [Anirudh Sharma.]│ │                                              │  │   │
│  │ │                  │ │ Receipt ID: REC-20260416-001                │  │   │
│  │ │ Email:           │ │ Date: April 16, 2026, 09:45:32              │  │   │
│  │ │ [anirudh.@...]   │ │                                              │  │   │
│  │ │                  │ │ CUSTOMER DETAILS:                            │  │   │
│  │ │ Payment Details  │ │ ──────────────────────────────────────────  │  │   │
│  │ │ ┌────────────────┤ │ Customer ID:     CUST-1001                 │  │   │
│  │ │ │ Payment ID:    │ │ Name:            Anirudh Sharma             │  │   │
│  │ │ │ [PAY-2001...]  │ │ Email:           anirudh.sharma@...        │  │   │
│  │ │ │                │ │                                              │  │   │
│  │ │ │ Amount:        │ │ PAYMENT DETAILS:                             │  │   │
│  │ │ │ [2499.99...]   │ │ ──────────────────────────────────────────  │  │   │
│  │ │ │                │ │ Payment ID:      PAY-2001                   │  │   │
│  │ │ │ Currency:      │ │ Amount:          $2,499.99 INR              │  │   │
│  │ │ │ [INR        ▼] │ │ Payment Method:  UPI                        │  │   │
│  │ │ │                │ │ Status:          ✓ Completed                │  │   │
│  │ │ │ Payment Method:│ │                                              │  │   │
│  │ │ │ [UPI        ▼] │ │ ════════════════════════════════════════════  │  │   │
│  │ │ │                │ │                                              │  │   │
│  │ │ │ ☑ Completed   │ └────────────────────────────────────────────┘  │   │
│  │ │ └────────────────┤                                                 │   │
│  │ │                  │ [↑ Scroll area ↓]                              │   │
│  │ │ ┌────────────────┤                                                 │   │
│  │ │ │ [Generate]     │ Status: Receipt generated successfully: REC... │   │
│  │ │ │ [Clear Form]   │ ▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔ │   │
│  │ │ └────────────────┤                                                 │   │
│  │ │                  │                                                  │   │
│  │ └──────────────────┴──────────────────────────────────────────────────┘   │
│  │                                                                            │
│  └────────────────────────────────────────────────────────────────────────────┘
│  Ready                                                                         │
└────────────────────────────────────────────────────────────────────────────────┘
```

## View Logs Tab Layout

```
┌────────────────────────────────────────────────────────────────────────────────────┐
│  Receipt Management System                                            [_][□][X]    │
├────────────────────────────────────────────────────────────────────────────────────┤
│  ┌─ Generate Receipt ─┬─ View Logs ─┐                                            │
│                       │              │                                            │
│                       │ Application Logs:                                         │
│                       │ ┌──────────────────────────────────────────────────────┐  │
│                       │ │ [2026-04-16 09:30:45] Payment PAY-2001 validated   │  │
│                       │ │ [2026-04-16 09:30:45] Receipt REC-20260416-001    │  │
│                       │ │                       created for CUST-1001       │  │
│                       │ │ [2026-04-16 09:30:45] Notification sent to        │  │
│                       │ │                       anirudh.sharma@example.com  │  │
│                       │ │ [2026-04-16 09:31:12] Payment PAY-2002 failed     │  │
│                       │ │                       validation: Amount is zero  │  │
│                       │ │ [2026-04-16 09:31:15] Payment PAY-2003 validated  │  │
│                       │ │ [2026-04-16 09:31:15] Receipt REC-20260416-002    │  │
│                       │ │                       created for CUST-1001       │  │
│                       │ │                                                    │  │
│                       │ │ [↑ Scroll area ↓]                                 │  │
│                       │ │                                                    │  │
│                       │ └──────────────────────────────────────────────────────┘  │
│                       │                      [Refresh Logs]                      │
│                       │                                                           │
│                       └───────────────────────────────────────────────────────────┘
│  Ready                                                                            │
└────────────────────────────────────────────────────────────────────────────────────┘
```

## Input Form - Detailed View

```
╔════════════════════════════════════════════╗
║         Customer & Payment Information      ║
╠════════════════════════════════════════════╣
║                                            ║
║  ┌──── Customer Details ────────────────┐  ║
║  │ Customer ID:     [CUST-1001       ] │  ║
║  │ Full Name:       [Anirudh Sharma  ] │  ║
║  │ Email:           [anirudh@...     ] │  ║
║  └────────────────────────────────────┘  ║
║                                            ║
║  ┌──── Payment Details ──────────────────┐ ║
║  │ Payment ID:      [PAY-2001         ] │ ║
║  │ Amount:          [2499.99          ] │ ║
║  │ Currency:        [INR           ▼  ] │ ║
║  │ Payment Method:  [UPI           ▼  ] │ ║
║  │ Payment Completed: ☑                 │ ║
║  └────────────────────────────────────┘  ║
║                                            ║
║  ┌──── Actions ──────────────────────────┐ ║
║  │  [ Generate Receipt ]  [ Clear Form ]  │ ║
║  └────────────────────────────────────┘  ║
║                                            ║
╚════════════════════════════════════════════╝
```

## Status Messages

### Success Status
```
┌─────────────────────────────────────────────────────────────────┐
│ ✓ Receipt generated successfully: REC-20260416-001              │  (Green)
└─────────────────────────────────────────────────────────────────┘
```

### Error Status
```
┌─────────────────────────────────────────────────────────────────┐
│ ✗ Error: All fields are required                                │  (Red)
└─────────────────────────────────────────────────────────────────┘
```

### Informational Status
```
┌─────────────────────────────────────────────────────────────────┐
│ Form cleared                                                     │  (Black)
└─────────────────────────────────────────────────────────────────┘
```

## User Workflow

```
START
  │
  ▼
┌─────────────────────────┐
│  Application Launches   │
│  (Swing GUI opens)      │
└────────────┬────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│ User Input: Customer & Payment Data  │
│  - Customer ID, Name, Email          │
│  - Payment ID, Amount, Currency      │
│  - Payment Method, Status            │
└────────────┬─────────────────────────┘
             │
             ▼
     ┌──────────────────┐
     │ Click Generate   │
     │ Receipt Button   │
     └────────┬─────────┘
              │
              ▼
    ┌─────────────────────────┐
    │ Validate Input          │
    │ - Check all fields      │
    │ - Validate amount       │
    │ - Check format          │
    └────────┬────────────────┘
             │
        ┌────┴───────┐
        │             │
   Invalid         Valid
        │             │
        ▼             ▼
  ┌────────────┐  ┌──────────────────────┐
  │ Show Error │  │ Generate Receipt     │
  │ Message    │  │ - Format output      │
  │ in Red     │  │ - Log transaction    │
  └────────────┘  │ - Show notification  │
        │         │ - Display receipt    │
        │         └──────────┬───────────┘
        │                    │
        │                    ▼
        │         ┌─────────────────────┐
        │         │ Display Receipt in  │
        │         │ Right Panel         │
        │         │ + Success Status    │
        │         └──────────┬──────────┘
        │                    │
        │                    ▼
        │         ┌─────────────────────┐
        │         │ User can:           │
        │         │ - View Logs tab     │
        │         │ - Clear form        │
        │         │ - Generate again    │
        │         └────────┬────────────┘
        │                  │
        └──────────┬───────┘
                   │
                   ▼
              ┌─────────────┐
              │ Application │
              │   Ready     │
              └─────────────┘
```

## Key UI Elements

### Currencies Available
- INR (Indian Rupee)
- USD (US Dollar)
- EUR (Euro)
- GBP (British Pound)
- JPY (Japanese Yen)

### Payment Methods Available
- UPI (Unified Payments Interface)
- CHEQUE
- CREDIT_CARD
- DEBIT_CARD
- NET_BANKING

### Action Buttons
- **Generate Receipt** - Process form and create receipt
- **Clear Form** - Reset all fields to defaults
- **Refresh Logs** - Reload transaction logs

### Display Areas
- **Receipt Display** - Formatted receipt output
- **Logs Display** - All transaction records
- **Status Bar** - Real-time feedback

## Color Scheme

- **White**: Input fields and display areas
- **Light Gray**: Panel borders and separators
- **Black**: Default text and informational messages
- **Red**: Error messages
- **Green**: Success messages
- **Blue**: Buttons and interactive elements

## Responsive Features

- Resizable window (minimum 1200x800)
- Scrollable text areas for long content
- Split panes for flexible layout adjustment
- All components scale proportionally
