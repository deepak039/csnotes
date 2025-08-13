# Indexing in DBMS

## What is an Index?

An index is a data structure that improves the speed of data retrieval operations on a database table. It creates shortcuts to data, similar to an index in a book that helps you quickly find specific topics.

## How Indexes Work

### Without Index:
```sql
-- Full table scan - O(n) time complexity
SELECT * FROM Employees WHERE EmployeeID = 1001;
-- Database scans every row until it finds EmployeeID = 1001
```

### With Index:
```sql
-- Index lookup - O(log n) time complexity
CREATE INDEX idx_employee_id ON Employees(EmployeeID);
SELECT * FROM Employees WHERE EmployeeID = 1001;
-- Database uses index to directly locate the row
```

## Types of Indexes

### 1. Primary Index (Clustered Index)

#### Characteristics:
- Built on primary key
- Data rows are physically ordered by index key
- One per table (since data can only be physically ordered one way)
- Leaf nodes contain actual data pages

#### Example:
```sql
CREATE TABLE Students (
    StudentID INT PRIMARY KEY,  -- Automatically creates clustered index
    Name VARCHAR(100),
    Email VARCHAR(100)
);
```

#### Structure:
```
Clustered Index on StudentID:
Root: [500, 1000, 1500]
├── Leaf: [100, 200, 300, 400] → Data Pages
├── Leaf: [600, 700, 800, 900] → Data Pages  
└── Leaf: [1100, 1200, 1300, 1400] → Data Pages
```

### 2. Secondary Index (Non-Clustered Index)

#### Characteristics:
- Built on non-primary key columns
- Separate structure from data table
- Multiple indexes per table allowed
- Leaf nodes contain pointers to data rows

#### Example:
```sql
CREATE INDEX idx_student_email ON Students(Email);
CREATE INDEX idx_student_name ON Students(Name);
```

#### Structure:
```
Non-Clustered Index on Email:
Root: [email_m, email_s]
├── Leaf: [alice@..., bob@..., charlie@...] → Row Pointers
└── Leaf: [david@..., eve@..., frank@...] → Row Pointers
```

### 3. Composite Index

#### Definition:
Index built on multiple columns.

#### Example:
```sql
CREATE INDEX idx_name_age ON Employees(LastName, FirstName, Age);

-- Efficient for queries like:
SELECT * FROM Employees WHERE LastName = 'Smith';
SELECT * FROM Employees WHERE LastName = 'Smith' AND FirstName = 'John';
SELECT * FROM Employees WHERE LastName = 'Smith' AND FirstName = 'John' AND Age = 30;

-- Not efficient for:
SELECT * FROM Employees WHERE FirstName = 'John';  -- Skips first column
SELECT * FROM Employees WHERE Age = 30;            -- Skips first columns
```

### 4. Unique Index

#### Definition:
Ensures uniqueness of indexed column values.

#### Example:
```sql
CREATE UNIQUE INDEX idx_employee_email ON Employees(Email);
-- Prevents duplicate email addresses
```

### 5. Partial Index

#### Definition:
Index on subset of rows that meet certain conditions.

#### Example:
```sql
-- PostgreSQL syntax
CREATE INDEX idx_active_employees ON Employees(LastName) 
WHERE Status = 'Active';

-- Only indexes active employees, saving space
```

## Index Data Structures

### 1. B-Tree Index

#### Characteristics:
- Balanced tree structure
- All leaf nodes at same level
- Efficient for range queries
- Most common index type

#### Structure:
```
B-Tree Example (Order 3):
        [50, 100]
       /    |    \
   [20,30] [70,80] [120,150]
   /  |  \ /  |  \ /   |   \
 [10][25][40][60][75][90][110][130][160]
```

#### Time Complexity:
- Search: O(log n)
- Insert: O(log n)
- Delete: O(log n)

#### SQL Example:
```sql
CREATE INDEX idx_salary_btree ON Employees(Salary);

-- Efficient queries:
SELECT * FROM Employees WHERE Salary = 50000;
SELECT * FROM Employees WHERE Salary BETWEEN 40000 AND 60000;
SELECT * FROM Employees WHERE Salary > 45000 ORDER BY Salary;
```

