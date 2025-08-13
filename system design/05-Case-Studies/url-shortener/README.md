# URL Shortener System Design (bit.ly, tinyurl)

## 📋 Problem Statement

Design a URL shortening service like bit.ly that:
- Converts long URLs to short URLs
- Redirects short URLs to original URLs
- Handles millions of URLs and billions of redirections
- Provides analytics and custom aliases

## 🎯 Requirements Analysis

### Functional Requirements
1. **URL Shortening**: Convert long URL to short URL
2. **URL Redirection**: Redirect short URL to original URL
3. **Custom Aliases**: Allow users to create custom short URLs
4. **Analytics**: Track click counts, geographic data, referrers
5. **Expiration**: URLs can have expiration dates
6. **User Accounts**: Registered users can manage their URLs

### Non-Functional Requirements
1. **Scale**: 100M URLs shortened per day, 10B redirections per day
2. **Availability**: 99.9% uptime
3. **Latency**: <100ms for redirection
4. **Storage**: 5 years of data retention
5. **Read/Write Ratio**: 100:1 (read-heavy system)

## 📊 Capacity Estimation

### Traffic Estimates
- **URL Shortening**: 100M/day = 1,157 requests/second
- **URL Redirection**: 10B/day = 115,740 requests/second
- **Peak Traffic**: 2x average = 231,480 redirections/second

### Storage Estimates
- **URLs per year**: 100M × 365 = 36.5B URLs
- **Storage per URL**: 500 bytes (URL + metadata)
- **Total storage (5 years)**: 36.5B × 5 × 500 bytes = 91.25 TB

### Bandwidth Estimates
- **Incoming**: 1,157 × 500 bytes = 0.58 MB/s
- **Outgoing**: 115,740 × 500 bytes = 57.87 MB/s

### Memory Estimates (Caching)
- **Cache 20% of daily requests**: 10B × 0.2 = 2B requests
- **Memory needed**: 2B × 500 bytes = 1TB
- **With replication**: 1TB × 3 = 3TB

## 🏗️ System Architecture

### High-Level Design

```
[Client] → [Load Balancer] → [Web Servers] → [Application Servers]
                                                      ↓
[Cache Layer] ← → [Database] ← → [Analytics DB]
                                                      ↓
                                            [Background Services]
```

### Detailed Architecture

```
                    [CDN/Edge Locations]
                            ↓
                    [Load Balancer (L7)]
                            ↓
            [Web Server Cluster (Nginx)]
                            ↓
                    [API Gateway]
                            ↓
        [URL Shortening Service] [URL Redirection Service]
                    ↓                       ↓
            [Write DB Master]        [Read Replicas]
                    ↓                       ↓
            [Cache Layer (Redis)]   [Analytics Service]
                                           ↓
                                    [Analytics DB]
```

## 🗄️ Database Design

### URL Mapping Table
```sql
CREATE TABLE urls (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    short_url VARCHAR(7) UNIQUE NOT NULL,
    long_url TEXT NOT NULL,
    user_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_short_url (short_url),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
);
```

### Analytics Table
```sql
CREATE TABLE url_analytics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    short_url VARCHAR(7) NOT NULL,
    clicked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT,
    referrer TEXT,
    country VARCHAR(2),
    city VARCHAR(100),
    INDEX idx_short_url_time (short_url, clicked_at),
    INDEX idx_clicked_at (clicked_at)
);
```

### User Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```

## 🔧 Core Algorithms

### URL Encoding Algorithm

#### Base62 Encoding
```python
class URLShortener:
    def __init__(self):
        self.base62_chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        self.base = len(self.base62_chars)
    
    def encode(self, num):
        """Convert number to base62 string"""
        if num == 0:
            return self.base62_chars[0]
        
        result = []
        while num > 0:
            result.append(self.base62_chars[num % self.base])
            num //= self.base
        
        return ''.join(reversed(result))
    
    def decode(self, short_url):
        """Convert base62 string to number"""
        num = 0
        for char in short_url:
            num = num * self.base + self.base62_chars.index(char)
        return num
```

