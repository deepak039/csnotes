# System Design Quick Reference

## 🚀 Capacity Estimation Cheat Sheet

### Common Numbers
```
1 KB = 1,000 bytes
1 MB = 1,000 KB
1 GB = 1,000 MB
1 TB = 1,000 GB

1 day = 86,400 seconds
1 month = 2.6M seconds
1 year = 31.5M seconds

Internet users: ~5B
Smartphone users: ~6B
```

### Latency Numbers
```
L1 cache: 0.5 ns
L2 cache: 7 ns
RAM: 100 ns
SSD: 150 μs
Network (same DC): 500 μs
Disk seek: 10 ms
Network (cross-continent): 150 ms
```

### Throughput Estimates
```
Single server: 1K-10K RPS
Load balancer: 100K+ RPS
Database (SQL): 1K-10K QPS
Database (NoSQL): 10K-100K QPS
Cache (Redis): 100K+ QPS
```

## 🏗️ Architecture Patterns Quick Guide

### Scaling Patterns
| Pattern | Use Case | Pros | Cons |
|---------|----------|------|------|
| Vertical Scaling | Simple apps, single server | Easy, no code changes | Limited, expensive |
| Horizontal Scaling | High traffic, distributed | Unlimited scale | Complex, consistency issues |
| Load Balancing | Multiple servers | High availability | Single point of failure |
| Caching | Read-heavy workloads | Fast reads | Cache invalidation |
| CDN | Global users, static content | Low latency | Cost, complexity |

### Database Patterns
| Pattern | Use Case | Pros | Cons |
|---------|----------|------|------|
| Read Replicas | Read-heavy | Better read performance | Eventual consistency |
| Sharding | Large datasets | Linear scaling | Complex queries |
| Federation | Microservices | Service isolation | Cross-service queries |
| Denormalization | Performance critical | Fast reads | Data redundancy |

### Reliability Patterns
| Pattern | Use Case | Pros | Cons |
|---------|----------|------|------|
| Circuit Breaker | External services | Prevent cascading failures | Added complexity |
| Bulkhead | Mixed workloads | Resource isolation | Resource overhead |
| Retry + Backoff | Transient failures | Improved reliability | Potential amplification |
| Health Checks | Service monitoring | Early failure detection | Monitoring overhead |

## 📊 CAP Theorem Quick Reference

### CP Systems (Consistency + Partition Tolerance)
- **Examples**: MongoDB, Redis, HBase
- **Use Cases**: Financial systems, inventory management
- **Trade-off**: May become unavailable during network partitions

### AP Systems (Availability + Partition Tolerance)
- **Examples**: Cassandra, DynamoDB, CouchDB
- **Use Cases**: Social media, content delivery, analytics
- **Trade-off**: May serve stale data during partitions

### CA Systems (Consistency + Availability)
- **Examples**: Traditional RDBMS (single node)
- **Use Cases**: Single-node applications
- **Trade-off**: Cannot handle network partitions

## 🔍 System Design Interview Checklist

### Requirements (10 minutes)
- [ ] Functional requirements
- [ ] Non-functional requirements (scale, performance, availability)
- [ ] Constraints and assumptions
- [ ] Success metrics

### Estimation (10 minutes)
- [ ] Daily/Monthly active users
- [ ] Read/Write ratio
- [ ] Storage requirements
- [ ] Bandwidth requirements
- [ ] Peak load calculations

### High-Level Design (15 minutes)
- [ ] Major components
- [ ] Data flow
- [ ] API design
- [ ] Database schema

### Detailed Design (15 minutes)
- [ ] Core algorithms
- [ ] Data structures
- [ ] Component interactions
- [ ] Error handling

### Scale & Optimize (10 minutes)
- [ ] Identify bottlenecks
- [ ] Scaling strategies
- [ ] Monitoring & metrics
- [ ] Trade-offs discussion

## 🎯 Common System Design Problems

### Beginner Level
1. **URL Shortener** - Encoding, caching, analytics
2. **Pastebin** - Text storage, expiration
3. **Key-Value Store** - Hashing, replication

