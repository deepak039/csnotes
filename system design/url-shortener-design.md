# Design URL Shortener (like bit.ly, tinyurl) - Interview Guide

## Interview Flow

### 1. Requirements Clarification (5 minutes)

**Interviewer**: "Design a URL shortener service like bit.ly"

**Candidate Questions to Ask**:
- What's the expected scale? How many URLs shortened per day?
- Do we need custom aliases or just random short URLs?
- Do we need analytics (click tracking, geographic data)?
- What's the expiration policy for URLs?
- Do we need user accounts or anonymous usage?
- Any specific requirements for short URL format?

**Assumed Requirements**:
- 100M URLs shortened per day
- 100:1 read/write ratio (10B redirections per day)
- Custom aliases optional
- Basic analytics needed
- URLs don't expire
- Both anonymous and registered users

---

### 2. Capacity Estimation (5 minutes)

**Write Operations**:
```
URLs shortened: 100M/day
Write QPS: 100M / (24 * 3600) = ~1,200 QPS
Peak Write QPS: 1,200 * 2 = ~2,400 QPS
```

**Read Operations**:
```
URL redirections: 10B/day  
Read QPS: 10B / (24 * 3600) = ~115,000 QPS
Peak Read QPS: 115,000 * 2 = ~230,000 QPS
```

**Storage Requirements**:
```
Per URL record:
- Original URL: 500 bytes (average)
- Short URL: 7 bytes
- Created timestamp: 7 bytes
- User ID: 8 bytes
- Total per record: ~500 bytes

Storage for 5 years:
100M * 365 * 5 * 500 bytes = ~90TB
```

**Bandwidth**:
```
Write: 1,200 * 500 bytes = 0.6 MB/s
Read: 115,000 * 500 bytes = 57.5 MB/s
```

---

### 3. High-Level Design (10 minutes)

```
[Client] → [Load Balancer] → [Web Servers] → [Cache] → [Database]
                                    ↓
                              [Analytics Service]
```

**Core Components**:
1. **URL Shortening Service**: Generate short URLs
2. **URL Redirection Service**: Redirect to original URLs  
3. **Database**: Store URL mappings
4. **Cache**: Fast lookups for popular URLs
5. **Analytics Service**: Track clicks and metrics

---

### 4. Detailed Design (15 minutes)

#### 4.1 URL Encoding Strategy

**Interviewer**: "How do you generate short URLs?"

**Approach 1: Base62 Encoding**
```python
def encode_base62(num):
    chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    result = ""
    while num > 0:
        result = chars[num % 62] + result
        num //= 62
    return result

# Example: 125 → "21" in base62
```

**Approach 2: Random String Generation**
```python
import random
import string

def generate_short_url():
    chars = string.ascii_letters + string.digits
    return ''.join(random.choice(chars) for _ in range(7))
```

**Interviewer**: "What are the pros and cons of each approach?"

**Base62 Encoding**:
- ✅ Predictable length
- ✅ No collisions if using counter
- ❌ Sequential, predictable
- ❌ Reveals information about volume

**Random Generation**:
- ✅ Unpredictable
- ✅ Better security
- ❌ Collision handling needed
- ❌ Variable performance

**Chosen Approach**: Base62 with counter for predictability

#### 4.2 Database Schema

```sql
-- URLs table
CREATE TABLE urls (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    short_url VARCHAR(7) UNIQUE NOT NULL,
    long_url TEXT NOT NULL,
    user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    INDEX idx_short_url (short_url),
    INDEX idx_user_id (user_id)
);

-- Analytics table
CREATE TABLE url_analytics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    short_url VARCHAR(7),
    clicked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT,
    referer TEXT,
    INDEX idx_short_url_time (short_url, clicked_at)
);
```

#### 4.3 API Design

**Create Short URL**:
```http
POST /api/v1/shorten
{
    "long_url": "https://example.com/very/long/url",
    "custom_alias": "mylink",  // optional
    "expires_at": "2024-12-31T23:59:59Z"  // optional
}

Response:
{
    "short_url": "https://short.ly/abc123",
    "long_url": "https://example.com/very/long/url",
    "created_at": "2024-01-01T10:00:00Z"
}
```

**Redirect**:
```http
GET /abc123
Response: 302 Redirect to original URL
```

**Analytics**:
```http
GET /api/v1/analytics/abc123
Response:
{
    "short_url": "abc123",
    "total_clicks": 1500,
    "clicks_by_day": [...],
    "top_countries": [...],
    "top_referrers": [...]
}
```

---

### 5. Deep Dive Questions & Solutions (15 minutes)

#### 5.1 Handling High Read Traffic

**Interviewer**: "How do you handle 230K QPS for redirections?"

**Solution: Multi-layer Caching**

```
Client → CDN → Load Balancer → App Server → Redis → Database
```

**Cache Strategy**:
```python
def get_long_url(short_url):
    # L1: Application cache
    if short_url in app_cache:
        return app_cache[short_url]
    
    # L2: Redis cache
    long_url = redis.get(short_url)
    if long_url:
        app_cache[short_url] = long_url
        return long_url
    
    # L3: Database
    long_url = database.get_long_url(short_url)
    if long_url:
        redis.setex(short_url, 3600, long_url)  # 1 hour TTL
        app_cache[short_url] = long_url
    
    return long_url
```

