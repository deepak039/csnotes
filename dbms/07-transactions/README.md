# Transactions in DBMS

## What is a Transaction?

A transaction is a logical unit of work that consists of one or more database operations. It represents a complete business operation that must be executed as a single, indivisible unit.

### Example:
```sql
-- Bank transfer transaction
BEGIN TRANSACTION;
UPDATE Accounts SET Balance = Balance - 100 WHERE AccountID = 1;  -- Debit
UPDATE Accounts SET Balance = Balance + 100 WHERE AccountID = 2;  -- Credit
INSERT INTO TransactionLog VALUES (NOW(), 'Transfer', 100, 1, 2);
COMMIT;
```

## ACID Properties

### 1. Atomicity

#### Definition:
All operations in a transaction must complete successfully, or none at all. It's an "all-or-nothing" principle.

#### Example:
```sql
BEGIN TRANSACTION;
UPDATE Inventory SET Quantity = Quantity - 1 WHERE ProductID = 101;
INSERT INTO Orders VALUES (1001, 101, 1, 'Pending');

-- If either operation fails, both are rolled back
COMMIT;  -- Both succeed
-- OR
ROLLBACK;  -- Both fail
```

#### Implementation:
- **Write-Ahead Logging (WAL)**: Log changes before applying them
- **Shadow Paging**: Keep original pages until transaction commits
- **Rollback Segments**: Store undo information

### 2. Consistency

#### Definition:
Database must remain in a valid state before and after transaction execution. All integrity constraints must be satisfied.

#### Example:
```sql
-- Constraint: Account balance cannot be negative
BEGIN TRANSACTION;
UPDATE Accounts SET Balance = Balance - 500 WHERE AccountID = 1;
-- If this would make balance negative, transaction fails
COMMIT;
```

#### Types of Consistency:
- **Entity Integrity**: Primary key constraints
- **Referential Integrity**: Foreign key constraints
- **Domain Integrity**: Data type and check constraints
- **User-Defined Integrity**: Business rules

### 3. Isolation

#### Definition:
Concurrent transactions should not interfere with each other. Each transaction should execute as if it's the only transaction in the system.

#### Isolation Levels:

##### Read Uncommitted (Level 0)
```sql
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
-- Can read uncommitted changes from other transactions
-- Allows: Dirty reads, Non-repeatable reads, Phantom reads
```

##### Read Committed (Level 1)
```sql
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
-- Can only read committed changes
-- Prevents: Dirty reads
-- Allows: Non-repeatable reads, Phantom reads
```

##### Repeatable Read (Level 2)
```sql
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
-- Same data read multiple times returns same result
-- Prevents: Dirty reads, Non-repeatable reads
-- Allows: Phantom reads
```

##### Serializable (Level 3)
```sql
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
-- Highest isolation level
-- Prevents: Dirty reads, Non-repeatable reads, Phantom reads
```

### 4. Durability

#### Definition:
Once a transaction is committed, its effects are permanent and survive system failures.

#### Implementation:
- **Write-Ahead Logging**: Changes logged to stable storage
- **Force Policy**: Log records written before commit
- **Database Backups**: Regular backup procedures
- **Recovery Mechanisms**: Ability to restore committed transactions

## Transaction States

### State Diagram:
```
[Active] → [Partially Committed] → [Committed]
    ↓              ↓
[Failed] ← [Aborted]
```

### 1. Active State
- Transaction is executing
- Read and write operations are performed
- Normal execution state

### 2. Partially Committed State
- All operations completed
- Waiting for final commit
- Changes not yet permanent

### 3. Committed State
- Transaction completed successfully
- Changes are permanent
- Cannot be undone

### 4. Failed State
- Transaction cannot continue normal execution
- Due to hardware/software failure
- Must be aborted

### 5. Aborted State
- Transaction has been rolled back
- Database restored to state before transaction
- Can be restarted or killed

## Concurrency Problems

### 1. Dirty Read

#### Problem:
Reading uncommitted changes from another transaction.

#### Example:
```sql
-- Transaction T1
BEGIN TRANSACTION;
UPDATE Accounts SET Balance = 1000 WHERE AccountID = 1;
-- T2 reads Balance = 1000 here (dirty read)
ROLLBACK;  -- T1 rolls back, but T2 already read wrong value

-- Transaction T2
SELECT Balance FROM Accounts WHERE AccountID = 1;  -- Reads 1000
-- But actual committed value is different
```

### 2. Non-Repeatable Read

#### Problem:
Reading the same data twice in a transaction returns different values.

