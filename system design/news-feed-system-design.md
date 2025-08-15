# Design News Feed System (like Facebook, Twitter) - Interview Guide

## Interview Flow

### 1. Requirements Clarification (5 minutes)

**Interviewer**: "Design a news feed system like Facebook or Twitter"

**Candidate Questions to Ask**:
- What's the scale? How many users and posts?
- Is it more like Facebook (friends) or Twitter (followers)?
- What types of content? (text, images, videos, links)
- Do we need real-time updates or eventual consistency is fine?
- Should we support features like likes, comments, shares?
- Any specific requirements for feed ranking/algorithm?
- Mobile and web support needed?
- Do we need to handle celebrity users with millions of followers?

**Assumed Requirements**:
- 1 billion users, 300M daily active users
- Each user has 200 friends/followers on average
- Users generate 2 posts per day on average
- Feed should load in <200ms
- Support text, images, videos
- Both pull and push models needed
- Basic ranking by recency and relevance

---

### 2. Capacity Estimation (5 minutes)

**User Activity**:
```
Daily Active Users (DAU): 300M
Posts per user per day: 2
Total posts per day: 300M × 2 = 600M posts/day
Posts per second: 600M / (24 × 3600) = ~7,000 posts/sec
Peak posts per second: 7,000 × 5 = ~35,000 posts/sec

Feed reads (users check feed 10 times/day):
Feed requests per day: 300M × 10 = 3B requests/day
Feed requests per second: 3B / (24 × 3600) = ~35,000 QPS
Peak feed QPS: 35,000 × 5 = ~175,000 QPS
```

**Storage Requirements**:
```
Per post storage:
- Post ID: 8 bytes
- User ID: 8 bytes
- Content: 1KB average
- Metadata: 200 bytes
- Total per post: ~1.2KB

Daily storage: 600M × 1.2KB = ~720GB/day
5-year storage: 720GB × 365 × 5 = ~1.3PB

Media storage (assuming 20% posts have media):
- Images: 200KB average
- Videos: 2MB average
- Daily media: 120M × 200KB + 20M × 2MB = ~64TB/day
```

**Memory for Feed Cache**:
```
Feed cache per user: 100 posts × 1.2KB = 120KB
For 300M active users: 300M × 120KB = ~36TB
```

---

### 3. High-Level Design (10 minutes)

```
[Mobile/Web Client] → [Load Balancer] → [API Gateway]
                                            ↓
                                    [Feed Generation Service]
                                            ↓
                    ┌─────────────────────────────────────┐
                    ↓                                     ↓
            [Post Service] ← → [User Service] ← → [Notification Service]
                    ↓                                     ↓
            [Post Database]                      [Feed Cache (Redis)]
                    ↓                                     ↓
            [Media Storage]                      [Message Queue]
```

**Core Components**:
1. **Post Service**: Create, store, and retrieve posts
2. **User Service**: Manage user relationships (friends/followers)
3. **Feed Generation Service**: Generate personalized feeds
4. **Feed Cache**: Store pre-computed feeds
5. **Notification Service**: Real-time updates
6. **Media Service**: Handle images/videos
7. **Message Queue**: Async processing

---

### 4. Detailed Design (15 minutes)

#### 4.1 Database Schema

```sql
-- Users table
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    email VARCHAR(100),
    created_at TIMESTAMP,
    INDEX idx_username (username)
);

-- Posts table
CREATE TABLE posts (
    post_id BIGINT PRIMARY KEY,
    user_id BIGINT,
    content TEXT,
    media_urls JSON,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    INDEX idx_user_created (user_id, created_at),
    INDEX idx_created (created_at)
);

-- User relationships (friends/followers)
CREATE TABLE user_relationships (
    follower_id BIGINT,
    followee_id BIGINT,
    created_at TIMESTAMP,
    PRIMARY KEY (follower_id, followee_id),
    INDEX idx_followee (followee_id)
);

-- Feed cache table (optional, usually in Redis)
CREATE TABLE feed_cache (
    user_id BIGINT,
    post_id BIGINT,
    score FLOAT,  -- for ranking
    created_at TIMESTAMP,
    PRIMARY KEY (user_id, post_id),
    INDEX idx_user_score (user_id, score DESC)
);
```

#### 4.2 API Design

**Post Creation**:
```http
POST /api/v1/posts
{
    "content": "Hello world!",
    "media_urls": ["https://cdn.example.com/image1.jpg"],
    "privacy": "public"
}

Response:
{
    "post_id": "123456789",
    "user_id": "987654321",
    "content": "Hello world!",
    "created_at": "2024-01-01T10:00:00Z"
}
```

