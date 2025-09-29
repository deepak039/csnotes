package com.example;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * Consumer Group Example
 * Demonstrates how multiple consumers work together to process messages
 * Run multiple instances of this class to see load balancing
 */
public class ConsumerGroupExample {
    
    public static void main(String[] args) {
        String consumerId = args.length > 0 ? args[0] : "consumer-1";
        
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-processing-group");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, consumerId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // Manual commit for reliability
        
        // Configure the deserializer for Order objects
        KafkaConsumer<String, Order> consumer = new KafkaConsumer<>(props);
        
        try {
            consumer.subscribe(Collections.singletonList("orders"));
            System.out.println(consumerId + " started and joined consumer group");
            
            while (true) {
                ConsumerRecords<String, Order> records = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, Order> record : records) {
                    Order order = record.value();
                    
                    // Simulate order processing
                    processOrder(consumerId, order, record.partition());
                    
                    // Simulate processing time
                    Thread.sleep(500);
                }
                
                // Commit offsets after processing batch
                if (!records.isEmpty()) {
                    consumer.commitSync();
                    System.out.println(consumerId + " committed offsets for " + records.count() + " messages");
                }
            }
            
        } catch (Exception e) {
            System.err.println(consumerId + " error: " + e.getMessage());
        } finally {
            consumer.close();
        }
    }
    
    private static void processOrder(String consumerId, Order order, int partition) {
        System.out.printf("[%s] Processing order from partition %d: %s%n", 
            consumerId, partition, order);
        
        // Simulate business logic
        double total = order.getQuantity() * order.getPrice();
        System.out.printf("[%s] Order total: $%.2f%n", consumerId, total);
    }
}