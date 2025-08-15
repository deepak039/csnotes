# Design Rate Limiter - Interview Guide

## Interview Flow

### 1. Requirements Clarification (5 minutes)

**Interviewer**: "Design a rate limiter that can limit the number of requests"

**Candidate Questions to Ask**:
- What type of rate limiting? (User-based, IP-based, API key-based?)
- What's the scale? How many requests per second?
- What should happen when limit is exceeded? (Block, queue, or throttle?)
- Should it be distributed across multiple servers?
- What time windows? (per second, minute, hour, day?)
- Do we need different limits for different users/tiers?
- Should it be a library or a service?
- Any specific requirements for accuracy vs performance?

**Assumed Requirements**:
- Support multiple rate limiting rules
- Distributed system (multiple servers)
- Handle 10M requests per second
- Different limits per user/API key
- Low latency (<1ms additional overhead)
- High availability
- Configurable time windows

---

### 2. Capacity Estimation (5 minutes)

**Scale Requirements**:
```
Requests to check: 10M/second
Peak load: 20M/second

Memory per user (sliding window):
- User ID: 8 bytes
- Timestamp + count: 16 bytes per entry
- 100 entries per user (for 1-minute window with 1-second granularity)
- Total per user: 8 + (16 * 100) = ~1.6KB

For 1M active users:
Memory needed: 1M * 1.6KB = ~1.6GB

Response time requirement: <1ms
Availability: 99.99%
```

---

### 3. High-Level Design (10 minutes)

```
[Client] → [Load Balancer] → [Rate Limiter Service] → [Application Service]
                                      ↓
                              [Redis Cluster]
                                      ↓
                              [Configuration Service]
```

**Core Components**:
1. **Rate Limiter Service**: Main logic for rate limiting
2. **Redis Cluster**: Store counters and timestamps
3. **Configuration Service**: Manage rate limiting rules
4. **Load Balancer**: Distribute requests
5. **Monitoring**: Track metrics and alerts

---

### 4. Detailed Design (15 minutes)

#### 4.1 Rate Limiting Algorithms

**Interviewer**: "What algorithms can you use for rate limiting?"

#### Algorithm 1: Token Bucket

```python
class TokenBucket:
    def __init__(self, capacity, refill_rate):
        self.capacity = capacity
        self.tokens = capacity
        self.refill_rate = refill_rate  # tokens per second
        self.last_refill = time.time()
    
    def allow_request(self):
        self._refill()
        if self.tokens > 0:
            self.tokens -= 1
            return True
        return False
    
    def _refill(self):
        now = time.time()
        tokens_to_add = (now - self.last_refill) * self.refill_rate
        self.tokens = min(self.capacity, self.tokens + tokens_to_add)
        self.last_refill = now
```

**Pros**: Smooth traffic, allows bursts
**Cons**: Complex implementation, memory per user

#### Algorithm 2: Leaky Bucket

```python
class LeakyBucket:
    def __init__(self, capacity, leak_rate):
        self.capacity = capacity
        self.queue = []
        self.leak_rate = leak_rate  # requests per second
        self.last_leak = time.time()
    
    def allow_request(self):
        self._leak()
        if len(self.queue) < self.capacity:
            self.queue.append(time.time())
            return True
        return False
    
    def _leak(self):
        now = time.time()
        requests_to_leak = int((now - self.last_leak) * self.leak_rate)
        self.queue = self.queue[requests_to_leak:]
        self.last_leak = now
```

**Pros**: Smooth output rate
**Cons**: May reject requests even when system not busy

#### Algorithm 3: Fixed Window Counter

```python
class FixedWindowCounter:
    def __init__(self, limit, window_size):
        self.limit = limit
        self.window_size = window_size  # in seconds
        self.counters = {}  # window_start -> count
    
    def allow_request(self, user_id):
        now = time.time()
        window_start = int(now // self.window_size) * self.window_size
        
        key = f"{user_id}:{window_start}"
        current_count = self.counters.get(key, 0)
        
        if current_count < self.limit:
            self.counters[key] = current_count + 1
            return True
        return False
```

**Pros**: Simple, memory efficient
**Cons**: Traffic spikes at window boundaries

#### Algorithm 4: Sliding Window Log

