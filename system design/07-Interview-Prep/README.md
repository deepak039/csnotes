# System Design Interview Preparation

## 🎯 Interview Framework

### The SCALE Method

#### **S** - Scope & Requirements
- **Functional Requirements**: What the system should do
- **Non-Functional Requirements**: Scale, performance, availability
- **Constraints**: Budget, timeline, technology limitations

#### **C** - Capacity Estimation
- **Users**: Daily/Monthly active users
- **Traffic**: Requests per second, peak loads
- **Storage**: Data size, growth rate
- **Bandwidth**: Network requirements

#### **A** - Architecture Design
- **High-Level**: Major components and their interactions
- **API Design**: Key endpoints and data flow
- **Database Schema**: Tables, relationships, indexes

#### **L** - Low-Level Design
- **Algorithms**: Core business logic
- **Data Structures**: Efficient data organization
- **Detailed Components**: Internal workings

#### **E** - Evaluate & Scale
- **Bottlenecks**: Identify performance issues
- **Scaling Solutions**: Horizontal/vertical scaling
- **Trade-offs**: Discuss alternatives and decisions

---

## 📋 Common Interview Questions

### Beginner Level (0-2 years)

#### 1. Design a URL Shortener
**Time**: 45 minutes
**Key Concepts**: Encoding, Caching, Database design
**Follow-up Questions**:
- How do you handle custom aliases?
- How do you prevent abuse?
- How do you implement analytics?

#### 2. Design a Pastebin
**Time**: 45 minutes
**Key Concepts**: Text storage, Expiration, Access control
**Follow-up Questions**:
- How do you handle large pastes?
- How do you implement syntax highlighting?
- How do you prevent spam?

#### 3. Design a Key-Value Store
**Time**: 45 minutes
**Key Concepts**: Hashing, Replication, Consistency
**Follow-up Questions**:
- How do you handle node failures?
- How do you implement consistent hashing?
- How do you ensure data durability?

### Intermediate Level (2-5 years)

#### 1. Design a Chat System
**Time**: 45-60 minutes
**Key Concepts**: WebSockets, Message queues, Real-time communication
**Follow-up Questions**:
- How do you handle group chats?
- How do you implement message history?
- How do you handle offline users?

#### 2. Design a Social Media Feed
**Time**: 45-60 minutes
**Key Concepts**: Timeline generation, Fan-out, Content ranking
**Follow-up Questions**:
- How do you handle celebrity users?
- How do you implement trending topics?
- How do you personalize feeds?

#### 3. Design a Web Crawler
**Time**: 45-60 minutes
**Key Concepts**: Distributed crawling, Politeness, Deduplication
**Follow-up Questions**:
- How do you handle dynamic content?
- How do you respect robots.txt?
- How do you detect duplicate content?

### Advanced Level (5+ years)

#### 1. Design YouTube/Netflix
**Time**: 60 minutes
**Key Concepts**: Video encoding, CDN, Recommendation systems
**Follow-up Questions**:
- How do you handle live streaming?
- How do you implement adaptive bitrate?
- How do you scale globally?

#### 2. Design Uber/Lyft
**Time**: 60 minutes
**Key Concepts**: Geospatial indexing, Real-time matching, Route optimization
**Follow-up Questions**:
- How do you handle surge pricing?
- How do you optimize driver allocation?
- How do you handle GPS inaccuracies?

#### 3. Design a Distributed Cache
**Time**: 60 minutes
**Key Concepts**: Consistent hashing, Replication, Eviction policies
**Follow-up Questions**:
- How do you handle hot keys?
- How do you implement cache warming?
- How do you ensure cache coherence?

---

## 🔍 Estimation Techniques

### Back-of-Envelope Calculations

