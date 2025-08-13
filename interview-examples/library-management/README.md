# Library Management System

## Problem Statement

Design a library management system that can:
- Manage books, members, and librarians
- Handle book borrowing and returning
- Track due dates and calculate fines
- Search books by various criteria
- Manage different types of users

## Requirements

### Functional Requirements
1. Add/remove books and members
2. Issue and return books
3. Search books by title, author, ISBN
4. Calculate fines for overdue books
5. Reserve books that are currently issued
6. Different user types with different privileges

### Non-Functional Requirements
1. System should handle concurrent operations
2. Fast search capabilities
3. Data persistence
4. Scalable architecture

## Classes and Relationships

### Core Classes
- `Library` - Main system controller
- `Book` - Represents a book
- `Member` - Library member
- `Librarian` - Library staff
- `BookIssue` - Tracks book borrowing
- `Fine` - Manages overdue fines

### User Types
- `Member` - Regular library member
- `Librarian` - Library staff with admin privileges
- `Student` - Student member with extended borrowing period

## Design Patterns Used

1. **Factory Pattern** - For creating different user types
2. **Observer Pattern** - For due date notifications
3. **Strategy Pattern** - For different fine calculation strategies
4. **Command Pattern** - For different operations

## Key Features

1. **Multi-user Support**: Different types of users with different privileges
2. **Book Reservation**: Reserve books that are currently issued
3. **Fine Management**: Automatic fine calculation for overdue books
4. **Search Functionality**: Search books by multiple criteria
5. **Notification System**: Notify users about due dates

## Implementation Highlights

- Thread-safe operations using synchronization
- Efficient search using HashMap indexing
- Flexible fine calculation system
- Extensible user type system