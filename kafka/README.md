# Apache Kafka End-to-End Tutorial

## What is Kafka and What Problems Does It Solve?

### Problems Kafka Solves:

1. **Scalable Message Passing**: Traditional message queues don't scale well with high throughput
2. **Real-time Data Streaming**: Need to process data as it arrives, not in batches
3. **System Decoupling**: Services need to communicate without tight coupling
4. **Data Integration**: Multiple systems need to share data reliably
5. **Event Sourcing**: Need to capture and replay events for audit/recovery

### Kafka's Solution:

- **Distributed**: Scales horizontally across multiple servers
- **Fault-tolerant**: Replicates data across brokers
- **High-throughput**: Handles millions of messages per second
- **Persistent**: Stores messages on disk for durability
- **Real-time**: Low-latency message delivery

## Core Concepts

### 1. Producer
Sends messages to Kafka topics

### 2. Consumer
Reads messages from Kafka topics

### 3. Topic
A category/feed where messages are published

### 4. Partition
Topics are split into partitions for scalability

### 5. Broker
Kafka server that stores and serves messages

### 6. Consumer Group
Multiple consumers working together to process messages

## Tutorial Structure

1. [Setup](#setup)
2. [Simple Producer](#simple-producer)
3. [Simple Consumer](#simple-consumer)
4. [Advanced Producer with Partitioning](#advanced-producer)
5. [Consumer Groups](#consumer-groups)
6. [Error Handling](#error-handling)
7. [Real-world Example: Order Processing System](#real-world-example)

## Setup

### Prerequisites
- Java 8+
- Apache Kafka (download from https://kafka.apache.org/downloads)

### Start Kafka Locally
```bash
# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka Server
bin/kafka-server-start.sh config/server.properties

# Create a topic
bin/kafka-topics.sh --create --topic orders --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```

### Dependencies (Maven)
```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>3.5.0</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.2</version>
</dependency>
```

## Next Steps
Run the examples in order:
1. `SimpleProducer.java`
2. `SimpleConsumer.java`
3. `AdvancedProducer.java`
4. `ConsumerGroupExample.java`
5. `OrderProcessingSystem.java`