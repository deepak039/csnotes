import java.util.*;
import java.time.LocalDateTime;
import java.time.Duration;

// Enums
enum VehicleType {
    MOTORCYCLE, CAR, TRUCK
}

enum SpotType {
    COMPACT, LARGE, HANDICAPPED
}

enum PaymentStatus {
    PENDING, COMPLETED, FAILED
}

// Vehicle classes
abstract class Vehicle {
    protected String licensePlate;
    protected VehicleType type;
    
    public Vehicle(String licensePlate, VehicleType type) {
        this.licensePlate = licensePlate;
        this.type = type;
    }
    
    public String getLicensePlate() { return licensePlate; }
    public VehicleType getType() { return type; }
}

class Motorcycle extends Vehicle {
    public Motorcycle(String licensePlate) {
        super(licensePlate, VehicleType.MOTORCYCLE);
    }
}

class Car extends Vehicle {
    public Car(String licensePlate) {
        super(licensePlate, VehicleType.CAR);
    }
}

class Truck extends Vehicle {
    public Truck(String licensePlate) {
        super(licensePlate, VehicleType.TRUCK);
    }
}

// Parking Spot classes
abstract class ParkingSpot {
    protected int spotNumber;
    protected SpotType type;
    protected boolean isOccupied;
    protected Vehicle vehicle;
    
    public ParkingSpot(int spotNumber, SpotType type) {
        this.spotNumber = spotNumber;
        this.type = type;
        this.isOccupied = false;
    }
    
    public boolean isAvailable() { return !isOccupied; }
    public abstract boolean canFitVehicle(Vehicle vehicle);
    
    public boolean parkVehicle(Vehicle vehicle) {
        if (canFitVehicle(vehicle) && isAvailable()) {
            this.vehicle = vehicle;
            this.isOccupied = true;
            return true;
        }
        return false;
    }
    
    public void removeVehicle() {
        this.vehicle = null;
        this.isOccupied = false;
    }
    
    public int getSpotNumber() { return spotNumber; }
    public SpotType getType() { return type; }
    public Vehicle getVehicle() { return vehicle; }
}

class CompactSpot extends ParkingSpot {
    public CompactSpot(int spotNumber) {
        super(spotNumber, SpotType.COMPACT);
    }
    
    public boolean canFitVehicle(Vehicle vehicle) {
        return vehicle.getType() == VehicleType.MOTORCYCLE || 
               vehicle.getType() == VehicleType.CAR;
    }
}

class LargeSpot extends ParkingSpot {
    public LargeSpot(int spotNumber) {
        super(spotNumber, SpotType.LARGE);
    }
    
    public boolean canFitVehicle(Vehicle vehicle) {
        return true; // Can fit any vehicle
    }
}

class HandicappedSpot extends ParkingSpot {
    public HandicappedSpot(int spotNumber) {
        super(spotNumber, SpotType.HANDICAPPED);
    }
    
    public boolean canFitVehicle(Vehicle vehicle) {
        return vehicle.getType() == VehicleType.CAR;
    }
}

// Ticket class
class Ticket {
    private String ticketId;
    private Vehicle vehicle;
    private ParkingSpot spot;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double fee;
    
    public Ticket(String ticketId, Vehicle vehicle, ParkingSpot spot) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = LocalDateTime.now();
    }
    
    // Getters and setters
    public String getTicketId() { return ticketId; }
    public Vehicle getVehicle() { return vehicle; }
    public ParkingSpot getSpot() { return spot; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public double getFee() { return fee; }
    
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
    public void setFee(double fee) { this.fee = fee; }
}

// Payment class
class Payment {
    private String paymentId;
    private double amount;
    private PaymentStatus status;
    private LocalDateTime paymentTime;
    
    public Payment(String paymentId, double amount) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }
    
    public boolean processPayment() {
        // Simulate payment processing
        this.status = PaymentStatus.COMPLETED;
        this.paymentTime = LocalDateTime.now();
        return true;
    }
    
    public PaymentStatus getStatus() { return status; }
    public double getAmount() { return amount; }
}

// Level class
class Level {
    private int levelNumber;
    private List<ParkingSpot> spots;
    private Map<SpotType, Integer> availableSpots;
    
    public Level(int levelNumber) {
        this.levelNumber = levelNumber;
        this.spots = new ArrayList<>();
        this.availableSpots = new HashMap<>();
        initializeSpots();
    }
    