#### Common Numbers to Remember
```
1 KB = 1,000 bytes
1 MB = 1,000 KB = 1,000,000 bytes
1 GB = 1,000 MB = 1,000,000,000 bytes
1 TB = 1,000 GB = 1,000,000,000,000 bytes

1 second = 1,000 milliseconds
1 day = 86,400 seconds
1 month = 30 days = 2,592,000 seconds
1 year = 365 days = 31,536,000 seconds

Internet users worldwide: ~5 billion
Smartphone users: ~6 billion
```

#### Latency Numbers
```
L1 cache reference: 0.5 ns
Branch mispredict: 5 ns
L2 cache reference: 7 ns
Mutex lock/unlock: 25 ns
Main memory reference: 100 ns
Compress 1KB with Zippy: 10,000 ns = 10 μs
Send 1KB over 1 Gbps network: 10,000 ns = 10 μs
Read 4KB randomly from SSD: 150,000 ns = 150 μs
Read 1MB sequentially from memory: 250,000 ns = 250 μs
Round trip within same datacenter: 500,000 ns = 500 μs
Read 1MB sequentially from SSD: 1,000,000 ns = 1 ms
Disk seek: 10,000,000 ns = 10 ms
Read 1MB sequentially from disk: 30,000,000 ns = 30 ms
Send packet CA→Netherlands→CA: 150,000,000 ns = 150 ms
```

### Estimation Examples

#### Twitter-like System
```
Assumptions:
- 300M monthly active users
- 50% daily active users = 150M DAU
- Average user posts 2 tweets/day
- Average user reads 100 tweets/day

Write Traffic:
- 150M users × 2 tweets/day = 300M tweets/day
- 300M tweets/day ÷ 86,400 seconds = 3,500 tweets/second
- Peak: 3,500 × 3 = 10,500 tweets/second

Read Traffic:
- 150M users × 100 reads/day = 15B reads/day
- 15B reads/day ÷ 86,400 seconds = 173,000 reads/second
- Peak: 173,000 × 3 = 520,000 reads/second

Storage:
- Average tweet size: 280 characters = 280 bytes
- Metadata: 200 bytes
- Total per tweet: 480 bytes
- Daily storage: 300M × 480 bytes = 144 GB/day
- 5 years: 144 GB × 365 × 5 = 262 TB
```

#### Video Streaming Service
```
Assumptions:
- 1B users worldwide
- 20% daily active users = 200M DAU
- Average user watches 1 hour/day
- Average video bitrate: 1 Mbps

Bandwidth:
- 200M users × 1 hour × 1 Mbps = 200M Mbps
- Peak usage (8 PM): 50% of daily usage in 4 hours
- Peak bandwidth: 200M × 0.5 ÷ 4 = 25M Mbps = 25 Tbps

Storage:
- New content: 10,000 hours/day
- Multiple qualities: 5 versions per video
- Average size per hour: 1 GB
- Daily storage: 10,000 × 5 × 1 GB = 50 TB/day
- Annual storage: 50 TB × 365 = 18.25 PB/year
```

---

## 🎯 Trade-offs Analysis Framework

### Consistency vs Availability vs Partition Tolerance (CAP)

#### When to Choose Consistency (CP)
- **Use Cases**: Banking, financial transactions, inventory management
- **Examples**: Traditional RDBMS, MongoDB, Redis
- **Trade-off**: May become unavailable during network partitions

#### When to Choose Availability (AP)
- **Use Cases**: Social media, content delivery, analytics
- **Examples**: Cassandra, DynamoDB, CouchDB
- **Trade-off**: May serve stale data during network partitions

### Performance vs Cost

#### High Performance (Expensive)
- **Strategies**: In-memory caching, SSD storage, premium instances
- **Use Cases**: Real-time trading, gaming, live streaming
- **Cost**: 2-10x more expensive

#### Cost-Optimized (Lower Performance)
- **Strategies**: HDD storage, smaller instances, longer cache TTL
- **Use Cases**: Batch processing, archival, development environments
- **Performance**: 2-5x slower response times

