# NoSQL Databases

## What is NoSQL?

NoSQL (Not Only SQL) databases are non-relational databases designed to handle large volumes of unstructured or semi-structured data. They provide flexible schemas, horizontal scalability, and high performance for specific use cases.

## Why NoSQL?

### Limitations of Relational Databases:
- **Rigid Schema**: Difficult to modify structure
- **Vertical Scaling**: Limited scalability options
- **Complex Joins**: Performance issues with large datasets
- **ACID Overhead**: May be unnecessary for some applications

### NoSQL Advantages:
- **Flexible Schema**: Easy to modify data structure
- **Horizontal Scaling**: Distribute across multiple servers
- **High Performance**: Optimized for specific data patterns
- **Big Data Handling**: Designed for large-scale data

## CAP Theorem

### Definition:
In a distributed system, you can only guarantee two of the following three properties:

#### Consistency (C)
All nodes see the same data simultaneously.
```
Example: Banking system - all ATMs must show same account balance
```

#### Availability (A)
System remains operational and responsive.
```
Example: Social media - service should always be accessible
```

#### Partition Tolerance (P)
System continues to operate despite network failures.
```
Example: Distributed system must work even if some nodes are unreachable
```

### CAP Trade-offs:
- **CA Systems**: Traditional RDBMS (MySQL, PostgreSQL)
- **CP Systems**: MongoDB, HBase, Redis
- **AP Systems**: Cassandra, DynamoDB, CouchDB

## Types of NoSQL Databases

### 1. Document Databases

#### Characteristics:
- Store data as documents (JSON, BSON, XML)
- Flexible schema within documents
- Support for nested structures
- Query by document content

#### Examples: MongoDB, CouchDB, Amazon DocumentDB

#### MongoDB Example:
```javascript
// Insert document
db.users.insertOne({
  _id: ObjectId("..."),
  name: "John Doe",
  email: "john@example.com",
  age: 30,
  address: {
    street: "123 Main St",
    city: "Boston",
    zipcode: "02101"
  },
  hobbies: ["reading", "swimming", "coding"],
  created_at: new Date()
});

// Query documents
db.users.find({ age: { $gte: 25 } });
db.users.find({ "address.city": "Boston" });
db.users.find({ hobbies: "coding" });

// Update document
db.users.updateOne(
  { _id: ObjectId("...") },
  { $set: { age: 31 }, $push: { hobbies: "hiking" } }
);
```

#### Use Cases:
- Content management systems
- User profiles and preferences
- Product catalogs
- Real-time analytics

### 2. Key-Value Stores

#### Characteristics:
- Simple key-value pairs
- Fast lookups by key
- Minimal query capabilities
- High performance and scalability

#### Examples: Redis, Amazon DynamoDB, Riak

#### Redis Example:
```redis
# String operations
SET user:1001:name "John Doe"
GET user:1001:name
INCR user:1001:login_count

# Hash operations
HSET user:1001 name "John Doe" email "john@example.com" age 30
HGET user:1001 name
HGETALL user:1001

# List operations
LPUSH user:1001:notifications "New message"
LRANGE user:1001:notifications 0 -1

# Set operations
SADD user:1001:skills "JavaScript" "Python" "SQL"
SMEMBERS user:1001:skills

# Sorted set operations
ZADD leaderboard 1500 "player1" 1200 "player2"
ZRANGE leaderboard 0 -1 WITHSCORES
```

#### Use Cases:
- Caching layers
- Session management
- Shopping carts
- Real-time recommendations
- Gaming leaderboards

### 3. Column-Family (Wide-Column)

#### Characteristics:
- Data organized in column families
- Rows can have different columns
- Optimized for write-heavy workloads
- Good for time-series data

#### Examples: Cassandra, HBase, Amazon SimpleDB

#### Cassandra Example:
```sql
-- Create keyspace
CREATE KEYSPACE ecommerce 
WITH REPLICATION = {
  'class': 'SimpleStrategy',
  'replication_factor': 3
};

-- Create column family
CREATE TABLE user_activity (
  user_id UUID,
  timestamp TIMESTAMP,
  activity_type TEXT,
  details MAP<TEXT, TEXT>,
  PRIMARY KEY (user_id, timestamp)
) WITH CLUSTERING ORDER BY (timestamp DESC);

-- Insert data
INSERT INTO user_activity (user_id, timestamp, activity_type, details)
VALUES (uuid(), '2024-01-01 10:00:00', 'login', {'ip': '192.168.1.1', 'device': 'mobile'});

-- Query data
SELECT * FROM user_activity 
WHERE user_id = ? AND timestamp >= '2024-01-01';
```