#### Example:
```sql
-- Transaction T1
BEGIN TRANSACTION;
SELECT Balance FROM Accounts WHERE AccountID = 1;  -- Reads 500
-- T2 updates and commits here
SELECT Balance FROM Accounts WHERE AccountID = 1;  -- Reads 600 (different!)
COMMIT;

-- Transaction T2
UPDATE Accounts SET Balance = 600 WHERE AccountID = 1;
COMMIT;
```

### 3. Phantom Read

#### Problem:
New rows appear between two identical queries in the same transaction.

#### Example:
```sql
-- Transaction T1
BEGIN TRANSACTION;
SELECT COUNT(*) FROM Orders WHERE Status = 'Pending';  -- Returns 5
-- T2 inserts new pending order here
SELECT COUNT(*) FROM Orders WHERE Status = 'Pending';  -- Returns 6 (phantom!)
COMMIT;

-- Transaction T2
INSERT INTO Orders VALUES (1001, 'Pending', ...);
COMMIT;
```

### 4. Lost Update

#### Problem:
Two transactions update the same data, and one update is lost.

#### Example:
```sql
-- Both transactions read Balance = 1000
-- Transaction T1
UPDATE Accounts SET Balance = 1000 + 100 WHERE AccountID = 1;  -- Sets to 1100

-- Transaction T2
UPDATE Accounts SET Balance = 1000 + 200 WHERE AccountID = 1;  -- Sets to 1200
-- T1's update is lost!
```

## Locking Mechanisms

### 1. Shared Lock (S-Lock)

#### Characteristics:
- Multiple transactions can hold shared locks on same data
- Allows reading but prevents writing
- Compatible with other shared locks

#### Example:
```sql
-- Transaction acquires shared lock for reading
SELECT * FROM Accounts WHERE AccountID = 1;
-- Other transactions can read but cannot modify
```

### 2. Exclusive Lock (X-Lock)

#### Characteristics:
- Only one transaction can hold exclusive lock
- Prevents both reading and writing by others
- Not compatible with any other locks

#### Example:
```sql
-- Transaction acquires exclusive lock for writing
UPDATE Accounts SET Balance = Balance + 100 WHERE AccountID = 1;
-- No other transaction can read or write this data
```

### 3. Lock Compatibility Matrix

| Lock Type | Shared (S) | Exclusive (X) |
|-----------|------------|---------------|
| **Shared (S)** | ✓ Compatible | ✗ Not Compatible |
| **Exclusive (X)** | ✗ Not Compatible | ✗ Not Compatible |

### 4. Two-Phase Locking (2PL)

#### Phases:
1. **Growing Phase**: Acquire locks, cannot release any lock
2. **Shrinking Phase**: Release locks, cannot acquire any lock

#### Example:
```sql
BEGIN TRANSACTION;
-- Growing Phase
LOCK TABLE Accounts IN SHARE MODE;      -- Acquire S-lock
LOCK TABLE Orders IN EXCLUSIVE MODE;    -- Acquire X-lock

-- Perform operations
SELECT * FROM Accounts;
INSERT INTO Orders VALUES (...);

-- Shrinking Phase
UNLOCK TABLE Accounts;                  -- Release S-lock
UNLOCK TABLE Orders;                    -- Release X-lock
COMMIT;
```

## Deadlock

### Definition:
A situation where two or more transactions are waiting for each other to release locks, creating a circular dependency.

### Example:
```sql
-- Transaction T1
BEGIN TRANSACTION;
UPDATE Accounts SET Balance = Balance - 100 WHERE AccountID = 1;  -- Locks Account 1
-- Waits for lock on Account 2
UPDATE Accounts SET Balance = Balance + 100 WHERE AccountID = 2;

-- Transaction T2
BEGIN TRANSACTION;
UPDATE Accounts SET Balance = Balance - 50 WHERE AccountID = 2;   -- Locks Account 2
-- Waits for lock on Account 1
UPDATE Accounts SET Balance = Balance + 50 WHERE AccountID = 1;

-- Deadlock: T1 waits for T2, T2 waits for T1
```

### Deadlock Detection

#### Wait-For Graph:
```
T1 → T2 (T1 waits for T2)
T2 → T1 (T2 waits for T1)
Cycle detected = Deadlock!
```

### Deadlock Prevention

#### 1. Ordered Locking
```sql
-- Always acquire locks in same order (by AccountID)
-- Transaction T1
LOCK Account 1, then Account 2

-- Transaction T2
LOCK Account 1, then Account 2  -- Same order prevents deadlock
```

#### 2. Timeout-Based
```sql
SET LOCK_TIMEOUT 5000;  -- 5 seconds
-- Transaction aborts if cannot acquire lock within timeout
```

#### 3. Timestamp Ordering
- **Wait-Die**: Older transaction waits, younger dies
- **Wound-Wait**: Older transaction wounds younger, younger waits

## Recovery and Logging