```python
class SlidingWindowLog:
    def __init__(self, limit, window_size):
        self.limit = limit
        self.window_size = window_size
        self.logs = {}  # user_id -> [timestamps]
    
    def allow_request(self, user_id):
        now = time.time()
        
        if user_id not in self.logs:
            self.logs[user_id] = []
        
        # Remove old entries
        cutoff = now - self.window_size
        self.logs[user_id] = [t for t in self.logs[user_id] if t > cutoff]
        
        if len(self.logs[user_id]) < self.limit:
            self.logs[user_id].append(now)
            return True
        return False
```

**Pros**: Accurate, no boundary issues
**Cons**: High memory usage

#### Algorithm 5: Sliding Window Counter (Hybrid)

```python
class SlidingWindowCounter:
    def __init__(self, limit, window_size):
        self.limit = limit
        self.window_size = window_size
        self.counters = {}  # (user_id, window) -> count
    
    def allow_request(self, user_id):
        now = time.time()
        current_window = int(now // self.window_size)
        previous_window = current_window - 1
        
        # Get counts from current and previous windows
        current_count = self.counters.get((user_id, current_window), 0)
        previous_count = self.counters.get((user_id, previous_window), 0)
        
        # Calculate sliding window count
        window_progress = (now % self.window_size) / self.window_size
        estimated_count = previous_count * (1 - window_progress) + current_count
        
        if estimated_count < self.limit:
            self.counters[(user_id, current_window)] = current_count + 1
            return True
        return False
```

**Pros**: Good balance of accuracy and efficiency
**Cons**: Approximation, not 100% accurate

**Interviewer**: "Which algorithm would you choose and why?"

**Answer**: "I'd choose **Sliding Window Counter** because it provides a good balance between accuracy and performance. It's more accurate than fixed window, more memory-efficient than sliding window log, and simpler than token bucket for distributed systems."

#### 4.2 Distributed Implementation

**Redis-based Implementation**:

```python
import redis
import time
import json

class DistributedRateLimiter:
    def __init__(self, redis_client):
        self.redis = redis_client
    
    def is_allowed(self, user_id, limit, window_size):
        """
        Sliding window counter implementation using Redis
        """
        now = time.time()
        current_window = int(now // window_size)
        previous_window = current_window - 1
        
        pipe = self.redis.pipeline()
        
        # Keys for current and previous windows
        current_key = f"rate_limit:{user_id}:{current_window}"
        previous_key = f"rate_limit:{user_id}:{previous_window}"
        
        # Get counts from both windows
        pipe.get(current_key)
        pipe.get(previous_key)
        results = pipe.execute()
        
        current_count = int(results[0] or 0)
        previous_count = int(results[1] or 0)
        
        # Calculate sliding window count
        window_progress = (now % window_size) / window_size
        estimated_count = previous_count * (1 - window_progress) + current_count
        
        if estimated_count < limit:
            # Increment counter and set expiration
            pipe = self.redis.pipeline()
            pipe.incr(current_key)
            pipe.expire(current_key, window_size * 2)  # Keep for 2 windows
            pipe.execute()
            return True
        
        return False
```

#### 4.3 Configuration Management

```python
class RateLimitConfig:
    def __init__(self, config_store):
        self.config_store = config_store
        self.cache = {}
        self.cache_ttl = 60  # Cache for 1 minute
    
    def get_limits(self, user_id, api_key=None):
        """
        Get rate limits for a user/API key
        Returns: [(limit, window_size), ...]
        """
        cache_key = f"limits:{user_id}:{api_key}"
        
        # Check cache first
        if cache_key in self.cache:
            cached_data, timestamp = self.cache[cache_key]
            if time.time() - timestamp < self.cache_ttl:
                return cached_data
        
        # Fetch from config store
        limits = self._fetch_limits_from_store(user_id, api_key)
        
        # Cache the result
        self.cache[cache_key] = (limits, time.time())
        
        return limits
    
    def _fetch_limits_from_store(self, user_id, api_key):
        # Priority: API key > User tier > Default
        if api_key:
            api_limits = self.config_store.get_api_key_limits(api_key)
            if api_limits:
                return api_limits
        
        user_tier = self.config_store.get_user_tier(user_id)
        return self.config_store.get_tier_limits(user_tier)

# Example configuration
DEFAULT_LIMITS = {
    'free': [(100, 3600), (10, 60)],      # 100/hour, 10/minute
    'premium': [(1000, 3600), (100, 60)], # 1000/hour, 100/minute
    'enterprise': [(10000, 3600), (1000, 60)] # 10000/hour, 1000/minute
}
```