    private void initializeSpots() {
        // Initialize with some spots of each type
        for (int i = 1; i <= 20; i++) {
            spots.add(new CompactSpot(i));
        }
        for (int i = 21; i <= 40; i++) {
            spots.add(new LargeSpot(i));
        }
        for (int i = 41; i <= 45; i++) {
            spots.add(new HandicappedSpot(i));
        }
        
        updateAvailableSpots();
    }
    
    private void updateAvailableSpots() {
        availableSpots.clear();
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable()) {
                availableSpots.put(spot.getType(), 
                    availableSpots.getOrDefault(spot.getType(), 0) + 1);
            }
        }
    }
    
    public ParkingSpot findAvailableSpot(Vehicle vehicle) {
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable() && spot.canFitVehicle(vehicle)) {
                return spot;
            }
        }
        return null;
    }
    
    public boolean parkVehicle(Vehicle vehicle) {
        ParkingSpot spot = findAvailableSpot(vehicle);
        if (spot != null) {
            boolean parked = spot.parkVehicle(vehicle);
            if (parked) {
                updateAvailableSpots();
            }
            return parked;
        }
        return false;
    }
    
    public void removeVehicle(ParkingSpot spot) {
        spot.removeVehicle();
        updateAvailableSpots();
    }
    
    public int getAvailableSpots(SpotType type) {
        return availableSpots.getOrDefault(type, 0);
    }
    
    public int getLevelNumber() { return levelNumber; }
}

// Main ParkingLot class
public class ParkingLot {
    private static ParkingLot instance;
    private List<Level> levels;
    private Map<String, Ticket> activeTickets;
    private int ticketCounter;
    
    private ParkingLot() {
        this.levels = new ArrayList<>();
        this.activeTickets = new HashMap<>();
        this.ticketCounter = 1;
        initializeLevels();
    }
    
    public static synchronized ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }
    
    private void initializeLevels() {
        // Initialize with 3 levels
        for (int i = 1; i <= 3; i++) {
            levels.add(new Level(i));
        }
    }
    
    public Ticket parkVehicle(Vehicle vehicle) {
        for (Level level : levels) {
            ParkingSpot spot = level.findAvailableSpot(vehicle);
            if (spot != null && level.parkVehicle(vehicle)) {
                String ticketId = "T" + ticketCounter++;
                Ticket ticket = new Ticket(ticketId, vehicle, spot);
                activeTickets.put(ticketId, ticket);
                return ticket;
            }
        }
        return null; // No available spot
    }
    
    public double calculateFee(Ticket ticket) {
        LocalDateTime exitTime = LocalDateTime.now();
        ticket.setExitTime(exitTime);
        
        Duration duration = Duration.between(ticket.getEntryTime(), exitTime);
        long hours = duration.toHours();
        if (duration.toMinutes() % 60 > 0) {
            hours++; // Round up to next hour
        }
        
        double hourlyRate = getHourlyRate(ticket.getVehicle().getType());
        double fee = hours * hourlyRate;
        ticket.setFee(fee);
        
        return fee;
    }
    
    private double getHourlyRate(VehicleType type) {
        switch (type) {
            case MOTORCYCLE: return 2.0;
            case CAR: return 5.0;
            case TRUCK: return 10.0;
            default: return 5.0;
        }
    }
    
    public boolean removeVehicle(Ticket ticket) {
        if (activeTickets.containsKey(ticket.getTicketId())) {
            ParkingSpot spot = ticket.getSpot();
            
            // Find the level and remove vehicle
            for (Level level : levels) {
                level.removeVehicle(spot);
            }
            
            activeTickets.remove(ticket.getTicketId());
            return true;
        }
        return false;
    }
    
    public void displayAvailability() {
        System.out.println("Parking Lot Availability:");
        for (Level level : levels) {
            System.out.println("Level " + level.getLevelNumber() + ":");
            System.out.println("  Compact: " + level.getAvailableSpots(SpotType.COMPACT));
            System.out.println("  Large: " + level.getAvailableSpots(SpotType.LARGE));
            System.out.println("  Handicapped: " + level.getAvailableSpots(SpotType.HANDICAPPED));
        }
    }
    
    public boolean processPayment(Ticket ticket) {
        Payment payment = new Payment("P" + System.currentTimeMillis(), ticket.getFee());
        return payment.processPayment();
    }
}