**Feed Retrieval**:
```http
GET /api/v1/feed?limit=20&cursor=abc123

Response:
{
    "posts": [
        {
            "post_id": "123456789",
            "user_id": "987654321",
            "username": "john_doe",
            "content": "Hello world!",
            "media_urls": ["https://cdn.example.com/image1.jpg"],
            "created_at": "2024-01-01T10:00:00Z",
            "likes_count": 42,
            "comments_count": 5
        }
    ],
    "next_cursor": "def456"
}
```

#### 4.3 Feed Generation Strategies

**Interviewer**: "How do you generate the news feed efficiently?"

#### Strategy 1: Pull Model (Fan-out on Read)

```python
class PullFeedGenerator:
    def __init__(self, post_service, user_service):
        self.post_service = post_service
        self.user_service = user_service
    
    def generate_feed(self, user_id, limit=20):
        # Get user's friends/followers
        friends = self.user_service.get_friends(user_id)
        
        # Get recent posts from all friends
        all_posts = []
        for friend_id in friends:
            posts = self.post_service.get_recent_posts(friend_id, limit=100)
            all_posts.extend(posts)
        
        # Sort by timestamp and return top posts
        all_posts.sort(key=lambda x: x['created_at'], reverse=True)
        return all_posts[:limit]
```

**Pros**: 
- Consistent data
- Works well for inactive users
- No storage overhead

**Cons**: 
- Slow for users with many friends
- High read latency

#### Strategy 2: Push Model (Fan-out on Write)

```python
class PushFeedGenerator:
    def __init__(self, user_service, feed_cache):
        self.user_service = user_service
        self.feed_cache = feed_cache
    
    def on_new_post(self, post):
        # Get all followers of the post author
        followers = self.user_service.get_followers(post['user_id'])
        
        # Add post to each follower's feed cache
        for follower_id in followers:
            self.feed_cache.add_to_feed(follower_id, post)
    
    def get_feed(self, user_id, limit=20):
        # Simply read from pre-computed cache
        return self.feed_cache.get_feed(user_id, limit)
```

**Pros**: 
- Fast feed retrieval
- Good for active users

**Cons**: 
- High write amplification
- Storage overhead
- Stale data for inactive users

#### Strategy 3: Hybrid Model

```python
class HybridFeedGenerator:
    def __init__(self, post_service, user_service, feed_cache):
        self.post_service = post_service
        self.user_service = user_service
        self.feed_cache = feed_cache
        self.celebrity_threshold = 1000000  # 1M followers
    
    def on_new_post(self, post):
        author_id = post['user_id']
        followers = self.user_service.get_followers(author_id)
        
        if len(followers) > self.celebrity_threshold:
            # Celebrity user - don't fan out, use pull model
            self.mark_as_celebrity_post(post)
        else:
            # Regular user - fan out to followers
            for follower_id in followers:
                self.feed_cache.add_to_feed(follower_id, post)
    
    def get_feed(self, user_id, limit=20):
        # Get cached posts (from push model)
        cached_posts = self.feed_cache.get_feed(user_id, limit)
        
        # Get posts from celebrity users (pull model)
        celebrity_friends = self.user_service.get_celebrity_friends(user_id)
        celebrity_posts = []
        for celebrity_id in celebrity_friends:
            posts = self.post_service.get_recent_posts(celebrity_id, limit=10)
            celebrity_posts.extend(posts)
        
        # Merge and sort
        all_posts = cached_posts + celebrity_posts
        all_posts.sort(key=lambda x: x['created_at'], reverse=True)
        
        return all_posts[:limit]
```

---

### 5. Deep Dive Questions & Solutions (15 minutes)

#### 5.1 Feed Ranking Algorithm

**Interviewer**: "How do you rank posts in the feed beyond just chronological order?"