### Intermediate Level
1. **Chat System** - Real-time messaging, WebSockets
2. **Social Media Feed** - Timeline generation, fan-out
3. **Web Crawler** - Distributed crawling, politeness

### Advanced Level
1. **Video Streaming** - Encoding, CDN, adaptive streaming
2. **Ride Sharing** - Geospatial indexing, matching
3. **Search Engine** - Indexing, ranking, distributed search

## 🛠️ Technology Stack Quick Reference

### Load Balancers
- **Layer 4**: AWS NLB, HAProxy
- **Layer 7**: AWS ALB, Nginx, Cloudflare

### Databases
- **SQL**: PostgreSQL, MySQL, Oracle
- **NoSQL Document**: MongoDB, CouchDB
- **NoSQL Key-Value**: Redis, DynamoDB
- **NoSQL Column**: Cassandra, HBase
- **NoSQL Graph**: Neo4j, Amazon Neptune

### Caching
- **In-Memory**: Redis, Memcached
- **CDN**: CloudFlare, AWS CloudFront
- **Application**: Caffeine, Guava Cache

### Message Queues
- **Traditional**: RabbitMQ, ActiveMQ
- **Streaming**: Apache Kafka, AWS Kinesis
- **Cloud**: AWS SQS, Google Pub/Sub

### Monitoring
- **Metrics**: Prometheus, DataDog, New Relic
- **Logging**: ELK Stack, Splunk, Fluentd
- **Tracing**: Jaeger, Zipkin, AWS X-Ray

## 📈 Scaling Milestones

### 1K Users
- Single server
- Simple database
- Basic monitoring

### 10K Users
- Load balancer
- Database read replicas
- CDN for static content

### 100K Users
- Microservices
- Caching layer
- Message queues

### 1M Users
- Database sharding
- Multiple data centers
- Advanced monitoring

### 10M+ Users
- Global CDN
- Event-driven architecture
- Machine learning for optimization

## 🔧 Performance Optimization Checklist

### Database
- [ ] Proper indexing
- [ ] Query optimization
- [ ] Connection pooling
- [ ] Read replicas
- [ ] Partitioning/Sharding

### Caching
- [ ] Application-level caching
- [ ] Database query caching
- [ ] CDN for static content
- [ ] Browser caching headers

### Network
- [ ] Compression (gzip)
- [ ] HTTP/2 or HTTP/3
- [ ] Connection keep-alive
- [ ] Minimize round trips

### Application
- [ ] Async processing
- [ ] Connection pooling
- [ ] Batch operations
- [ ] Lazy loading

## 🚨 Common Pitfalls to Avoid

### Design Phase
- ❌ Not asking clarifying questions
- ❌ Jumping to implementation details
- ❌ Ignoring non-functional requirements
- ❌ Over-engineering from the start

### Scaling Phase
- ❌ Premature optimization
- ❌ Not considering data consistency
- ❌ Ignoring single points of failure
- ❌ Not planning for failure scenarios

### Implementation Phase
- ❌ Not monitoring key metrics
- ❌ Ignoring security considerations
- ❌ Not documenting trade-offs
- ❌ Skipping load testing

## 📚 Essential Formulas

### Availability Calculation
```
Availability = MTBF / (MTBF + MTTR)
Where:
- MTBF = Mean Time Between Failures
- MTTR = Mean Time To Recovery
```

### Throughput vs Latency
```
Throughput = Number of requests / Time period
Latency = Time to process single request

Little's Law: L = λ × W
Where:
- L = Average number of requests in system
- λ = Average arrival rate
- W = Average time spent in system
```

### Storage Calculation
```
Storage = Records × Record_Size × Replication_Factor
Bandwidth = RPS × Average_Response_Size
```

### Cache Hit Ratio
```
Hit_Ratio = Cache_Hits / (Cache_Hits + Cache_Misses)
Effective_Latency = Hit_Ratio × Cache_Latency + (1 - Hit_Ratio) × DB_Latency
```

---

*This quick reference guide provides essential information for system design interviews and real-world architecture decisions. Keep it handy for quick lookups during problem-solving sessions.*