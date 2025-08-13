import java.time.LocalDateTime;
import java.util.*;

// Enums
enum TransactionType {
    WITHDRAW, DEPOSIT, BALANCE_INQUIRY, TRANSFER
}

enum TransactionStatus {
    SUCCESS, FAILED, PENDING
}

enum ATMState {
    IDLE, CARD_INSERTED, PIN_ENTERED, TRANSACTION_PROCESSING
}

// Card class
class Card {
    private String cardNumber;
    private String holderName;
    private LocalDateTime expiryDate;
    private String cvv;
    
    public Card(String cardNumber, String holderName, LocalDateTime expiryDate, String cvv) {
        this.cardNumber = cardNumber;
        this.holderName = holderName;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }
    
    public boolean isValid() {
        return LocalDateTime.now().isBefore(expiryDate);
    }
    
    // Getters
    public String getCardNumber() { return cardNumber; }
    public String getHolderName() { return holderName; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
}

// Account class
class Account {
    private String accountNumber;
    private String pin;
    private double balance;
    private List<Transaction> transactionHistory;
    private double dailyWithdrawLimit;
    private double dailyWithdrawnAmount;
    private LocalDateTime lastWithdrawDate;
    
    public Account(String accountNumber, String pin, double balance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = balance;
        this.transactionHistory = new ArrayList<>();
        this.dailyWithdrawLimit = 1000.0;
        this.dailyWithdrawnAmount = 0.0;
        this.lastWithdrawDate = LocalDateTime.now().minusDays(1);
    }
    
    public boolean validatePin(String inputPin) {
        return this.pin.equals(inputPin);
    }
    
    public synchronized boolean withdraw(double amount) {
        resetDailyLimitIfNeeded();
        
        if (amount <= 0 || amount > balance) {
            return false;
        }
        
        if (dailyWithdrawnAmount + amount > dailyWithdrawLimit) {
            return false;
        }
        
        balance -= amount;
        dailyWithdrawnAmount += amount;
        return true;
    }
    
    public synchronized void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    private void resetDailyLimitIfNeeded() {
        LocalDateTime today = LocalDateTime.now();
        if (lastWithdrawDate.toLocalDate().isBefore(today.toLocalDate())) {
            dailyWithdrawnAmount = 0.0;
            lastWithdrawDate = today;
        }
    }
    
    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }
    
    // Getters
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public List<Transaction> getTransactionHistory() { return new ArrayList<>(transactionHistory); }
    public double getRemainingDailyLimit() { 
        resetDailyLimitIfNeeded();
        return dailyWithdrawLimit - dailyWithdrawnAmount; 
    }
}

// Transaction classes
abstract class Transaction {
    protected String transactionId;
    protected String accountNumber;
    protected TransactionType type;
    protected double amount;
    protected LocalDateTime timestamp;
    protected TransactionStatus status;
    
    public Transaction(String transactionId, String accountNumber, TransactionType type, double amount) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
    }
    
    public abstract boolean execute(Account account, CashDispenser cashDispenser);
    
    // Getters
    public String getTransactionId() { return transactionId; }
    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public TransactionStatus getStatus() { return status; }
    
    protected void setStatus(TransactionStatus status) { this.status = status; }
}

class WithdrawTransaction extends Transaction {
    public WithdrawTransaction(String transactionId, String accountNumber, double amount) {
        super(transactionId, accountNumber, TransactionType.WITHDRAW, amount);
    }
    
    @Override
    public boolean execute(Account account, CashDispenser cashDispenser) {
        if (cashDispenser.canDispense(amount) && account.withdraw(amount)) {
            Map<Integer, Integer> dispensedCash = cashDispenser.dispenseCash(amount);
            if (dispensedCash != null) {
                setStatus(TransactionStatus.SUCCESS);
                account.addTransaction(this);
                return true;
            } else {
                // Rollback account withdrawal
                account.deposit(amount);
            }
        }
        setStatus(TransactionStatus.FAILED);
        return false;
    }
}

class DepositTransaction extends Transaction {
    public DepositTransaction(String transactionId, String accountNumber, double amount) {
        super(transactionId, accountNumber, TransactionType.DEPOSIT, amount);
    }
    
    @Override
    public boolean execute(Account account, CashDispenser cashDispenser) {
        account.deposit(amount);
        setStatus(TransactionStatus.SUCCESS);
        account.addTransaction(this);
        return true;
    }
}

class BalanceInquiryTransaction extends Transaction {
    public BalanceInquiryTransaction(String transactionId, String accountNumber) {
        super(transactionId, accountNumber, TransactionType.BALANCE_INQUIRY, 0);
    }
    