```python
class FeedRanker:
    def __init__(self):
        self.weights = {
            'recency': 0.3,
            'engagement': 0.4,
            'relationship': 0.2,
            'content_type': 0.1
        }
    
    def calculate_score(self, post, user_id):
        # Recency score (newer posts get higher score)
        recency_score = self.calculate_recency_score(post['created_at'])
        
        # Engagement score (likes, comments, shares)
        engagement_score = self.calculate_engagement_score(post)
        
        # Relationship score (closer friends get higher score)
        relationship_score = self.calculate_relationship_score(
            post['user_id'], user_id
        )
        
        # Content type score (videos > images > text)
        content_score = self.calculate_content_score(post)
        
        # Weighted sum
        total_score = (
            self.weights['recency'] * recency_score +
            self.weights['engagement'] * engagement_score +
            self.weights['relationship'] * relationship_score +
            self.weights['content_type'] * content_score
        )
        
        return total_score
    
    def calculate_recency_score(self, created_at):
        hours_ago = (datetime.now() - created_at).total_seconds() / 3600
        return max(0, 1 - (hours_ago / 24))  # Decay over 24 hours
    
    def calculate_engagement_score(self, post):
        likes = post.get('likes_count', 0)
        comments = post.get('comments_count', 0)
        shares = post.get('shares_count', 0)
        
        # Weighted engagement score
        engagement = likes + (comments * 2) + (shares * 3)
        return min(1.0, engagement / 100)  # Normalize to 0-1
    
    def rank_posts(self, posts, user_id):
        scored_posts = []
        for post in posts:
            score = self.calculate_score(post, user_id)
            scored_posts.append((post, score))
        
        # Sort by score descending
        scored_posts.sort(key=lambda x: x[1], reverse=True)
        return [post for post, score in scored_posts]
```

#### 5.2 Real-time Updates

**Interviewer**: "How do you provide real-time feed updates?"

**WebSocket Implementation**:

```python
import asyncio
import websockets
import json

class FeedWebSocketHandler:
    def __init__(self, feed_service, redis_client):
        self.feed_service = feed_service
        self.redis = redis_client
        self.connections = {}  # user_id -> websocket
    
    async def handle_connection(self, websocket, user_id):
        self.connections[user_id] = websocket
        
        # Subscribe to user's feed updates
        pubsub = self.redis.pubsub()
        await pubsub.subscribe(f"feed_updates:{user_id}")
        
        try:
            async for message in pubsub.listen():
                if message['type'] == 'message':
                    update = json.loads(message['data'])
                    await websocket.send(json.dumps(update))
        except websockets.exceptions.ConnectionClosed:
            pass
        finally:
            del self.connections[user_id]
            await pubsub.unsubscribe(f"feed_updates:{user_id}")
    
    async def broadcast_new_post(self, post):
        # Get all followers of the post author
        followers = await self.feed_service.get_followers(post['user_id'])
        
        # Broadcast to connected followers
        for follower_id in followers:
            if follower_id in self.connections:
                websocket = self.connections[follower_id]
                try:
                    await websocket.send(json.dumps({
                        'type': 'new_post',
                        'post': post
                    }))
                except:
                    # Connection closed, remove it
                    del self.connections[follower_id]
```

**Server-Sent Events (SSE) Alternative**:

```python
from flask import Flask, Response
import json
import time

class FeedSSEHandler:
    def __init__(self, redis_client):
        self.redis = redis_client
    
    def feed_stream(self, user_id):
        def event_stream():
            pubsub = self.redis.pubsub()
            pubsub.subscribe(f"feed_updates:{user_id}")
            
            for message in pubsub.listen():
                if message['type'] == 'message':
                    data = json.loads(message['data'])
                    yield f"data: {json.dumps(data)}\n\n"
        
        return Response(event_stream(), mimetype="text/plain")

# Usage in Flask
@app.route('/api/v1/feed/stream/<user_id>')
def feed_stream(user_id):
    return sse_handler.feed_stream(user_id)
```

#### 5.3 Handling Hot Users (Celebrities)

**Interviewer**: "How do you handle users with millions of followers?"

**Celebrity User Strategy**:

```python
class CelebrityHandler:
    def __init__(self, user_service, feed_cache):
        self.user_service = user_service
        self.feed_cache = feed_cache
        self.celebrity_threshold = 1000000
        self.celebrity_cache_ttl = 300  # 5 minutes
    
    def is_celebrity(self, user_id):
        follower_count = self.user_service.get_follower_count(user_id)
        return follower_count > self.celebrity_threshold
    
    def handle_celebrity_post(self, post):
        # Don't fan out to all followers
        # Instead, cache the post for pull-based retrieval
        
        celebrity_id = post['user_id']
        
        # Add to celebrity posts cache
        self.feed_cache.add_celebrity_post(celebrity_id, post)
        
        # Only fan out to most active followers (top 1%)
        top_followers = self.user_service.get_top_active_followers(
            celebrity_id, limit=10000
        )
        
        for follower_id in top_followers:
            self.feed_cache.add_to_feed(follower_id, post)
    
    def get_celebrity_posts_for_user(self, user_id):
        celebrity_friends = self.user_service.get_celebrity_friends(user_id)
        
        celebrity_posts = []
        for celebrity_id in celebrity_friends:
            posts = self.feed_cache.get_celebrity_posts(celebrity_id, limit=5)
            celebrity_posts.extend(posts)
        
        return celebrity_posts
```