#### Counter-Based Approach
```python
class CounterBasedShortener:
    def __init__(self):
        self.counter = 1000000  # Start from 1M to ensure 7-char URLs
        self.encoder = URLShortener()
    
    def shorten_url(self, long_url, user_id=None):
        # Get next counter value (atomic operation)
        url_id = self.get_next_counter()
        short_url = self.encoder.encode(url_id)
        
        # Store in database
        self.store_url_mapping(url_id, short_url, long_url, user_id)
        
        return f"https://short.ly/{short_url}"
    
    def get_next_counter(self):
        # Atomic increment using database or Redis
        return redis_client.incr("url_counter")
```

### Hash-Based Approach
```python
import hashlib
import base64

class HashBasedShortener:
    def shorten_url(self, long_url, user_id=None):
        # Create hash of URL + timestamp + user_id
        hash_input = f"{long_url}{time.time()}{user_id or ''}"
        hash_value = hashlib.md5(hash_input.encode()).digest()
        
        # Take first 6 bytes and encode in base64
        short_hash = base64.urlsafe_b64encode(hash_value[:6]).decode()[:7]
        
        # Handle collisions
        while self.url_exists(short_hash):
            hash_input += "1"  # Add salt and rehash
            hash_value = hashlib.md5(hash_input.encode()).digest()
            short_hash = base64.urlsafe_b64encode(hash_value[:6]).decode()[:7]
        
        self.store_url_mapping(short_hash, long_url, user_id)
        return f"https://short.ly/{short_hash}"
```

## 🚀 API Design

### REST API Endpoints

#### Shorten URL
```http
POST /api/v1/shorten
Content-Type: application/json
Authorization: Bearer <token>

{
    "long_url": "https://example.com/very/long/url/path",
    "custom_alias": "my-link",  // optional
    "expires_at": "2024-12-31T23:59:59Z"  // optional
}

Response:
{
    "short_url": "https://short.ly/abc123",
    "long_url": "https://example.com/very/long/url/path",
    "created_at": "2024-01-15T10:30:00Z",
    "expires_at": "2024-12-31T23:59:59Z"
}
```

#### Get URL Info
```http
GET /api/v1/urls/abc123

Response:
{
    "short_url": "https://short.ly/abc123",
    "long_url": "https://example.com/very/long/url/path",
    "created_at": "2024-01-15T10:30:00Z",
    "expires_at": "2024-12-31T23:59:59Z",
    "click_count": 1250,
    "is_active": true
}
```

#### Redirect (Browser)
```http
GET /abc123

Response:
HTTP/1.1 301 Moved Permanently
Location: https://example.com/very/long/url/path
Cache-Control: public, max-age=3600
```

#### Analytics
```http
GET /api/v1/urls/abc123/analytics?period=7d

Response:
{
    "total_clicks": 1250,
    "unique_clicks": 890,
    "clicks_by_day": [
        {"date": "2024-01-15", "clicks": 180},
        {"date": "2024-01-16", "clicks": 220}
    ],
    "top_countries": [
        {"country": "US", "clicks": 450},
        {"country": "UK", "clicks": 200}
    ],
    "top_referrers": [
        {"referrer": "google.com", "clicks": 300},
        {"referrer": "twitter.com", "clicks": 150}
    ]
}
```

## 🎯 Caching Strategy

### Multi-Level Caching

#### 1. CDN/Edge Cache
```
Cache-Control: public, max-age=86400
```
- **What**: Static redirects for popular URLs
- **TTL**: 24 hours
- **Hit Rate**: 80% for popular URLs

#### 2. Application Cache (Redis)
```python
def get_long_url(short_url):
    # Try cache first
    long_url = redis_client.get(f"url:{short_url}")
    if long_url:
        return long_url.decode()
    
    # Cache miss - query database
    long_url = database.get_long_url(short_url)
    if long_url:
        # Cache for 1 hour
        redis_client.setex(f"url:{short_url}", 3600, long_url)
    
    return long_url
```

#### 3. Database Query Cache
- **What**: Frequently accessed URL mappings
- **TTL**: 1 hour
- **Eviction**: LRU policy

### Cache Warming Strategy
```python
def warm_cache():
    # Get top 1000 most clicked URLs from last 24 hours
    popular_urls = analytics_db.get_popular_urls(limit=1000, hours=24)
    
    for url in popular_urls:
        cache_key = f"url:{url.short_url}"
        redis_client.setex(cache_key, 3600, url.long_url)
```

## 📈 Scaling Strategies

### Database Scaling