    @Override
    public boolean execute(Account account, CashDispenser cashDispenser) {
        setStatus(TransactionStatus.SUCCESS);
        account.addTransaction(this);
        return true;
    }
}

// CashDispenser class
class CashDispenser {
    private Map<Integer, Integer> cashInventory; // denomination -> count
    private int totalCash;
    
    public CashDispenser() {
        this.cashInventory = new HashMap<>();
        initializeCash();
    }
    
    private void initializeCash() {
        cashInventory.put(100, 10);  // 10 notes of $100
        cashInventory.put(50, 20);   // 20 notes of $50
        cashInventory.put(20, 50);   // 50 notes of $20
        cashInventory.put(10, 100);  // 100 notes of $10
        updateTotalCash();
    }
    
    private void updateTotalCash() {
        totalCash = cashInventory.entrySet().stream()
                   .mapToInt(entry -> entry.getKey() * entry.getValue())
                   .sum();
    }
    
    public synchronized boolean canDispense(double amount) {
        return amount <= totalCash && canMakeAmount((int) amount);
    }
    
    private boolean canMakeAmount(int amount) {
        Map<Integer, Integer> tempInventory = new HashMap<>(cashInventory);
        List<Integer> denominations = new ArrayList<>(tempInventory.keySet());
        denominations.sort(Collections.reverseOrder());
        
        for (int denomination : denominations) {
            int count = tempInventory.get(denomination);
            int notesNeeded = Math.min(amount / denomination, count);
            amount -= notesNeeded * denomination;
            
            if (amount == 0) {
                return true;
            }
        }
        
        return amount == 0;
    }
    
    public synchronized Map<Integer, Integer> dispenseCash(double amount) {
        if (!canDispense(amount)) {
            return null;
        }
        
        Map<Integer, Integer> dispensed = new HashMap<>();
        int remainingAmount = (int) amount;
        List<Integer> denominations = new ArrayList<>(cashInventory.keySet());
        denominations.sort(Collections.reverseOrder());
        
        for (int denomination : denominations) {
            int availableNotes = cashInventory.get(denomination);
            int notesNeeded = Math.min(remainingAmount / denomination, availableNotes);
            
            if (notesNeeded > 0) {
                dispensed.put(denomination, notesNeeded);
                cashInventory.put(denomination, availableNotes - notesNeeded);
                remainingAmount -= notesNeeded * denomination;
            }
            
            if (remainingAmount == 0) {
                break;
            }
        }
        
        if (remainingAmount == 0) {
            updateTotalCash();
            return dispensed;
        }
        
        return null;
    }
    
    public void addCash(int denomination, int count) {
        cashInventory.put(denomination, cashInventory.getOrDefault(denomination, 0) + count);
        updateTotalCash();
    }
    
    public int getTotalCash() { return totalCash; }
    public Map<Integer, Integer> getCashInventory() { return new HashMap<>(cashInventory); }
}

// Receipt class
class Receipt {
    private String transactionId;
    private String accountNumber;
    private TransactionType type;
    private double amount;
    private double balance;
    private LocalDateTime timestamp;
    
    public Receipt(Transaction transaction, double balance) {
        this.transactionId = transaction.getTransactionId();
        this.accountNumber = transaction.accountNumber;
        this.type = transaction.getType();
        this.amount = transaction.getAmount();
        this.balance = balance;
        this.timestamp = transaction.getTimestamp();
    }
    
    public void print() {
        System.out.println("=== ATM RECEIPT ===");
        System.out.println("Transaction ID: " + transactionId);
        System.out.println("Account: " + accountNumber);
        System.out.println("Type: " + type);
        if (amount > 0) {
            System.out.println("Amount: $" + amount);
        }
        System.out.println("Balance: $" + balance);
        System.out.println("Date: " + timestamp);
        System.out.println("==================");
    }
}

// Main ATM class
public class ATMSystem {
    private ATMState currentState;
    private Card insertedCard;
    private Account currentAccount;
    private CashDispenser cashDispenser;
    private Map<String, Account> accounts;
    private int transactionCounter;
    
    public ATMSystem() {
        this.currentState = ATMState.IDLE;
        this.cashDispenser = new CashDispenser();
        this.accounts = new HashMap<>();
        this.transactionCounter = 1;
        initializeAccounts();
    }
    
    private void initializeAccounts() {
        accounts.put("1234567890", new Account("1234567890", "1234", 1000.0));
        accounts.put("0987654321", new Account("0987654321", "5678", 2000.0));
    }
    
