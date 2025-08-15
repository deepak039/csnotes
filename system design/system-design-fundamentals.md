# System Design Fundamentals - Interview Ready Notes

## Table of Contents
1. [Basics](#basics)
2. [Load Balancing](#load-balancing)
3. [Consistency vs Availability](#consistency-vs-availability)
4. [Message Queues](#message-queues)
5. [Caching](#caching)
6. [Microservices](#microservices)
7. [Authentication Mechanisms](#authentication-mechanisms)
8. [System Design Tradeoffs](#system-design-tradeoffs)

---

## Basics

### What is System Design?

**System Design** is the process of defining the architecture, components, modules, interfaces, and data for a system to satisfy specified requirements. It involves:

- **Architecture Planning**: How components interact
- **Scalability**: Handling increased load
- **Reliability**: System uptime and fault tolerance
- **Performance**: Response time and throughput
- **Security**: Data protection and access control

**Key Principles:**
- Scalability
- Reliability
- Availability
- Consistency
- Partition Tolerance

---

### Horizontal vs Vertical Scaling

#### Vertical Scaling (Scale Up)
- **Definition**: Adding more power (CPU, RAM, Storage) to existing machines
- **Pros**: 
  - Simple to implement
  - No code changes needed
  - Better for applications requiring strong consistency
- **Cons**: 
  - Hardware limits
  - Single point of failure
  - Expensive at scale
- **Example**: Upgrading server from 8GB to 32GB RAM

#### Horizontal Scaling (Scale Out)
- **Definition**: Adding more machines to the resource pool
- **Pros**: 
  - No hardware limits
  - Better fault tolerance
  - Cost-effective
- **Cons**: 
  - Complex implementation
  - Data consistency challenges
  - Network overhead
- **Example**: Adding 5 more web servers behind a load balancer

**Interview Tip**: Always prefer horizontal scaling in system design interviews unless there's a specific reason for vertical scaling.

---

### Capacity Estimation

**Capacity Estimation** involves calculating system resources needed to handle expected load.

#### Key Metrics to Calculate:
1. **QPS (Queries Per Second)**
2. **Storage Requirements**
3. **Bandwidth Requirements**
4. **Memory Requirements**

#### Example: Twitter-like System
```
Assumptions:
- 300M monthly active users
- 50% users post daily
- Average 2 tweets per user per day
- Each tweet = 280 characters = 280 bytes

Calculations:
Daily Tweets = 300M × 0.5 × 2 = 300M tweets/day
QPS = 300M / (24 × 3600) = ~3,500 QPS
Peak QPS = 3,500 × 3 = ~10,500 QPS

Storage per day = 300M × 280 bytes = 84GB/day
Storage per year = 84GB × 365 = ~30TB/year
```

---

### What is HTTP?

**HTTP (HyperText Transfer Protocol)** is a stateless, application-layer protocol for distributed, collaborative, hypermedia information systems.

#### Key Characteristics:
- **Stateless**: Each request is independent
- **Request-Response**: Client sends request, server sends response
- **Text-based**: Human-readable format
- **Port 80** (HTTP) / **Port 443** (HTTPS)

#### HTTP Methods:
- **GET**: Retrieve data
- **POST**: Create new resource
- **PUT**: Update entire resource
- **PATCH**: Partial update
- **DELETE**: Remove resource

#### HTTP Status Codes:
- **2xx**: Success (200 OK, 201 Created)
- **3xx**: Redirection (301 Moved, 304 Not Modified)
- **4xx**: Client Error (400 Bad Request, 404 Not Found, 401 Unauthorized)
- **5xx**: Server Error (500 Internal Server Error, 503 Service Unavailable)

---

### Communication Patterns: WebSocket vs Short Polling vs Long Polling

#### Short Polling
```
Client → Server: "Any updates?"
Server → Client: "No" (immediate response)
[Wait 5 seconds]
Client → Server: "Any updates?"
Server → Client: "Yes, here's data"
```

**Pros**: Simple to implement
**Cons**: Inefficient, high server load, delayed updates
**Use Case**: Low-frequency updates

#### Long Polling
```
Client → Server: "Any updates?" (holds connection)
[Server waits until data available or timeout]
Server → Client: "Yes, here's data" (after 30 seconds)
Client → Server: "Any updates?" (immediately reconnects)
```

**Pros**: Near real-time, efficient than short polling
**Cons**: Complex error handling, connection management
**Use Case**: Chat applications, notifications

#### WebSocket
```
Client ↔ Server: Persistent bidirectional connection
Server → Client: Data (anytime)
Client → Server: Data (anytime)
```

**Pros**: True real-time, bidirectional, low overhead
**Cons**: Complex implementation, connection management
**Use Case**: Gaming, live trading, collaborative editing

**Interview Answer Framework:**
- **Short Polling**: Simple but inefficient
- **Long Polling**: Better than short polling, good for moderate real-time needs
- **WebSocket**: Best for real-time bidirectional communication

---

## Load Balancing

### What is Load Balancing?

**Load Balancing** distributes incoming network traffic across multiple servers to ensure no single server bears too much demand.

#### Types of Load Balancers:

1. **Layer 4 (Transport Layer)**
   - Routes based on IP and port
   - Faster, less CPU intensive
   - Cannot inspect application data

2. **Layer 7 (Application Layer)**
   - Routes based on content (HTTP headers, URLs)
   - More intelligent routing
   - Higher latency, more CPU intensive

#### Load Balancing Algorithms:

1. **Round Robin**: Requests distributed sequentially
2. **Weighted Round Robin**: Servers get requests based on capacity
3. **Least Connections**: Route to server with fewest active connections
4. **IP Hash**: Route based on client IP hash
5. **Geographic**: Route based on client location

#### Example Configuration:
```
Load Balancer
├── Server 1 (Weight: 3)
├── Server 2 (Weight: 2)
└── Server 3 (Weight: 1)

Out of 6 requests:
- Server 1 gets 3 requests
- Server 2 gets 2 requests  
- Server 3 gets 1 request
```

---

### Consistent Hashing

**Consistent Hashing** is a distributed hashing scheme that operates independently of the number of servers in a distributed hash table.

#### Problem it Solves:
Traditional hashing: `server = hash(key) % N`
- Adding/removing servers requires rehashing all keys
- Causes massive data movement

#### How Consistent Hashing Works:

1. **Hash Ring**: Servers and keys are placed on a circular hash ring
2. **Key Placement**: Each key is assigned to the first server clockwise from its position
3. **Server Addition**: Only keys between new server and previous server need to move
4. **Server Removal**: Only keys on removed server need to move

```
Hash Ring Example:
     Server A (100)
         |
Key1 ----+---- Server B (200)
         |
     Server C (300)

Key1 (hash=150) → goes to Server B (first server clockwise)
```

#### Virtual Nodes:
- Each physical server gets multiple positions on ring
- Better load distribution
- Reduces hotspots

**Interview Key Points:**
- Minimizes data movement when servers are added/removed
- Used in Amazon DynamoDB, Apache Cassandra
- Essential for distributed caching systems

---

### Sharding

**Sharding** is a database partitioning technique that splits large databases into smaller, more manageable pieces called shards.

#### Types of Sharding:

1. **Horizontal Sharding (Most Common)**
   - Split rows across multiple databases
   - Each shard has same schema, different data

2. **Vertical Sharding**
   - Split columns across multiple databases
   - Each shard has different schema

#### Sharding Strategies:

1. **Range-based Sharding**
   ```
   Shard 1: User IDs 1-1000
   Shard 2: User IDs 1001-2000
   Shard 3: User IDs 2001-3000
   ```
   **Pros**: Simple, range queries efficient
   **Cons**: Uneven distribution, hotspots

2. **Hash-based Sharding**
   ```
   shard = hash(user_id) % num_shards
   ```
   **Pros**: Even distribution
   **Cons**: Range queries difficult, resharding complex

3. **Directory-based Sharding**
   - Lookup service maintains shard mapping
   **Pros**: Flexible, easy to add shards
   **Cons**: Additional complexity, single point of failure

#### Challenges:
- **Cross-shard Queries**: Expensive joins across shards
- **Rebalancing**: Moving data when adding shards
- **Hotspots**: Uneven load distribution

---

## Consistency vs Availability

Based on **CAP Theorem**: In a distributed system, you can only guarantee 2 out of 3:
- **Consistency**: All nodes see the same data simultaneously
- **Availability**: System remains operational
- **Partition Tolerance**: System continues despite network failures

### Consistency Models:

#### Strong Consistency
- All reads receive the most recent write
- **Example**: Banking systems, inventory management
- **Implementation**: Synchronous replication, distributed locks

#### Eventual Consistency
- System will become consistent over time
- **Example**: DNS, social media feeds
- **Implementation**: Asynchronous replication

#### Weak Consistency
- No guarantees when all nodes will be consistent
- **Example**: Live video streaming, gaming

### Availability Patterns:

#### Active-Passive (Master-Slave)
```
Master (Active) ← Writes
   ↓ Replication
Slave (Passive) ← Reads
```
- Master handles writes, slave handles reads
- Failover to slave if master fails

#### Active-Active (Master-Master)
```
Master 1 ↔ Master 2
   ↓         ↓
Reads    Reads/Writes
```
- Both nodes handle reads and writes
- Conflict resolution needed

**Interview Framework:**
- **Choose Consistency**: Financial systems, inventory
- **Choose Availability**: Social media, content delivery
- **Real systems**: Usually eventual consistency with strong consistency where needed

---

## Message Queues

### What is a Message Queue?

**Message Queue** is a form of asynchronous service-to-service communication used in serverless and microservices architectures.

#### Key Benefits:
- **Decoupling**: Services don't need to know about each other
- **Scalability**: Handle traffic spikes
- **Reliability**: Messages persist until processed
- **Asynchronous Processing**: Non-blocking operations

#### Message Queue Components:
- **Producer**: Sends messages
- **Queue**: Stores messages
- **Consumer**: Processes messages
- **Broker**: Manages queues

---

### Publisher-Subscriber Model

**Pub-Sub** is a messaging pattern where publishers send messages to topics, and subscribers receive messages from topics they're interested in.

```
Publisher 1 ──┐
              ├── Topic A ──┬── Subscriber 1
Publisher 2 ──┘            ├── Subscriber 2
                           └── Subscriber 3

Publisher 3 ────── Topic B ──── Subscriber 4
```

#### Key Characteristics:
- **Loose Coupling**: Publishers don't know subscribers
- **Scalability**: Multiple subscribers per topic
- **Filtering**: Subscribers can filter messages

#### Examples:
- **Apache Kafka**: High-throughput, distributed
- **Amazon SNS**: Managed pub-sub service
- **Redis Pub/Sub**: Simple, fast

---

### Event-Driven Systems

**Event-Driven Architecture** is a software architecture pattern where services communicate through events.

#### Components:
1. **Event Producers**: Generate events
2. **Event Router**: Routes events to consumers
3. **Event Consumers**: Process events

#### Event Types:
- **Domain Events**: Business logic events (OrderPlaced, UserRegistered)
- **System Events**: Technical events (ServerStarted, DatabaseConnected)

#### Example Flow:
```
User places order → OrderService emits "OrderPlaced" event
                 → InventoryService reduces stock
                 → PaymentService processes payment
                 → NotificationService sends email
```

#### Benefits:
- **Loose Coupling**: Services independent
- **Scalability**: Easy to add new consumers
- **Resilience**: Failure in one service doesn't affect others

---

### Database as Message Queue

Using databases as message queues for simple use cases.

#### Implementation Pattern:
```sql
CREATE TABLE message_queue (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payload JSON,
    status ENUM('pending', 'processing', 'completed', 'failed'),
    created_at TIMESTAMP,
    processed_at TIMESTAMP
);
```

#### Processing Logic:
```sql
-- Consumer picks up message
UPDATE message_queue 
SET status = 'processing', processed_at = NOW() 
WHERE status = 'pending' 
ORDER BY created_at 
LIMIT 1;

-- After processing
UPDATE message_queue 
SET status = 'completed' 
WHERE id = ?;
```

#### Pros:
- Simple to implement
- ACID properties
- No additional infrastructure

#### Cons:
- Not designed for high throughput
- Polling overhead
- Limited scalability

**When to Use**: Low-volume, simple workflows

---

## Caching

### Distributed Caching

**Distributed Caching** stores frequently accessed data across multiple cache servers to improve application performance.

#### Cache Patterns:

1. **Cache-Aside (Lazy Loading)**
   ```
   if (data not in cache):
       data = fetch_from_database()
       cache.set(key, data)
   return data
   ```
   **Pros**: Only requested data cached
   **Cons**: Cache miss penalty

2. **Write-Through**
   ```
   cache.set(key, data)
   database.save(data)
   ```
   **Pros**: Data always consistent
   **Cons**: Write latency

3. **Write-Behind (Write-Back)**
   ```
   cache.set(key, data)
   // Asynchronously write to database later
   ```
   **Pros**: Fast writes
   **Cons**: Risk of data loss

4. **Refresh-Ahead**
   ```
   if (cache_expiry_soon):
       background_refresh_cache()
   ```
   **Pros**: Always fresh data
   **Cons**: Complex implementation

---

### Content Delivery Networks (CDN)

**CDN** is a geographically distributed network of servers that deliver web content to users based on their location.

#### How CDN Works:
```
User (New York) → CDN Edge Server (New York) → Origin Server (California)
                     ↓ Cache content
User (New York) → CDN Edge Server (New York) [Cache Hit - Fast Response]
```

#### CDN Benefits:
- **Reduced Latency**: Content served from nearby servers
- **Reduced Load**: Origin server handles fewer requests
- **High Availability**: Multiple edge servers
- **DDoS Protection**: Distributed infrastructure

#### CDN Types:
1. **Push CDN**: You upload content to CDN
2. **Pull CDN**: CDN fetches content on first request

---

### Cache Replacement Policies

When cache is full, which item to remove?

1. **LRU (Least Recently Used)**
   - Remove item not accessed for longest time
   - **Implementation**: Doubly linked list + HashMap
   - **Use Case**: General purpose

2. **LFU (Least Frequently Used)**
   - Remove item with lowest access count
   - **Implementation**: HashMap + Min-Heap
   - **Use Case**: When access patterns are predictable

3. **FIFO (First In, First Out)**
   - Remove oldest item
   - **Implementation**: Queue
   - **Use Case**: Simple, when all items equally likely

4. **Random**
   - Remove random item
   - **Implementation**: Simple
   - **Use Case**: When other policies too complex

**Interview Tip**: LRU is most commonly asked. Be ready to implement it.

---

## Microservices

### Microservices vs Monoliths

#### Monolithic Architecture
```
Single Application
├── User Management
├── Order Processing  
├── Payment Processing
├── Inventory Management
└── Notification Service
```

**Pros**:
- Simple deployment
- Easy testing
- Good performance (no network calls)
- Strong consistency

**Cons**:
- Single point of failure
- Difficult to scale individual components
- Technology lock-in
- Large team coordination issues

#### Microservices Architecture
```
API Gateway
├── User Service
├── Order Service
├── Payment Service
├── Inventory Service
└── Notification Service
```

**Pros**:
- Independent deployment
- Technology diversity
- Better fault isolation
- Team autonomy
- Scalability

**Cons**:
- Network complexity
- Data consistency challenges
- Operational overhead
- Testing complexity

---

### Monolith to Microservices Migration

#### Migration Strategies:

1. **Strangler Fig Pattern**
   ```
   Old Monolith ← Gradually replace functions
        ↓
   New Microservices
   ```
   - Gradually replace parts of monolith
   - Route traffic to new services
   - Retire old components

2. **Database Decomposition**
   ```
   Shared Database → Service-specific Databases
   ```
   - Extract service-specific data
   - Implement data synchronization
   - Handle distributed transactions

3. **API Gateway Pattern**
   ```
   Clients → API Gateway → Microservices
   ```
   - Single entry point
   - Request routing
   - Cross-cutting concerns (auth, logging)

#### Migration Steps:
1. **Identify Boundaries**: Domain-driven design
2. **Extract Services**: Start with least coupled components
3. **Data Migration**: Separate databases
4. **API Design**: RESTful APIs
5. **Monitoring**: Distributed tracing

---

## Authentication Mechanisms

### OAuth 2.0

**OAuth 2.0** is an authorization framework that enables applications to obtain limited access to user accounts.

#### OAuth Flow:
```
1. User → Client: "Login with Google"
2. Client → Auth Server: Redirect user
3. User → Auth Server: Login credentials
4. Auth Server → Client: Authorization code
5. Client → Auth Server: Exchange code for token
6. Auth Server → Client: Access token
7. Client → Resource Server: API call with token
```

#### OAuth Roles:
- **Resource Owner**: User
- **Client**: Your application
- **Authorization Server**: Google, Facebook, etc.
- **Resource Server**: API server

#### Grant Types:
1. **Authorization Code**: Most secure, for web apps
2. **Implicit**: For single-page apps (deprecated)
3. **Client Credentials**: For server-to-server
4. **Resource Owner Password**: For trusted apps

---

### Token-Based Authentication

#### JWT (JSON Web Token)
```
Header.Payload.Signature

eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

#### JWT Structure:
1. **Header**: Algorithm and token type
2. **Payload**: Claims (user data)
3. **Signature**: Verification signature

#### Token Types:
- **Access Token**: Short-lived (15 minutes)
- **Refresh Token**: Long-lived (days/weeks)

#### Token Flow:
```
1. User login → Server validates → Returns access + refresh token
2. Client stores tokens
3. API requests include access token
4. When access token expires → Use refresh token to get new access token
```

---

### Access Control Lists (ACL) and Rule Engines

#### Access Control List (ACL)
```json
{
  "user_id": "123",
  "permissions": [
    {
      "resource": "/api/users",
      "actions": ["read", "write"]
    },
    {
      "resource": "/api/orders", 
      "actions": ["read"]
    }
  ]
}
```

#### Role-Based Access Control (RBAC)
```json
{
  "user_id": "123",
  "roles": ["admin", "user"],
  "role_permissions": {
    "admin": ["read", "write", "delete"],
    "user": ["read"]
  }
}
```

#### Rule Engines
```javascript
// Example rule
if (user.role === 'admin' || 
   (user.role === 'manager' && resource.department === user.department)) {
    return 'ALLOW';
}
return 'DENY';
```

**Implementation Patterns**:
- **Attribute-Based Access Control (ABAC)**: Fine-grained permissions
- **Policy-Based Access Control**: Centralized policy management

---

## System Design Tradeoffs

### Pull vs Push

#### Pull Model (Polling)
```
Client → Server: "Any updates?"
Server → Client: Response
```
**Pros**: Simple, client controls frequency
**Cons**: Inefficient, delayed updates
**Use Case**: Email clients, RSS readers

#### Push Model
```
Server → Client: "Here's an update"
```
**Pros**: Real-time, efficient
**Cons**: Complex, connection management
**Use Case**: Chat apps, live notifications

---

### Memory vs Latency

#### High Memory, Low Latency
- Cache everything in memory
- **Example**: Redis, in-memory databases
- **Tradeoff**: Expensive, limited by RAM

#### Low Memory, Higher Latency  
- Fetch from disk/network as needed
- **Example**: Traditional databases
- **Tradeoff**: Slower, but cost-effective

**Decision Framework**: Cache frequently accessed data, store rest on disk

---

### Throughput vs Latency

#### High Throughput
- Process many requests per second
- **Techniques**: Batching, async processing
- **Example**: Data processing pipelines

#### Low Latency
- Fast response times
- **Techniques**: Caching, CDN, optimized algorithms
- **Example**: Trading systems, gaming

**Tradeoff**: Batching increases throughput but adds latency

---

### Consistency vs Availability

#### Strong Consistency
- All nodes have same data
- **Example**: Banking systems
- **Tradeoff**: May sacrifice availability

#### High Availability
- System always responds
- **Example**: Social media feeds
- **Tradeoff**: May have stale data

**Decision**: Choose based on business requirements

---

### Latency vs Accuracy

#### Low Latency
- Fast approximate results
- **Example**: Search suggestions, recommendations
- **Techniques**: Sampling, approximation algorithms

#### High Accuracy
- Precise but slower results
- **Example**: Financial calculations, scientific computing
- **Techniques**: Exact algorithms, verification

---

### SQL vs NoSQL

#### SQL Databases
**Pros**:
- ACID properties
- Complex queries
- Mature ecosystem
- Strong consistency

**Cons**:
- Vertical scaling limitations
- Schema rigidity
- Complex sharding

**Use Cases**: Financial systems, complex relationships

#### NoSQL Databases

1. **Document (MongoDB)**
   - Flexible schema
   - Good for content management

2. **Key-Value (Redis, DynamoDB)**
   - Simple, fast
   - Good for caching, sessions

3. **Column-Family (Cassandra)**
   - Wide columns
   - Good for time-series data

4. **Graph (Neo4j)**
   - Relationships
   - Good for social networks

**Decision Framework**:
- **Use SQL**: Complex queries, transactions, consistency
- **Use NoSQL**: Scale, flexibility, specific data models

---

## Interview Tips

### System Design Interview Process:
1. **Clarify Requirements** (5 minutes)
2. **Estimate Scale** (5 minutes)  
3. **High-Level Design** (10 minutes)
4. **Detailed Design** (15 minutes)
5. **Scale the Design** (10 minutes)
6. **Address Bottlenecks** (5 minutes)

### Key Points to Remember:
- Always start with simple design, then scale
- Discuss tradeoffs explicitly
- Consider failure scenarios
- Estimate numbers (back-of-envelope calculations)
- Draw diagrams
- Ask clarifying questions

### Common Mistakes to Avoid:
- Jumping to complex solutions immediately
- Not considering scale
- Ignoring failure scenarios
- Not discussing tradeoffs
- Over-engineering

This comprehensive guide covers all the fundamental concepts you need for system design interviews. Practice drawing these concepts and explaining tradeoffs clearly.