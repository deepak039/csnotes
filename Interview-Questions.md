# Popular LLD and OOP Interview Questions

## Table of Contents
1. [OOP Fundamentals](#oop-fundamentals)
2. [SOLID Principles](#solid-principles)
3. [Design Patterns](#design-patterns)
4. [System Design Questions](#system-design-questions)
5. [Coding Problems](#coding-problems)
6. [Scenario-Based Questions](#scenario-based-questions)

## OOP Fundamentals

### Basic Concepts

**Q1: What are the four pillars of OOP? Explain each with examples.**
- Encapsulation, Inheritance, Polymorphism, Abstraction
- Provide real-world examples for each

**Q2: What is the difference between abstraction and encapsulation?**
- Abstraction: Hiding complexity, showing only essential features
- Encapsulation: Bundling data and methods, controlling access

**Q3: Explain method overloading vs method overriding with examples.**
```java
// Overloading - compile time
class Calculator {
    int add(int a, int b) { return a + b; }
    double add(double a, double b) { return a + b; }
}

// Overriding - runtime
class Animal {
    void sound() { System.out.println("Animal sound"); }
}
class Dog extends Animal {
    void sound() { System.out.println("Bark"); }
}
```

**Q4: What is polymorphism? Types of polymorphism?**
- Runtime polymorphism (method overriding)
- Compile-time polymorphism (method overloading)

**Q5: Difference between abstract class and interface?**
| Abstract Class | Interface |
|----------------|-----------|
| Can have concrete methods | Only abstract methods (before Java 8) |
| Can have constructors | Cannot have constructors |
| Single inheritance | Multiple inheritance |
| Can have instance variables | Only constants |

### Advanced OOP Concepts

**Q6: Composition vs Inheritance - when to use which?**
- Inheritance: "is-a" relationship
- Composition: "has-a" relationship
- Favor composition over inheritance

**Q7: What is diamond problem? How does Java solve it?**
- Multiple inheritance ambiguity
- Java uses interfaces with default methods

**Q8: Explain association, aggregation, and composition.**
```java
// Association - uses-a
class Student {
    void attendClass(Course course) { }
}

// Aggregation - has-a (weak)
class Department {
    List<Student> students; // Students can exist without department
}

// Composition - part-of (strong)
class House {
    List<Room> rooms; // Rooms cannot exist without house
}
```

**Q9: What are access modifiers? Explain visibility.**
- public, protected, default, private
- Visibility in inheritance and packages

**Q10: Can you override static methods? Why or why not?**
- No, static methods belong to class, not instance
- Method hiding vs method overriding

## SOLID Principles

**Q11: Explain Single Responsibility Principle with example.**
```java
// Bad
class User {
    void save() { } // Database responsibility
    void sendEmail() { } // Email responsibility
}

// Good
class User { }
class UserRepository { void save(User user) { } }
class EmailService { void sendEmail(User user) { } }
```

**Q12: What is Open/Closed Principle? Provide example.**
- Open for extension, closed for modification
- Use interfaces and inheritance

**Q13: Explain Liskov Substitution Principle.**
- Subtypes must be substitutable for base types
- Rectangle-Square problem example

**Q14: What is Interface Segregation Principle?**
- Clients shouldn't depend on interfaces they don't use
- Split large interfaces into smaller ones

**Q15: Explain Dependency Inversion Principle.**
- High-level modules shouldn't depend on low-level modules
- Both should depend on abstractions

## Design Patterns

### Creational Patterns

**Q16: Implement Singleton pattern. What are different ways?**
```java
// Thread-safe singleton
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

**Q17: When would you use Factory pattern?**
- When object creation logic is complex
- When you need to create objects without specifying exact classes

**Q18: Difference between Factory and Abstract Factory pattern?**
- Factory: Creates objects of one type
- Abstract Factory: Creates families of related objects

**Q19: Explain Builder pattern. When to use?**
- For objects with many optional parameters
- Immutable objects with complex construction

### Behavioral Patterns

**Q20: Implement Observer pattern.**
```java
interface Observer {
    void update(String message);
}

class Subject {
    private List<Observer> observers = new ArrayList<>();
    
    public void addObserver(Observer observer) {
        observers.add(observer);
    }
    
    public void notifyObservers(String message) {
        observers.forEach(observer -> observer.update(message));
    }
}
```

**Q21: What is Strategy pattern? Provide example.**
- Define family of algorithms, make them interchangeable
- Payment processing example

**Q22: Explain Command pattern.**
- Encapsulate requests as objects
- Undo/Redo functionality

**Q23: What is State pattern?**
- Object behavior changes based on internal state
- State machine implementation

### Structural Patterns

**Q24: Explain Decorator pattern.**
- Add behavior to objects dynamically
- Coffee shop example with add-ons

**Q25: What is Adapter pattern?**
- Make incompatible interfaces work together
- Legacy system integration

**Q26: Facade pattern vs Proxy pattern?**
- Facade: Simplifies complex subsystem
- Proxy: Controls access to another object

## System Design Questions

**Q27: Design a parking lot system.**
- Classes: ParkingLot, Level, ParkingSpot, Vehicle, Ticket
- Handle different vehicle types and spot sizes

**Q28: Design a library management system.**
- Classes: Library, Book, Member, BookIssue, Fine
- Handle borrowing, returning, fines

**Q29: Design an ATM machine.**
- Classes: ATM, Card, Account, Transaction, CashDispenser
- Handle authentication, transactions, cash management

**Q30: Design a chess game.**
- Classes: Board, Piece, Player, Game, Move
- Handle piece movements, game rules

**Q31: Design a hotel booking system.**
- Classes: Hotel, Room, Booking, Customer, Payment
- Handle reservations, availability

**Q32: Design a social media feed.**
- Classes: User, Post, Feed, Timeline, Notification
- Handle posts, followers, feed generation

## Coding Problems

**Q33: Implement a generic Stack class.**
```java
public class Stack<T> {
    private List<T> items = new ArrayList<>();
    
    public void push(T item) {
        items.add(item);
    }
    
    public T pop() {
        if (isEmpty()) throw new EmptyStackException();
        return items.remove(items.size() - 1);
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
}
```

**Q34: Design a thread-safe counter.**
```java
public class Counter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
    
    public synchronized int getCount() {
        return count;
    }
}
```

**Q35: Implement a LRU Cache.**
```java
public class LRUCache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> map;
    private final Node<K, V> head, tail;
    
    // Implementation with doubly linked list
}
```

**Q36: Design a rate limiter.**
- Token bucket algorithm
- Sliding window approach

**Q37: Implement a connection pool.**
```java
public class ConnectionPool {
    private Queue<Connection> pool;
    private int maxSize;
    
    public synchronized Connection getConnection() {
        // Return connection from pool
    }
    
    public synchronized void releaseConnection(Connection conn) {
        // Return connection to pool
    }
}
```

## Scenario-Based Questions

**Q38: How would you design a system to handle 1 million concurrent users?**
- Load balancing, caching, database sharding
- Microservices architecture

**Q39: Your application is running slow. How would you optimize it?**
- Profiling, caching, database optimization
- Algorithm improvements

**Q40: How do you ensure thread safety in your design?**
- Synchronization, locks, concurrent collections
- Immutable objects

**Q41: How would you handle system failures?**
- Circuit breaker pattern
- Retry mechanisms, fallback strategies

**Q42: Design a system that can scale horizontally.**
- Stateless services
- Database partitioning
- Load balancing

**Q43: How do you maintain data consistency in distributed systems?**
- ACID properties
- Eventual consistency
- Distributed transactions

## Advanced Questions

**Q44: Explain CAP theorem in context of system design.**
- Consistency, Availability, Partition tolerance
- Trade-offs in distributed systems

**Q45: How would you implement a distributed cache?**
- Consistent hashing
- Replication strategies

**Q46: Design a URL shortener like bit.ly.**
- Base62 encoding
- Database design
- Caching strategy

**Q47: How do you handle versioning in APIs?**
- URL versioning
- Header versioning
- Backward compatibility

**Q48: Explain microservices vs monolithic architecture.**
- Pros and cons of each approach
- When to use which

**Q49: How do you implement authentication and authorization?**
- JWT tokens
- OAuth 2.0
- Role-based access control

**Q50: Design a notification system.**
- Push notifications
- Email/SMS integration
- Priority handling

## Tips for Interview Success

### Preparation Strategy
1. **Understand fundamentals** - Master OOP principles
2. **Practice coding** - Implement design patterns
3. **System thinking** - Think about scalability, maintainability
4. **Ask questions** - Clarify requirements before designing
5. **Start simple** - Begin with basic design, then add complexity
6. **Explain trade-offs** - Discuss pros and cons of your decisions

### During the Interview
1. **Listen carefully** - Understand the problem completely
2. **Think out loud** - Explain your thought process
3. **Start with high-level design** - Then dive into details
4. **Consider edge cases** - Handle error scenarios
5. **Be open to feedback** - Adapt your design based on interviewer input
6. **Write clean code** - Follow naming conventions and best practices

### Common Mistakes to Avoid
1. **Over-engineering** - Don't make it too complex initially
2. **Ignoring requirements** - Make sure you address all requirements
3. **Poor communication** - Explain your design clearly
4. **Not considering scalability** - Think about future growth
5. **Forgetting error handling** - Always consider failure scenarios
6. **Not testing your design** - Walk through examples to validate

### Key Areas to Focus
1. **Design Patterns** - Know when and how to apply them
2. **SOLID Principles** - Understand and apply in designs
3. **System Architecture** - Think about components and their interactions
4. **Database Design** - Understand normalization, indexing
5. **Caching Strategies** - Know different caching patterns
6. **Concurrency** - Handle multi-threading scenarios
7. **Performance** - Consider time and space complexity