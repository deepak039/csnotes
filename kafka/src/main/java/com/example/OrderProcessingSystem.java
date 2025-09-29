package com.example;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Real-world Order Processing System
 * Demonstrates a complete event-driven architecture with Kafka
 * 
 * Flow: Orders -> Validation -> Payment -> Fulfillment -> Notifications
 */
public class OrderProcessingSystem {
    
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        // Start different processing stages
        executor.submit(() -> new OrderValidator().start());
        executor.submit(() -> new PaymentProcessor().start());
        executor.submit(() -> new FulfillmentService().start());
        executor.submit(() -> new NotificationService().start());
        
        // Keep main thread alive
        try {
            Thread.sleep(60000); // Run for 1 minute
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        executor.shutdown();
    }
    
    // Order Validation Service
    static class OrderValidator {
        private KafkaConsumer<String, Order> consumer;
        private KafkaProducer<String, Order> producer;
        
        public OrderValidator() {
            this.consumer = createConsumer("order-validator", "orders");
            this.producer = createProducer();
        }
        
        public void start() {
            System.out.println("Order Validator started");
            
            while (true) {
                ConsumerRecords<String, Order> records = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, Order> record : records) {
                    Order order = record.value();
                    
                    // Validate order
                    if (validateOrder(order)) {
                        System.out.println("✓ Order validated: " + order.getOrderId());
                        
                        // Send to payment processing
                        ProducerRecord<String, Order> paymentRecord = 
                            new ProducerRecord<>("payment-requests", order.getCustomerId(), order);
                        producer.send(paymentRecord);
                    } else {
                        System.out.println("✗ Order validation failed: " + order.getOrderId());
                    }
                }
                
                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }
        }
        
        private boolean validateOrder(Order order) {
            // Simple validation logic
            return order.getQuantity() > 0 && order.getPrice() > 0;
        }
    }
    
    // Payment Processing Service
    static class PaymentProcessor {
        private KafkaConsumer<String, Order> consumer;
        private KafkaProducer<String, Order> producer;
        
        public PaymentProcessor() {
            this.consumer = createConsumer("payment-processor", "payment-requests");
            this.producer = createProducer();
        }
        
        public void start() {
            System.out.println("Payment Processor started");
            
            while (true) {
                ConsumerRecords<String, Order> records = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, Order> record : records) {
                    Order order = record.value();
                    
                    // Process payment
                    if (processPayment(order)) {
                        System.out.println("💳 Payment processed: " + order.getOrderId());
                        
                        // Send to fulfillment
                        ProducerRecord<String, Order> fulfillmentRecord = 
                            new ProducerRecord<>("fulfillment-requests", order.getCustomerId(), order);
                        producer.send(fulfillmentRecord);
                    } else {
                        System.out.println("💳 Payment failed: " + order.getOrderId());
                    }
                }
                
                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }
        }
        
        private boolean processPayment(Order order) {
            // Simulate payment processing (90% success rate)
            return Math.random() > 0.1;
        }
    }
    
    // Fulfillment Service
    static class FulfillmentService {
        private KafkaConsumer<String, Order> consumer;
        private KafkaProducer<String, String> producer;
        
        public FulfillmentService() {
            this.consumer = createConsumer("fulfillment-service", "fulfillment-requests");
            this.producer = createStringProducer();
        }
        
        public void start() {
            System.out.println("Fulfillment Service started");
            
            while (true) {
                ConsumerRecords<String, Order> records = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, Order> record : records) {
                    Order order = record.value();
                    
                    // Fulfill order
                    System.out.println("📦 Order fulfilled: " + order.getOrderId());
                    
                    // Send notification
                    String notification = String.format("Order %s has been shipped to customer %s", 
                        order.getOrderId(), order.getCustomerId());
                    
                    ProducerRecord<String, String> notificationRecord = 
                        new ProducerRecord<>("notifications", order.getCustomerId(), notification);
                    producer.send(notificationRecord);
                }
                
                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }
        }
    }
    
    // Notification Service
    static class NotificationService {
        private KafkaConsumer<String, String> consumer;
        
        public NotificationService() {
            this.consumer = createStringConsumer("notification-service", "notifications");
        }
        
        public void start() {
            System.out.println("Notification Service started");
            
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, String> record : records) {
                    String notification = record.value();
                    String customerId = record.key();
                    
                    // Send notification (email, SMS, push, etc.)
                    System.out.println("📧 Notification sent to " + customerId + ": " + notification);
                }
                
                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }
        }
    }
    
    // Helper methods to create consumers and producers
    private static KafkaConsumer<String, Order> createConsumer(String groupId, String topic) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        
        KafkaConsumer<String, Order> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
        return consumer;
    }
    
    private static KafkaConsumer<String, String> createStringConsumer(String groupId, String topic) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
        return consumer;
    }
    
    private static KafkaProducer<String, Order> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        
        return new KafkaProducer<>(props);
    }
    
    private static KafkaProducer<String, String> createStringProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        
        return new KafkaProducer<>(props);
    }
}