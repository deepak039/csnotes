# Kafka Core Concepts Deep Dive

## 1. Problems Kafka Solves

### Traditional System Challenges:
- **Point-to-point integration**: N systems need N*(N-1)/2 connections
- **Data silos**: Each system has its own data format and protocol
- **Scalability bottlenecks**: Single points of failure
- **Real-time processing**: Batch processing doesn't meet modern needs
- **Data consistency**: Hard to maintain across multiple systems

### Kafka's Solutions:
- **Decoupling**: Publishers don't know about subscribers
- **Scalability**: Horizontal scaling across multiple brokers
- **Durability**: Messages persisted to disk with replication
- **Performance**: High throughput (millions of messages/second)
- **Fault tolerance**: Automatic failover and recovery

## 2. Key Concepts Explained

### Topics and Partitions
```
Topic: "orders"
├── Partition 0: [msg1] [msg2] [msg3] ...
├── Partition 1: [msg4] [msg5] [msg6] ...
└── Partition 2: [msg7] [msg8] [msg9] ...
```

**Why Partitions?**
- **Parallelism**: Multiple consumers can read different partitions
- **Scalability**: Distribute load across multiple brokers
- **Ordering**: Messages within a partition are ordered

### Consumer Groups
```
Consumer Group: "order-processors"
├── Consumer 1 → Partition 0, 1
└── Consumer 2 → Partition 2

If Consumer 1 fails:
└── Consumer 2 → Partition 0, 1, 2 (rebalancing)
```

**Benefits:**
- **Load balancing**: Work distributed among consumers
- **Fault tolerance**: Automatic rebalancing on failures
- **Scalability**: Add/remove consumers dynamically

### Message Ordering and Partitioning Strategy

#### 1. No Key (Round-robin)
```java
ProducerRecord<String, String> record = 
    new ProducerRecord<>("orders", null, "message");
// Messages distributed evenly across partitions
```

#### 2. With Key (Hash-based)
```java
ProducerRecord<String, String> record = 
    new ProducerRecord<>("orders", "customer-123", "message");
// All messages with same key go to same partition
// Guarantees ordering for messages from same customer
```

#### 3. Custom Partitioner
```java
public class CustomerPartitioner implements Partitioner {
    public int partition(String topic, Object key, byte[] keyBytes, 
                        Object value, byte[] valueBytes, Cluster cluster) {
        // Custom logic: VIP customers to partition 0
        if (key.toString().startsWith("vip-")) {
            return 0;
        }
        // Regular customers distributed across other partitions
        return (key.hashCode() % (cluster.partitionCountForTopic(topic) - 1)) + 1;
    }
}
```

## 3. Delivery Semantics

### At Most Once (acks=0)
- Producer doesn't wait for acknowledgment
- **Risk**: Message loss if broker fails
- **Use case**: Metrics, logs where some loss is acceptable

### At Least Once (acks=1, default)
- Producer waits for leader acknowledgment
- **Risk**: Duplicate messages if producer retries
- **Use case**: Most common scenario

### Exactly Once (acks=all + idempotence)
```java
props.put(ProducerConfig.ACKS_CONFIG, "all");
props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
```
- **Guarantee**: No duplicates, no loss
- **Use case**: Financial transactions, critical data

## 4. Performance Tuning

### Producer Optimization
```java
// Batching for throughput
props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
props.put(ProducerConfig.LINGER_MS_CONFIG, 5);

// Compression
props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

// Memory management
props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
```

### Consumer Optimization
```java
// Fetch size for throughput
props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024);
props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);

// Processing batch size
props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
```

## 5. Error Handling Patterns

### Producer Error Handling
```java
producer.send(record, (metadata, exception) -> {
    if (exception != null) {
        if (exception instanceof RetriableException) {
            // Kafka will retry automatically
            logger.warn("Retriable error: " + exception.getMessage());
        } else {
            // Non-retriable error - handle manually
            logger.error("Failed to send message: " + exception.getMessage());
            // Send to dead letter queue or alert
        }
    }
});
```

### Consumer Error Handling
```java
try {
    processMessage(record);
    consumer.commitSync(); // Commit only after successful processing
} catch (Exception e) {
    logger.error("Processing failed for message: " + record.value(), e);
    
    // Options:
    // 1. Skip message and continue
    // 2. Retry with backoff
    // 3. Send to dead letter topic
    // 4. Stop processing and alert
}
```

## 6. Monitoring and Observability

### Key Metrics to Monitor
- **Producer**: throughput, error rate, batch size
- **Consumer**: lag, throughput, processing time
- **Broker**: disk usage, network I/O, partition count
- **Topics**: message rate, size, retention

### JMX Metrics Examples
```java
// Consumer lag
kafka.consumer:type=consumer-fetch-manager-metrics,client-id="{client-id}",topic="{topic}",partition="{partition}"

// Producer throughput
kafka.producer:type=producer-metrics,client-id="{client-id}"
```

## 7. Best Practices

### Topic Design
- **Naming**: Use consistent naming convention (e.g., `domain.entity.action`)
- **Partitions**: Start with 2-3x number of consumers
- **Retention**: Set based on business requirements and storage capacity

### Schema Evolution
```java
// Use schema registry for backward compatibility
// Avro, JSON Schema, or Protobuf
{
  "type": "record",
  "name": "Order",
  "fields": [
    {"name": "orderId", "type": "string"},
    {"name": "customerId", "type": "string"},
    {"name": "amount", "type": "double", "default": 0.0} // New field with default
  ]
}
```

### Security
```java
// SSL/SASL configuration
props.put("security.protocol", "SASL_SSL");
props.put("sasl.mechanism", "PLAIN");
props.put("sasl.jaas.config", 
    "org.apache.kafka.common.security.plain.PlainLoginModule required " +
    "username=\"user\" password=\"password\";");
```

## 8. Common Anti-patterns to Avoid

1. **Too many partitions**: Increases metadata overhead
2. **No key strategy**: Loses ordering guarantees
3. **Synchronous processing**: Blocks consumer thread
4. **Large messages**: Use external storage for large payloads
5. **No error handling**: Silent failures are dangerous
6. **Hardcoded configurations**: Use configuration files/environment variables