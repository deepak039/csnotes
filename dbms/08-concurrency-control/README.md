# Concurrency Control

## Overview

Concurrency control ensures that multiple transactions can execute simultaneously without compromising data integrity. It manages the interaction between concurrent transactions to maintain database consistency.

## Why Concurrency Control is Needed

### Without Concurrency Control:
- **Lost Updates**: Transactions overwrite each other's changes
- **Dirty Reads**: Reading uncommitted data
- **Inconsistent Analysis**: Reading data while it's being modified
- **Non-repeatable Reads**: Same query returns different results

### With Concurrency Control:
- **Data Integrity**: Consistent and correct data
- **Isolation**: Transactions appear to run independently
- **Performance**: Maximum safe concurrency

## Concurrency Control Techniques

### 1. Lock-Based Protocols

#### Basic Locking

##### Shared Lock (S-Lock)
```sql
-- Multiple transactions can read simultaneously
LOCK TABLE Accounts IN SHARE MODE;
SELECT * FROM Accounts WHERE AccountID = 1;
```

##### Exclusive Lock (X-Lock)
```sql
-- Only one transaction can write
LOCK TABLE Accounts IN EXCLUSIVE MODE;
UPDATE Accounts SET Balance = Balance + 100 WHERE AccountID = 1;
```

#### Two-Phase Locking (2PL)

##### Basic 2PL
```sql
-- Phase 1: Growing (acquire locks only)
BEGIN TRANSACTION;
LOCK Accounts(1) IN EXCLUSIVE MODE;  -- Acquire X-lock
LOCK Orders(100) IN SHARED MODE;     -- Acquire S-lock

-- Perform operations
UPDATE Accounts SET Balance = Balance - 100 WHERE AccountID = 1;
SELECT * FROM Orders WHERE OrderID = 100;

-- Phase 2: Shrinking (release locks only)
UNLOCK Accounts(1);                  -- Release X-lock
UNLOCK Orders(100);                  -- Release S-lock
COMMIT;
```

##### Strict 2PL
```sql
-- Holds all locks until transaction commits/aborts
BEGIN TRANSACTION;
-- Acquire locks as needed
UPDATE Accounts SET Balance = Balance - 100 WHERE AccountID = 1;
SELECT * FROM Orders WHERE OrderID = 100;
-- All locks released only at COMMIT/ROLLBACK
COMMIT;
```

##### Rigorous 2PL
```sql
-- Holds all locks until transaction commits
-- More restrictive than Strict 2PL
BEGIN TRANSACTION;
-- All locks held until COMMIT only (not ROLLBACK)
COMMIT;
```

#### Lock Granularity

##### Database Level
```sql
LOCK DATABASE CompanyDB IN EXCLUSIVE MODE;
-- Entire database locked - very coarse granularity
```

##### Table Level
```sql
LOCK TABLE Employees IN SHARE MODE;
-- Entire table locked - coarse granularity
```

##### Page Level
```sql
-- Database system automatically locks pages
-- Medium granularity - balance between overhead and concurrency
```

##### Row Level
```sql
-- Most databases use row-level locking by default
UPDATE Employees SET Salary = 50000 WHERE EmployeeID = 1001;
-- Only locks specific row - fine granularity
```

#### Hierarchical Locking

##### Intention Locks
```sql
-- Intention Shared (IS): Intends to acquire S-locks on descendants
-- Intention Exclusive (IX): Intends to acquire X-locks on descendants
-- Shared Intention Exclusive (SIX): S-lock on node, IX on descendants

-- Example hierarchy: Database → Table → Page → Row
-- To lock a row, must acquire appropriate intention locks on ancestors
```

##### Lock Compatibility Matrix
| Current\Request | IS | IX | S | SIX | X |
|----------------|----|----|---|-----|---|
| **IS** | ✓ | ✓ | ✓ | ✓ | ✗ |
| **IX** | ✓ | ✓ | ✗ | ✗ | ✗ |
| **S** | ✓ | ✗ | ✓ | ✗ | ✗ |
| **SIX** | ✓ | ✗ | ✗ | ✗ | ✗ |
| **X** | ✗ | ✗ | ✗ | ✗ | ✗ |

### 2. Timestamp-Based Protocols

#### Basic Timestamp Ordering

##### Concept:
Each transaction gets a unique timestamp. Operations are executed in timestamp order.

##### Rules:
```sql
-- For transaction Ti with timestamp TS(Ti):
-- Read operation: TS(Ti) > W-timestamp(X) → Allow read
-- Write operation: TS(Ti) > R-timestamp(X) AND TS(Ti) > W-timestamp(X) → Allow write
```