#### 5.4 Feed Cache Implementation

```python
import redis
import json
import time

class FeedCache:
    def __init__(self, redis_client):
        self.redis = redis_client
        self.feed_size_limit = 1000  # Max posts per user feed
        self.ttl = 86400  # 24 hours
    
    def add_to_feed(self, user_id, post):
        feed_key = f"feed:{user_id}"
        
        # Add post with score (timestamp for ordering)
        score = time.time()
        post_data = json.dumps(post)
        
        pipe = self.redis.pipeline()
        
        # Add to sorted set
        pipe.zadd(feed_key, {post_data: score})
        
        # Keep only latest N posts
        pipe.zremrangebyrank(feed_key, 0, -(self.feed_size_limit + 1))
        
        # Set expiration
        pipe.expire(feed_key, self.ttl)
        
        pipe.execute()
    
    def get_feed(self, user_id, limit=20, offset=0):
        feed_key = f"feed:{user_id}"
        
        # Get posts in reverse chronological order
        post_data_list = self.redis.zrevrange(
            feed_key, offset, offset + limit - 1
        )
        
        posts = []
        for post_data in post_data_list:
            post = json.loads(post_data)
            posts.append(post)
        
        return posts
    
    def remove_from_all_feeds(self, post_id):
        """Remove a post from all feeds (e.g., when deleted)"""
        # This is expensive - consider using a background job
        # For now, mark as deleted and filter during read
        deleted_posts_key = "deleted_posts"
        self.redis.sadd(deleted_posts_key, post_id)
        self.redis.expire(deleted_posts_key, 86400)  # 24 hours
    
    def is_post_deleted(self, post_id):
        return self.redis.sismember("deleted_posts", post_id)
```

---

### 6. Scale the Design (10 minutes)

#### 6.1 Database Sharding

**Posts Sharding**:
```python
class PostSharding:
    def __init__(self, shard_count=1000):
        self.shard_count = shard_count
    
    def get_shard_for_post(self, post_id):
        return post_id % self.shard_count
    
    def get_shard_for_user_posts(self, user_id):
        # All posts by a user go to the same shard
        return user_id % self.shard_count
    
    def get_database_connection(self, shard_id):
        # Return appropriate database connection
        db_server = f"posts_db_{shard_id // 100}"  # 10 shards per server
        return get_connection(db_server)
```

**User Relationship Sharding**:
```python
class RelationshipSharding:
    def get_shard_for_relationship(self, follower_id, followee_id):
        # Shard by follower_id to keep all relationships of a user together
        return follower_id % self.shard_count
    
    def get_followers(self, user_id):
        # This might require querying multiple shards
        # Consider denormalizing for better performance
        pass
```

#### 6.2 Global Architecture

```
                    [Global CDN]
                         |
                [Global Load Balancer]
                         |
        ┌────────────────┼────────────────┐
        |                |                |
   [US Region]      [EU Region]     [Asia Region]
        |                |                |
[Regional LB]    [Regional LB]    [Regional LB]
        |                |                |
[App Servers]    [App Servers]    [App Servers]
        |                |                |
[Regional Cache] [Regional Cache] [Regional Cache]
        |                |                |
[Regional DB]    [Regional DB]    [Regional DB]
        |                |                |
        └──────── [Global Master DB] ─────┘
```

#### 6.3 Caching Strategy

**Multi-level Caching**:
```python
class MultiLevelCache:
    def __init__(self):
        self.l1_cache = {}  # Application memory cache
        self.l2_cache = redis.Redis()  # Redis cache
        self.l3_cache = memcached.Client()  # Memcached
    
    def get_feed(self, user_id, limit=20):
        # L1: Application cache
        cache_key = f"feed:{user_id}:{limit}"
        if cache_key in self.l1_cache:
            return self.l1_cache[cache_key]
        
        # L2: Redis cache
        feed = self.l2_cache.get(cache_key)
        if feed:
            self.l1_cache[cache_key] = feed
            return feed
        
        # L3: Memcached
        feed = self.l3_cache.get(cache_key)
        if feed:
            self.l2_cache.setex(cache_key, 300, feed)  # 5 min TTL
            self.l1_cache[cache_key] = feed
            return feed
        
        # Generate feed and cache at all levels
        feed = self.generate_feed(user_id, limit)
        self.cache_at_all_levels(cache_key, feed)
        return feed
```