---

### 5. Deep Dive Questions & Solutions (15 minutes)

#### 5.1 Handling Race Conditions

**Interviewer**: "How do you handle race conditions in a distributed environment?"

**Problem**: Multiple servers checking and incrementing counters simultaneously

**Solution 1: Lua Scripts (Atomic Operations)**

```lua
-- Redis Lua script for atomic rate limiting
local key = KEYS[1]
local limit = tonumber(ARGV[1])
local window = tonumber(ARGV[2])
local current_time = tonumber(ARGV[3])

-- Get current count
local current_count = redis.call('GET', key) or 0
current_count = tonumber(current_count)

if current_count < limit then
    -- Increment and set expiration
    redis.call('INCR', key)
    redis.call('EXPIRE', key, window)
    return {1, current_count + 1}  -- allowed, new count
else
    return {0, current_count}  -- not allowed, current count
end
```

```python
class AtomicRateLimiter:
    def __init__(self, redis_client):
        self.redis = redis_client
        self.lua_script = self.redis.register_script(LUA_SCRIPT)
    
    def is_allowed(self, user_id, limit, window):
        key = f"rate_limit:{user_id}:{int(time.time() // window)}"
        result = self.lua_script(keys=[key], args=[limit, window, time.time()])
        return result[0] == 1
```

**Solution 2: Optimistic Locking**

```python
def is_allowed_with_optimistic_lock(self, user_id, limit, window):
    key = f"rate_limit:{user_id}:{int(time.time() // window)}"
    
    for _ in range(3):  # Retry up to 3 times
        # Watch the key for changes
        self.redis.watch(key)
        
        current_count = int(self.redis.get(key) or 0)
        
        if current_count >= limit:
            self.redis.unwatch()
            return False
        
        # Start transaction
        pipe = self.redis.pipeline()
        pipe.incr(key)
        pipe.expire(key, window * 2)
        
        try:
            pipe.execute()
            return True
        except redis.WatchError:
            # Key was modified, retry
            continue
    
    return False  # Failed after retries
```

#### 5.2 Multi-tier Rate Limiting

**Interviewer**: "How do you implement different rate limits for different user tiers?"

```python
class MultiTierRateLimiter:
    def __init__(self, redis_client, config_service):
        self.redis = redis_client
        self.config = config_service
        self.limiters = {
            'sliding_window': SlidingWindowCounter(),
            'token_bucket': TokenBucket(),
            'fixed_window': FixedWindowCounter()
        }
    
    def is_allowed(self, user_id, api_key=None, endpoint=None):
        # Get all applicable limits
        limits = self.config.get_limits(user_id, api_key, endpoint)
        
        # Check each limit
        for limit_config in limits:
            algorithm = limit_config['algorithm']
            limit = limit_config['limit']
            window = limit_config['window']
            
            limiter = self.limiters[algorithm]
            if not limiter.is_allowed(user_id, limit, window):
                return False, limit_config
        
        return True, None

# Example configuration
RATE_LIMIT_RULES = {
    'user_123': [
        {'algorithm': 'sliding_window', 'limit': 1000, 'window': 3600},
        {'algorithm': 'token_bucket', 'limit': 10, 'window': 1}
    ],
    'api_key_abc': [
        {'algorithm': 'fixed_window', 'limit': 5000, 'window': 3600}
    ],
    'endpoint_/api/upload': [
        {'algorithm': 'sliding_window', 'limit': 100, 'window': 3600}
    ]
}
```

#### 5.3 Rate Limiter as Middleware

**Interviewer**: "How would you implement this as middleware in a web application?"

