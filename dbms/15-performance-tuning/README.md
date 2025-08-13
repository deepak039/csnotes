# Database Performance Tuning

## Overview

Database performance tuning is the process of optimizing database systems to achieve maximum efficiency, speed, and resource utilization. It involves analyzing and improving various aspects of database operations.

## Performance Metrics

### 1. Response Time
```sql
-- Measure query execution time
SET @start_time = NOW(6);
SELECT * FROM Orders WHERE CustomerID = 1001;
SELECT TIMESTAMPDIFF(MICROSECOND, @start_time, NOW(6)) / 1000 AS execution_time_ms;
```

### 2. Throughput
```sql
-- Transactions per second (TPS)
-- Queries per second (QPS)
-- Concurrent user capacity
```

### 3. Resource Utilization
```sql
-- CPU usage
-- Memory consumption
-- Disk I/O
-- Network bandwidth
```

### 4. Availability
```sql
-- Uptime percentage
-- Mean Time Between Failures (MTBF)
-- Mean Time To Recovery (MTTR)
```

## Query Optimization

### 1. Execution Plan Analysis

#### SQL Server:
```sql
-- View execution plan
SET SHOWPLAN_ALL ON
SELECT * FROM Employees e 
JOIN Departments d ON e.DeptID = d.DeptID 
WHERE e.Salary > 50000;

-- Include actual execution plan
SET STATISTICS IO ON
SET STATISTICS TIME ON
```

#### MySQL:
```sql
-- Explain query execution
EXPLAIN SELECT * FROM Employees e 
JOIN Departments d ON e.DeptID = d.DeptID 
WHERE e.Salary > 50000;

-- Detailed analysis
EXPLAIN FORMAT=JSON SELECT ...;
```

#### PostgreSQL:
```sql
-- Analyze execution plan
EXPLAIN ANALYZE SELECT * FROM Employees e 
JOIN Departments d ON e.DeptID = d.DeptID 
WHERE e.Salary > 50000;
```

### 2. Query Rewriting Techniques

#### Use Appropriate WHERE Clauses:
```sql
-- Bad: Non-sargable query
SELECT * FROM Orders WHERE YEAR(OrderDate) = 2024;

-- Good: Sargable query
SELECT * FROM Orders WHERE OrderDate >= '2024-01-01' AND OrderDate < '2025-01-01';
```

#### Optimize JOINs:
```sql
-- Bad: Cartesian product
SELECT * FROM Customers c, Orders o WHERE c.CustomerID = o.CustomerID;

-- Good: Explicit JOIN
SELECT * FROM Customers c 
INNER JOIN Orders o ON c.CustomerID = o.CustomerID;
```

#### Use EXISTS instead of IN for subqueries:
```sql
-- Less efficient
SELECT * FROM Customers 
WHERE CustomerID IN (SELECT CustomerID FROM Orders WHERE OrderDate > '2024-01-01');

-- More efficient
SELECT * FROM Customers c
WHERE EXISTS (SELECT 1 FROM Orders o WHERE o.CustomerID = c.CustomerID AND o.OrderDate > '2024-01-01');
```

### 3. Index Optimization

#### Covering Indexes:
```sql
-- Query that benefits from covering index
SELECT CustomerID, OrderDate, TotalAmount 
FROM Orders 
WHERE OrderDate BETWEEN '2024-01-01' AND '2024-12-31';

-- Create covering index
CREATE INDEX idx_orders_covering 
ON Orders (OrderDate, CustomerID, TotalAmount);
```

#### Composite Index Column Order:
```sql
-- Query pattern analysis
SELECT * FROM Employees WHERE DeptID = 10 AND Salary > 50000;
SELECT * FROM Employees WHERE DeptID = 10;

-- Optimal index (most selective column first)
CREATE INDEX idx_employees_dept_salary ON Employees (DeptID, Salary);
```

#### Partial Indexes:
```sql
-- PostgreSQL: Index only active records
CREATE INDEX idx_active_employees ON Employees (LastName) WHERE Status = 'Active';

-- SQL Server: Filtered index
CREATE INDEX idx_active_employees ON Employees (LastName) WHERE Status = 'Active';
```