---

### 7. Address Bottlenecks (5 minutes)

#### 7.1 Common Bottlenecks & Solutions

**Database Write Bottleneck**:
- **Problem**: High write volume for posts and feed updates
- **Solution**: Write-through cache, async processing

```python
class AsyncPostProcessor:
    def __init__(self, message_queue):
        self.queue = message_queue
    
    def create_post(self, post_data):
        # Immediate response to user
        post_id = self.generate_post_id()
        
        # Queue for async processing
        self.queue.publish('post_created', {
            'post_id': post_id,
            'post_data': post_data
        })
        
        return {'post_id': post_id, 'status': 'processing'}
    
    def process_post_async(self, message):
        # Save to database
        self.save_post_to_db(message['post_data'])
        
        # Fan out to followers
        self.fan_out_to_followers(message['post_data'])
        
        # Update search index
        self.update_search_index(message['post_data'])
```

**Feed Generation Bottleneck**:
- **Problem**: Generating feeds for millions of users
- **Solution**: Pre-computation, background jobs

```python
class FeedPrecomputation:
    def __init__(self, scheduler):
        self.scheduler = scheduler
    
    def schedule_feed_updates(self):
        # Update feeds for active users more frequently
        active_users = self.get_active_users()
        for user_id in active_users:
            self.scheduler.schedule_job(
                'update_feed',
                args=[user_id],
                delay=300  # 5 minutes
            )
        
        # Update feeds for inactive users less frequently
        inactive_users = self.get_inactive_users()
        for user_id in inactive_users:
            self.scheduler.schedule_job(
                'update_feed',
                args=[user_id],
                delay=3600  # 1 hour
            )
```

---

### 8. Additional Considerations

#### 8.1 Content Moderation

```python
class ContentModerator:
    def __init__(self, ml_service):
        self.ml_service = ml_service
    
    def moderate_post(self, post):
        # Check for inappropriate content
        if self.contains_spam(post['content']):
            return {'approved': False, 'reason': 'spam'}
        
        if self.contains_hate_speech(post['content']):
            return {'approved': False, 'reason': 'hate_speech'}
        
        # Check images/videos
        for media_url in post.get('media_urls', []):
            if not self.is_appropriate_media(media_url):
                return {'approved': False, 'reason': 'inappropriate_media'}
        
        return {'approved': True}
```

#### 8.2 Privacy Controls

```python
class PrivacyManager:
    def can_see_post(self, post, viewer_id):
        if post['privacy'] == 'public':
            return True
        
        if post['privacy'] == 'friends':
            return self.are_friends(post['user_id'], viewer_id)
        
        if post['privacy'] == 'private':
            return post['user_id'] == viewer_id
        
        return False
```

#### 8.3 Analytics and Monitoring

```python
class FeedAnalytics:
    def track_feed_view(self, user_id, posts_shown):
        # Track what posts were shown to user
        self.analytics.track('feed_view', {
            'user_id': user_id,
            'posts_count': len(posts_shown),
            'timestamp': time.time()
        })
    
    def track_post_engagement(self, post_id, user_id, action):
        # Track likes, comments, shares, clicks
        self.analytics.track('post_engagement', {
            'post_id': post_id,
            'user_id': user_id,
            'action': action,
            'timestamp': time.time()
        })
```

---

## Interview Success Tips

### What Interviewers Look For:
1. **Understanding of Scale**: Handling billions of users and posts
2. **Feed Generation Strategy**: Pull vs Push vs Hybrid models
3. **Caching Strategy**: Multi-level caching for performance
4. **Real-time Updates**: WebSocket/SSE implementation
5. **Ranking Algorithm**: Beyond chronological ordering

### Common Mistakes:
- Not considering celebrity users with millions of followers
- Ignoring the write amplification problem in push model
- Not discussing feed ranking algorithms
- Overlooking real-time update requirements
- Not considering privacy and content moderation

### Key Discussion Points:
- "How do you handle users with millions of followers?"
- "What's the trade-off between pull and push models?"
- "How do you ensure feeds are personalized and relevant?"
- "How do you handle real-time updates at scale?"
- "What happens when a user deletes a post?"