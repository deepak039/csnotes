# SQL Fundamentals

## SQL Overview

SQL (Structured Query Language) is a standard language for managing relational databases. It provides commands for defining, manipulating, and controlling data.

## SQL Categories

### 1. DDL (Data Definition Language)
Commands that define database structure and schema.

#### CREATE
```sql
-- Create Database
CREATE DATABASE CompanyDB;

-- Create Table
CREATE TABLE Employees (
    EmpID INT PRIMARY KEY,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL,
    Email VARCHAR(100) UNIQUE,
    Salary DECIMAL(10,2),
    DeptID INT,
    HireDate DATE DEFAULT CURRENT_DATE
);

-- Create Index
CREATE INDEX idx_employee_email ON Employees(Email);
```

#### ALTER
```sql
-- Add Column
ALTER TABLE Employees ADD COLUMN Phone VARCHAR(15);

-- Modify Column
ALTER TABLE Employees MODIFY COLUMN Salary DECIMAL(12,2);

-- Drop Column
ALTER TABLE Employees DROP COLUMN Phone;

-- Add Constraint
ALTER TABLE Employees ADD CONSTRAINT fk_dept 
FOREIGN KEY (DeptID) REFERENCES Departments(DeptID);
```

#### DROP
```sql
-- Drop Table
DROP TABLE Employees;

-- Drop Database
DROP DATABASE CompanyDB;

-- Drop Index
DROP INDEX idx_employee_email;
```

### 2. DML (Data Manipulation Language)
Commands that manipulate data within tables.

#### INSERT
```sql
-- Insert Single Row
INSERT INTO Employees (EmpID, FirstName, LastName, Email, Salary, DeptID)
VALUES (1, 'John', 'Doe', 'john.doe@email.com', 50000, 101);

-- Insert Multiple Rows
INSERT INTO Employees VALUES 
(2, 'Jane', 'Smith', 'jane.smith@email.com', 55000, 102, '2024-01-15'),
(3, 'Bob', 'Johnson', 'bob.johnson@email.com', 48000, 101, '2024-02-01');

-- Insert from Another Table
INSERT INTO ArchivedEmployees 
SELECT * FROM Employees WHERE HireDate < '2020-01-01';
```

#### UPDATE
```sql
-- Update Single Row
UPDATE Employees 
SET Salary = 52000 
WHERE EmpID = 1;

-- Update Multiple Rows
UPDATE Employees 
SET Salary = Salary * 1.1 
WHERE DeptID = 101;

-- Update with JOIN
UPDATE Employees e
JOIN Departments d ON e.DeptID = d.DeptID
SET e.Salary = e.Salary * 1.05
WHERE d.DeptName = 'Engineering';
```

#### DELETE
```sql
-- Delete Specific Rows
DELETE FROM Employees WHERE EmpID = 1;

-- Delete with Condition
DELETE FROM Employees WHERE Salary < 30000;

-- Delete All Rows (but keep table structure)
DELETE FROM Employees;
```

#### SELECT
```sql
-- Basic SELECT
SELECT FirstName, LastName, Salary FROM Employees;

-- SELECT with WHERE
SELECT * FROM Employees WHERE Salary > 50000;

-- SELECT with ORDER BY
SELECT * FROM Employees ORDER BY Salary DESC, LastName ASC;

-- SELECT with LIMIT
SELECT * FROM Employees ORDER BY Salary DESC LIMIT 5;
```

### 3. DCL (Data Control Language)
Commands that control access to data.

#### GRANT
```sql
-- Grant SELECT permission
GRANT SELECT ON Employees TO user1;

-- Grant multiple permissions
GRANT SELECT, INSERT, UPDATE ON Employees TO user2;

-- Grant all permissions
GRANT ALL PRIVILEGES ON CompanyDB.* TO admin_user;
```

#### REVOKE
```sql
-- Revoke specific permission
REVOKE INSERT ON Employees FROM user2;

-- Revoke all permissions
REVOKE ALL PRIVILEGES ON CompanyDB.* FROM user1;
```

### 4. TCL (Transaction Control Language)
Commands that manage transactions.