```python
from flask import Flask, request, jsonify
import functools

class RateLimitMiddleware:
    def __init__(self, app, rate_limiter):
        self.app = app
        self.rate_limiter = rate_limiter
        self.app.before_request(self.check_rate_limit)
    
    def check_rate_limit(self):
        # Extract user identifier
        user_id = self.get_user_id(request)
        api_key = request.headers.get('X-API-Key')
        endpoint = request.endpoint
        
        # Check rate limit
        allowed, violated_rule = self.rate_limiter.is_allowed(
            user_id, api_key, endpoint
        )
        
        if not allowed:
            return jsonify({
                'error': 'Rate limit exceeded',
                'rule': violated_rule,
                'retry_after': self.calculate_retry_after(violated_rule)
            }), 429
    
    def get_user_id(self, request):
        # Try different methods to identify user
        if 'user_id' in request.headers:
            return request.headers['user_id']
        if 'X-API-Key' in request.headers:
            return f"api_key:{request.headers['X-API-Key']}"
        return f"ip:{request.remote_addr}"

# Decorator approach
def rate_limit(limit, window, algorithm='sliding_window'):
    def decorator(f):
        @functools.wraps(f)
        def wrapper(*args, **kwargs):
            user_id = get_current_user_id()
            
            if not rate_limiter.is_allowed(user_id, limit, window):
                return jsonify({'error': 'Rate limit exceeded'}), 429
            
            return f(*args, **kwargs)
        return wrapper
    return decorator

# Usage
@app.route('/api/data')
@rate_limit(limit=100, window=3600)  # 100 requests per hour
def get_data():
    return jsonify({'data': 'some data'})
```

#### 5.4 Handling Distributed Clock Skew

**Interviewer**: "What if servers have slightly different clocks?"

**Problem**: Clock differences can cause inconsistent rate limiting

**Solution: Centralized Time Service**

```python
class TimeService:
    def __init__(self, ntp_servers):
        self.ntp_servers = ntp_servers
        self.time_offset = 0
        self.last_sync = 0
        self.sync_interval = 300  # 5 minutes
    
    def get_current_time(self):
        # Sync with NTP if needed
        if time.time() - self.last_sync > self.sync_interval:
            self.sync_with_ntp()
        
        return time.time() + self.time_offset
    
    def sync_with_ntp(self):
        try:
            # Get time from NTP server
            ntp_time = self.get_ntp_time()
            local_time = time.time()
            self.time_offset = ntp_time - local_time
            self.last_sync = local_time
        except Exception:
            # Fallback to local time
            pass

class ClockAwareRateLimiter:
    def __init__(self, redis_client, time_service):
        self.redis = redis_client
        self.time_service = time_service
    
    def is_allowed(self, user_id, limit, window):
        # Use synchronized time
        now = self.time_service.get_current_time()
        current_window = int(now // window)
        
        key = f"rate_limit:{user_id}:{current_window}"
        # ... rest of implementation
```

---

### 6. Scale the Design (10 minutes)

#### 6.1 Global Scale Architecture

```
                    [Global Load Balancer]
                            |
        ┌───────────────────┼───────────────────┐
        |                   |                   |
   [US Region]         [EU Region]        [Asia Region]
        |                   |                   |
[Rate Limiter Cluster] [Rate Limiter Cluster] [Rate Limiter Cluster]
        |                   |                   |
[Redis Cluster US]     [Redis Cluster EU]    [Redis Cluster Asia]
        |                   |                   |
        └─────────── [Global Config Service] ──┘
```

#### 6.2 Performance Optimizations

**Local Caching**:
```python
class CachedRateLimiter:
    def __init__(self, redis_client):
        self.redis = redis_client
        self.local_cache = {}
        self.cache_size = 10000
        self.cache_ttl = 10  # 10 seconds
    
    def is_allowed(self, user_id, limit, window):
        # Check local cache first
        cache_key = f"{user_id}:{limit}:{window}"
        
        if cache_key in self.local_cache:
            cached_data, timestamp = self.local_cache[cache_key]
            if time.time() - timestamp < self.cache_ttl:
                return self._check_local_limit(cached_data)
        
        # Fallback to Redis
        return self._check_redis_limit(user_id, limit, window)
```

**Connection Pooling**:
```python
class OptimizedRateLimiter:
    def __init__(self, redis_config):
        self.redis_pool = redis.ConnectionPool(
            host=redis_config['host'],
            port=redis_config['port'],
            max_connections=100,
            retry_on_timeout=True
        )
        self.redis = redis.Redis(connection_pool=self.redis_pool)
```

