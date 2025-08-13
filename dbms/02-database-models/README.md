# Database Models

## Overview

Database models define how data is structured, stored, and manipulated in a database system. Each model has its own approach to organizing data and relationships.

## 1. Hierarchical Model

### Structure
- Tree-like structure with parent-child relationships
- Each child has exactly one parent
- Root node has no parent

### Characteristics
- **One-to-Many relationships** only
- Navigation through predefined paths
- Fast access to data through hierarchy

### Example
```
Company (Root)
├── Department A
│   ├── Employee 1
│   └── Employee 2
└── Department B
    ├── Employee 3
    └── Employee 4
```

### Advantages
- Simple and fast for hierarchical data
- Efficient storage and retrieval
- Data integrity through structure

### Disadvantages
- Inflexible structure
- Difficult to represent many-to-many relationships
- Complex queries for non-hierarchical access

## 2. Network Model

### Structure
- Graph structure with nodes and edges
- Records connected through links
- Supports many-to-many relationships

### Characteristics
- More flexible than hierarchical model
- Complex navigation through pointers
- CODASYL (Conference on Data Systems Languages) standard

### Example
```
Student ←→ Course (Many-to-Many)
Student ←→ Department (Many-to-One)
Course ←→ Department (Many-to-One)
```

### Advantages
- Supports complex relationships
- More flexible than hierarchical
- Efficient for complex queries

### Disadvantages
- Complex structure and navigation
- Difficult to maintain
- Requires knowledge of physical structure

## 3. Relational Model

### Structure
- Data organized in tables (relations)
- Rows (tuples) and columns (attributes)
- Relationships through foreign keys

### Key Concepts

#### Tables (Relations)
- Collection of related data entries
- Each table has a unique name
- Consists of rows and columns

#### Attributes (Columns)
- Properties of entities
- Have specific data types
- Domain defines possible values

#### Tuples (Rows)
- Individual records in a table
- Each tuple is unique
- Represents one instance of entity

#### Keys
- **Primary Key**: Uniquely identifies each row
- **Foreign Key**: References primary key of another table
- **Candidate Key**: Potential primary keys
- **Super Key**: Set of attributes that uniquely identify tuples

### Example
```sql
-- Students Table
StudentID | Name    | Age | DepartmentID
1        | Alice   | 20  | 101
2        | Bob     | 21  | 102

-- Departments Table
DeptID | DeptName     | Location
101    | Computer Sci | Building A
102    | Mathematics  | Building B
```

### Advantages
- Simple and intuitive structure
- Flexible query capabilities (SQL)
- Data independence
- Strong theoretical foundation
- ACID properties support

### Disadvantages
- Performance overhead for complex queries
- Impedance mismatch with object-oriented programming
- Limited support for complex data types

## 4. Object-Oriented Model

### Structure
- Data stored as objects
- Objects have attributes and methods
- Supports inheritance and encapsulation

### Key Concepts

#### Objects
- Instances of classes
- Contain both data and behavior
- Unique object identifier (OID)

#### Classes
- Templates for creating objects
- Define attributes and methods
- Support inheritance hierarchy

#### Inheritance
- Classes can inherit from other classes
- Promotes code reuse
- Supports polymorphism

### Example
```java
class Person {
    String name;
    int age;
    void displayInfo() { /* method */ }
}

class Student extends Person {
    String studentID;
    String major;
    void enroll() { /* method */ }
}
```

### Advantages
- Natural representation of real-world entities
- Supports complex data types
- Code reusability through inheritance
- Encapsulation provides data security

### Disadvantages
- Complex implementation
- Performance overhead
- Limited standardization
- Steep learning curve

## 5. NoSQL Models

### Document Model

#### Structure
- Data stored as documents (JSON, XML, BSON)
- Documents can contain nested structures
- Schema-flexible

#### Example
```json
{
  "_id": "student123",
  "name": "Alice",
  "age": 20,
  "courses": [
    {"name": "Database", "grade": "A"},
    {"name": "Algorithms", "grade": "B+"}
  ],
  "address": {
    "street": "123 Main St",
    "city": "Boston"
  }
}
```

#### Use Cases
- Content management systems
- Catalogs and inventories
- User profiles and preferences