#### COMMIT
```sql
START TRANSACTION;
UPDATE Employees SET Salary = Salary * 1.1 WHERE DeptID = 101;
INSERT INTO AuditLog VALUES (NOW(), 'Salary increase for Dept 101');
COMMIT;
```

#### ROLLBACK
```sql
START TRANSACTION;
DELETE FROM Employees WHERE DeptID = 101;
-- Oops, wrong department!
ROLLBACK;
```

#### SAVEPOINT
```sql
START TRANSACTION;
UPDATE Employees SET Salary = Salary * 1.1;
SAVEPOINT sp1;
DELETE FROM Employees WHERE Salary < 30000;
ROLLBACK TO sp1;  -- Only rollback the DELETE
COMMIT;
```

## Advanced SQL Concepts

### 1. Joins

#### INNER JOIN
```sql
SELECT e.FirstName, e.LastName, d.DeptName
FROM Employees e
INNER JOIN Departments d ON e.DeptID = d.DeptID;
```

#### LEFT JOIN
```sql
SELECT e.FirstName, e.LastName, d.DeptName
FROM Employees e
LEFT JOIN Departments d ON e.DeptID = d.DeptID;
```

#### RIGHT JOIN
```sql
SELECT e.FirstName, e.LastName, d.DeptName
FROM Employees e
RIGHT JOIN Departments d ON e.DeptID = d.DeptID;
```

#### FULL OUTER JOIN
```sql
SELECT e.FirstName, e.LastName, d.DeptName
FROM Employees e
FULL OUTER JOIN Departments d ON e.DeptID = d.DeptID;
```

#### SELF JOIN
```sql
SELECT e1.FirstName AS Employee, e2.FirstName AS Manager
FROM Employees e1
JOIN Employees e2 ON e1.ManagerID = e2.EmpID;
```

### 2. Subqueries

#### Single Row Subquery
```sql
SELECT FirstName, LastName
FROM Employees
WHERE Salary = (SELECT MAX(Salary) FROM Employees);
```

#### Multiple Row Subquery
```sql
SELECT FirstName, LastName
FROM Employees
WHERE DeptID IN (SELECT DeptID FROM Departments WHERE Location = 'New York');
```

#### Correlated Subquery
```sql
SELECT FirstName, LastName, Salary
FROM Employees e1
WHERE Salary > (SELECT AVG(Salary) FROM Employees e2 WHERE e2.DeptID = e1.DeptID);
```

#### EXISTS
```sql
SELECT FirstName, LastName
FROM Employees e
WHERE EXISTS (SELECT 1 FROM Projects p WHERE p.EmpID = e.EmpID);
```

### 3. Aggregate Functions

#### Basic Aggregates
```sql
SELECT 
    COUNT(*) as TotalEmployees,
    AVG(Salary) as AverageSalary,
    MIN(Salary) as MinSalary,
    MAX(Salary) as MaxSalary,
    SUM(Salary) as TotalSalary
FROM Employees;
```

#### GROUP BY
```sql
SELECT DeptID, COUNT(*) as EmployeeCount, AVG(Salary) as AvgSalary
FROM Employees
GROUP BY DeptID;
```

#### HAVING
```sql
SELECT DeptID, COUNT(*) as EmployeeCount
FROM Employees
GROUP BY DeptID
HAVING COUNT(*) > 5;
```

### 4. Window Functions

#### ROW_NUMBER()
```sql
SELECT FirstName, LastName, Salary,
       ROW_NUMBER() OVER (ORDER BY Salary DESC) as SalaryRank
FROM Employees;
```

#### RANK() and DENSE_RANK()
```sql
SELECT FirstName, LastName, Salary,
       RANK() OVER (ORDER BY Salary DESC) as Rank,
       DENSE_RANK() OVER (ORDER BY Salary DESC) as DenseRank
FROM Employees;
```

#### Partitioned Window Functions
```sql
SELECT FirstName, LastName, DeptID, Salary,
       ROW_NUMBER() OVER (PARTITION BY DeptID ORDER BY Salary DESC) as DeptRank
FROM Employees;
```

### 5. Common Table Expressions (CTE)

