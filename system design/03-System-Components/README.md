# System Components Deep Dive

## 🗄️ Storage Systems

### File Systems
**Purpose**: Store and organize files on disk

#### Types
1. **Local File Systems**
   - **Examples**: NTFS, ext4, APFS
   - **Use Case**: Single machine storage
   - **Pros**: Fast access, simple
   - **Cons**: Not distributed, limited scalability

2. **Distributed File Systems**
   - **Examples**: HDFS, GFS, Amazon EFS
   - **Use Case**: Big data, distributed computing
   - **Pros**: Scalable, fault-tolerant
   - **Cons**: Complex, network overhead

3. **Object Storage**
   - **Examples**: Amazon S3, Google Cloud Storage
   - **Use Case**: Web applications, backup, archival
   - **Pros**: Unlimited scale, REST API, metadata
   - **Cons**: No file system semantics

### Object Storage Deep Dive

#### Amazon S3 Architecture
```
[Client] → [Load Balancer] → [API Gateway] → [Storage Nodes]
                                                    ↓
                              [Metadata Store] ← [Replication Service]
```

#### Key Features
- **Durability**: 99.999999999% (11 9's)
- **Availability**: 99.99%
- **Scalability**: Virtually unlimited
- **Consistency**: Strong read-after-write for new objects

#### Storage Classes
```python
# S3 Storage Classes
STORAGE_CLASSES = {
    'STANDARD': {
        'durability': '99.999999999%',
        'availability': '99.99%',
        'cost': 'High',
        'retrieval': 'Immediate'
    },
    'STANDARD_IA': {
        'durability': '99.999999999%',
        'availability': '99.9%',
        'cost': 'Medium',
        'retrieval': 'Immediate'
    },
    'GLACIER': {
        'durability': '99.999999999%',
        'availability': '99.99%',
        'cost': 'Low',
        'retrieval': '1-5 minutes to 12 hours'
    }
}
```

### Content Delivery Network (CDN)

#### How CDN Works
```
[User] → [Edge Server] → [Origin Server]
           ↓ (if cache miss)
    [Cached Content]
```

#### CDN Benefits
- **Reduced Latency**: Content served from nearby edge servers
- **Reduced Load**: Origin server handles fewer requests
- **Improved Availability**: Multiple edge servers provide redundancy
- **DDoS Protection**: Distributed infrastructure absorbs attacks

#### CDN Configuration Example
```javascript
// CloudFront distribution configuration
{
  "DistributionConfig": {
    "Origins": [{
      "Id": "myOrigin",
      "DomainName": "example.com",
      "CustomOriginConfig": {
        "HTTPPort": 80,
        "HTTPSPort": 443,
        "OriginProtocolPolicy": "https-only"
      }
    }],
    "DefaultCacheBehavior": {
      "TargetOriginId": "myOrigin",
      "ViewerProtocolPolicy": "redirect-to-https",
      "CachePolicyId": "managed-caching-optimized",
      "TTL": {
        "DefaultTTL": 86400,  // 24 hours
        "MaxTTL": 31536000    // 1 year
      }
    }
  }
}
```

---

## 📨 Messaging Systems

### Message Queue Patterns

#### 1. Point-to-Point (Queue)
```
[Producer] → [Queue] → [Consumer]
```
- **Characteristics**: One message, one consumer
- **Use Case**: Task processing, order processing
- **Examples**: Amazon SQS, RabbitMQ

#### 2. Publish-Subscribe (Topic)
```
[Publisher] → [Topic] → [Subscriber 1]
                    → [Subscriber 2]
                    → [Subscriber 3]
```
- **Characteristics**: One message, multiple consumers
- **Use Case**: Event notifications, real-time updates
- **Examples**: Apache Kafka, Amazon SNS

### Apache Kafka Deep Dive

#### Kafka Architecture
```
[Producer] → [Broker 1] → [Consumer Group 1]
         → [Broker 2] → [Consumer Group 2]
         → [Broker 3]
```

#### Key Concepts
- **Topic**: Category of messages
- **Partition**: Ordered sequence of messages within topic
- **Broker**: Kafka server that stores messages
- **Consumer Group**: Group of consumers sharing message processing

#### Kafka Configuration
```properties
# Server configuration
num.network.threads=8
num.io.threads=16
socket.send.buffer.bytes=102400
socket.receive.buffer.bytes=102400
socket.request.max.bytes=104857600

# Log configuration
log.retention.hours=168
log.segment.bytes=1073741824
log.retention.check.interval.ms=300000

# Replication
default.replication.factor=3
min.insync.replicas=2
```

#### Producer Example
```python
from kafka import KafkaProducer
import json

producer = KafkaProducer(
    bootstrap_servers=['kafka1:9092', 'kafka2:9092'],
    value_serializer=lambda x: json.dumps(x).encode('utf-8'),
    acks='all',  # Wait for all replicas
    retries=3,
    batch_size=16384,
    linger_ms=10
)

# Send message
producer.send('user-events', {
    'user_id': 12345,
    'event': 'page_view',
    'timestamp': time.time()
})
```

#### Consumer Example
```python
from kafka import KafkaConsumer
import json

consumer = KafkaConsumer(
    'user-events',
    bootstrap_servers=['kafka1:9092', 'kafka2:9092'],
    value_deserializer=lambda m: json.loads(m.decode('utf-8')),
    group_id='analytics-group',
    auto_offset_reset='earliest',
    enable_auto_commit=True
)

for message in consumer:
    event = message.value
    process_user_event(event)
```

### Message Delivery Guarantees

#### 1. At-Most-Once
- **Guarantee**: Message delivered zero or one time
- **Risk**: Message loss
- **Use Case**: Metrics, logging (where loss is acceptable)

#### 2. At-Least-Once
- **Guarantee**: Message delivered one or more times
- **Risk**: Duplicate processing
- **Use Case**: Most common pattern, requires idempotent processing

#### 3. Exactly-Once
- **Guarantee**: Message delivered exactly one time
- **Complexity**: High (requires distributed transactions)
- **Use Case**: Financial transactions, critical data processing

---

## 🔍 Search Systems

### Search Architecture
```
[Query] → [Query Parser] → [Index] → [Ranking] → [Results]
                              ↓
                        [Document Store]
```

### Elasticsearch Deep Dive

#### Index Structure
```json
{
  "mappings": {
    "properties": {
      "title": {
        "type": "text",
        "analyzer": "standard"
      },
      "content": {
        "type": "text",
        "analyzer": "english"
      },
      "tags": {
        "type": "keyword"
      },
      "created_at": {
        "type": "date"
      },
      "popularity_score": {
        "type": "float"
      }
    }
  }
}
```

#### Search Query Example
```json
{
  "query": {
    "bool": {
      "must": [
        {
          "multi_match": {
            "query": "system design",
            "fields": ["title^2", "content"]
          }
        }
      ],
      "filter": [
        {
          "range": {
            "created_at": {
              "gte": "2024-01-01"
            }
          }
        }
      ],
      "should": [
        {
          "term": {
            "tags": "tutorial"
          }
        }
      ]
    }
  },
  "sort": [
    {
      "popularity_score": {
        "order": "desc"
      }
    },
    "_score"
  ]
}
```

### Search Optimization Techniques

#### 1. Indexing Strategies
```python
class SearchIndexer:
    def __init__(self):
        self.es = Elasticsearch(['es1:9200', 'es2:9200'])
    
    def index_document(self, doc_id, document):
        # Extract searchable content
        searchable_content = self.extract_text(document)
        
        # Create search document
        search_doc = {
            'title': document['title'],
            'content': searchable_content,
            'tags': document.get('tags', []),
            'created_at': document['created_at'],
            'popularity_score': self.calculate_popularity(document)
        }
        
        # Index with routing for better performance
        self.es.index(
            index='documents',
            id=doc_id,
            body=search_doc,
            routing=document.get('category')
        )
```

#### 2. Query Optimization
```python
def search_documents(query, filters=None, page=1, size=20):
    # Build query
    search_query = {
        "query": {
            "bool": {
                "must": [
                    {
                        "multi_match": {
                            "query": query,
                            "fields": ["title^3", "content^1"],
                            "type": "best_fields",
                            "fuzziness": "AUTO"
                        }
                    }
                ]
            }
        },
        "highlight": {
            "fields": {
                "title": {},
                "content": {"fragment_size": 150}
            }
        },
        "from": (page - 1) * size,
        "size": size
    }
    
    # Add filters
    if filters:
        search_query["query"]["bool"]["filter"] = filters
    
    return es.search(index="documents", body=search_query)
```

---

## 📊 Monitoring & Observability

### Three Pillars of Observability

#### 1. Metrics
**Definition**: Numerical measurements over time

```python
# Prometheus metrics example
from prometheus_client import Counter, Histogram, Gauge

# Counter: Monotonically increasing
request_count = Counter('http_requests_total', 'Total HTTP requests', ['method', 'endpoint'])

# Histogram: Distribution of values
request_duration = Histogram('http_request_duration_seconds', 'HTTP request duration')

# Gauge: Current value
active_connections = Gauge('active_connections', 'Active database connections')

# Usage
@request_duration.time()
def handle_request():
    request_count.labels(method='GET', endpoint='/api/users').inc()
    # Process request
    active_connections.set(get_active_connections())
```

#### 2. Logs
**Definition**: Discrete events with timestamps

```python
import structlog

logger = structlog.get_logger()

def process_order(order_id, user_id):
    logger.info(
        "Processing order",
        order_id=order_id,
        user_id=user_id,
        action="order_processing_started"
    )
    
    try:
        # Process order
        result = process_payment(order_id)
        
        logger.info(
            "Order processed successfully",
            order_id=order_id,
            user_id=user_id,
            payment_id=result.payment_id,
            action="order_processing_completed"
        )
    except Exception as e:
        logger.error(
            "Order processing failed",
            order_id=order_id,
            user_id=user_id,
            error=str(e),
            action="order_processing_failed"
        )
        raise
```

#### 3. Traces
**Definition**: Request journey through distributed system

```python
from opentelemetry import trace
from opentelemetry.exporter.jaeger.thrift import JaegerExporter
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor

# Setup tracing
trace.set_tracer_provider(TracerProvider())
tracer = trace.get_tracer(__name__)

jaeger_exporter = JaegerExporter(
    agent_host_name="jaeger",
    agent_port=6831,
)

span_processor = BatchSpanProcessor(jaeger_exporter)
trace.get_tracer_provider().add_span_processor(span_processor)

# Usage
def get_user_profile(user_id):
    with tracer.start_as_current_span("get_user_profile") as span:
        span.set_attribute("user.id", user_id)
        
        # Get user from database
        with tracer.start_as_current_span("database.query") as db_span:
            db_span.set_attribute("db.statement", "SELECT * FROM users WHERE id = ?")
            user = database.get_user(user_id)
        
        # Get user preferences
        with tracer.start_as_current_span("get_preferences") as pref_span:
            preferences = get_user_preferences(user_id)
        
        return {**user, **preferences}
```

### Monitoring Stack Example

#### Prometheus + Grafana + AlertManager
```yaml
# docker-compose.yml
version: '3.8'
services:
  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
  
  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
  
  alertmanager:
    image: prom/alertmanager
    ports:
      - "9093:9093"
    volumes:
      - ./alertmanager.yml:/etc/alertmanager/alertmanager.yml
```

#### Alert Rules
```yaml
# alert-rules.yml
groups:
- name: system-alerts
  rules:
  - alert: HighErrorRate
    expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.1
    for: 2m
    labels:
      severity: critical
    annotations:
      summary: "High error rate detected"
      description: "Error rate is {{ $value }} errors per second"
  
  - alert: HighMemoryUsage
    expr: (node_memory_MemTotal_bytes - node_memory_MemAvailable_bytes) / node_memory_MemTotal_bytes > 0.9
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: "High memory usage"
      description: "Memory usage is above 90%"
```

---

## 🔍 Deep Dive Questions

### Storage Systems
1. **How do you ensure data consistency in distributed storage?**
2. **What are the trade-offs between object storage and file systems?**
3. **How do you implement efficient data replication?**

### Messaging Systems
1. **How do you handle message ordering in distributed queues?**
2. **What strategies prevent message loss during system failures?**
3. **How do you implement backpressure in messaging systems?**

### Search Systems
1. **How do you handle real-time search index updates?**
2. **What techniques improve search relevance and ranking?**
3. **How do you scale search across multiple data centers?**

### Monitoring
1. **How do you implement distributed tracing effectively?**
2. **What metrics are most important for system health?**
3. **How do you prevent alert fatigue in monitoring systems?**

---

## 🎯 Best Practices

### Storage
- **Partition data** based on access patterns
- **Use appropriate consistency levels** for your use case
- **Implement data lifecycle policies** for cost optimization
- **Monitor storage performance** and capacity

### Messaging
- **Design for idempotent processing** to handle duplicates
- **Use dead letter queues** for failed messages
- **Implement circuit breakers** to prevent cascade failures
- **Monitor queue depth** and processing lag

### Search
- **Optimize index structure** for query patterns
- **Use appropriate analyzers** for different content types
- **Implement search result caching** for popular queries
- **Monitor search performance** and relevance metrics

### Monitoring
- **Implement structured logging** for better searchability
- **Use appropriate sampling rates** for high-volume traces
- **Set up meaningful alerts** based on SLIs/SLOs
- **Create comprehensive dashboards** for different audiences

---

*Next: [Design Patterns](../04-Design-Patterns/) →*