#### 1. Read Replicas
```python
class DatabaseRouter:
    def __init__(self):
        self.master = get_master_connection()
        self.replicas = get_replica_connections()
    
    def write(self, query, params):
        return self.master.execute(query, params)
    
    def read(self, query, params):
        # Route reads to replicas
        replica = random.choice(self.replicas)
        return replica.execute(query, params)
```

#### 2. Sharding Strategy
```python
class URLShardingStrategy:
    def __init__(self, num_shards=16):
        self.num_shards = num_shards
        self.shards = [get_shard_connection(i) for i in range(num_shards)]
    
    def get_shard(self, short_url):
        # Hash-based sharding
        shard_id = hash(short_url) % self.num_shards
        return self.shards[shard_id]
    
    def store_url(self, short_url, long_url):
        shard = self.get_shard(short_url)
        return shard.execute(
            "INSERT INTO urls (short_url, long_url) VALUES (?, ?)",
            (short_url, long_url)
        )
```

### Application Scaling

#### Auto-Scaling Configuration
```yaml
# Kubernetes HPA
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: url-shortener-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: url-shortener
  minReplicas: 10
  maxReplicas: 100
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

## 🔒 Security Considerations

### Rate Limiting
```python
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address

limiter = Limiter(
    app,
    key_func=get_remote_address,
    default_limits=["1000 per hour"]
)

@app.route('/api/v1/shorten', methods=['POST'])
@limiter.limit("10 per minute")
def shorten_url():
    # Implementation
    pass
```

### Input Validation
```python
import validators
from urllib.parse import urlparse

def validate_url(url):
    # Check if URL is valid
    if not validators.url(url):
        raise ValueError("Invalid URL format")
    
    # Check URL length
    if len(url) > 2048:
        raise ValueError("URL too long")
    
    # Block malicious domains
    parsed = urlparse(url)
    if parsed.hostname in BLOCKED_DOMAINS:
        raise ValueError("Domain not allowed")
    
    return True
```

### Abuse Prevention
```python
class AbuseDetector:
    def __init__(self):
        self.redis = redis.Redis()
    
    def check_abuse(self, user_id, ip_address):
        # Check requests per user
        user_key = f"user_requests:{user_id}"
        user_requests = self.redis.incr(user_key)
        self.redis.expire(user_key, 3600)  # 1 hour window
        
        if user_requests > 1000:  # 1000 requests per hour
            raise RateLimitExceeded("User rate limit exceeded")
        
        # Check requests per IP
        ip_key = f"ip_requests:{ip_address}"
        ip_requests = self.redis.incr(ip_key)
        self.redis.expire(ip_key, 3600)
        
        if ip_requests > 100:  # 100 requests per hour per IP
            raise RateLimitExceeded("IP rate limit exceeded")
```

## 📊 Analytics Implementation

### Real-time Analytics
```python
class AnalyticsCollector:
    def __init__(self):
        self.kafka_producer = KafkaProducer(
            bootstrap_servers=['kafka1:9092', 'kafka2:9092'],
            value_serializer=lambda x: json.dumps(x).encode('utf-8')
        )
    
    def track_click(self, short_url, request):
        event = {
            'short_url': short_url,
            'timestamp': time.time(),
            'ip_address': request.remote_addr,
            'user_agent': request.headers.get('User-Agent'),
            'referrer': request.headers.get('Referer'),
            'country': self.get_country_from_ip(request.remote_addr)
        }
        
        # Send to Kafka for real-time processing
        self.kafka_producer.send('url_clicks', event)
```

### Batch Analytics Processing
```python
class AnalyticsProcessor:
    def process_daily_stats(self, date):
        # Aggregate clicks by URL
        daily_stats = self.aggregate_clicks_by_url(date)
        
        # Update URL statistics
        for short_url, stats in daily_stats.items():
            self.update_url_stats(short_url, stats)
        
        # Generate reports
        self.generate_daily_report(date, daily_stats)
    
    def aggregate_clicks_by_url(self, date):
        query = """
        SELECT short_url, 
               COUNT(*) as total_clicks,
               COUNT(DISTINCT ip_address) as unique_clicks,
               COUNT(DISTINCT country) as countries_reached
        FROM url_analytics 
        WHERE DATE(clicked_at) = %s
        GROUP BY short_url
        """
        return self.analytics_db.execute(query, (date,))
