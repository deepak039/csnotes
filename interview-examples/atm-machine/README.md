# ATM Machine System

## Problem Statement

Design an ATM machine system that can:
- Authenticate users with card and PIN
- Handle different types of transactions (withdraw, deposit, balance inquiry)
- Manage cash inventory
- Handle multiple denominations
- Maintain transaction history

## Requirements

### Functional Requirements
1. Card authentication and PIN verification
2. Cash withdrawal with denomination management
3. Cash deposit functionality
4. Balance inquiry
5. Transaction history
6. Receipt generation
7. Account management

### Non-Functional Requirements
1. Secure authentication
2. Thread-safe operations for concurrent users
3. Proper error handling
4. Transaction logging
5. Cash management

## Classes and Relationships

### Core Classes
- `ATM` - Main ATM controller
- `Card` - Represents ATM card
- `Account` - Bank account
- `Transaction` - Individual transaction
- `CashDispenser` - Manages cash inventory
- `Receipt` - Transaction receipt

### Transaction Types
- `WithdrawTransaction` - Cash withdrawal
- `DepositTransaction` - Cash deposit
- `BalanceInquiryTransaction` - Balance check
- `TransferTransaction` - Money transfer

## Design Patterns Used

1. **State Pattern** - For ATM states (idle, card inserted, PIN entered, etc.)
2. **Strategy Pattern** - For different transaction types
3. **Factory Pattern** - For creating transactions
4. **Command Pattern** - For transaction operations

## Key Features

1. **Multi-denomination Support**: Handle different cash denominations
2. **Secure Authentication**: Card and PIN verification
3. **Transaction Logging**: Complete audit trail
4. **Cash Management**: Track and manage cash inventory
5. **Receipt Generation**: Print transaction receipts
6. **Error Handling**: Proper error messages and recovery

## Implementation Highlights

- State machine for ATM operations
- Thread-safe cash management
- Comprehensive transaction logging
- Flexible denomination handling