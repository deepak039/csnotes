import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

// Enums
enum BookStatus {
    AVAILABLE, ISSUED, RESERVED, LOST
}

enum UserType {
    MEMBER, LIBRARIAN, STUDENT
}

// Book class
class Book {
    private String isbn;
    private String title;
    private String author;
    private String category;
    private BookStatus status;
    private LocalDate publishDate;
    
    public Book(String isbn, String title, String author, String category) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.status = BookStatus.AVAILABLE;
    }
    
    // Getters and setters
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }
    
    @Override
    public String toString() {
        return "Book{" + "isbn='" + isbn + "', title='" + title + 
               "', author='" + author + "', status=" + status + '}';
    }
}

// User classes
abstract class User {
    protected String userId;
    protected String name;
    protected String email;
    protected UserType userType;
    protected List<BookIssue> borrowedBooks;
    
    public User(String userId, String name, String email, UserType userType) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.borrowedBooks = new ArrayList<>();
    }
    
    public abstract int getMaxBooksAllowed();
    public abstract int getBorrowingPeriodDays();
    
    // Getters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UserType getUserType() { return userType; }
    public List<BookIssue> getBorrowedBooks() { return borrowedBooks; }
}

class Member extends User {
    public Member(String userId, String name, String email) {
        super(userId, name, email, UserType.MEMBER);
    }
    
    public int getMaxBooksAllowed() { return 5; }
    public int getBorrowingPeriodDays() { return 14; }
}

class Student extends User {
    private String studentId;
    
    public Student(String userId, String name, String email, String studentId) {
        super(userId, name, email, UserType.STUDENT);
        this.studentId = studentId;
    }
    
    public int getMaxBooksAllowed() { return 10; }
    public int getBorrowingPeriodDays() { return 30; }
    public String getStudentId() { return studentId; }
}

class Librarian extends User {
    public Librarian(String userId, String name, String email) {
        super(userId, name, email, UserType.LIBRARIAN);
    }
    
    public int getMaxBooksAllowed() { return Integer.MAX_VALUE; }
    public int getBorrowingPeriodDays() { return 365; }
}

// BookIssue class
class BookIssue {
    private String issueId;
    private Book book;
    private User user;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean isReturned;
    
    public BookIssue(String issueId, Book book, User user) {
        this.issueId = issueId;
        this.book = book;
        this.user = user;
        this.issueDate = LocalDate.now();
        this.dueDate = issueDate.plusDays(user.getBorrowingPeriodDays());
        this.isReturned = false;
    }
    
    public void returnBook() {
        this.returnDate = LocalDate.now();
        this.isReturned = true;
    }
    
    public boolean isOverdue() {
        return !isReturned && LocalDate.now().isAfter(dueDate);
    }
    