### Key-Value Model

#### Structure
- Simple key-value pairs
- Keys are unique identifiers
- Values can be any data type

#### Example
```
user:1001 → {"name": "Alice", "email": "alice@email.com"}
session:abc123 → {"userId": 1001, "loginTime": "2024-01-01T10:00:00Z"}
```

#### Use Cases
- Caching systems
- Session management
- Shopping carts

### Column-Family Model

#### Structure
- Data organized in column families
- Rows can have different columns
- Optimized for write-heavy workloads

#### Example
```
Row Key: user123
Column Family: profile
  name: "Alice"
  age: 20
  email: "alice@email.com"

Column Family: activity
  last_login: "2024-01-01"
  page_views: 150
```

#### Use Cases
- Time-series data
- IoT applications
- Analytics platforms

### Graph Model

#### Structure
- Data represented as nodes and edges
- Nodes represent entities
- Edges represent relationships

#### Example
```
(Alice)-[FRIENDS_WITH]->(Bob)
(Alice)-[WORKS_AT]->(Company A)
(Bob)-[LIVES_IN]->(New York)
```

#### Use Cases
- Social networks
- Recommendation engines
- Fraud detection

## Model Comparison

| Model | Structure | Relationships | Flexibility | Query Language | Use Cases |
|-------|-----------|---------------|-------------|----------------|-----------|
| **Hierarchical** | Tree | One-to-Many | Low | Procedural | Legacy systems |
| **Network** | Graph | Many-to-Many | Medium | Procedural | Complex relationships |
| **Relational** | Tables | All types | High | SQL | Business applications |
| **Object-Oriented** | Objects | All types | High | OQL | Complex applications |
| **Document** | Documents | Embedded/Referenced | Very High | Query APIs | Web applications |
| **Key-Value** | Key-Value pairs | None | Medium | Simple APIs | Caching, sessions |
| **Column-Family** | Column families | Limited | High | CQL/APIs | Big data, analytics |
| **Graph** | Nodes/Edges | Rich relationships | High | Graph queries | Social networks |

## Interview Questions

### Basic Level

**Q1: What is a database model?**
**A:** A database model defines the logical structure of a database, including how data is stored, organized, and manipulated. It provides a framework for database design and operations.

**Q2: Compare hierarchical and network database models.**
**A:** Hierarchical model uses tree structure with one-to-many relationships, while network model uses graph structure supporting many-to-many relationships. Network model is more flexible but complex.

**Q3: What are the main components of the relational model?**
**A:** Tables (relations), rows (tuples), columns (attributes), and keys (primary, foreign, candidate). Data is organized in tables with relationships established through foreign keys.

### Intermediate Level

**Q4: Explain the advantages and disadvantages of the relational model.**
**A:** 
**Advantages**: Simple structure, SQL support, data independence, ACID properties, theoretical foundation
**Disadvantages**: Performance overhead, impedance mismatch with OOP, limited complex data type support

**Q5: What is the difference between document and key-value NoSQL models?**
**A:** Document model stores structured documents (JSON/XML) with nested data and flexible schema. Key-value model stores simple key-value pairs with minimal structure, optimized for fast lookups.

**Q6: When would you choose a graph database over a relational database?**
**A:** Choose graph databases for highly connected data with complex relationships like social networks, recommendation systems, or fraud detection where relationship traversal is more important than structured queries.

### Advanced Level

**Q7: How do object-oriented databases handle inheritance and polymorphism?**
**A:** Object-oriented databases support class hierarchies where subclasses inherit attributes and methods from parent classes. Polymorphism allows objects of different classes to be treated uniformly through common interfaces.

**Q8: Compare ACID properties in relational vs NoSQL databases.**
**A:** Relational databases strictly enforce ACID properties. NoSQL databases often trade ACID compliance for performance and scalability, following BASE (Basically Available, Soft state, Eventual consistency) principles.

**Q9: What factors should you consider when choosing between different database models?**
**A:** Consider data structure complexity, scalability requirements, consistency needs, query patterns, development team expertise, performance requirements, and integration with existing systems.

---

[← Previous: Introduction](../01-introduction/README.md) | [Back to Index](../README.md) | [Next: Relational Concepts →](../03-relational-concepts/README.md)