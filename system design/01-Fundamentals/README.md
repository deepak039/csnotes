# System Design Fundamentals

## 🎯 Core Principles

### 1. Scalability
**Definition**: System's ability to handle increased load by adding resources

#### Horizontal Scaling (Scale Out)
- Add more servers/instances
- **Pros**: Better fault tolerance, unlimited scaling potential
- **Cons**: Complex data consistency, network overhead
- **Examples**: Web servers, microservices

#### Vertical Scaling (Scale Up)
- Add more power (CPU, RAM) to existing server
- **Pros**: Simple, no code changes needed
- **Cons**: Hardware limits, single point of failure
- **Examples**: Database servers, monolithic applications

**Key Metrics**:
- Requests per second (RPS)
- Concurrent users
- Data volume growth

### 2. Reliability
**Definition**: System continues to work correctly even when failures occur

#### Fault Tolerance Strategies
- **Redundancy**: Multiple instances of critical components
- **Replication**: Data copied across multiple locations
- **Graceful Degradation**: Reduced functionality instead of complete failure

#### Failure Types
- **Hardware Failures**: Server crashes, disk failures
- **Software Failures**: Bugs, memory leaks, cascading failures
- **Human Errors**: Configuration mistakes, deployment errors

**Reliability Metrics**:
- Mean Time Between Failures (MTBF)
- Mean Time To Recovery (MTTR)
- Error rates

### 3. Availability
**Definition**: System remains operational over time

#### Availability Levels
- **99%** (8.77 hours downtime/year) - Basic
- **99.9%** (52.6 minutes downtime/year) - Standard
- **99.99%** (5.26 minutes downtime/year) - High
- **99.999%** (31.5 seconds downtime/year) - Ultra-high

#### Achieving High Availability
- **Load Balancers**: Distribute traffic, health checks
- **Auto-scaling**: Automatic resource adjustment
- **Circuit Breakers**: Prevent cascade failures
- **Health Monitoring**: Proactive issue detection

### 4. Consistency
**Definition**: All nodes see the same data simultaneously

#### ACID Properties (Databases)
- **Atomicity**: All or nothing transactions
- **Consistency**: Data integrity maintained
- **Isolation**: Concurrent transactions don't interfere
- **Durability**: Committed data survives system failures

#### CAP Theorem
*You can only guarantee 2 out of 3*:
- **Consistency**: All nodes have same data
- **Availability**: System remains operational
- **Partition Tolerance**: System continues despite network failures

**Trade-offs**:
- **CP Systems**: Consistent + Partition Tolerant (MongoDB, Redis)
- **AP Systems**: Available + Partition Tolerant (Cassandra, DynamoDB)
- **CA Systems**: Consistent + Available (Traditional RDBMS)

### 5. Performance
**Definition**: How fast and efficiently system responds

#### Key Metrics
- **Latency**: Time to process single request
- **Throughput**: Requests processed per unit time
- **Response Time**: End-to-end request completion time

#### Performance Optimization
- **Caching**: Store frequently accessed data
- **CDN**: Geographically distributed content
- **Database Indexing**: Faster data retrieval
- **Connection Pooling**: Reuse database connections

---

## 🔍 Deep Dive Questions

### Scalability Questions
1. How would you scale a read-heavy vs write-heavy system?
2. What are the challenges of horizontal scaling?
3. When would you choose vertical over horizontal scaling?

### Reliability Questions
1. How do you design for failure in distributed systems?
2. What's the difference between fault tolerance and fault avoidance?
3. How do you handle cascading failures?

### Availability Questions
1. How do you achieve 99.99% availability?
2. What's the relationship between availability and consistency?
3. How do you measure and monitor availability?

### Consistency Questions
1. Explain eventual consistency with examples
2. When would you choose consistency over availability?
3. How does sharding affect consistency?

### Performance Questions
1. How do you identify performance bottlenecks?
2. What's the difference between latency and throughput?
3. How do you optimize for both read and write performance?

---

## 📊 Real-World Examples

### Netflix (Availability + Performance)
- **Challenge**: Stream to 200M+ users globally
- **Solution**: Microservices, CDN, chaos engineering
- **Trade-off**: Eventual consistency for high availability

### Amazon (Consistency vs Availability)
- **Challenge**: Shopping cart must always be available
- **Solution**: Eventually consistent, always writable
- **Trade-off**: Temporary inconsistency for availability

### Google Search (Performance)
- **Challenge**: Sub-second response for billions of queries
- **Solution**: Distributed indexing, caching, parallel processing
- **Trade-off**: Complex infrastructure for speed

---

## 🎯 Key Takeaways

1. **No Perfect System**: Every design involves trade-offs
2. **Context Matters**: Requirements drive architectural decisions
3. **Measure Everything**: Use metrics to guide decisions
4. **Plan for Failure**: Assume components will fail
5. **Start Simple**: Add complexity only when needed

---

*Next: [Core Concepts](../02-Core-Concepts/) →*