---

### 7. Address Bottlenecks (5 minutes)

#### 7.1 Common Bottlenecks & Solutions

**Redis Bottleneck**:
- **Problem**: Single Redis instance becomes bottleneck
- **Solution**: Redis Cluster with consistent hashing

```python
class ShardedRateLimiter:
    def __init__(self, redis_nodes):
        self.redis_nodes = redis_nodes
        self.num_shards = len(redis_nodes)
    
    def get_redis_client(self, user_id):
        shard = hash(user_id) % self.num_shards
        return self.redis_nodes[shard]
    
    def is_allowed(self, user_id, limit, window):
        redis_client = self.get_redis_client(user_id)
        return self._check_limit(redis_client, user_id, limit, window)
```

**Network Latency**:
- **Problem**: Network calls add latency
- **Solution**: Batch operations, async processing

```python
import asyncio
import aioredis

class AsyncRateLimiter:
    def __init__(self, redis_url):
        self.redis = None
    
    async def init_redis(self):
        self.redis = await aioredis.from_url(redis_url)
    
    async def is_allowed_batch(self, requests):
        """Check multiple rate limits in one batch"""
        pipe = self.redis.pipeline()
        
        for req in requests:
            key = f"rate_limit:{req['user_id']}:{int(time.time() // req['window'])}"
            pipe.get(key)
        
        results = await pipe.execute()
        
        # Process results and update counters
        allowed_requests = []
        pipe = self.redis.pipeline()
        
        for i, (req, count) in enumerate(zip(requests, results)):
            current_count = int(count or 0)
            if current_count < req['limit']:
                allowed_requests.append(req)
                key = f"rate_limit:{req['user_id']}:{int(time.time() // req['window'])}"
                pipe.incr(key)
                pipe.expire(key, req['window'] * 2)
        
        await pipe.execute()
        return allowed_requests
```

---

### 8. Additional Considerations

#### 8.1 Monitoring & Alerting

```python
class RateLimiterMetrics:
    def __init__(self, metrics_client):
        self.metrics = metrics_client
    
    def record_request(self, user_id, allowed, algorithm):
        self.metrics.increment('rate_limiter.requests.total')
        
        if allowed:
            self.metrics.increment('rate_limiter.requests.allowed')
        else:
            self.metrics.increment('rate_limiter.requests.blocked')
        
        self.metrics.increment(f'rate_limiter.algorithm.{algorithm}')
    
    def record_latency(self, latency_ms):
        self.metrics.histogram('rate_limiter.latency', latency_ms)
```

#### 8.2 Circuit Breaker Pattern

```python
class CircuitBreakerRateLimiter:
    def __init__(self, redis_client, failure_threshold=5):
        self.redis = redis_client
        self.failure_threshold = failure_threshold
        self.failure_count = 0
        self.last_failure_time = 0
        self.circuit_open = False
        self.recovery_timeout = 60  # 1 minute
    
    def is_allowed(self, user_id, limit, window):
        # Check if circuit is open
        if self.circuit_open:
            if time.time() - self.last_failure_time > self.recovery_timeout:
                self.circuit_open = False
                self.failure_count = 0
            else:
                # Fail open - allow requests when Redis is down
                return True
        
        try:
            result = self._check_redis_limit(user_id, limit, window)
            self.failure_count = 0  # Reset on success
            return result
        except Exception as e:
            self.failure_count += 1
            self.last_failure_time = time.time()
            
            if self.failure_count >= self.failure_threshold:
                self.circuit_open = True
            
            # Fail open - allow requests when Redis is down
            return True
```

---

## Interview Success Tips

### What Interviewers Look For:
1. **Algorithm Knowledge**: Understanding different rate limiting algorithms
2. **Distributed Systems**: Handling race conditions, consistency
3. **Scalability**: Designing for high throughput
4. **Trade-offs**: Discussing accuracy vs performance

### Common Mistakes:
- Not considering race conditions
- Ignoring distributed system challenges
- Choosing overly complex algorithms
- Not discussing monitoring and failure handling

### Key Discussion Points:
- "What happens when Redis goes down?"
- "How do you handle clock skew across servers?"
- "What's the trade-off between accuracy and performance?"
- "How do you prevent race conditions in distributed environment?"