### Scalability vs Complexity

#### Simple Architecture
- **Characteristics**: Monolithic, single database, minimal components
- **Pros**: Easy to develop, deploy, and debug
- **Cons**: Limited scalability, single points of failure
- **Suitable for**: Small teams, early-stage products

#### Complex Architecture
- **Characteristics**: Microservices, distributed databases, multiple components
- **Pros**: High scalability, fault tolerance, team independence
- **Cons**: Complex deployment, debugging, and monitoring
- **Suitable for**: Large teams, mature products

---

## 📊 System Design Patterns

### Scalability Patterns

#### 1. Database Scaling
```python
# Read Replicas Pattern
class DatabaseRouter:
    def __init__(self):
        self.master = MasterDB()
        self.replicas = [ReplicaDB1(), ReplicaDB2(), ReplicaDB3()]
    
    def write(self, query):
        return self.master.execute(query)
    
    def read(self, query):
        replica = random.choice(self.replicas)
        return replica.execute(query)

# Sharding Pattern
class ShardedDatabase:
    def __init__(self, shards):
        self.shards = shards
    
    def get_shard(self, key):
        shard_id = hash(key) % len(self.shards)
        return self.shards[shard_id]
    
    def get(self, key):
        shard = self.get_shard(key)
        return shard.get(key)
```

#### 2. Caching Patterns
```python
# Cache-Aside Pattern
def get_user(user_id):
    # Try cache first
    user = cache.get(f"user:{user_id}")
    if user:
        return user
    
    # Cache miss - get from database
    user = database.get_user(user_id)
    cache.set(f"user:{user_id}", user, ttl=3600)
    return user

# Write-Through Pattern
def update_user(user_id, data):
    # Update database
    database.update_user(user_id, data)
    
    # Update cache
    cache.set(f"user:{user_id}", data, ttl=3600)
```

### Reliability Patterns

#### 1. Circuit Breaker
```python
class CircuitBreaker:
    def __init__(self, failure_threshold=5, timeout=60):
        self.failure_threshold = failure_threshold
        self.timeout = timeout
        self.failure_count = 0
        self.last_failure_time = None
        self.state = 'CLOSED'  # CLOSED, OPEN, HALF_OPEN
    
    def call(self, func, *args, **kwargs):
        if self.state == 'OPEN':
            if time.time() - self.last_failure_time > self.timeout:
                self.state = 'HALF_OPEN'
            else:
                raise CircuitBreakerOpenException()
        
        try:
            result = func(*args, **kwargs)
            self.on_success()
            return result
        except Exception as e:
            self.on_failure()
            raise e
    
    def on_success(self):
        self.failure_count = 0
        self.state = 'CLOSED'
    
    def on_failure(self):
        self.failure_count += 1
        self.last_failure_time = time.time()
        
        if self.failure_count >= self.failure_threshold:
            self.state = 'OPEN'
```

#### 2. Retry with Exponential Backoff
```python
import time
import random

def retry_with_backoff(func, max_retries=3, base_delay=1):
    for attempt in range(max_retries + 1):
        try:
            return func()
        except Exception as e:
            if attempt == max_retries:
                raise e
            
            # Exponential backoff with jitter
            delay = base_delay * (2 ** attempt) + random.uniform(0, 1)
            time.sleep(delay)
```

---

## 🎯 Interview Tips & Best Practices

### Before the Interview

#### Preparation Checklist
- [ ] Review fundamental concepts (CAP theorem, ACID, BASE)
- [ ] Practice capacity estimation calculations
- [ ] Study common system design patterns
- [ ] Review real-world architectures (Netflix, Uber, etc.)
- [ ] Practice drawing diagrams quickly
- [ ] Prepare questions about requirements

#### Common Mistakes to Avoid
1. **Jumping into solution without understanding requirements**
2. **Not asking clarifying questions**
3. **Designing for unlimited scale from the beginning**
4. **Ignoring non-functional requirements**
5. **Not discussing trade-offs**
6. **Over-engineering the solution**