## Index Tuning Strategies

### 1. Index Analysis

#### Find Missing Indexes:
```sql
-- SQL Server: Missing index suggestions
SELECT 
    migs.avg_total_user_cost * (migs.avg_user_impact / 100.0) * (migs.user_seeks + migs.user_scans) AS improvement_measure,
    'CREATE INDEX [missing_index_' + CONVERT(varchar, mig.index_group_handle) + '_' + CONVERT(varchar, mid.index_handle) + ']'
    + ' ON ' + mid.statement + ' (' + ISNULL(mid.equality_columns,'') 
    + CASE WHEN mid.equality_columns IS NOT NULL AND mid.inequality_columns IS NOT NULL THEN ',' ELSE '' END
    + ISNULL(mid.inequality_columns, '') + ')' 
    + ISNULL(' INCLUDE (' + mid.included_columns + ')', '') AS create_index_statement
FROM sys.dm_db_missing_index_groups mig
INNER JOIN sys.dm_db_missing_index_group_stats migs ON migs.group_handle = mig.index_group_handle
INNER JOIN sys.dm_db_missing_index_details mid ON mig.index_handle = mid.index_handle
ORDER BY improvement_measure DESC;
```

#### Find Unused Indexes:
```sql
-- SQL Server: Unused indexes
SELECT 
    i.name AS IndexName,
    OBJECT_NAME(i.object_id) AS TableName,
    i.type_desc,
    us.user_seeks,
    us.user_scans,
    us.user_lookups,
    us.user_updates
FROM sys.indexes i
LEFT JOIN sys.dm_db_index_usage_stats us ON i.object_id = us.object_id AND i.index_id = us.index_id
WHERE OBJECTPROPERTY(i.object_id, 'IsUserTable') = 1
    AND i.index_id > 0
    AND (us.user_seeks + us.user_scans + us.user_lookups) = 0
ORDER BY us.user_updates DESC;
```

### 2. Index Maintenance

#### Rebuild Fragmented Indexes:
```sql
-- SQL Server: Check fragmentation
SELECT 
    OBJECT_NAME(ips.object_id) AS TableName,
    i.name AS IndexName,
    ips.avg_fragmentation_in_percent,
    ips.page_count
FROM sys.dm_db_index_physical_stats(DB_ID(), NULL, NULL, NULL, 'DETAILED') ips
JOIN sys.indexes i ON ips.object_id = i.object_id AND ips.index_id = i.index_id
WHERE ips.avg_fragmentation_in_percent > 10
    AND ips.page_count > 1000;

-- Rebuild highly fragmented indexes
ALTER INDEX ALL ON TableName REBUILD;
```

## Database Configuration Tuning

### 1. Memory Configuration

#### SQL Server:
```sql
-- Configure maximum server memory
EXEC sp_configure 'max server memory (MB)', 8192;
RECONFIGURE;

-- Buffer pool hit ratio (should be > 95%)
SELECT 
    (1.0 - (CAST(cntr_value AS FLOAT) / 
    (SELECT cntr_value FROM sys.dm_os_performance_counters 
     WHERE counter_name = 'Buffer cache hit ratio base'))) * 100.0 AS buffer_hit_ratio
FROM sys.dm_os_performance_counters 
WHERE counter_name = 'Buffer cache hit ratio';
```

#### MySQL:
```sql
-- Configure InnoDB buffer pool
SET GLOBAL innodb_buffer_pool_size = 8589934592;  -- 8GB

-- Check buffer pool hit ratio
SHOW ENGINE INNODB STATUS;
```

#### PostgreSQL:
```sql
-- Configure shared buffers (postgresql.conf)
shared_buffers = 2GB
effective_cache_size = 8GB
work_mem = 256MB
maintenance_work_mem = 1GB
```

### 2. Connection Pooling

#### Connection Pool Configuration:
```sql
-- SQL Server: Maximum connections
EXEC sp_configure 'user connections', 500;

-- MySQL: Connection limits
SET GLOBAL max_connections = 500;
SET GLOBAL max_user_connections = 50;

-- PostgreSQL (postgresql.conf)
max_connections = 200
```

