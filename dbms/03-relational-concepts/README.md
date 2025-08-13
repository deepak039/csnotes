# Relational Database Concepts

## Core Components

### 1. Tables (Relations)

A table is a collection of related data organized in rows and columns.

#### Characteristics:
- Each table has a unique name
- Contains zero or more rows (tuples)
- Has a fixed number of columns (attributes)
- Order of rows is not significant
- Order of columns is not significant

#### Example:
```sql
CREATE TABLE Students (
    StudentID INT PRIMARY KEY,
    FirstName VARCHAR(50),
    LastName VARCHAR(50),
    Email VARCHAR(100),
    DateOfBirth DATE
);
```

### 2. Rows (Tuples)

A row represents a single record or instance of an entity.

#### Properties:
- Each row is unique
- Contains values for all attributes
- NULL values allowed for optional attributes
- Represents one entity instance

### 3. Columns (Attributes)

A column represents a property or characteristic of an entity.

#### Properties:
- Has a specific data type
- Has a domain (set of allowed values)
- Can have constraints
- May allow NULL values

#### Common Data Types:
```sql
-- Numeric Types
INT, BIGINT, DECIMAL(10,2), FLOAT

-- String Types
VARCHAR(255), CHAR(10), TEXT

-- Date/Time Types
DATE, TIME, DATETIME, TIMESTAMP

-- Boolean Type
BOOLEAN
```

## Keys in Relational Databases

### 1. Super Key

A set of one or more attributes that uniquely identifies each tuple in a relation.

#### Example:
In Students table: {StudentID}, {StudentID, FirstName}, {Email}, {Email, LastName}

### 2. Candidate Key

A minimal super key - no proper subset is also a super key.

#### Properties:
- Uniquely identifies each tuple
- No redundant attributes
- A relation can have multiple candidate keys

#### Example:
```sql
-- Both StudentID and Email are candidate keys
StudentID: 1001, Email: "alice@email.com"
StudentID: 1002, Email: "bob@email.com"
```

### 3. Primary Key

The candidate key chosen to uniquely identify tuples in a relation.

#### Properties:
- Must be unique
- Cannot be NULL
- Should be stable (rarely changes)
- Preferably simple (single attribute)

#### Example:
```sql
CREATE TABLE Students (
    StudentID INT PRIMARY KEY,  -- Primary Key
    Email VARCHAR(100) UNIQUE,  -- Alternate Key
    FirstName VARCHAR(50),
    LastName VARCHAR(50)
);
```

### 4. Foreign Key

An attribute (or set of attributes) that references the primary key of another table.

#### Properties:
- Establishes relationships between tables
- Can be NULL (unless specified otherwise)
- Must match existing primary key value
- Maintains referential integrity

#### Example:
```sql
CREATE TABLE Enrollments (
    EnrollmentID INT PRIMARY KEY,
    StudentID INT,
    CourseID INT,
    Grade CHAR(2),
    FOREIGN KEY (StudentID) REFERENCES Students(StudentID),
    FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
);
```

### 5. Composite Key

A primary key consisting of multiple attributes.

#### Example:
```sql
CREATE TABLE OrderItems (
    OrderID INT,
    ProductID INT,
    Quantity INT,
    Price DECIMAL(10,2),
    PRIMARY KEY (OrderID, ProductID)  -- Composite Key
);
```

## Relationships

### 1. One-to-One (1:1)

Each record in Table A relates to exactly one record in Table B.

#### Example:
```sql
-- Person and Passport relationship
CREATE TABLE Persons (
    PersonID INT PRIMARY KEY,
    Name VARCHAR(100)
);

CREATE TABLE Passports (
    PassportID INT PRIMARY KEY,
    PassportNumber VARCHAR(20),
    PersonID INT UNIQUE,
    FOREIGN KEY (PersonID) REFERENCES Persons(PersonID)
);
```

### 2. One-to-Many (1:M)

Each record in Table A can relate to multiple records in Table B.

#### Example:
```sql
-- Department and Employee relationship
CREATE TABLE Departments (
    DeptID INT PRIMARY KEY,
    DeptName VARCHAR(100)
);

CREATE TABLE Employees (
    EmpID INT PRIMARY KEY,
    Name VARCHAR(100),
    DeptID INT,
    FOREIGN KEY (DeptID) REFERENCES Departments(DeptID)
);
```

### 3. Many-to-Many (M:N)

Records in Table A can relate to multiple records in Table B and vice versa.

#### Implementation using Junction Table:
```sql
-- Student and Course relationship
CREATE TABLE Students (
    StudentID INT PRIMARY KEY,
    Name VARCHAR(100)
);

CREATE TABLE Courses (
    CourseID INT PRIMARY KEY,
    CourseName VARCHAR(100)
);

CREATE TABLE StudentCourses (
    StudentID INT,
    CourseID INT,
    EnrollmentDate DATE,
    PRIMARY KEY (StudentID, CourseID),
    FOREIGN KEY (StudentID) REFERENCES Students(StudentID),
    FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
);
```

## Constraints

### 1. Domain Constraints

Restrict the values that can be stored in an attribute.