##### Example:
```sql
-- T1 (TS=100), T2 (TS=200), T3 (TS=300)
-- Data item X: R-TS(X)=0, W-TS(X)=0

-- T2 reads X: TS(T2)=200 > W-TS(X)=0 → Allow, R-TS(X)=200
-- T1 writes X: TS(T1)=100 < R-TS(X)=200 → Reject, rollback T1
-- T3 writes X: TS(T3)=300 > R-TS(X)=200 → Allow, W-TS(X)=300
```

#### Thomas Write Rule

##### Enhancement:
Ignore outdated writes instead of aborting transactions.

```sql
-- If TS(Ti) < W-timestamp(X), ignore the write (don't abort)
-- Improves performance by reducing unnecessary aborts
```

### 3. Validation-Based (Optimistic) Protocols

#### Three Phases:

##### 1. Read Phase
```sql
-- Transaction reads data and performs computations
-- All writes are to local copies (not actual database)
BEGIN TRANSACTION;
local_balance = SELECT Balance FROM Accounts WHERE AccountID = 1;
local_balance = local_balance + 100;  -- Local computation
```

##### 2. Validation Phase
```sql
-- Check if transaction conflicts with others
-- Validate read set and write set
IF (no conflicts detected) THEN
    proceed to write phase
ELSE
    abort transaction
END IF;
```

##### 3. Write Phase
```sql
-- Apply all writes to database atomically
UPDATE Accounts SET Balance = local_balance WHERE AccountID = 1;
COMMIT;
```

#### Validation Rules:
```sql
-- For transactions Ti and Tj where TS(Ti) < TS(Tj):
-- Rule 1: Ti completes before Tj starts
-- Rule 2: Ti completes before Tj starts validation AND WriteSet(Ti) ∩ ReadSet(Tj) = ∅
-- Rule 3: Ti completes validation before Tj completes validation AND 
--         WriteSet(Ti) ∩ ReadSet(Tj) = ∅ AND WriteSet(Ti) ∩ WriteSet(Tj) = ∅
```

### 4. Multiversion Concurrency Control (MVCC)

#### Concept:
Maintain multiple versions of data items. Readers don't block writers, writers don't block readers.

#### Implementation:
```sql
-- Each data item has multiple versions with timestamps
-- Version chain: X1(TS=100) → X2(TS=200) → X3(TS=300)

-- Transaction with TS=250 reads version X2(TS=200)
-- Transaction with TS=350 reads version X3(TS=300)
```

#### Example in PostgreSQL:
```sql
-- Transaction 1
BEGIN;
UPDATE accounts SET balance = balance + 100 WHERE id = 1;
-- Creates new version, old version still available for other transactions

-- Transaction 2 (concurrent)
BEGIN;
SELECT balance FROM accounts WHERE id = 1;
-- Reads old version, doesn't wait for Transaction 1
```

#### Garbage Collection:
```sql
-- Remove old versions when no transaction needs them
-- VACUUM process in PostgreSQL cleans up old tuple versions
```

## Deadlock Handling

### 1. Deadlock Prevention

#### Wait-Die Scheme
```sql
-- If Ti requests resource held by Tj:
-- If TS(Ti) < TS(Tj): Ti waits (older waits for younger)
-- If TS(Ti) > TS(Tj): Ti dies (younger dies, restarts with same timestamp)
```

#### Wound-Wait Scheme
```sql
-- If Ti requests resource held by Tj:
-- If TS(Ti) < TS(Tj): Ti wounds Tj (older preempts younger)
-- If TS(Ti) > TS(Tj): Ti waits (younger waits for older)
```

### 2. Deadlock Detection

#### Wait-For Graph
```sql
-- Nodes: Active transactions
-- Edges: Ti → Tj if Ti waits for Tj
-- Cycle in graph indicates deadlock

-- Example:
-- T1 waits for T2: T1 → T2
-- T2 waits for T3: T2 → T3  
-- T3 waits for T1: T3 → T1
-- Cycle detected: T1 → T2 → T3 → T1 (Deadlock!)
```

#### Detection Algorithm:
```python
def detect_deadlock(wait_for_graph):
    # Depth-First Search to detect cycles
    visited = set()
    rec_stack = set()
    
    for node in graph.nodes:
        if node not in visited:
            if has_cycle(node, visited, rec_stack, graph):
                return True  # Deadlock detected
    return False
```

