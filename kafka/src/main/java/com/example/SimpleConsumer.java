package com.example;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * Simple Kafka Consumer Example
 * Demonstrates basic message consumption from a Kafka topic
 */
public class SimpleConsumer {
    
    public static void main(String[] args) {
        // Consumer configuration
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "simple-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Read from beginning
        
        // Create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        
        try {
            // Subscribe to topic
            consumer.subscribe(Collections.singletonList("orders"));
            System.out.println("Consumer started. Waiting for messages...");
            
            // Poll for messages
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Received: key=%s, value=%s, partition=%d, offset=%d%n",
                        record.key(), record.value(), record.partition(), record.offset());
                }
                
                // Break after processing some messages (for demo purposes)
                if (!records.isEmpty()) {
                    System.out.println("Processed " + records.count() + " messages");
                    break;
                }
            }
            
        } finally {
            consumer.close();
        }
    }
}