```

## 🔍 Deep Dive Questions & Answers

### Q1: How do you handle URL collisions in hash-based approach?
**Answer**: 
- **Detection**: Check if generated short URL already exists in database
- **Resolution**: Add salt to original URL and regenerate hash
- **Prevention**: Use longer hash (8-10 characters) to reduce collision probability
- **Alternative**: Implement collision counter and append to hash

### Q2: How do you ensure high availability during database failures?
**Answer**:
- **Read Replicas**: Multiple read replicas across different AZs
- **Master Failover**: Automatic promotion of replica to master
- **Circuit Breaker**: Fail fast when database is unavailable
- **Graceful Degradation**: Return cached results or error messages
- **Health Checks**: Continuous monitoring of database health

### Q3: How do you handle the case where a short URL is accessed millions of times?
**Answer**:
- **CDN Caching**: Cache redirect responses at edge locations
- **Application Caching**: Multi-level caching (Redis, in-memory)
- **Database Optimization**: Read replicas, connection pooling
- **Async Analytics**: Decouple click tracking from redirect response
- **Rate Limiting**: Prevent abuse and DDoS attacks

### Q4: How do you implement custom aliases while avoiding conflicts?
**Answer**:
```python
def create_custom_alias(alias, long_url, user_id):
    # Validate alias format
    if not re.match(r'^[a-zA-Z0-9_-]{3,20}$', alias):
        raise ValueError("Invalid alias format")
    
    # Check if alias is available
    if self.alias_exists(alias):
        raise ValueError("Alias already taken")
    
    # Reserve alias atomically
    try:
        self.store_url_mapping(alias, long_url, user_id)
    except IntegrityError:
        raise ValueError("Alias taken by another user")
```

### Q5: How do you handle URL expiration efficiently?
**Answer**:
- **Database Index**: Index on expires_at column for efficient queries
- **Background Job**: Periodic cleanup of expired URLs
- **Cache TTL**: Set cache expiration based on URL expiration
- **Lazy Deletion**: Mark as expired during access, clean up later
```python
def cleanup_expired_urls():
    expired_urls = db.execute(
        "SELECT short_url FROM urls WHERE expires_at < NOW() AND is_active = 1"
    )
    
    for url in expired_urls:
        # Mark as inactive
        db.execute(
            "UPDATE urls SET is_active = 0 WHERE short_url = ?", 
            (url.short_url,)
        )
        
        # Remove from cache
        cache.delete(f"url:{url.short_url}")
```

## 🎯 Trade-offs Analysis

### Consistency vs Availability
- **Choice**: Eventual consistency for high availability
- **Reason**: URL redirects can tolerate slight delays in analytics
- **Implementation**: Async analytics processing, cached redirects

### Storage vs Performance
- **Choice**: Denormalized data for faster reads
- **Reason**: Read-heavy workload (100:1 ratio)
- **Implementation**: Store computed analytics in separate tables

### Cost vs Latency
- **Choice**: Multi-level caching for low latency
- **Reason**: User experience critical for redirect service
- **Implementation**: CDN + Redis + application cache

## 📋 Monitoring & Alerting

### Key Metrics
- **Availability**: 99.9% uptime SLA
- **Latency**: P95 < 100ms for redirects
- **Throughput**: Handle 200K+ requests/second
- **Error Rate**: < 0.1% error rate
- **Cache Hit Rate**: > 80% cache hit rate

### Alerting Rules
```yaml
# Prometheus alerting rules
groups:
- name: url-shortener
  rules:
  - alert: HighErrorRate
    expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.01
    for: 2m
    annotations:
      summary: "High error rate detected"
  
  - alert: HighLatency
    expr: histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m])) > 0.1
    for: 5m
    annotations:
      summary: "High latency detected"
```

---

## 🚀 Next Steps & Extensions

### Advanced Features
1. **QR Code Generation**: Generate QR codes for short URLs
2. **Bulk URL Shortening**: API for batch processing
3. **A/B Testing**: Different destinations for same short URL
4. **Geographic Routing**: Route to different URLs based on location
5. **API Rate Limiting**: Tiered pricing based on usage

### Performance Optimizations
1. **Connection Pooling**: Optimize database connections
2. **Async Processing**: Non-blocking I/O for better throughput
3. **Compression**: Compress analytics data for storage efficiency
4. **Partitioning**: Time-based partitioning for analytics tables

---

*This completes the comprehensive URL Shortener system design. The design handles scale, provides detailed implementation examples, and addresses real-world challenges with practical solutions.*