### 3. Disk I/O Optimization

#### File Placement:
```sql
-- Separate data, log, and tempdb files
-- Place on different physical drives
-- Use SSD for high-performance requirements

-- SQL Server: Multiple data files
ALTER DATABASE MyDatabase 
ADD FILE (
    NAME = 'MyDatabase_Data2',
    FILENAME = 'D:\Data\MyDatabase_Data2.mdf',
    SIZE = 1GB,
    FILEGROWTH = 256MB
);
```

## Partitioning Strategies

### 1. Horizontal Partitioning (Sharding)

#### Range Partitioning:
```sql
-- SQL Server: Partition by date
CREATE PARTITION FUNCTION pf_OrderDate (DATE)
AS RANGE RIGHT FOR VALUES ('2023-01-01', '2024-01-01', '2025-01-01');

CREATE PARTITION SCHEME ps_OrderDate
AS PARTITION pf_OrderDate
TO (FileGroup1, FileGroup2, FileGroup3, FileGroup4);

CREATE TABLE Orders (
    OrderID INT,
    OrderDate DATE,
    CustomerID INT,
    Amount DECIMAL(10,2)
) ON ps_OrderDate(OrderDate);
```

#### Hash Partitioning:
```sql
-- PostgreSQL: Hash partitioning
CREATE TABLE Orders (
    OrderID SERIAL,
    CustomerID INT,
    OrderDate DATE,
    Amount DECIMAL(10,2)
) PARTITION BY HASH (CustomerID);

CREATE TABLE Orders_p0 PARTITION OF Orders FOR VALUES WITH (modulus 4, remainder 0);
CREATE TABLE Orders_p1 PARTITION OF Orders FOR VALUES WITH (modulus 4, remainder 1);
CREATE TABLE Orders_p2 PARTITION OF Orders FOR VALUES WITH (modulus 4, remainder 2);
CREATE TABLE Orders_p3 PARTITION OF Orders FOR VALUES WITH (modulus 4, remainder 3);
```

### 2. Vertical Partitioning

#### Split Large Tables:
```sql
-- Original large table
CREATE TABLE Employees (
    EmpID INT PRIMARY KEY,
    FirstName VARCHAR(50),
    LastName VARCHAR(50),
    Email VARCHAR(100),
    Phone VARCHAR(20),
    Address TEXT,
    Biography TEXT,  -- Large column
    Photo VARBINARY(MAX),  -- Large column
    Salary DECIMAL(10,2),
    DeptID INT
);

-- Split into frequently and infrequently accessed columns
CREATE TABLE Employees_Core (
    EmpID INT PRIMARY KEY,
    FirstName VARCHAR(50),
    LastName VARCHAR(50),
    Email VARCHAR(100),
    Salary DECIMAL(10,2),
    DeptID INT
);

CREATE TABLE Employees_Details (
    EmpID INT PRIMARY KEY,
    Phone VARCHAR(20),
    Address TEXT,
    Biography TEXT,
    Photo VARBINARY(MAX),
    FOREIGN KEY (EmpID) REFERENCES Employees_Core(EmpID)
);
```

## Caching Strategies

### 1. Query Result Caching

#### Application-Level Caching:
```python
import redis
import json

# Redis cache implementation
cache = redis.Redis(host='localhost', port=6379, db=0)

def get_user_orders(user_id):
    cache_key = f"user_orders:{user_id}"
    
    # Try to get from cache
    cached_result = cache.get(cache_key)
    if cached_result:
        return json.loads(cached_result)
    
    # Query database
    result = execute_query("SELECT * FROM Orders WHERE CustomerID = ?", user_id)
    
    # Cache the result (expire in 1 hour)
    cache.setex(cache_key, 3600, json.dumps(result))
    
    return result
```

### 2. Database-Level Caching

#### MySQL Query Cache:
```sql
-- Enable query cache
SET GLOBAL query_cache_type = ON;
SET GLOBAL query_cache_size = 268435456;  -- 256MB

-- Check query cache status
SHOW STATUS LIKE 'Qcache%';
```

## Monitoring and Alerting

### 1. Performance Monitoring