**Cache Levels**:
- **CDN**: Cache popular URLs globally (99% hit rate)
- **Redis**: Distributed cache (95% hit rate for remaining)
- **Database**: Final fallback

#### 5.2 Database Scaling

**Interviewer**: "How do you scale the database?"

**Read Replicas**:
```
Master DB (Writes) → Slave DB 1 (Reads)
                  → Slave DB 2 (Reads)
                  → Slave DB 3 (Reads)
```

**Sharding Strategy**:
```python
def get_shard(short_url):
    return hash(short_url) % num_shards

# Shard 0: URLs starting with 0-9, a-k
# Shard 1: URLs starting with l-v  
# Shard 2: URLs starting with w-z, A-Z
```

#### 5.3 Generating Unique Short URLs at Scale

**Interviewer**: "How do you ensure unique short URLs across multiple servers?"

**Solution: Range-based Counter**
```
Server 1: Handles ranges 1-1,000,000
Server 2: Handles ranges 1,000,001-2,000,000
Server 3: Handles ranges 2,000,001-3,000,000
```

**Implementation**:
```python
class URLShortener:
    def __init__(self, server_id, range_size=1000000):
        self.server_id = server_id
        self.counter_start = server_id * range_size
        self.counter_end = (server_id + 1) * range_size
        self.current_counter = self.counter_start
    
    def generate_short_url(self):
        if self.current_counter >= self.counter_end:
            raise Exception("Range exhausted")
        
        short_url = self.encode_base62(self.current_counter)
        self.current_counter += 1
        return short_url
```

#### 5.4 Custom Aliases

**Interviewer**: "How do you handle custom aliases?"

**Challenges**:
- Collision detection
- Reserved words
- Validation

**Solution**:
```python
def create_custom_alias(custom_alias, long_url):
    # Validate alias
    if not is_valid_alias(custom_alias):
        raise ValueError("Invalid alias format")
    
    if is_reserved_word(custom_alias):
        raise ValueError("Alias is reserved")
    
    # Check availability
    if alias_exists(custom_alias):
        raise ValueError("Alias already taken")
    
    # Create mapping
    return create_url_mapping(custom_alias, long_url)

def is_valid_alias(alias):
    return (len(alias) >= 3 and 
            len(alias) <= 20 and 
            alias.isalnum())

RESERVED_WORDS = {'api', 'www', 'admin', 'help', 'about'}
```

---

### 6. Scale the Design (10 minutes)

#### 6.1 Global Scale Architecture

```
                    [Global DNS/CDN]
                           |
        ┌─────────────────────────────────────┐
        |                                     |
   [US Region]                         [EU Region]
        |                                     |
[Load Balancer]                      [Load Balancer]
        |                                     |
[App Servers]                        [App Servers]
        |                                     |
[Regional Cache]                     [Regional Cache]
        |                                     |
[Regional DB]                        [Regional DB]
        |                                     |
        └──────── [Master Database] ─────────┘
```

#### 6.2 Performance Optimizations

**Database Optimizations**:
```sql
-- Partitioning by creation date
CREATE TABLE urls_2024_01 PARTITION OF urls 
FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

-- Covering index for common queries
CREATE INDEX idx_short_url_covering ON urls (short_url) 
INCLUDE (long_url, created_at);
```

**Application Optimizations**:
- Connection pooling
- Async processing for analytics
- Batch writes for analytics data

---

### 7. Address Bottlenecks (5 minutes)

#### 7.1 Potential Issues & Solutions

**Single Point of Failure**:
- **Problem**: Database master failure
- **Solution**: Master-slave failover, multiple masters

**Hot Partitions**:
- **Problem**: Some URLs much more popular
- **Solution**: Consistent hashing, cache popular URLs at CDN

**Analytics Bottleneck**:
- **Problem**: High write volume for click tracking
- **Solution**: Message queue for async processing

```python
# Async analytics processing
def handle_redirect(short_url):
    long_url = get_long_url(short_url)
    
    # Immediate redirect
    redirect(long_url)
    
    # Async analytics (non-blocking)
    analytics_queue.publish({
        'short_url': short_url,
        'timestamp': now(),
        'ip': request.ip,
        'user_agent': request.user_agent
    })
```

**Rate Limiting**:
```python
# Prevent abuse
@rate_limit(requests_per_minute=100)
def shorten_url(request):
    # Implementation
    pass
```

---

### 8. Additional Considerations

#### 8.1 Security
- Input validation and sanitization
- Rate limiting to prevent abuse
- HTTPS enforcement
- Malicious URL detection

#### 8.2 Monitoring
- QPS metrics
- Error rates
- Cache hit rates
- Database performance
- Popular URLs tracking

#### 8.3 Business Metrics
- Daily active users
- URLs created per user
- Click-through rates
- Geographic distribution

---

## Interview Success Tips

### What Interviewers Look For:
1. **Systematic Approach**: Following structured design process
2. **Scalability Thinking**: Considering growth and bottlenecks
3. **Trade-off Analysis**: Discussing pros/cons of different approaches
4. **Real-world Considerations**: Security, monitoring, business metrics

### Common Mistakes:
- Jumping to complex solutions immediately
- Not asking clarifying questions
- Ignoring capacity estimation
- Not considering failure scenarios
- Over-engineering the initial design

### Key Talking Points:
- "Let's start simple and scale up"
- "What are the trade-offs here?"
- "How would this handle failure?"
- "What would be the bottleneck at scale?"