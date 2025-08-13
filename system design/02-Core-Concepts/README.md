# Core System Design Concepts

## 🔄 Load Balancing

### Definition
Distributes incoming requests across multiple servers to ensure no single server becomes overwhelmed.

### Load Balancing Algorithms

#### 1. Round Robin
- **How**: Requests distributed sequentially
- **Pros**: Simple, equal distribution
- **Cons**: Doesn't consider server capacity
- **Use Case**: Homogeneous servers with similar workloads

#### 2. Weighted Round Robin
- **How**: Servers assigned weights based on capacity
- **Pros**: Accounts for server differences
- **Cons**: Static weights may not reflect real-time load
- **Use Case**: Heterogeneous server configurations

#### 3. Least Connections
- **How**: Routes to server with fewest active connections
- **Pros**: Better for long-lived connections
- **Cons**: Overhead of tracking connections
- **Use Case**: Database connections, persistent sessions

#### 4. IP Hash
- **How**: Hash client IP to determine server
- **Pros**: Session affinity, consistent routing
- **Cons**: Uneven distribution with limited IPs
- **Use Case**: Stateful applications requiring session persistence

### Load Balancer Types

#### Layer 4 (Transport Layer)
- **Function**: Routes based on IP and port
- **Pros**: Fast, protocol agnostic
- **Cons**: Limited routing intelligence
- **Examples**: AWS NLB, HAProxy

#### Layer 7 (Application Layer)
- **Function**: Routes based on content (HTTP headers, URLs)
- **Pros**: Intelligent routing, SSL termination
- **Cons**: Higher latency, more resource intensive
- **Examples**: AWS ALB, Nginx, Cloudflare

---

## 💾 Caching

### Definition
Storing frequently accessed data in fast storage to reduce latency and database load.

### Cache Levels

#### 1. Browser Cache
- **Location**: Client-side
- **Scope**: Single user
- **TTL**: Hours to days
- **Use Case**: Static assets (CSS, JS, images)

#### 2. CDN Cache
- **Location**: Edge servers globally
- **Scope**: Geographic region
- **TTL**: Minutes to hours
- **Use Case**: Static content, media files

#### 3. Application Cache
- **Location**: Application server memory
- **Scope**: Single application instance
- **TTL**: Minutes to hours
- **Use Case**: Computed results, session data

#### 4. Database Cache
- **Location**: Database server memory
- **Scope**: Database instance
- **TTL**: Automatic (LRU/LFU)
- **Use Case**: Query results, frequently accessed rows

### Cache Patterns

#### 1. Cache-Aside (Lazy Loading)
```
1. Check cache for data
2. If miss, fetch from database
3. Store in cache for future requests
```
- **Pros**: Only cache requested data
- **Cons**: Cache miss penalty, potential stale data

#### 2. Write-Through
```
1. Write to cache and database simultaneously
2. Cache always consistent with database
```
- **Pros**: Data consistency, cache always warm
- **Cons**: Write latency, unnecessary cache writes

#### 3. Write-Behind (Write-Back)
```
1. Write to cache immediately
2. Write to database asynchronously
```
- **Pros**: Low write latency, high throughput
- **Cons**: Risk of data loss, complexity

#### 4. Refresh-Ahead
```
1. Proactively refresh cache before expiration
2. Based on access patterns
```
- **Pros**: Reduced cache misses, consistent performance
- **Cons**: Additional complexity, resource overhead

### Cache Eviction Policies
- **LRU** (Least Recently Used): Remove oldest accessed items
- **LFU** (Least Frequently Used): Remove least accessed items
- **FIFO** (First In, First Out): Remove oldest items
- **TTL** (Time To Live): Remove expired items

---

## 🗄️ Databases

### SQL vs NoSQL

#### SQL Databases (RDBMS)
**Characteristics**:
- ACID compliance
- Structured schema
- Complex queries with JOINs
- Vertical scaling

**Use Cases**:
- Financial systems
- E-commerce transactions
- Complex reporting
- Strong consistency requirements

**Examples**: PostgreSQL, MySQL, Oracle

#### NoSQL Databases

