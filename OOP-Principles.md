# Object-Oriented Programming (OOP) Principles

## Table of Contents
1. [Core OOP Principles](#core-oop-principles)
2. [Encapsulation](#encapsulation)
3. [Inheritance](#inheritance)
4. [Polymorphism](#polymorphism)
5. [Abstraction](#abstraction)
6. [Advanced OOP Concepts](#advanced-oop-concepts)
7. [Best Practices](#best-practices)

## Core OOP Principles

Object-Oriented Programming is based on four fundamental principles that help create modular, reusable, and maintainable code.

## Encapsulation

**Definition**: Bundling data (attributes) and methods (functions) that operate on that data within a single unit (class), and restricting access to internal implementation details.

### Key Benefits
- Data hiding and security
- Code maintainability
- Controlled access to data
- Reduced complexity

### Example: Bank Account System

```java
public class BankAccount {
    // Private fields - data hiding
    private String accountNumber;
    private double balance;
    private String ownerName;
    
    // Constructor
    public BankAccount(String accountNumber, String ownerName, double initialBalance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = initialBalance >= 0 ? initialBalance : 0;
    }
    
    // Public methods - controlled access
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited: $" + amount);
        } else {
            System.out.println("Invalid deposit amount");
        }
    }
    
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrawn: $" + amount);
            return true;
        } else {
            System.out.println("Insufficient funds or invalid amount");
            return false;
        }
    }
    
    // Getter methods - controlled read access
    public double getBalance() {
        return balance;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    // Private helper method - internal implementation
    private boolean validateTransaction(double amount) {
        return amount > 0 && amount <= balance;
    }
}
```

### Access Modifiers in Java

```java
public class AccessModifiersExample {
    public String publicField;        // Accessible everywhere
    protected String protectedField;  // Accessible in package and subclasses
    String packageField;              // Accessible within package (default)
    private String privateField;      // Accessible only within class
    
    public void publicMethod() { }
    protected void protectedMethod() { }
    void packageMethod() { }
    private void privateMethod() { }
}
```

## Inheritance

**Definition**: Mechanism where a new class (child/subclass) inherits properties and methods from an existing class (parent/superclass).

### Types of Inheritance
- Single Inheritance
- Multilevel Inheritance
- Hierarchical Inheritance

### Example: Vehicle Hierarchy

```java
// Base class (Parent)
abstract class Vehicle {
    protected String brand;
    protected String model;
    protected int year;
    protected double price;
    
    public Vehicle(String brand, String model, int year, double price) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
    }
    
    // Common methods for all vehicles
    public void start() {
        System.out.println(brand + " " + model + " is starting...");
    }
    
    public void stop() {
        System.out.println(brand + " " + model + " has stopped.");
    }
    
    // Abstract method - must be implemented by subclasses
    public abstract void displayInfo();
    
    // Getters
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public double getPrice() { return price; }
}

// Derived class (Child)
class Car extends Vehicle {
    private int numberOfDoors;
    private String fuelType;
    
    public Car(String brand, String model, int year, double price, 
               int numberOfDoors, String fuelType) {
        super(brand, model, year, price); // Call parent constructor
        this.numberOfDoors = numberOfDoors;
        this.fuelType = fuelType;
    }
    
    // Override parent method
    @Override
    public void displayInfo() {
        System.out.println("Car: " + brand + " " + model + " (" + year + ")");
        System.out.println("Doors: " + numberOfDoors + ", Fuel: " + fuelType);
        System.out.println("Price: $" + price);
    }
    
    // Car-specific method
    public void openTrunk() {
        System.out.println("Trunk opened");
    }
}

class Motorcycle extends Vehicle {
    private boolean hasSidecar;
    private int engineCC;
    
    public Motorcycle(String brand, String model, int year, double price,
                     boolean hasSidecar, int engineCC) {
        super(brand, model, year, price);
        this.hasSidecar = hasSidecar;
        this.engineCC = engineCC;
    }
    
    @Override
    public void displayInfo() {
        System.out.println("Motorcycle: " + brand + " " + model + " (" + year + ")");
        System.out.println("Engine: " + engineCC + "cc, Sidecar: " + hasSidecar);
        System.out.println("Price: $" + price);
    }
    
    public void wheelie() {
        System.out.println("Performing wheelie!");
    }
}

// Multilevel inheritance
class SportsCar extends Car {
    private int topSpeed;
    private boolean hasTurbo;
    
    public SportsCar(String brand, String model, int year, double price,
                    int numberOfDoors, String fuelType, int topSpeed, boolean hasTurbo) {
        super(brand, model, year, price, numberOfDoors, fuelType);
        this.topSpeed = topSpeed;
        this.hasTurbo = hasTurbo;
    }
    
    @Override
    public void displayInfo() {
        super.displayInfo(); // Call parent's displayInfo
        System.out.println("Top Speed: " + topSpeed + " mph, Turbo: " + hasTurbo);
    }
    
    public void activateSportMode() {
        System.out.println("Sport mode activated!");
    }
}
```

## Polymorphism

**Definition**: Ability of objects of different types to be treated as instances of the same type through a common interface.

### Types of Polymorphism
1. **Compile-time Polymorphism** (Method Overloading)
2. **Runtime Polymorphism** (Method Overriding)

### Example: Shape Drawing System

```java
// Base interface
interface Drawable {
    void draw();
    double calculateArea();
    double calculatePerimeter();
}

// Concrete implementations
class Circle implements Drawable {
    private double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    @Override
    public void draw() {
        System.out.println("Drawing a circle with radius: " + radius);
    }
    
    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
    
    @Override
    public double calculatePerimeter() {
        return 2 * Math.PI * radius;
    }
}

class Rectangle implements Drawable {
    private double width, height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void draw() {
        System.out.println("Drawing a rectangle: " + width + "x" + height);
    }
    
    @Override
    public double calculateArea() {
        return width * height;
    }
    
    @Override
    public double calculatePerimeter() {
        return 2 * (width + height);
    }
}

class Triangle implements Drawable {
    private double side1, side2, side3;
    
    public Triangle(double side1, double side2, double side3) {
        this.side1 = side1;
        this.side2 = side2;
        this.side3 = side3;
    }
    
    @Override
    public void draw() {
        System.out.println("Drawing a triangle");
    }
    
    @Override
    public double calculateArea() {
        double s = (side1 + side2 + side3) / 2;
        return Math.sqrt(s * (s - side1) * (s - side2) * (s - side3));
    }
    
    @Override
    public double calculatePerimeter() {
        return side1 + side2 + side3;
    }
}

// Polymorphism in action
class ShapeProcessor {
    public void processShapes(Drawable[] shapes) {
        for (Drawable shape : shapes) {
            shape.draw(); // Runtime polymorphism
            System.out.println("Area: " + shape.calculateArea());
            System.out.println("Perimeter: " + shape.calculatePerimeter());
            System.out.println("---");
        }
    }
    
    public double getTotalArea(Drawable[] shapes) {
        double totalArea = 0;
        for (Drawable shape : shapes) {
            totalArea += shape.calculateArea();
        }
        return totalArea;
    }
}
```

### Method Overloading (Compile-time Polymorphism)

```java
class Calculator {
    // Method overloading - same method name, different parameters
    public int add(int a, int b) {
        return a + b;
    }
    
    public double add(double a, double b) {
        return a + b;
    }
    
    public int add(int a, int b, int c) {
        return a + b + c;
    }
    
    public String add(String a, String b) {
        return a + b;
    }
}
```

## Abstraction

**Definition**: Hiding complex implementation details while showing only essential features of an object.

### Abstract Classes vs Interfaces

```java
// Abstract class - can have both abstract and concrete methods
abstract class Animal {
    protected String name;
    protected int age;
    
    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // Concrete method
    public void sleep() {
        System.out.println(name + " is sleeping");
    }
    
    // Abstract methods - must be implemented by subclasses
    public abstract void makeSound();
    public abstract void move();
    
    // Getters
    public String getName() { return name; }
    public int getAge() { return age; }
}

// Interface - only abstract methods (before Java 8)
interface Flyable {
    void fly();
    void land();
    
    // Default method (Java 8+)
    default void glide() {
        System.out.println("Gliding through the air");
    }
    
    // Static method (Java 8+)
    static void checkWeatherConditions() {
        System.out.println("Checking weather for flying");
    }
}

// Concrete implementation
class Bird extends Animal implements Flyable {
    private String species;
    
    public Bird(String name, int age, String species) {
        super(name, age);
        this.species = species;
    }
    
    @Override
    public void makeSound() {
        System.out.println(name + " chirps");
    }
    
    @Override
    public void move() {
        System.out.println(name + " is walking or flying");
    }
    
    @Override
    public void fly() {
        System.out.println(name + " is flying high");
    }
    
    @Override
    public void land() {
        System.out.println(name + " has landed safely");
    }
}
```

## Advanced OOP Concepts

### 1. Composition vs Inheritance

```java
// Composition - "HAS-A" relationship
class Engine {
    private String type;
    private int horsepower;
    
    public Engine(String type, int horsepower) {
        this.type = type;
        this.horsepower = horsepower;
    }
    
    public void start() {
        System.out.println(type + " engine started (" + horsepower + " HP)");
    }
    
    public void stop() {
        System.out.println("Engine stopped");
    }
}

class GPS {
    public void navigate(String destination) {
        System.out.println("Navigating to: " + destination);
    }
}

class Car {
    private Engine engine;  // Composition
    private GPS gps;        // Composition
    private String model;
    
    public Car(String model, Engine engine, GPS gps) {
        this.model = model;
        this.engine = engine;
        this.gps = gps;
    }
    
    public void start() {
        engine.start();
        System.out.println(model + " is ready to drive");
    }
    
    public void navigateTo(String destination) {
        gps.navigate(destination);
    }
}
```

### 2. Association, Aggregation, and Composition

```java
// Association - "USES-A" relationship
class Student {
    private String name;
    
    public Student(String name) {
        this.name = name;
    }
    
    public void attendClass(Course course) {
        System.out.println(name + " is attending " + course.getName());
    }
}

class Course {
    private String name;
    
    public Course(String name) {
        this.name = name;
    }
    
    public String getName() { return name; }
}

// Aggregation - "HAS-A" relationship (weak)
class Department {
    private String name;
    private List<Student> students;
    
    public Department(String name) {
        this.name = name;
        this.students = new ArrayList<>();
    }
    
    public void addStudent(Student student) {
        students.add(student);
    }
    
    // Students can exist without department
}

// Composition - "PART-OF" relationship (strong)
class House {
    private List<Room> rooms;
    
    public House() {
        rooms = new ArrayList<>();
        // Rooms are created with house
        rooms.add(new Room("Living Room"));
        rooms.add(new Room("Bedroom"));
        rooms.add(new Room("Kitchen"));
    }
    
    // Rooms cannot exist without house
    class Room {
        private String name;
        
        public Room(String name) {
            this.name = name;
        }
    }
}
```

### 3. Method Overriding Rules

```java
class Parent {
    public void display() {
        System.out.println("Parent display");
    }
    
    protected void show() {
        System.out.println("Parent show");
    }
    
    // Cannot be overridden
    final void finalMethod() {
        System.out.println("Final method");
    }
    
    // Cannot be overridden
    static void staticMethod() {
        System.out.println("Static method");
    }
}

class Child extends Parent {
    @Override
    public void display() {  // Can increase visibility
        System.out.println("Child display");
    }
    
    @Override
    public void show() {     // protected to public - allowed
        System.out.println("Child show");
    }
    
    // This would cause compilation error:
    // @Override
    // private void display() { } // Cannot reduce visibility
}
```

## Best Practices

### 1. Favor Composition over Inheritance

```java
// Instead of deep inheritance hierarchies
class FlyingCar extends Car {
    // Complex inheritance
}

// Use composition
class Vehicle {
    private MovementCapability movement;
    private Engine engine;
    
    public Vehicle(MovementCapability movement, Engine engine) {
        this.movement = movement;
        this.engine = engine;
    }
    
    public void move() {
        movement.move();
    }
}

interface MovementCapability {
    void move();
}

class FlyingCapability implements MovementCapability {
    public void move() {
        System.out.println("Flying through the air");
    }
}

class DrivingCapability implements MovementCapability {
    public void move() {
        System.out.println("Driving on road");
    }
}
```

### 2. Program to Interfaces

```java
// Good practice
List<String> list = new ArrayList<>();
Map<String, Integer> map = new HashMap<>();

// Instead of
ArrayList<String> list = new ArrayList<>();
HashMap<String, Integer> map = new HashMap<>();
```

### 3. Use Proper Encapsulation

```java
public class Person {
    private String name;
    private int age;
    private List<String> hobbies;
    
    public Person(String name, int age) {
        this.name = name;
        setAge(age);  // Use setter for validation
        this.hobbies = new ArrayList<>();
    }
    
    public void setAge(int age) {
        if (age >= 0 && age <= 150) {
            this.age = age;
        } else {
            throw new IllegalArgumentException("Invalid age");
        }
    }
    
    // Return defensive copy
    public List<String> getHobbies() {
        return new ArrayList<>(hobbies);
    }
    
    public void addHobby(String hobby) {
        if (hobby != null && !hobby.trim().isEmpty()) {
            hobbies.add(hobby);
        }
    }
}
```

### 4. Follow SOLID Principles

```java
// Single Responsibility
class EmailValidator {
    public boolean isValid(String email) {
        return email.contains("@") && email.contains(".");
    }
}

class EmailSender {
    public void sendEmail(String email, String message) {
        // Send email logic
    }
}

// Open/Closed
interface NotificationSender {
    void send(String message);
}

class EmailNotification implements NotificationSender {
    public void send(String message) {
        // Email implementation
    }
}

class SMSNotification implements NotificationSender {
    public void send(String message) {
        // SMS implementation
    }
}
```

## Key Takeaways

1. **Encapsulation**: Hide internal details, provide controlled access
2. **Inheritance**: Reuse code through "is-a" relationships
3. **Polymorphism**: Write flexible code that works with multiple types
4. **Abstraction**: Focus on what an object does, not how it does it
5. **Composition**: Build complex objects from simpler ones
6. **Interface Programming**: Depend on abstractions, not concrete classes
7. **SOLID Principles**: Create maintainable and extensible code