### During the Interview

#### Time Management (45-60 minutes)
- **Requirements & Estimation** (10-15 minutes)
- **High-Level Design** (15-20 minutes)
- **Detailed Design** (15-20 minutes)
- **Scale & Optimize** (5-10 minutes)

#### Communication Tips
- **Think out loud**: Explain your reasoning
- **Ask questions**: Clarify ambiguous requirements
- **Start simple**: Begin with basic design, then add complexity
- **Discuss trade-offs**: Explain why you chose one approach over another
- **Be honest**: Admit when you don't know something

#### Drawing Diagrams
```
Tips for effective diagrams:
- Use boxes for services/components
- Use arrows for data flow
- Label connections clearly
- Keep it simple and readable
- Use consistent symbols
- Add legends if needed
```

### Sample Interview Flow

#### URL Shortener Example
```
1. Requirements (5 minutes)
   - Functional: Shorten URLs, redirect, analytics
   - Non-functional: 100M URLs/day, 10B redirects/day, 99.9% availability

2. Estimation (5 minutes)
   - Write: 1,157 requests/second
   - Read: 115,740 requests/second
   - Storage: 91.25 TB over 5 years

3. High-Level Design (10 minutes)
   - Load balancer → Web servers → App servers → Database
   - Cache layer for popular URLs
   - Analytics service for tracking

4. API Design (5 minutes)
   - POST /shorten {long_url} → {short_url}
   - GET /{short_url} → redirect to long_url
   - GET /analytics/{short_url} → stats

5. Database Design (5 minutes)
   - URLs table: id, short_url, long_url, created_at
   - Analytics table: short_url, clicked_at, ip, country

6. Algorithm (5 minutes)
   - Base62 encoding of auto-increment ID
   - Handle collisions for custom aliases

7. Scale & Optimize (10 minutes)
   - Read replicas for database
   - CDN for popular redirects
   - Sharding by short_url hash
   - Rate limiting for abuse prevention
```

---

## 📚 Study Resources

### Books
- **"Designing Data-Intensive Applications"** by Martin Kleppmann
- **"System Design Interview"** by Alex Xu
- **"Building Microservices"** by Sam Newman
- **"High Performance Browser Networking"** by Ilya Grigorik

### Online Courses
- **Grokking the System Design Interview** (Educative)
- **System Design Course** (Exponent)
- **Distributed Systems** (MIT 6.824)

### Practice Platforms
- **Pramp** - Mock interviews with peers
- **InterviewBit** - System design problems
- **LeetCode** - System design questions
- **Educative.io** - Interactive courses

### Blogs & Articles
- **High Scalability** - Real-world architectures
- **AWS Architecture Center** - Cloud design patterns
- **Google Cloud Architecture Framework** - Best practices
- **Netflix Tech Blog** - Microservices at scale

### YouTube Channels
- **Gaurav Sen** - System design concepts
- **Tech Dummies** - Interview preparation
- **Success in Tech** - Mock interviews

---

## 🎯 Final Checklist

### Day Before Interview
- [ ] Review your notes on fundamental concepts
- [ ] Practice drawing system diagrams
- [ ] Prepare questions about the company's architecture
- [ ] Get good sleep and stay hydrated

### During Interview
- [ ] Listen carefully to requirements
- [ ] Ask clarifying questions
- [ ] Start with simple design
- [ ] Explain your thought process
- [ ] Discuss trade-offs and alternatives
- [ ] Be prepared to dive deep into any component

### After Interview
- [ ] Send thank you email
- [ ] Reflect on what went well and what to improve
- [ ] Continue practicing based on feedback

---

*Remember: System design interviews are about demonstrating your thought process, not finding the perfect solution. Focus on clear communication, logical reasoning, and practical trade-offs.*