### 2. B+ Tree Index

#### Characteristics:
- Variation of B-Tree
- All data stored in leaf nodes
- Internal nodes only store keys
- Leaf nodes linked for sequential access

#### Advantages over B-Tree:
- Better for range queries
- More efficient disk I/O
- Consistent performance

#### Structure:
```
B+ Tree Example:
        [50, 100]
       /    |    \
   [20,30] [70,80] [120,150]
   
Leaf Level (linked):
[10,20,25,30] ↔ [40,50,60,70] ↔ [75,80,90,100] ↔ [110,120,130,150]
```

### 3. Hash Index

#### Characteristics:
- Uses hash function to map keys to buckets
- Excellent for equality searches
- Poor for range queries
- O(1) average time complexity

#### Example:
```sql
-- MySQL Memory Engine
CREATE TABLE Sessions (
    SessionID CHAR(32) PRIMARY KEY,
    UserID INT,
    Data TEXT
) ENGINE=MEMORY;

-- Hash index automatically created on SessionID
```

#### Hash Function Example:
```
Hash Function: key % bucket_count
Key: 'user123' → Hash: 7 → Bucket 7
Key: 'user456' → Hash: 3 → Bucket 3
```

#### Collision Handling:
```
Bucket 7: ['user123'] → ['user789'] → NULL  (Chaining)
```

### 4. Bitmap Index

#### Characteristics:
- Uses bitmaps for each distinct value
- Efficient for low-cardinality columns
- Excellent for data warehousing
- Supports fast Boolean operations

#### Example:
```sql
-- Gender column with values: M, F, NULL
CREATE BITMAP INDEX idx_gender ON Employees(Gender);
```

#### Bitmap Structure:
```
Gender = 'M':  [1,0,1,0,1,0,0,1]
Gender = 'F':  [0,1,0,1,0,1,1,0]
Gender = NULL: [0,0,0,0,0,0,0,0]

Row IDs:       [1,2,3,4,5,6,7,8]
```

#### Query Processing:
```sql
-- Find all male employees in IT department
SELECT * FROM Employees 
WHERE Gender = 'M' AND Department = 'IT';

-- Bitmap operations:
-- Gender='M' bitmap:     [1,0,1,0,1,0,0,1]
-- Department='IT' bitmap:[1,1,0,0,1,1,0,0]
-- AND operation result:  [1,0,0,0,1,0,0,0]
-- Result: Rows 1 and 5
```

## Index Selection Strategies

### 1. Columns to Index

#### Good Candidates:
```sql
-- Primary keys (automatic)
CREATE TABLE Orders (OrderID INT PRIMARY KEY);

-- Foreign keys
CREATE INDEX idx_customer_id ON Orders(CustomerID);

-- Frequently searched columns
CREATE INDEX idx_email ON Users(Email);

-- Columns in WHERE clauses
CREATE INDEX idx_status ON Orders(Status);

-- Columns in ORDER BY
CREATE INDEX idx_order_date ON Orders(OrderDate);

-- Columns in JOIN conditions
CREATE INDEX idx_product_id ON OrderItems(ProductID);
```

#### Poor Candidates:
```sql
-- Small tables (< 1000 rows)
-- Columns with high update frequency
-- Columns with low selectivity (few distinct values)
-- Very wide columns (large text fields)
```

### 2. Composite Index Design

#### Column Order Matters:
```sql
-- Good: Most selective column first
CREATE INDEX idx_status_date ON Orders(Status, OrderDate);

-- Queries that benefit:
SELECT * FROM Orders WHERE Status = 'Pending';
SELECT * FROM Orders WHERE Status = 'Pending' AND OrderDate > '2024-01-01';

-- Query that doesn't benefit:
SELECT * FROM Orders WHERE OrderDate > '2024-01-01';  -- Skips first column
```

## Index Maintenance

### 1. Index Statistics

#### Update Statistics:
```sql
-- SQL Server
UPDATE STATISTICS Employees;

-- PostgreSQL
ANALYZE Employees;

-- MySQL
ANALYZE TABLE Employees;
```

### 2. Index Fragmentation