### 3. Deadlock Recovery

#### Victim Selection Criteria:
```sql
-- Choose transaction to abort based on:
-- 1. Least amount of work done
-- 2. Fewest locks held
-- 3. Fewest transactions waiting for it
-- 4. How many times it has been rolled back
```

## Isolation Levels Implementation

### 1. Read Uncommitted
```sql
-- No shared locks acquired for reads
-- Allows dirty reads
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
SELECT * FROM Accounts WHERE AccountID = 1;  -- May read uncommitted data
```

### 2. Read Committed
```sql
-- Shared locks acquired and released immediately after read
-- Prevents dirty reads
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
SELECT * FROM Accounts WHERE AccountID = 1;  -- Only reads committed data
```

### 3. Repeatable Read
```sql
-- Shared locks held until end of transaction
-- Prevents dirty reads and non-repeatable reads
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
BEGIN TRANSACTION;
SELECT * FROM Accounts WHERE AccountID = 1;  -- Lock held
-- ... other operations ...
SELECT * FROM Accounts WHERE AccountID = 1;  -- Same result guaranteed
COMMIT;  -- Lock released
```

### 4. Serializable
```sql
-- Strictest isolation level
-- Uses predicate locking or serializable snapshot isolation
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
-- Prevents all anomalies including phantom reads
```

## Performance Considerations

### Lock Overhead
```sql
-- Lock acquisition/release costs
-- Lock table maintenance
-- Deadlock detection overhead
-- Memory usage for lock structures
```

### Granularity Trade-offs
```sql
-- Fine granularity (row-level):
--   + Higher concurrency
--   - Higher overhead
--   - More locks to manage

-- Coarse granularity (table-level):
--   + Lower overhead
--   + Fewer locks to manage
--   - Lower concurrency
```

### Choosing Concurrency Control Method
```sql
-- High conflict workloads: Pessimistic (locking)
-- Low conflict workloads: Optimistic (validation-based)
-- Read-heavy workloads: MVCC
-- Write-heavy workloads: Locking with appropriate granularity
```

## Interview Questions

### Basic Level

**Q1: What is concurrency control and why is it needed?**
**A:** Concurrency control manages simultaneous transaction execution to maintain data integrity. It's needed to prevent problems like lost updates, dirty reads, and inconsistent data when multiple transactions access the same data.

**Q2: Explain the difference between shared and exclusive locks.**
**A:** Shared locks allow multiple transactions to read the same data simultaneously but prevent writes. Exclusive locks allow only one transaction to access data for both reading and writing, blocking all other access.

**Q3: What is two-phase locking?**
**A:** 2PL has two phases: growing phase (acquire locks only) and shrinking phase (release locks only). Once a transaction releases any lock, it cannot acquire new locks. This ensures serializability.

### Intermediate Level

**Q4: Compare pessimistic vs optimistic concurrency control.**
**A:** Pessimistic assumes conflicts will occur and uses locks to prevent them. Optimistic assumes conflicts are rare, allows concurrent execution, and validates at commit time. Pessimistic is better for high-conflict scenarios, optimistic for low-conflict.

**Q5: How does MVCC work and what are its advantages?**
**A:** MVCC maintains multiple versions of data items with timestamps. Readers access appropriate versions without blocking writers. Advantages include no read-write conflicts, better concurrency, and consistent snapshots.

**Q6: Explain deadlock detection using wait-for graphs.**
**A:** Wait-for graph has transactions as nodes and edges representing wait relationships. A cycle in the graph indicates deadlock. Detection algorithms use DFS to find cycles periodically.

### Advanced Level

**Q7: How do different isolation levels affect concurrency and consistency?**
**A:** Lower isolation levels (Read Uncommitted) allow more concurrency but less consistency. Higher levels (Serializable) provide strong consistency but reduce concurrency. Choice depends on application requirements for data accuracy vs performance.

**Q8: Design a concurrency control system for a high-throughput OLTP system.**
**A:** Use row-level locking with intention locks, implement deadlock detection with timeout, consider MVCC for read-heavy operations, optimize lock granularity based on access patterns, and use lock-free data structures where possible.

**Q9: How would you handle concurrency in a distributed database system?**
**A:** Use distributed locking protocols, implement two-phase commit for atomic transactions, handle network partitions gracefully, consider eventual consistency models, and use vector clocks for ordering events across nodes.

---

[← Previous: Transactions](../07-transactions/README.md) | [Back to Index](../README.md) | [Next: Recovery Systems →](../09-recovery-systems/README.md)