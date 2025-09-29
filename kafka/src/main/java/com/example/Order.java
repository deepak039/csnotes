package com.example;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Order data model for Kafka messages
 */
public class Order {
    @JsonProperty("orderId")
    private String orderId;
    
    @JsonProperty("customerId")
    private String customerId;
    
    @JsonProperty("product")
    private String product;
    
    @JsonProperty("quantity")
    private int quantity;
    
    @JsonProperty("price")
    private double price;
    
    // Default constructor for Jackson
    public Order() {}
    
    public Order(String orderId, String customerId, String product, int quantity, double price) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }
    
    // Getters and setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    @Override
    public String toString() {
        return String.format("Order{orderId='%s', customerId='%s', product='%s', quantity=%d, price=%.2f}",
            orderId, customerId, product, quantity, price);
    }
}