# ☕ Java LLD Starter Template for ATM

> **Goal:** Never start from a blank screen in an LLD interview again.

---

## 1️⃣ Core LLD Package Structure (Recommended)

```text
com.example.atm
 ├── domain        // entities & value objects (e.g., Account, Card, Transaction)
 ├── service       // business logic (e.g., ATMService, TransactionService)
 ├── strategy      // pluggable behaviors (e.g., WithdrawalStrategy)
 ├── repository    // storage abstractions (e.g., AccountRepository)
 ├── factory       // object creation (e.g., TransactionFactory)
 └── api           // public interfaces / controllers (The ATM screen/keypad)
```

---

## 2️⃣ The `Entity` Template (The "Noun" Objects)

```java
// A transaction captures a single operation at the ATM.
public abstract class Transaction {
    private final String transactionId;
    private final LocalDateTime timestamp;

    // constructor, getters
}

public class BalanceInquiry extends Transaction { ... }

public class Withdrawal extends Transaction {
    private final double amount;
    // constructor, getters
}

// Represents the user's bank account.
public class Account {
    private String accountNumber;
    private double balance;
    // getters, setters
}
```

---

## 3️⃣ The `Service` Template (The "Verb" Objects)

```java
// Business logic for ATM operations.
public class ATMService {
    private final AccountRepository accountRepository;
    private final WithdrawalStrategy withdrawalStrategy;

    public ATMService(AccountRepository accRepo, WithdrawalStrategy wStrategy) {
        this.accountRepository = accRepo;
        this.withdrawalStrategy = wStrategy;
    }

    public double checkBalance(String accountNumber) {
        // ... find account and return balance.
    }

    public void withdraw(String accountNumber, double amount) {
        // ... find account, check funds, debit amount, dispense cash.
        withdrawalStrategy.dispense(amount);
    }

    public void deposit(String accountNumber, double amount) {
        // ... find account, credit amount.
    }
}
```

---

## 4️⃣ The `Strategy` Template (For "What If It Changes?")

```java
// Pluggable logic for dispensing cash.
public interface WithdrawalStrategy {
    void dispense(double amount);
}

// Example: A strategy for dispensing specific notes.
public class NoteDispensingStrategy implements WithdrawalStrategy {
    @Override
    public void dispense(double amount) {
        // ... logic to calculate and dispense notes (e.g., 2x$20, 1x$10).
    }
}
```

---

## 5️⃣ The `Repository` Template (The "Storage" Layer)

```java
// Abstraction for account data storage.
public interface AccountRepository {
    Optional<Account> findByAccountNumber(String accountNumber);
    void save(Account account);
}

// In-memory implementation for interviews.
public class InMemoryAccountRepository implements AccountRepository {
    private final Map<String, Account> accounts = new HashMap<>();
    // ... implement methods
}
```

---

## 6️⃣ The `Factory` Template (For Complex Object Creation)

```java
// Creates different types of transactions.
public class TransactionFactory {
    public static Transaction createWithdrawal(double amount) {
        return new Withdrawal(amount);
    }

    public static Transaction createBalanceInquiry() {
        return new BalanceInquiry();
    }
}
```
