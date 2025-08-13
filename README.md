# Low Level Design (LLD) Notes

## Table of Contents
1. [Programming Principles](#programming-principles)
2. [Design Patterns](#design-patterns)
3. [SOLID Principles](#solid-principles)
4. [System Design Concepts](#system-design-concepts)
5. [Popular Interview Examples](#popular-interview-examples)

## Programming Principles

### 1. SOLID Principles

#### Single Responsibility Principle (SRP)
A class should have only one reason to change.

```java
// Bad Example
class User {
    private String name;
    private String email;
    
    public void save() { /* database logic */ }
    public void sendEmail() { /* email logic */ }
}

// Good Example
class User {
    private String name;
    private String email;
    // getters and setters
}

class UserRepository {
    public void save(User user) { /* database logic */ }
}

class EmailService {
    public void sendEmail(User user) { /* email logic */ }
}
```

#### Open/Closed Principle (OCP)
Classes should be open for extension but closed for modification.

```java
// Bad Example
class AreaCalculator {
    public double calculateArea(Object shape) {
        if (shape instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) shape;
            return rectangle.length * rectangle.width;
        } else if (shape instanceof Circle) {
            Circle circle = (Circle) shape;
            return Math.PI * circle.radius * circle.radius;
        }
        return 0;
    }
}

// Good Example
interface Shape {
    double calculateArea();
}

class Rectangle implements Shape {
    private double length, width;
    
    public double calculateArea() {
        return length * width;
    }
}

class Circle implements Shape {
    private double radius;
    
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
}
```

#### Liskov Substitution Principle (LSP)
Objects of a superclass should be replaceable with objects of its subclasses.

```java
// Good Example
abstract class Bird {
    abstract void move();
}

class FlyingBird extends Bird {
    void move() { fly(); }
    void fly() { /* flying logic */ }
}

class WalkingBird extends Bird {
    void move() { walk(); }
    void walk() { /* walking logic */ }
}
```

#### Interface Segregation Principle (ISP)
Clients should not be forced to depend on interfaces they don't use.

```java
// Bad Example
interface Worker {
    void work();
    void eat();
    void sleep();
}

// Good Example
interface Workable {
    void work();
}

interface Eatable {
    void eat();
}

interface Sleepable {
    void sleep();
}

class Human implements Workable, Eatable, Sleepable {
    public void work() { /* work logic */ }
    public void eat() { /* eat logic */ }
    public void sleep() { /* sleep logic */ }
}
```

#### Dependency Inversion Principle (DIP)
High-level modules should not depend on low-level modules. Both should depend on abstractions.

```java
// Bad Example
class EmailService {
    public void sendEmail(String message) { /* email logic */ }
}

class NotificationService {
    private EmailService emailService = new EmailService();
    
    public void sendNotification(String message) {
        emailService.sendEmail(message);
    }
}

// Good Example
interface MessageService {
    void sendMessage(String message);
}

class EmailService implements MessageService {
    public void sendMessage(String message) { /* email logic */ }
}

class NotificationService {
    private MessageService messageService;
    
    public NotificationService(MessageService messageService) {
        this.messageService = messageService;
    }
    
    public void sendNotification(String message) {
        messageService.sendMessage(message);
    }
}
```

## Design Patterns

### 1. Singleton Pattern
Ensures a class has only one instance and provides global access to it.

```java
public class Singleton {
    private static volatile Singleton instance;
    
    private Singleton() {}
    
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

### 2. Factory Pattern
Creates objects without specifying their exact classes.

```java
interface Animal {
    void makeSound();
}

class Dog implements Animal {
    public void makeSound() { System.out.println("Woof!"); }
}

class Cat implements Animal {
    public void makeSound() { System.out.println("Meow!"); }
}

class AnimalFactory {
    public static Animal createAnimal(String type) {
        switch (type.toLowerCase()) {
            case "dog": return new Dog();
            case "cat": return new Cat();
            default: throw new IllegalArgumentException("Unknown animal type");
        }
    }
}
```

### 3. Observer Pattern
Defines a one-to-many dependency between objects.

```java
import java.util.*;

interface Observer {
    void update(String message);
}

class Subject {
    private List<Observer> observers = new ArrayList<>();
    
    public void addObserver(Observer observer) {
        observers.add(observer);
    }
    
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }
    
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}

class ConcreteObserver implements Observer {
    private String name;
    
    public ConcreteObserver(String name) {
        this.name = name;
    }
    
    public void update(String message) {
        System.out.println(name + " received: " + message);
    }
}
```

### 4. Strategy Pattern
Defines a family of algorithms and makes them interchangeable.

```java
interface PaymentStrategy {
    void pay(double amount);
}

class CreditCardPayment implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " using Credit Card");
    }
}

class PayPalPayment implements PaymentStrategy {
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " using PayPal");
    }
}