#### Basic CTE
```sql
WITH HighEarners AS (
    SELECT * FROM Employees WHERE Salary > 60000
)
SELECT FirstName, LastName, Salary
FROM HighEarners
ORDER BY Salary DESC;
```

#### Recursive CTE
```sql
WITH RECURSIVE EmployeeHierarchy AS (
    -- Base case: top-level managers
    SELECT EmpID, FirstName, LastName, ManagerID, 1 as Level
    FROM Employees
    WHERE ManagerID IS NULL
    
    UNION ALL
    
    -- Recursive case: employees with managers
    SELECT e.EmpID, e.FirstName, e.LastName, e.ManagerID, eh.Level + 1
    FROM Employees e
    JOIN EmployeeHierarchy eh ON e.ManagerID = eh.EmpID
)
SELECT * FROM EmployeeHierarchy ORDER BY Level, LastName;
```

## SQL Best Practices

### 1. Query Optimization
```sql
-- Use indexes effectively
CREATE INDEX idx_employee_dept_salary ON Employees(DeptID, Salary);

-- Avoid SELECT *
SELECT FirstName, LastName, Email FROM Employees;  -- Good
SELECT * FROM Employees;  -- Avoid

-- Use WHERE clauses to limit results
SELECT * FROM Employees WHERE DeptID = 101;
```

### 2. Data Integrity
```sql
-- Use constraints
CREATE TABLE Orders (
    OrderID INT PRIMARY KEY,
    CustomerID INT NOT NULL,
    OrderDate DATE DEFAULT CURRENT_DATE,
    Amount DECIMAL(10,2) CHECK (Amount > 0),
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID)
);
```

### 3. Transaction Management
```sql
-- Use transactions for related operations
START TRANSACTION;
UPDATE Accounts SET Balance = Balance - 100 WHERE AccountID = 1;
UPDATE Accounts SET Balance = Balance + 100 WHERE AccountID = 2;
COMMIT;
```

## Interview Questions

### Basic Level

**Q1: What are the different types of SQL commands?**
**A:** DDL (CREATE, ALTER, DROP), DML (SELECT, INSERT, UPDATE, DELETE), DCL (GRANT, REVOKE), and TCL (COMMIT, ROLLBACK, SAVEPOINT).

**Q2: What is the difference between DELETE and TRUNCATE?**
**A:** DELETE removes specific rows and can be rolled back, supports WHERE clause. TRUNCATE removes all rows, is faster, cannot be rolled back, and resets auto-increment counters.

**Q3: Explain the difference between WHERE and HAVING clauses.**
**A:** WHERE filters rows before grouping, cannot use aggregate functions. HAVING filters groups after GROUP BY, can use aggregate functions.

### Intermediate Level

**Q4: What are the different types of JOINs? Explain with examples.**
**A:** 
- **INNER JOIN**: Returns matching rows from both tables
- **LEFT JOIN**: Returns all rows from left table, matching from right
- **RIGHT JOIN**: Returns all rows from right table, matching from left
- **FULL OUTER JOIN**: Returns all rows from both tables

**Q5: What is a correlated subquery? How is it different from a regular subquery?**
**A:** Correlated subquery references columns from the outer query and executes once for each row. Regular subquery executes once and returns results to outer query.

**Q6: Explain window functions and their advantages.**
**A:** Window functions perform calculations across related rows without grouping. They provide ROW_NUMBER(), RANK(), LAG(), LEAD(), etc., allowing complex analytics while preserving row-level detail.

### Advanced Level

**Q7: How do you optimize SQL queries for better performance?**
**A:** Use appropriate indexes, avoid SELECT *, use WHERE clauses early, optimize JOINs, use LIMIT for large datasets, analyze execution plans, and consider query rewriting.

**Q8: What are CTEs and when would you use them?**
**A:** Common Table Expressions create temporary named result sets. Use for complex queries, recursive operations, improving readability, and avoiding repeated subqueries.

**Q9: Explain the execution order of SQL clauses.**
**A:** FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT. Understanding this helps write correct queries and optimize performance.

---

[← Previous: Relational Concepts](../03-relational-concepts/README.md) | [Back to Index](../README.md) | [Next: Normalization →](../05-normalization/README.md)