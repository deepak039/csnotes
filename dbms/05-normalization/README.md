# Normalization

## What is Normalization?

Normalization is the process of organizing data in a database to reduce redundancy and improve data integrity. It involves decomposing tables into smaller, well-structured tables and defining relationships between them.

## Goals of Normalization

1. **Eliminate Data Redundancy**: Reduce duplicate data storage
2. **Ensure Data Integrity**: Maintain consistency and accuracy
3. **Minimize Update Anomalies**: Prevent inconsistencies during updates
4. **Optimize Storage**: Reduce storage space requirements
5. **Improve Query Performance**: Enable efficient data retrieval

## Types of Anomalies

### 1. Insertion Anomaly
Cannot insert certain data without having other unrelated data.

**Example**: Cannot add a new department without having at least one employee.

### 2. Update Anomaly
Need to update the same information in multiple places.

**Example**: Changing department name requires updating all employee records in that department.

### 3. Deletion Anomaly
Deleting a record causes loss of other valuable information.

**Example**: Deleting the last employee in a department loses department information.

## Functional Dependencies

A functional dependency X → Y means that for any two tuples, if they have the same value for X, they must have the same value for Y.

### Examples:
```
StudentID → StudentName, Email, DateOfBirth
CourseID → CourseName, Credits, Department
ISBN → BookTitle, Author, Publisher
```

### Types of Functional Dependencies:

#### 1. Trivial Dependency
X → Y where Y ⊆ X
```
{StudentID, StudentName} → StudentName  (Trivial)
```

#### 2. Non-trivial Dependency
X → Y where Y ⊄ X
```
StudentID → StudentName  (Non-trivial)
```

#### 3. Completely Non-trivial Dependency
X → Y where X ∩ Y = ∅
```
StudentID → Email  (Completely non-trivial)
```

## Normal Forms

### 1. First Normal Form (1NF)

#### Rules:
- Each column contains atomic (indivisible) values
- No repeating groups or arrays
- Each row is unique
- Order of rows and columns doesn't matter

#### Example - Violation of 1NF:
```sql
-- BAD: Multiple phone numbers in one column
CREATE TABLE Students_Bad (
    StudentID INT,
    Name VARCHAR(100),
    PhoneNumbers VARCHAR(200)  -- "123-456-7890, 987-654-3210"
);
```

#### Example - 1NF Compliant:
```sql
-- GOOD: Atomic values
CREATE TABLE Students (
    StudentID INT PRIMARY KEY,
    Name VARCHAR(100),
    Email VARCHAR(100)
);

CREATE TABLE StudentPhones (
    StudentID INT,
    PhoneNumber VARCHAR(15),
    PhoneType VARCHAR(10),
    PRIMARY KEY (StudentID, PhoneNumber),
    FOREIGN KEY (StudentID) REFERENCES Students(StudentID)
);
```

### 2. Second Normal Form (2NF)

#### Rules:
- Must be in 1NF
- No partial dependencies on composite primary key
- All non-key attributes must be fully functionally dependent on the entire primary key

#### Example - Violation of 2NF:
```sql
-- BAD: Partial dependency
CREATE TABLE StudentCourses_Bad (
    StudentID INT,
    CourseID INT,
    StudentName VARCHAR(100),    -- Depends only on StudentID
    CourseName VARCHAR(100),     -- Depends only on CourseID
    Grade CHAR(2),               -- Depends on both StudentID and CourseID
    PRIMARY KEY (StudentID, CourseID)
);
```

#### Example - 2NF Compliant:
```sql
-- GOOD: Separate tables
CREATE TABLE Students (
    StudentID INT PRIMARY KEY,
    StudentName VARCHAR(100)
);

CREATE TABLE Courses (
    CourseID INT PRIMARY KEY,
    CourseName VARCHAR(100)
);

CREATE TABLE Enrollments (
    StudentID INT,
    CourseID INT,
    Grade CHAR(2),
    PRIMARY KEY (StudentID, CourseID),
    FOREIGN KEY (StudentID) REFERENCES Students(StudentID),
    FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
);
```

### 3. Third Normal Form (3NF)

#### Rules:
- Must be in 2NF
- No transitive dependencies
- All non-key attributes must be directly dependent on the primary key