#### Check Fragmentation:
```sql
-- SQL Server
SELECT 
    i.name AS IndexName,
    s.avg_fragmentation_in_percent
FROM sys.dm_db_index_physical_stats(DB_ID(), NULL, NULL, NULL, NULL) s
JOIN sys.indexes i ON s.object_id = i.object_id AND s.index_id = i.index_id;
```

#### Rebuild Fragmented Indexes:
```sql
-- SQL Server
ALTER INDEX idx_employee_name ON Employees REBUILD;

-- PostgreSQL
REINDEX INDEX idx_employee_name;
```

### 3. Unused Index Detection

```sql
-- SQL Server: Find unused indexes
SELECT 
    i.name AS IndexName,
    s.user_seeks,
    s.user_scans,
    s.user_lookups,
    s.user_updates
FROM sys.indexes i
LEFT JOIN sys.dm_db_index_usage_stats s ON i.object_id = s.object_id AND i.index_id = s.index_id
WHERE s.user_seeks + s.user_scans + s.user_lookups = 0;
```

## Index Performance Impact

### Benefits:
- **Faster SELECT queries**: O(log n) vs O(n)
- **Efficient sorting**: ORDER BY uses index order
- **Quick joins**: Index lookups for JOIN operations
- **Unique constraint enforcement**: Prevents duplicates

### Costs:
- **Storage overhead**: Additional disk space
- **Insert/Update/Delete slowdown**: Index maintenance
- **Memory usage**: Index pages in buffer pool

### Example Performance Comparison:
```sql
-- Without index: Full table scan
SELECT * FROM Employees WHERE LastName = 'Smith';
-- Cost: 1000 page reads for 1M rows

-- With index: Index seek
CREATE INDEX idx_lastname ON Employees(LastName);
SELECT * FROM Employees WHERE LastName = 'Smith';
-- Cost: 3-4 page reads (index height + data page)
```

## Interview Questions

### Basic Level

**Q1: What is a database index and why is it used?**
**A:** An index is a data structure that improves query performance by creating shortcuts to data. It's used to speed up SELECT operations, enforce uniqueness, and optimize JOIN operations.

**Q2: What's the difference between clustered and non-clustered indexes?**
**A:** Clustered index physically orders data rows and contains actual data in leaf nodes (one per table). Non-clustered index is separate from data and contains pointers to data rows (multiple allowed per table).

**Q3: What are the disadvantages of having too many indexes?**
**A:** Increased storage space, slower INSERT/UPDATE/DELETE operations due to index maintenance, and higher memory usage for index pages.

### Intermediate Level

**Q4: Explain B-Tree and B+ Tree indexes. Which is better and why?**
**A:** B-Tree stores data in all nodes; B+ Tree stores data only in leaf nodes with linked leaves. B+ Tree is better for databases because it provides consistent performance, better range queries, and more efficient disk I/O.

**Q5: When would you use a composite index vs multiple single-column indexes?**
**A:** Use composite index when queries frequently filter on multiple columns together. Single-column indexes are better when columns are queried independently. Composite indexes are more efficient for multi-column WHERE clauses.

**Q6: How do hash indexes work and when should you use them?**
**A:** Hash indexes use hash functions to map keys to buckets, providing O(1) lookups for equality searches. Use them for exact match queries on high-cardinality columns, but avoid for range queries.

### Advanced Level

**Q7: How would you design indexes for a query that uses WHERE, ORDER BY, and GROUP BY?**
**A:** Create a composite index with WHERE columns first (most selective), then GROUP BY columns, then ORDER BY columns. This allows index to satisfy all operations efficiently.

**Q8: Explain index fragmentation and how to handle it.**
**A:** Fragmentation occurs when index pages are not physically contiguous or have empty space. Handle it by monitoring fragmentation levels and rebuilding/reorganizing indexes when fragmentation exceeds thresholds (typically 10-30%).

**Q9: How do you determine if an index is being used effectively?**
**A:** Monitor query execution plans, check index usage statistics, analyze query performance metrics, and look for table scans vs index seeks. Remove unused indexes and add missing indexes based on query patterns.

---

[← Previous: Normalization](../05-normalization/README.md) | [Back to Index](../README.md) | [Next: Transactions →](../07-transactions/README.md)