#### Use Cases:
- Time-series data
- IoT sensor data
- Log aggregation
- Real-time analytics
- Event sourcing

### 4. Graph Databases

#### Characteristics:
- Data stored as nodes and relationships
- Optimized for traversing relationships
- Support for complex queries on connections
- ACID properties often supported

#### Examples: Neo4j, Amazon Neptune, ArangoDB

#### Neo4j Example:
```cypher
// Create nodes
CREATE (alice:Person {name: 'Alice', age: 30})
CREATE (bob:Person {name: 'Bob', age: 25})
CREATE (company:Company {name: 'TechCorp'})

// Create relationships
CREATE (alice)-[:FRIENDS_WITH {since: '2020-01-01'}]->(bob)
CREATE (alice)-[:WORKS_AT {position: 'Developer', since: '2022-01-01'}]->(company)
CREATE (bob)-[:WORKS_AT {position: 'Designer', since: '2021-06-01'}]->(company)

// Query relationships
MATCH (p:Person)-[:FRIENDS_WITH]->(friend:Person)
WHERE p.name = 'Alice'
RETURN friend.name

// Find colleagues
MATCH (p:Person)-[:WORKS_AT]->(c:Company)<-[:WORKS_AT]-(colleague:Person)
WHERE p.name = 'Alice' AND p <> colleague
RETURN colleague.name

// Shortest path
MATCH path = shortestPath((alice:Person {name: 'Alice'})-[*]-(target:Person {name: 'Charlie'}))
RETURN path
```

#### Use Cases:
- Social networks
- Recommendation engines
- Fraud detection
- Knowledge graphs
- Network analysis

## NoSQL vs SQL Comparison

| Aspect | SQL (RDBMS) | NoSQL |
|--------|-------------|-------|
| **Schema** | Fixed, predefined | Flexible, dynamic |
| **Scalability** | Vertical (scale up) | Horizontal (scale out) |
| **ACID** | Full ACID compliance | Eventual consistency |
| **Queries** | Complex SQL queries | Simple queries, some support complex |
| **Relationships** | Strong relationships via JOINs | Denormalized, embedded relationships |
| **Consistency** | Strong consistency | Eventual consistency |
| **Use Cases** | Complex transactions, reporting | Big data, real-time, flexible schema |

## Data Modeling in NoSQL

### 1. Document Database Modeling

#### Embedding vs Referencing:

##### Embedding (Denormalization):
```javascript
// User document with embedded posts
{
  _id: ObjectId("..."),
  name: "John Doe",
  email: "john@example.com",
  posts: [
    {
      title: "My First Post",
      content: "Hello World!",
      created_at: ISODate("2024-01-01")
    },
    {
      title: "Another Post",
      content: "More content...",
      created_at: ISODate("2024-01-02")
    }
  ]
}
```

##### Referencing (Normalization):
```javascript
// User document
{
  _id: ObjectId("user123"),
  name: "John Doe",
  email: "john@example.com"
}

// Post documents
{
  _id: ObjectId("post456"),
  title: "My First Post",
  content: "Hello World!",
  author_id: ObjectId("user123"),
  created_at: ISODate("2024-01-01")
}
```

### 2. Key-Value Modeling

#### Hierarchical Keys:
```redis
# User data
user:1001:profile → {"name": "John", "email": "john@example.com"}
user:1001:preferences → {"theme": "dark", "language": "en"}
user:1001:stats → {"login_count": 42, "last_login": "2024-01-01"}

# Session data
session:abc123 → {"user_id": 1001, "expires": "2024-01-01T12:00:00Z"}
```

### 3. Column-Family Modeling

#### Time-Series Pattern:
```sql
-- Sensor data table
CREATE TABLE sensor_data (
  sensor_id UUID,
  date DATE,
  timestamp TIMESTAMP,
  temperature DOUBLE,
  humidity DOUBLE,
  pressure DOUBLE,
  PRIMARY KEY ((sensor_id, date), timestamp)
);
```

## Consistency Models

### 1. Strong Consistency
```
All reads receive the most recent write
Example: Banking transactions
```

### 2. Eventual Consistency
```
System will become consistent over time
Example: Social media posts, DNS updates
```