class PaymentContext {
    private PaymentStrategy strategy;
    
    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void executePayment(double amount) {
        strategy.pay(amount);
    }
}
```

## System Design Concepts

### 1. Encapsulation
Bundling data and methods that operate on that data within a single unit.

```java
public class BankAccount {
    private double balance;
    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }
    
    public double getBalance() {
        return balance;
    }
}
```

### 2. Inheritance
Mechanism where a new class inherits properties and methods from an existing class.

```java
abstract class Vehicle {
    protected String brand;
    
    public Vehicle(String brand) {
        this.brand = brand;
    }
    
    abstract void start();
    
    public void stop() {
        System.out.println(brand + " vehicle stopped");
    }
}

class Car extends Vehicle {
    public Car(String brand) {
        super(brand);
    }
    
    void start() {
        System.out.println(brand + " car started");
    }
}
```

### 3. Polymorphism
Ability of objects of different types to be treated as instances of the same type.

```java
interface Drawable {
    void draw();
}

class Circle implements Drawable {
    public void draw() {
        System.out.println("Drawing a circle");
    }
}

class Rectangle implements Drawable {
    public void draw() {
        System.out.println("Drawing a rectangle");
    }
}

// Usage
Drawable[] shapes = {new Circle(), new Rectangle()};
for (Drawable shape : shapes) {
    shape.draw(); // Polymorphic behavior
}
```

## Key Concepts for Interviews

### 1. Composition vs Inheritance
- **Inheritance**: "is-a" relationship
- **Composition**: "has-a" relationship

```java
// Inheritance
class Animal {
    void eat() { System.out.println("Eating..."); }
}

class Dog extends Animal {
    void bark() { System.out.println("Barking..."); }
}

// Composition
class Engine {
    void start() { System.out.println("Engine started"); }
}

class Car {
    private Engine engine = new Engine();
    
    void startCar() {
        engine.start();
        System.out.println("Car started");
    }
}
```

### 2. Abstract Classes vs Interfaces

```java
// Abstract Class
abstract class Animal {
    protected String name;
    
    public Animal(String name) {
        this.name = name;
    }
    
    abstract void makeSound();
    
    public void sleep() {
        System.out.println(name + " is sleeping");
    }
}

// Interface
interface Flyable {
    void fly();
    
    default void glide() {
        System.out.println("Gliding...");
    }
}
```

## Popular Interview Examples

See the `interview-examples/` folder for detailed implementations of:
- Parking Lot System
- Library Management System
- ATM Machine
- Elevator System
- Chat Application
- Online Shopping Cart
- Hotel Booking System
- Movie Ticket Booking
- Snake and Ladder Game
- Tic Tac Toe Game

Each example includes:
- Problem statement
- Class diagrams
- Complete Java implementation
- Test cases
- Time and space complexity analysis

## Best Practices

1. **Follow naming conventions**: Use meaningful names for classes, methods, and variables
2. **Keep methods small**: Each method should do one thing well
3. **Use appropriate data structures**: Choose the right collection for the job
4. **Handle exceptions properly**: Use try-catch blocks and custom exceptions
5. **Write clean, readable code**: Use proper indentation and comments
6. **Apply design patterns appropriately**: Don't over-engineer simple solutions
7. **Consider performance**: Think about time and space complexity
8. **Write unit tests**: Ensure your code works as expected

## Resources for Further Learning

- Clean Code by Robert C. Martin
- Design Patterns: Elements of Reusable Object-Oriented Software
- Effective Java by Joshua Bloch
- Head First Design Patterns