#### Key Metrics to Monitor:
```sql
-- SQL Server: Performance counters
SELECT 
    counter_name,
    cntr_value,
    cntr_type
FROM sys.dm_os_performance_counters
WHERE object_name LIKE '%SQL Statistics%'
   OR object_name LIKE '%Buffer Manager%'
   OR object_name LIKE '%Memory Manager%';
```

#### Long-Running Queries:
```sql
-- SQL Server: Find long-running queries
SELECT 
    r.session_id,
    r.start_time,
    r.status,
    r.command,
    r.cpu_time,
    r.total_elapsed_time,
    t.text AS query_text
FROM sys.dm_exec_requests r
CROSS APPLY sys.dm_exec_sql_text(r.sql_handle) t
WHERE r.total_elapsed_time > 10000  -- More than 10 seconds
ORDER BY r.total_elapsed_time DESC;
```

### 2. Automated Alerts

#### Performance Threshold Alerts:
```sql
-- Create alert for high CPU usage
-- Create alert for blocking processes
-- Create alert for low disk space
-- Create alert for failed backups
```

## Best Practices

### 1. Query Design
- Use appropriate WHERE clauses
- Avoid SELECT *
- Use JOINs instead of subqueries when possible
- Limit result sets with TOP/LIMIT
- Use stored procedures for complex logic

### 2. Index Design
- Create indexes on frequently queried columns
- Use composite indexes for multi-column queries
- Avoid over-indexing
- Regular index maintenance
- Monitor index usage

### 3. Database Design
- Proper normalization
- Appropriate data types
- Constraints for data integrity
- Partitioning for large tables
- Archive old data

### 4. System Configuration
- Adequate memory allocation
- Proper disk configuration
- Connection pooling
- Regular maintenance tasks
- Backup and recovery planning

## Interview Questions

### Basic Level

**Q1: What are the key metrics for database performance?**
**A:** Response time (query execution time), throughput (TPS/QPS), resource utilization (CPU, memory, disk I/O), and availability (uptime percentage). These metrics help identify performance bottlenecks.

**Q2: How do you identify slow queries?**
**A:** Use database-specific tools like EXPLAIN in MySQL/PostgreSQL, execution plans in SQL Server, enable slow query logs, monitor query execution times, and use performance monitoring tools.

**Q3: What is the difference between clustered and non-clustered indexes in terms of performance?**
**A:** Clustered indexes are faster for range queries and sorting since data is physically ordered. Non-clustered indexes are better for point lookups and when multiple indexes are needed on the same table.

### Intermediate Level

**Q4: How would you optimize a query that's performing poorly?**
**A:** Analyze execution plan, check for missing indexes, rewrite query to be sargable, optimize JOINs, use appropriate WHERE clauses, consider query hints, and ensure statistics are up to date.

**Q5: Explain different partitioning strategies and when to use them.**
**A:** Range partitioning for time-series data, hash partitioning for even distribution, list partitioning for discrete values. Use when tables are very large, to improve query performance, or to enable parallel processing.

**Q6: How do you handle database performance in a high-concurrency environment?**
**A:** Use connection pooling, optimize locking strategies, implement proper indexing, consider read replicas, use caching layers, and monitor for deadlocks and blocking processes.

### Advanced Level

**Q7: Design a performance monitoring strategy for a large-scale database system.**
**A:** Implement automated monitoring for key metrics, set up alerting thresholds, create performance baselines, use APM tools, monitor query patterns, track resource utilization trends, and establish regular performance reviews.

**Q8: How would you optimize a database system that's experiencing high I/O wait times?**
**A:** Analyze disk usage patterns, optimize indexes to reduce I/O, implement proper partitioning, use SSD storage, separate data/log/tempdb files, optimize buffer pool settings, and consider read replicas for read-heavy workloads.

**Q9: What strategies would you use to scale a database system that's reaching its performance limits?**
**A:** Vertical scaling (more powerful hardware), horizontal scaling (sharding/partitioning), read replicas, caching layers, database optimization, archiving old data, and considering NoSQL alternatives for specific use cases.

---

[← Previous: Database Security](../14-database-security/README.md) | [Back to Index](../README.md)