#### Example - Violation of 3NF:
```sql
-- BAD: Transitive dependency
CREATE TABLE Employees_Bad (
    EmpID INT PRIMARY KEY,
    Name VARCHAR(100),
    DeptID INT,
    DeptName VARCHAR(100),       -- Transitively dependent: EmpID → DeptID → DeptName
    DeptLocation VARCHAR(100)    -- Transitively dependent: EmpID → DeptID → DeptLocation
);
```

#### Example - 3NF Compliant:
```sql
-- GOOD: Remove transitive dependencies
CREATE TABLE Departments (
    DeptID INT PRIMARY KEY,
    DeptName VARCHAR(100),
    DeptLocation VARCHAR(100)
);

CREATE TABLE Employees (
    EmpID INT PRIMARY KEY,
    Name VARCHAR(100),
    DeptID INT,
    FOREIGN KEY (DeptID) REFERENCES Departments(DeptID)
);
```

### 4. Boyce-Codd Normal Form (BCNF)

#### Rules:
- Must be in 3NF
- For every functional dependency X → Y, X must be a super key
- Stronger version of 3NF

#### Example - 3NF but not BCNF:
```sql
-- Student-Advisor-Subject relationship
-- Assumptions:
-- - Each student has one advisor per subject
-- - Each advisor teaches only one subject
-- - A subject can have multiple advisors

CREATE TABLE StudentAdvisor_3NF (
    StudentID INT,
    Subject VARCHAR(50),
    Advisor VARCHAR(100),
    PRIMARY KEY (StudentID, Subject)
);

-- Functional Dependencies:
-- {StudentID, Subject} → Advisor  (Primary key dependency)
-- Advisor → Subject                (Violates BCNF - Advisor is not a super key)
```

#### Example - BCNF Compliant:
```sql
-- GOOD: Decompose to achieve BCNF
CREATE TABLE Advisors (
    Advisor VARCHAR(100) PRIMARY KEY,
    Subject VARCHAR(50)
);

CREATE TABLE StudentAdvisors (
    StudentID INT,
    Advisor VARCHAR(100),
    PRIMARY KEY (StudentID, Advisor),
    FOREIGN KEY (Advisor) REFERENCES Advisors(Advisor)
);
```

### 5. Fourth Normal Form (4NF)

#### Rules:
- Must be in BCNF
- No multi-valued dependencies
- If A →→ B (A multi-determines B), then A →→ C for all other attributes C

#### Example - Violation of 4NF:
```sql
-- BAD: Multi-valued dependencies
CREATE TABLE StudentSkillsHobbies_Bad (
    StudentID INT,
    Skill VARCHAR(50),
    Hobby VARCHAR(50),
    PRIMARY KEY (StudentID, Skill, Hobby)
);

-- Multi-valued dependencies:
-- StudentID →→ Skill (independent of Hobby)
-- StudentID →→ Hobby (independent of Skill)
```

#### Example - 4NF Compliant:
```sql
-- GOOD: Separate multi-valued dependencies
CREATE TABLE StudentSkills (
    StudentID INT,
    Skill VARCHAR(50),
    PRIMARY KEY (StudentID, Skill)
);

CREATE TABLE StudentHobbies (
    StudentID INT,
    Hobby VARCHAR(50),
    PRIMARY KEY (StudentID, Hobby)
);
```

### 6. Fifth Normal Form (5NF)

#### Rules:
- Must be in 4NF
- No join dependencies
- Cannot be decomposed further without loss of information

#### Example:
```sql
-- Complex relationship: Student-Course-Instructor
-- Where specific combinations are valid
CREATE TABLE ValidCombinations (
    StudentID INT,
    CourseID INT,
    InstructorID INT,
    PRIMARY KEY (StudentID, CourseID, InstructorID)
);

-- If this cannot be decomposed into smaller tables without losing information,
-- it's in 5NF
```

## Denormalization

### When to Denormalize:

1. **Performance Requirements**: When joins become too expensive
2. **Read-Heavy Workloads**: When reads far exceed writes
3. **Reporting Needs**: For analytical queries and reports
4. **Caching Strategies**: To reduce computation overhead

### Example:
```sql
-- Denormalized for reporting
CREATE TABLE EmployeeReport (
    EmpID INT,
    EmpName VARCHAR(100),
    DeptID INT,
    DeptName VARCHAR(100),      -- Denormalized
    DeptLocation VARCHAR(100),  -- Denormalized
    Salary DECIMAL(10,2),
    LastUpdated TIMESTAMP
);
```