    public boolean insertCard(Card card) {
        if (currentState != ATMState.IDLE || !card.isValid()) {
            return false;
        }
        
        this.insertedCard = card;
        this.currentState = ATMState.CARD_INSERTED;
        System.out.println("Card inserted successfully. Please enter PIN.");
        return true;
    }
    
    public boolean enterPin(String pin) {
        if (currentState != ATMState.CARD_INSERTED) {
            return false;
        }
        
        Account account = accounts.get(insertedCard.getCardNumber());
        if (account != null && account.validatePin(pin)) {
            this.currentAccount = account;
            this.currentState = ATMState.PIN_ENTERED;
            System.out.println("PIN verified successfully.");
            return true;
        }
        
        System.out.println("Invalid PIN. Please try again.");
        return false;
    }
    
    public boolean withdraw(double amount) {
        if (currentState != ATMState.PIN_ENTERED) {
            return false;
        }
        
        currentState = ATMState.TRANSACTION_PROCESSING;
        String transactionId = "T" + transactionCounter++;
        
        WithdrawTransaction transaction = new WithdrawTransaction(
            transactionId, currentAccount.getAccountNumber(), amount);
        
        boolean success = transaction.execute(currentAccount, cashDispenser);
        
        if (success) {
            Receipt receipt = new Receipt(transaction, currentAccount.getBalance());
            receipt.print();
            System.out.println("Please collect your cash.");
        } else {
            System.out.println("Transaction failed. Please try again.");
        }
        
        currentState = ATMState.PIN_ENTERED;
        return success;
    }
    
    public boolean deposit(double amount) {
        if (currentState != ATMState.PIN_ENTERED) {
            return false;
        }
        
        currentState = ATMState.TRANSACTION_PROCESSING;
        String transactionId = "T" + transactionCounter++;
        
        DepositTransaction transaction = new DepositTransaction(
            transactionId, currentAccount.getAccountNumber(), amount);
        
        boolean success = transaction.execute(currentAccount, cashDispenser);
        
        if (success) {
            Receipt receipt = new Receipt(transaction, currentAccount.getBalance());
            receipt.print();
            System.out.println("Deposit successful.");
        }
        
        currentState = ATMState.PIN_ENTERED;
        return success;
    }
    
    public double checkBalance() {
        if (currentState != ATMState.PIN_ENTERED) {
            return -1;
        }
        
        String transactionId = "T" + transactionCounter++;
        BalanceInquiryTransaction transaction = new BalanceInquiryTransaction(
            transactionId, currentAccount.getAccountNumber());
        
        transaction.execute(currentAccount, cashDispenser);
        
        Receipt receipt = new Receipt(transaction, currentAccount.getBalance());
        receipt.print();
        
        return currentAccount.getBalance();
    }
    
    public void ejectCard() {
        this.insertedCard = null;
        this.currentAccount = null;
        this.currentState = ATMState.IDLE;
        System.out.println("Please collect your card. Thank you!");
    }
    
    public void displayMenu() {
        if (currentState == ATMState.PIN_ENTERED) {
            System.out.println("\n=== ATM MENU ===");
            System.out.println("1. Withdraw Cash");
            System.out.println("2. Deposit Cash");
            System.out.println("3. Check Balance");
            System.out.println("4. Transaction History");
            System.out.println("5. Exit");
            System.out.println("================");
        }
    }
    
    public void displayTransactionHistory() {
        if (currentState == ATMState.PIN_ENTERED) {
            List<Transaction> history = currentAccount.getTransactionHistory();
            System.out.println("\n=== TRANSACTION HISTORY ===");
            for (Transaction transaction : history) {
                System.out.println(transaction.getTimestamp() + " - " + 
                                 transaction.getType() + " - $" + transaction.getAmount());
            }
            System.out.println("===========================");
        }
    }
    
    public ATMState getCurrentState() { return currentState; }
    public double getRemainingDailyLimit() { 
        return currentAccount != null ? currentAccount.getRemainingDailyLimit() : 0; 
    }
    
    // Admin functions
    public void addCashToDispenser(int denomination, int count) {
        cashDispenser.addCash(denomination, count);
    }
    
    public void displayCashInventory() {
        System.out.println("Cash Inventory:");
        Map<Integer, Integer> inventory = cashDispenser.getCashInventory();
        for (Map.Entry<Integer, Integer> entry : inventory.entrySet()) {
            System.out.println("$" + entry.getKey() + ": " + entry.getValue() + " notes");
        }
        System.out.println("Total Cash: $" + cashDispenser.getTotalCash());
    }
}