### 3. Weak Consistency
```
No guarantees about when data will be consistent
Example: Live video streaming, gaming
```

## NoSQL Scaling Patterns

### 1. Sharding (Horizontal Partitioning)

#### Hash-based Sharding:
```javascript
// MongoDB sharding
sh.enableSharding("ecommerce")
sh.shardCollection("ecommerce.products", {"product_id": "hashed"})

// Data distributed across shards based on hash of product_id
```

#### Range-based Sharding:
```javascript
// Shard by date ranges
sh.shardCollection("ecommerce.orders", {"order_date": 1})
// Shard 1: 2024-01-01 to 2024-03-31
// Shard 2: 2024-04-01 to 2024-06-30
```

### 2. Replication

#### Master-Slave Replication:
```javascript
// MongoDB replica set
rs.initiate({
  _id: "myReplicaSet",
  members: [
    {_id: 0, host: "mongodb1.example.com:27017"},
    {_id: 1, host: "mongodb2.example.com:27017"},
    {_id: 2, host: "mongodb3.example.com:27017"}
  ]
})
```

## Performance Optimization

### 1. Indexing in NoSQL

#### MongoDB Indexes:
```javascript
// Single field index
db.users.createIndex({email: 1})

// Compound index
db.users.createIndex({status: 1, created_at: -1})

// Text index for search
db.products.createIndex({name: "text", description: "text"})

// Geospatial index
db.locations.createIndex({coordinates: "2dsphere"})
```

### 2. Query Optimization

#### MongoDB Query Optimization:
```javascript
// Use projection to limit returned fields
db.users.find({status: "active"}, {name: 1, email: 1})

// Use limit and skip for pagination
db.users.find().sort({created_at: -1}).limit(10).skip(20)

// Use aggregation pipeline for complex operations
db.orders.aggregate([
  {$match: {status: "completed"}},
  {$group: {_id: "$customer_id", total: {$sum: "$amount"}}},
  {$sort: {total: -1}},
  {$limit: 10}
])
```

## Interview Questions

### Basic Level

**Q1: What is NoSQL and how is it different from SQL databases?**
**A:** NoSQL databases are non-relational databases with flexible schemas, designed for horizontal scaling and handling unstructured data. Unlike SQL databases, they don't require fixed schemas and can scale across multiple servers easily.

**Q2: Explain the CAP theorem.**
**A:** CAP theorem states that in a distributed system, you can only guarantee two of three properties: Consistency (all nodes see same data), Availability (system remains operational), and Partition tolerance (works despite network failures).

**Q3: What are the main types of NoSQL databases?**
**A:** Document databases (MongoDB), Key-Value stores (Redis), Column-family (Cassandra), and Graph databases (Neo4j). Each is optimized for different data patterns and use cases.

### Intermediate Level

**Q4: When would you choose NoSQL over SQL databases?**
**A:** Choose NoSQL for: flexible/evolving schemas, horizontal scaling needs, big data applications, real-time web applications, and when eventual consistency is acceptable. Choose SQL for complex transactions, strong consistency requirements, and complex queries.

**Q5: Explain sharding and replication in NoSQL databases.**
**A:** Sharding distributes data across multiple servers for horizontal scaling. Replication creates copies of data for availability and fault tolerance. Both are essential for building scalable, reliable NoSQL systems.

**Q6: How do you model relationships in document databases?**
**A:** Use embedding for one-to-few relationships and when data is accessed together. Use referencing for one-to-many or many-to-many relationships and when data is accessed independently. Consider query patterns and data size.

### Advanced Level

**Q7: Compare different consistency models in NoSQL systems.**
**A:** Strong consistency ensures all reads get latest write but may impact availability. Eventual consistency allows temporary inconsistencies for better availability and performance. Weak consistency provides no guarantees but offers best performance.

**Q8: How would you design a NoSQL system for a social media application?**
**A:** Use document database for user profiles, key-value store for sessions/cache, column-family for activity feeds/timelines, and graph database for social connections. Implement sharding by user ID and use eventual consistency for non-critical data.

**Q9: What are the trade-offs of denormalization in NoSQL?**
**A:** Benefits include faster reads, fewer queries, and better performance. Drawbacks include data duplication, complex updates, potential inconsistencies, and increased storage requirements. Choose based on read vs write patterns.

---

[← Previous: Distributed Databases](../12-distributed-databases/README.md) | [Back to Index](../README.md) | [Next: Database Security →](../14-database-security/README.md)