## Normalization Process Example

### Original Unnormalized Table:
```sql
CREATE TABLE StudentRecords (
    StudentID INT,
    StudentName VARCHAR(100),
    StudentEmail VARCHAR(100),
    Course1 VARCHAR(50),
    Course1_Credits INT,
    Course2 VARCHAR(50),
    Course2_Credits INT,
    AdvisorName VARCHAR(100),
    AdvisorEmail VARCHAR(100),
    AdvisorDept VARCHAR(50)
);
```

### Step 1: Convert to 1NF
```sql
CREATE TABLE StudentCourses_1NF (
    StudentID INT,
    StudentName VARCHAR(100),
    StudentEmail VARCHAR(100),
    CourseName VARCHAR(50),
    CourseCredits INT,
    AdvisorName VARCHAR(100),
    AdvisorEmail VARCHAR(100),
    AdvisorDept VARCHAR(50)
);
```

### Step 2: Convert to 2NF
```sql
CREATE TABLE Students_2NF (
    StudentID INT PRIMARY KEY,
    StudentName VARCHAR(100),
    StudentEmail VARCHAR(100),
    AdvisorName VARCHAR(100),
    AdvisorEmail VARCHAR(100),
    AdvisorDept VARCHAR(50)
);

CREATE TABLE Courses_2NF (
    CourseName VARCHAR(50) PRIMARY KEY,
    CourseCredits INT
);

CREATE TABLE StudentCourses_2NF (
    StudentID INT,
    CourseName VARCHAR(50),
    PRIMARY KEY (StudentID, CourseName)
);
```

### Step 3: Convert to 3NF
```sql
CREATE TABLE Students_3NF (
    StudentID INT PRIMARY KEY,
    StudentName VARCHAR(100),
    StudentEmail VARCHAR(100),
    AdvisorID INT
);

CREATE TABLE Advisors_3NF (
    AdvisorID INT PRIMARY KEY,
    AdvisorName VARCHAR(100),
    AdvisorEmail VARCHAR(100),
    AdvisorDept VARCHAR(50)
);

CREATE TABLE Courses_3NF (
    CourseName VARCHAR(50) PRIMARY KEY,
    CourseCredits INT
);

CREATE TABLE StudentCourses_3NF (
    StudentID INT,
    CourseName VARCHAR(50),
    PRIMARY KEY (StudentID, CourseName)
);
```

## Interview Questions

### Basic Level

**Q1: What is normalization and why is it important?**
**A:** Normalization is organizing data to reduce redundancy and improve integrity. It's important to eliminate update anomalies, save storage space, and maintain data consistency.

**Q2: What are the different types of anomalies in unnormalized databases?**
**A:** Insertion anomaly (can't insert data without unrelated data), Update anomaly (must update multiple places), and Deletion anomaly (lose information when deleting records).

**Q3: What is the difference between 1NF and 2NF?**
**A:** 1NF requires atomic values and no repeating groups. 2NF additionally requires no partial dependencies on composite primary keys.

### Intermediate Level

**Q4: Explain functional dependency with examples.**
**A:** Functional dependency X → Y means if two tuples have the same X value, they must have the same Y value. Example: StudentID → StudentName, Email.

**Q5: What is the difference between 3NF and BCNF?**
**A:** 3NF allows non-key attributes to determine other non-key attributes. BCNF requires that for every functional dependency X → Y, X must be a super key.

**Q6: When would you consider denormalization?**
**A:** For performance optimization in read-heavy systems, reporting requirements, reducing join complexity, or when storage space is less critical than query speed.

### Advanced Level

**Q7: Explain multi-valued dependency and 4NF.**
**A:** Multi-valued dependency A →→ B means A determines a set of values for B, independent of other attributes. 4NF eliminates multi-valued dependencies by decomposing into separate tables.

**Q8: How do you determine if a table is in BCNF?**
**A:** Check all functional dependencies. If any dependency X → Y exists where X is not a super key, the table violates BCNF and needs decomposition.

**Q9: What are the trade-offs between normalization and denormalization?**
**A:** Normalization reduces redundancy and improves integrity but may impact query performance. Denormalization improves read performance but increases storage and update complexity.

---

[← Previous: SQL Fundamentals](../04-sql-fundamentals/README.md) | [Back to Index](../README.md) | [Next: Indexing →](../06-indexing/README.md)