##### Document Stores
- **Structure**: JSON-like documents
- **Use Case**: Content management, catalogs
- **Examples**: MongoDB, CouchDB
- **Pros**: Flexible schema, natural object mapping
- **Cons**: Limited query capabilities

##### Key-Value Stores
- **Structure**: Simple key-value pairs
- **Use Case**: Caching, session storage
- **Examples**: Redis, DynamoDB
- **Pros**: High performance, simple operations
- **Cons**: Limited query flexibility

##### Column-Family
- **Structure**: Column-oriented storage
- **Use Case**: Analytics, time-series data
- **Examples**: Cassandra, HBase
- **Pros**: Efficient for wide tables, compression
- **Cons**: Complex data modeling

##### Graph Databases
- **Structure**: Nodes and relationships
- **Use Case**: Social networks, recommendations
- **Examples**: Neo4j, Amazon Neptune
- **Pros**: Natural relationship queries
- **Cons**: Limited scalability for non-graph queries

### Database Scaling Strategies

#### 1. Read Replicas
- **How**: Copy data to multiple read-only instances
- **Pros**: Improved read performance, high availability
- **Cons**: Eventual consistency, replication lag
- **Use Case**: Read-heavy applications

#### 2. Sharding (Horizontal Partitioning)
- **How**: Split data across multiple databases
- **Pros**: Linear scalability, fault isolation
- **Cons**: Complex queries, rebalancing challenges
- **Strategies**:
  - **Range-based**: Partition by value ranges
  - **Hash-based**: Partition by hash function
  - **Directory-based**: Lookup service for shard location

#### 3. Federation (Vertical Partitioning)
- **How**: Split databases by feature/service
- **Pros**: Reduced read/write traffic, smaller databases
- **Cons**: Complex cross-database queries
- **Use Case**: Microservices architecture

---

## 📨 Message Queues

### Definition
Asynchronous communication mechanism between services using queues to store messages.

### Queue Types

#### 1. Point-to-Point
- **Pattern**: One producer, one consumer
- **Delivery**: Each message consumed once
- **Use Case**: Task processing, order processing

#### 2. Publish-Subscribe
- **Pattern**: One producer, multiple consumers
- **Delivery**: Each message delivered to all subscribers
- **Use Case**: Event notifications, real-time updates

### Message Queue Benefits
- **Decoupling**: Services don't need direct connections
- **Reliability**: Messages persist until processed
- **Scalability**: Handle traffic spikes with buffering
- **Fault Tolerance**: Retry failed message processing

### Popular Message Queues
- **RabbitMQ**: Feature-rich, supports multiple protocols
- **Apache Kafka**: High-throughput, distributed streaming
- **Amazon SQS**: Managed service, highly scalable
- **Redis Pub/Sub**: In-memory, low latency

---

## 🔍 Deep Dive Questions

### Load Balancing
1. How do you handle server failures in load balancing?
2. What's the difference between sticky sessions and stateless design?
3. How do you implement health checks for load balancers?

### Caching
1. How do you handle cache invalidation in distributed systems?
2. What are the trade-offs between different cache patterns?
3. How do you prevent cache stampede problems?

### Databases
1. When would you choose NoSQL over SQL?
2. How do you handle transactions in a sharded database?
3. What are the challenges of database migration?

### Message Queues
1. How do you ensure message ordering in distributed queues?
2. What's the difference between at-least-once and exactly-once delivery?
3. How do you handle poison messages?

---

## 🏗️ Implementation Examples

### Load Balancer Configuration (Nginx)
```nginx
upstream backend {
    least_conn;
    server backend1.example.com weight=3;
    server backend2.example.com weight=2;
    server backend3.example.com backup;
}

server {
    location / {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### Cache Implementation (Redis)
```python
import redis
import json

cache = redis.Redis(host='localhost', port=6379, db=0)

def get_user(user_id):
    # Try cache first
    cached_user = cache.get(f"user:{user_id}")
    if cached_user:
        return json.loads(cached_user)
    
    # Cache miss - fetch from database
    user = database.get_user(user_id)
    
    # Store in cache with TTL
    cache.setex(f"user:{user_id}", 3600, json.dumps(user))
    return user
```

---

*Next: [System Components](../03-System-Components/) →*