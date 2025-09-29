package com.example;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.Random;

/**
 * Advanced Kafka Producer with JSON serialization and custom partitioning
 * Demonstrates real-world usage patterns
 */
public class AdvancedProducer {
    
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        
        // Performance and reliability settings
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // Wait for all replicas
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        
        KafkaProducer<String, Order> producer = new KafkaProducer<>(props);
        Random random = new Random();
        
        try {
            // Send sample orders
            String[] products = {"Laptop", "Phone", "Tablet", "Headphones", "Mouse"};
            
            for (int i = 0; i < 20; i++) {
                String orderId = "order-" + (1000 + i);
                String customerId = "customer-" + (i % 5); // 5 different customers
                String product = products[random.nextInt(products.length)];
                int quantity = random.nextInt(5) + 1;
                double price = 100 + random.nextDouble() * 900;
                
                Order order = new Order(orderId, customerId, product, quantity, price);
                
                // Use customerId as key for partitioning (orders from same customer go to same partition)
                ProducerRecord<String, Order> record = 
                    new ProducerRecord<>("orders", customerId, order);
                
                producer.send(record, (metadata, exception) -> {
                    if (exception == null) {
                        System.out.printf("Order sent: %s -> partition=%d, offset=%d%n",
                            order.getOrderId(), metadata.partition(), metadata.offset());
                    } else {
                        System.err.println("Failed to send order: " + exception.getMessage());
                    }
                });
                
                // Small delay to see the flow
                Thread.sleep(100);
            }
            
            producer.flush();
            System.out.println("All orders sent!");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            producer.close();
        }
    }
}