### 1. Write-Ahead Logging (WAL)

#### Rules:
1. Log record must be written before data page
2. All log records for transaction must be written before commit

#### Log Record Format:
```
<TransactionID, Operation, DataItem, OldValue, NewValue>
<T1, UPDATE, Account1.Balance, 1000, 1100>
<T1, INSERT, Orders.OrderID, NULL, 1001>
<T1, COMMIT>
```

### 2. Checkpointing

#### Purpose:
Reduce recovery time by periodically saving database state.

#### Process:
1. Stop accepting new transactions
2. Complete all active transactions
3. Write all dirty pages to disk
4. Write checkpoint record to log

#### Recovery with Checkpoints:
```
Timeline: ... [Checkpoint] ... [Failure]
Recovery starts from checkpoint, not beginning of log
```

## Transaction Examples

### 1. E-commerce Order Processing
```sql
BEGIN TRANSACTION;

-- Check inventory
SELECT Quantity FROM Inventory WHERE ProductID = 101;

-- Reserve inventory
UPDATE Inventory SET Quantity = Quantity - 1 WHERE ProductID = 101;

-- Create order
INSERT INTO Orders (CustomerID, ProductID, Quantity, Status) 
VALUES (1001, 101, 1, 'Processing');

-- Process payment
INSERT INTO Payments (OrderID, Amount, Status) 
VALUES (LAST_INSERT_ID(), 99.99, 'Completed');

-- Update order status
UPDATE Orders SET Status = 'Confirmed' WHERE OrderID = LAST_INSERT_ID();

COMMIT;
```

### 2. Banking Transfer with Error Handling
```sql
DELIMITER //
CREATE PROCEDURE TransferFunds(
    IN from_account INT,
    IN to_account INT,
    IN amount DECIMAL(10,2)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;
    
    -- Check sufficient balance
    IF (SELECT Balance FROM Accounts WHERE AccountID = from_account) < amount THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient funds';
    END IF;
    
    -- Perform transfer
    UPDATE Accounts SET Balance = Balance - amount WHERE AccountID = from_account;
    UPDATE Accounts SET Balance = Balance + amount WHERE AccountID = to_account;
    
    -- Log transaction
    INSERT INTO TransactionLog VALUES (NOW(), 'TRANSFER', amount, from_account, to_account);
    
    COMMIT;
END //
DELIMITER ;
```

## Interview Questions

### Basic Level

**Q1: What is a transaction and why is it important?**
**A:** A transaction is a logical unit of work consisting of one or more database operations that must be executed as a single, indivisible unit. It ensures data consistency and integrity in multi-user environments.

**Q2: Explain the ACID properties.**
**A:** 
- **Atomicity**: All-or-nothing execution
- **Consistency**: Database remains in valid state
- **Isolation**: Transactions don't interfere with each other
- **Durability**: Committed changes are permanent

**Q3: What are the different transaction states?**
**A:** Active (executing), Partially Committed (operations complete, waiting for commit), Committed (successfully completed), Failed (cannot continue), and Aborted (rolled back).

### Intermediate Level

**Q4: What are the different isolation levels and what problems do they prevent?**
**A:** 
- **Read Uncommitted**: Prevents nothing
- **Read Committed**: Prevents dirty reads
- **Repeatable Read**: Prevents dirty reads and non-repeatable reads
- **Serializable**: Prevents all concurrency problems

**Q5: Explain deadlock and how it can be prevented.**
**A:** Deadlock occurs when transactions wait for each other in a cycle. Prevention methods include ordered locking, timeouts, and timestamp ordering protocols like Wait-Die and Wound-Wait.

**Q6: What is two-phase locking and why is it important?**
**A:** 2PL has growing phase (acquire locks) and shrinking phase (release locks). It ensures serializability by preventing transactions from acquiring locks after releasing any lock.

### Advanced Level

**Q7: How does Write-Ahead Logging ensure durability and atomicity?**
**A:** WAL writes log records before data changes, ensuring recovery information exists. For durability, all log records are written before commit. For atomicity, undo information allows rollback of incomplete transactions.

**Q8: Compare optimistic vs pessimistic concurrency control.**
**A:** Pessimistic uses locks to prevent conflicts (suitable for high-conflict scenarios). Optimistic assumes conflicts are rare, validates at commit time (better for low-conflict, read-heavy workloads).

**Q9: How would you design a transaction system for a distributed database?**
**A:** Use two-phase commit protocol, implement distributed deadlock detection, consider eventual consistency models, and handle network partitions with appropriate timeout and retry mechanisms.

---

[← Previous: Indexing](../06-indexing/README.md) | [Back to Index](../README.md) | [Next: Concurrency Control →](../08-concurrency-control/README.md)