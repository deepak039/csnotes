import java.time.LocalDateTime;

/**
 * Demo class to showcase the LLD implementations
 * This demonstrates how to use the various systems designed
 */
public class Demo {
    
    public static void main(String[] args) {
        System.out.println("=== Low Level Design Examples Demo ===\n");
        
        // Demo Parking Lot System
        demoParkingLot();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Demo Library Management System
        demoLibraryManagement();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Demo ATM System
        demoATMSystem();
    }
    
    private static void demoParkingLot() {
        System.out.println("PARKING LOT SYSTEM DEMO");
        System.out.println("=".repeat(25));
        
        ParkingLot parkingLot = ParkingLot.getInstance();
        
        // Create vehicles
        Vehicle car1 = new Car("ABC123");
        Vehicle motorcycle1 = new Motorcycle("XYZ789");
        Vehicle truck1 = new Truck("TRK456");
        
        // Park vehicles
        System.out.println("Parking vehicles...");
        Ticket ticket1 = parkingLot.parkVehicle(car1);
        Ticket ticket2 = parkingLot.parkVehicle(motorcycle1);
        Ticket ticket3 = parkingLot.parkVehicle(truck1);
        
        if (ticket1 != null) {
            System.out.println("Car parked successfully. Ticket: " + ticket1.getTicketId());
        }
        if (ticket2 != null) {
            System.out.println("Motorcycle parked successfully. Ticket: " + ticket2.getTicketId());
        }
        if (ticket3 != null) {
            System.out.println("Truck parked successfully. Ticket: " + ticket3.getTicketId());
        }
        
        // Display availability
        parkingLot.displayAvailability();
        
        // Calculate and pay fee
        if (ticket1 != null) {
            double fee = parkingLot.calculateFee(ticket1);
            System.out.println("Parking fee for car: $" + fee);
            
            boolean paymentSuccess = parkingLot.processPayment(ticket1);
            if (paymentSuccess) {
                parkingLot.removeVehicle(ticket1);
                System.out.println("Car removed successfully");
            }
        }
    }
    
    private static void demoLibraryManagement() {
        System.out.println("LIBRARY MANAGEMENT SYSTEM DEMO");
        System.out.println("=".repeat(32));
        
        LibraryManagement library = new LibraryManagement();
        
        // Add books
        Book book1 = new Book("978-0134685991", "Effective Java", "Joshua Bloch", "Programming");
        Book book2 = new Book("978-0132350884", "Clean Code", "Robert Martin", "Programming");
        Book book3 = new Book("978-0596009205", "Head First Design Patterns", "Eric Freeman", "Programming");
        
        library.addBook(book1);
        library.addBook(book2);
        library.addBook(book3);
        
        // Add users
        Member member1 = new Member("M001", "John Doe", "john@email.com");
        Student student1 = new Student("S001", "Jane Smith", "jane@email.com", "STU123");
        Librarian librarian1 = new Librarian("L001", "Bob Wilson", "bob@email.com");
        
        library.addUser(member1);
        library.addUser(student1);
        library.addUser(librarian1);
        
        // Issue books
        System.out.println("Issuing books...");
        BookIssue issue1 = library.issueBook("978-0134685991", "M001");
        BookIssue issue2 = library.issueBook("978-0132350884", "S001");
        
        if (issue1 != null) {
            System.out.println("Book issued to " + issue1.getUser().getName() + 
                             ". Due date: " + issue1.getDueDate());
        }
        if (issue2 != null) {
            System.out.println("Book issued to " + issue2.getUser().getName() + 
                             ". Due date: " + issue2.getDueDate());
        }
        
        // Search books
        System.out.println("\nSearching for books by author 'Joshua Bloch':");
        var searchResults = library.searchByAuthor("Joshua Bloch");
        for (Book book : searchResults) {
            System.out.println("Found: " + book.getTitle() + " - Status: " + book.getStatus());
        }
        
        // Display library stats
        library.displayLibraryStats();
    }
    
    private static void demoATMSystem() {
        System.out.println("ATM SYSTEM DEMO");
        System.out.println("=".repeat(16));
        
        ATMSystem atm = new ATMSystem();
        
        // Create a card
        Card card = new Card("1234567890", "John Doe", 
                           LocalDateTime.now().plusYears(2), "123");
        
        // Insert card
        System.out.println("Inserting card...");
        boolean cardInserted = atm.insertCard(card);
        if (cardInserted) {
            System.out.println("Card accepted");
            
            // Enter PIN
            boolean pinEntered = atm.enterPin("1234");
            if (pinEntered) {
                System.out.println("PIN verified");
                
                // Display menu
                atm.displayMenu();
                
                // Check balance
                System.out.println("\nChecking balance...");
                double balance = atm.checkBalance();
                System.out.println("Current balance: $" + balance);
                
                // Withdraw money
                System.out.println("\nWithdrawing $100...");
                boolean withdrawSuccess = atm.withdraw(100);
                if (withdrawSuccess) {
                    System.out.println("Withdrawal successful");
                }
                
                // Deposit money
                System.out.println("\nDepositing $50...");
                boolean depositSuccess = atm.deposit(50);
                if (depositSuccess) {
                    System.out.println("Deposit successful");
                }
                
                // Check balance again
                System.out.println("\nFinal balance check...");
                balance = atm.checkBalance();
                
                // Display transaction history
                atm.displayTransactionHistory();
                
                // Eject card
                atm.ejectCard();
            }
        }
        
        // Display cash inventory (admin function)
        System.out.println("\nATM Cash Inventory:");
        atm.displayCashInventory();
    }
}