    public long getOverdueDays() {
        if (!isOverdue()) return 0;
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
    
    // Getters
    public String getIssueId() { return issueId; }
    public Book getBook() { return book; }
    public User getUser() { return user; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned() { return isReturned; }
}

// Fine class
class Fine {
    private String fineId;
    private User user;
    private BookIssue bookIssue;
    private double amount;
    private boolean isPaid;
    private LocalDate fineDate;
    
    public Fine(String fineId, User user, BookIssue bookIssue, double amount) {
        this.fineId = fineId;
        this.user = user;
        this.bookIssue = bookIssue;
        this.amount = amount;
        this.isPaid = false;
        this.fineDate = LocalDate.now();
    }
    
    public void payFine() {
        this.isPaid = true;
    }
    
    // Getters
    public String getFineId() { return fineId; }
    public User getUser() { return user; }
    public BookIssue getBookIssue() { return bookIssue; }
    public double getAmount() { return amount; }
    public boolean isPaid() { return isPaid; }
    public LocalDate getFineDate() { return fineDate; }
}

// BookReservation class
class BookReservation {
    private String reservationId;
    private Book book;
    private User user;
    private LocalDate reservationDate;
    private boolean isActive;
    
    public BookReservation(String reservationId, Book book, User user) {
        this.reservationId = reservationId;
        this.book = book;
        this.user = user;
        this.reservationDate = LocalDate.now();
        this.isActive = true;
    }
    
    public void cancelReservation() {
        this.isActive = false;
    }
    
    // Getters
    public String getReservationId() { return reservationId; }
    public Book getBook() { return book; }
    public User getUser() { return user; }
    public LocalDate getReservationDate() { return reservationDate; }
    public boolean isActive() { return isActive; }
}

// Main Library class
public class LibraryManagement {
    private Map<String, Book> books;
    private Map<String, User> users;
    private Map<String, BookIssue> bookIssues;
    private Map<String, Fine> fines;
    private Map<String, BookReservation> reservations;
    private Queue<BookReservation> reservationQueue;
    
    // Search indexes
    private Map<String, List<Book>> titleIndex;
    private Map<String, List<Book>> authorIndex;
    private Map<String, List<Book>> categoryIndex;
    
    private int issueCounter;
    private int fineCounter;
    private int reservationCounter;
    
    public LibraryManagement() {
        this.books = new HashMap<>();
        this.users = new HashMap<>();
        this.bookIssues = new HashMap<>();
        this.fines = new HashMap<>();
        this.reservations = new HashMap<>();
        this.reservationQueue = new LinkedList<>();
        
        this.titleIndex = new HashMap<>();
        this.authorIndex = new HashMap<>();
        this.categoryIndex = new HashMap<>();
        
        this.issueCounter = 1;
        this.fineCounter = 1;
        this.reservationCounter = 1;
    }
    
    // Book management
    public void addBook(Book book) {
        books.put(book.getIsbn(), book);
        updateSearchIndexes(book);
    }
    
    public void removeBook(String isbn) {
        Book book = books.remove(isbn);
        if (book != null) {
            removeFromSearchIndexes(book);
        }
    }
    
    private void updateSearchIndexes(Book book) {
        titleIndex.computeIfAbsent(book.getTitle().toLowerCase(), k -> new ArrayList<>()).add(book);
        authorIndex.computeIfAbsent(book.getAuthor().toLowerCase(), k -> new ArrayList<>()).add(book);
        categoryIndex.computeIfAbsent(book.getCategory().toLowerCase(), k -> new ArrayList<>()).add(book);
    }
    
    private void removeFromSearchIndexes(Book book) {
        titleIndex.getOrDefault(book.getTitle().toLowerCase(), new ArrayList<>()).remove(book);
        authorIndex.getOrDefault(book.getAuthor().toLowerCase(), new ArrayList<>()).remove(book);
        categoryIndex.getOrDefault(book.getCategory().toLowerCase(), new ArrayList<>()).remove(book);
    }
    
    // User management
    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }
    
    public void removeUser(String userId) {
        users.remove(userId);
    }
    
    // Book issuing
    public synchronized BookIssue issueBook(String isbn, String userId) {
        Book book = books.get(isbn);
        User user = users.get(userId);
        
        if (book == null || user == null) {
            return null;
        }
        
        if (book.getStatus() != BookStatus.AVAILABLE) {
            return null;
        }
        
        if (user.getBorrowedBooks().size() >= user.getMaxBooksAllowed()) {
            return null;
        }
        
        String issueId = "I" + issueCounter++;
        BookIssue bookIssue = new BookIssue(issueId, book, user);
        
        book.setStatus(BookStatus.ISSUED);
        user.getBorrowedBooks().add(bookIssue);
        bookIssues.put(issueId, bookIssue);
        
        return bookIssue;
    }
    
    // Book returning
    public synchronized boolean returnBook(String issueId) {
        BookIssue bookIssue = bookIssues.get(issueId);
        if (bookIssue == null || bookIssue.isReturned()) {
            return false;
        }
        
        bookIssue.returnBook();
        bookIssue.getBook().setStatus(BookStatus.AVAILABLE);
        bookIssue.getUser().getBorrowedBooks().remove(bookIssue);
        
        // Calculate fine if overdue
        if (bookIssue.isOverdue()) {
            double fineAmount = calculateFine(bookIssue);
            String fineId = "F" + fineCounter++;
            Fine fine = new Fine(fineId, bookIssue.getUser(), bookIssue, fineAmount);
            fines.put(fineId, fine);
        }
        
        // Check for reservations
        processReservations(bookIssue.getBook());
        
        return true;
    }
    
    private double calculateFine(BookIssue bookIssue) {
        long overdueDays = bookIssue.getOverdueDays();
        return overdueDays * 1.0; // $1 per day
    }
    
    // Book reservation
    public BookReservation reserveBook(String isbn, String userId) {
        Book book = books.get(isbn);
        User user = users.get(userId);
        
        if (book == null || user == null || book.getStatus() == BookStatus.AVAILABLE) {
            return null;
        }
        
        String reservationId = "R" + reservationCounter++;
        BookReservation reservation = new BookReservation(reservationId, book, user);
        
        reservations.put(reservationId, reservation);
        reservationQueue.offer(reservation);
        
        return reservation;
    }
    
    private void processReservations(Book book) {
        Iterator<BookReservation> iterator = reservationQueue.iterator();
        while (iterator.hasNext()) {
            BookReservation reservation = iterator.next();
            if (reservation.getBook().equals(book) && reservation.isActive()) {
                // Notify user about book availability
                System.out.println("Book " + book.getTitle() + " is now available for " + 
                                 reservation.getUser().getName());
                iterator.remove();
                break;
            }
        }
    }
    
    // Search functionality
    public List<Book> searchByTitle(String title) {
        return titleIndex.getOrDefault(title.toLowerCase(), new ArrayList<>());
    }
    
    public List<Book> searchByAuthor(String author) {
        return authorIndex.getOrDefault(author.toLowerCase(), new ArrayList<>());
    }
    
    public List<Book> searchByCategory(String category) {
        return categoryIndex.getOrDefault(category.toLowerCase(), new ArrayList<>());
    }
    
    public Book searchByIsbn(String isbn) {
        return books.get(isbn);
    }
    
    // Fine management
    public List<Fine> getUserFines(String userId) {
        List<Fine> userFines = new ArrayList<>();
        for (Fine fine : fines.values()) {
            if (fine.getUser().getUserId().equals(userId) && !fine.isPaid()) {
                userFines.add(fine);
            }
        }
        return userFines;
    }
    
    public boolean payFine(String fineId) {
        Fine fine = fines.get(fineId);
        if (fine != null && !fine.isPaid()) {
            fine.payFine();
            return true;
        }
        return false;
    }
    
    // Reports
    public List<BookIssue> getOverdueBooks() {
        List<BookIssue> overdueBooks = new ArrayList<>();
        for (BookIssue issue : bookIssues.values()) {
            if (issue.isOverdue()) {
                overdueBooks.add(issue);
            }
        }
        return overdueBooks;
    }
    
    public void displayLibraryStats() {
        System.out.println("Library Statistics:");
        System.out.println("Total Books: " + books.size());
        System.out.println("Total Users: " + users.size());
        System.out.println("Books Issued: " + bookIssues.values().stream()
                          .mapToInt(issue -> issue.isReturned() ? 0 : 1).sum());
        System.out.println("Overdue Books: " + getOverdueBooks().size());
        System.out.println("Active Reservations: " + reservationQueue.size());
    }
}