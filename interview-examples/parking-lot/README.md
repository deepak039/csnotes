# Parking Lot System

## Problem Statement

Design a parking lot system that can:
- Handle different types of vehicles (Car, Motorcycle, Truck)
- Have different parking spot sizes (Compact, Large, Handicapped)
- Track available spots
- Calculate parking fees
- Handle entry and exit of vehicles

## Requirements

### Functional Requirements
1. Multiple levels in parking lot
2. Different spot sizes for different vehicle types
3. Track which spots are occupied
4. Calculate parking fee based on time
5. Display available spots count
6. Handle payment processing

### Non-Functional Requirements
1. System should be scalable
2. Low latency for spot assignment
3. Thread-safe operations
4. Extensible for new vehicle types

## Classes and Relationships

### Core Classes
- `ParkingLot` - Main system controller
- `Level` - Represents a parking level
- `ParkingSpot` - Individual parking spot
- `Vehicle` - Base class for all vehicles
- `Ticket` - Parking ticket for vehicles
- `Payment` - Handle payment processing

### Vehicle Types
- `Car` - Regular car
- `Motorcycle` - Two-wheeler
- `Truck` - Large vehicle

### Spot Types
- `CompactSpot` - For motorcycles and small cars
- `LargeSpot` - For cars and trucks
- `HandicappedSpot` - Accessible spots

## Design Patterns Used

1. **Factory Pattern** - For creating different vehicle types
2. **Strategy Pattern** - For different pricing strategies
3. **Observer Pattern** - For spot availability notifications
4. **Singleton Pattern** - For parking lot instance

## Implementation Details

### Time Complexity
- Park vehicle: O(n) where n is number of spots per level
- Remove vehicle: O(1) with proper indexing
- Check availability: O(1)

### Space Complexity
- O(n) where n is total number of parking spots

## Key Features

1. **Multi-level Support**: Can handle multiple parking levels
2. **Flexible Pricing**: Different rates for different vehicle types
3. **Real-time Availability**: Track available spots in real-time
4. **Payment Integration**: Support multiple payment methods
5. **Extensible Design**: Easy to add new vehicle or spot types

## Usage Example

```java
ParkingLot parkingLot = ParkingLot.getInstance();
Vehicle car = new Car("ABC123");
Ticket ticket = parkingLot.parkVehicle(car);

// Later when leaving
double fee = parkingLot.calculateFee(ticket);
parkingLot.removeVehicle(ticket);
```

## Test Cases

1. Park different types of vehicles
2. Handle full parking lot scenario
3. Calculate correct parking fees
4. Test concurrent parking operations
5. Validate spot assignment logic