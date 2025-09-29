package com.example;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * Simple Kafka Producer Example
 * Demonstrates basic message publishing to a Kafka topic
 */
public class SimpleProducer {
    
    public static void main(String[] args) {
        // Producer configuration
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        // Create producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        
        try {
            // Send 10 messages
            for (int i = 0; i < 10; i++) {
                String key = "user-" + i;
                String value = "Hello Kafka! Message " + i;
                
                ProducerRecord<String, String> record = 
                    new ProducerRecord<>("orders", key, value);
                
                // Asynchronous send with callback
                producer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        if (exception == null) {
                            System.out.printf("Sent: key=%s, value=%s, partition=%d, offset=%d%n",
                                key, value, metadata.partition(), metadata.offset());
                        } else {
                            System.err.println("Error sending message: " + exception.getMessage());
                        }
                    }
                });
            }
            
            // Wait for all messages to be sent
            producer.flush();
            System.out.println("All messages sent successfully!");
            
        } finally {
            producer.close();
        }
    }
}