```sql
CREATE TABLE Products (
    ProductID INT PRIMARY KEY,
    Price DECIMAL(10,2) CHECK (Price > 0),
    Category VARCHAR(50) CHECK (Category IN ('Electronics', 'Clothing', 'Books'))
);
```

### 2. Key Constraints

Ensure uniqueness of key attributes.

```sql
CREATE TABLE Users (
    UserID INT PRIMARY KEY,           -- Primary Key Constraint
    Email VARCHAR(100) UNIQUE,        -- Unique Constraint
    Username VARCHAR(50) UNIQUE
);
```

### 3. Referential Integrity Constraints

Ensure foreign key values match existing primary key values.

```sql
CREATE TABLE Orders (
    OrderID INT PRIMARY KEY,
    CustomerID INT,
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
```

### 4. Entity Integrity Constraints

Primary key cannot be NULL.

```sql
-- This is automatically enforced when PRIMARY KEY is specified
CREATE TABLE Books (
    ISBN VARCHAR(13) PRIMARY KEY,  -- Cannot be NULL
    Title VARCHAR(200) NOT NULL,   -- Explicit NOT NULL
    Author VARCHAR(100)
);
```

## Relational Algebra Operations

### 1. Selection (σ)

Selects rows that satisfy a given condition.

```sql
-- σ(Age > 21)(Students)
SELECT * FROM Students WHERE Age > 21;
```

### 2. Projection (π)

Selects specific columns from a relation.

```sql
-- π(Name, Email)(Students)
SELECT Name, Email FROM Students;
```

### 3. Union (∪)

Combines tuples from two relations (must be union-compatible).

```sql
-- Students ∪ Teachers (assuming same schema)
SELECT Name FROM Students
UNION
SELECT Name FROM Teachers;
```

### 4. Intersection (∩)

Returns common tuples from two relations.

```sql
-- Students ∩ Teachers
SELECT Name FROM Students
INTERSECT
SELECT Name FROM Teachers;
```

### 5. Difference (-)

Returns tuples in first relation but not in second.

```sql
-- Students - GraduatedStudents
SELECT Name FROM Students
EXCEPT
SELECT Name FROM GraduatedStudents;
```

### 6. Cartesian Product (×)

Combines every tuple from first relation with every tuple from second.

```sql
-- Students × Courses
SELECT * FROM Students CROSS JOIN Courses;
```

### 7. Join (⋈)

Combines related tuples from two relations.

```sql
-- Natural Join
SELECT * FROM Students NATURAL JOIN Enrollments;

-- Theta Join
SELECT * FROM Students s JOIN Enrollments e ON s.StudentID = e.StudentID;
```

## Schema Design Principles

### 1. Conceptual Design
- Identify entities and relationships
- Create Entity-Relationship (ER) diagram
- Define attributes and constraints

### 2. Logical Design
- Convert ER diagram to relational schema
- Apply normalization rules
- Define keys and constraints

### 3. Physical Design
- Choose storage structures
- Create indexes
- Optimize for performance

## Interview Questions

### Basic Level

**Q1: What is a relation in the relational model?**
**A:** A relation is a table with rows (tuples) and columns (attributes). It represents a set of related data with a specific structure, where each row is unique and represents one instance of an entity.

**Q2: Explain the difference between primary key and foreign key.**
**A:** Primary key uniquely identifies each row in a table and cannot be NULL. Foreign key references the primary key of another table to establish relationships and maintain referential integrity.

**Q3: What are the different types of relationships in relational databases?**
**A:** One-to-One (1:1), One-to-Many (1:M), and Many-to-Many (M:N). M:N relationships require a junction table for implementation.

### Intermediate Level

**Q4: What is the difference between candidate key and super key?**
**A:** Super key is any set of attributes that uniquely identifies tuples. Candidate key is a minimal super key with no redundant attributes. A table can have multiple candidate keys.

**Q5: How do you implement a many-to-many relationship?**
**A:** Create a junction table with foreign keys referencing the primary keys of both related tables. The junction table's primary key is typically a composite key of both foreign keys.

**Q6: Explain referential integrity and its enforcement options.**
**A:** Referential integrity ensures foreign key values match existing primary key values. Enforcement options include CASCADE (propagate changes), SET NULL, SET DEFAULT, and RESTRICT (prevent changes).

### Advanced Level

**Q7: What are the different types of constraints in relational databases?**
**A:** Domain constraints (data type and value restrictions), key constraints (uniqueness), referential integrity constraints (foreign key relationships), and entity integrity constraints (primary key not null).

**Q8: How do relational algebra operations relate to SQL queries?**
**A:** Selection (σ) → WHERE clause, Projection (π) → SELECT clause, Join (⋈) → JOIN operations, Union (∪) → UNION operator, etc. SQL is based on relational algebra principles.

**Q9: What factors should you consider when choosing a primary key?**
**A:** Uniqueness, stability (rarely changes), simplicity (preferably single attribute), non-null values, and performance implications for indexing and joins.

---

[← Previous: Database Models](../02-database-models/README.md) | [Back to Index](../README.md) | [Next: SQL Fundamentals →](../04-sql-fundamentals/README.md)