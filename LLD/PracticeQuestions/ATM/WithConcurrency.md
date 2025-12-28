# ☕ Java LLD Concurrency Template for ATM

> **Goal:** Design a thread-safe ATM system that correctly handles concurrent withdrawals from the same account, preventing overdrafts and data corruption.

---

## 1️⃣ Core Concurrency Problem

The most critical concurrency issue in an ATM system is the **"read-modify-write" race condition** on a user's account balance. Imagine a user has $100 in their account.

1.  **Thread A** (ATM in City A) reads the balance: $100.
2.  **Thread B** (ATM in City B) reads the balance: $100.
3.  Thread A wants to withdraw $80. It calculates `$100 - $80 = $20`.
4.  Thread B wants to withdraw $70. It calculates `$100 - $70 = $30`.
5.  Thread A writes the new balance: $20.
6.  Thread B writes the new balance: $30.

**Result:** The final balance is $30. The user has withdrawn a total of $150 from a $100 account, and the bank has lost money. The update from Thread A was completely lost.

---

## 2️⃣ The `Entity` Template (Thread-Safe Account)

The `Account` entity itself must protect its balance. We can achieve this using locks or atomic operations.

### Option A: Using `ReentrantLock`

```java
// The Account entity protects its balance with a dedicated lock.
public class Account {
    private final String accountNumber;
    private double balance;
    private final ReentrantLock lock = new ReentrantLock();

    public Account(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    // The entire check-and-debit operation must be synchronized.
    public boolean withdraw(double amount) {
        lock.lock();
        try {
            if (this.balance >= amount) {
                this.balance -= amount;
                return true; // Success
            }
            return false; // Insufficient funds
        } finally {
            lock.unlock(); // Always release the lock.
        }
    }

    public void deposit(double amount) {
        lock.lock();
        try {
            this.balance += amount;
        } finally {
            lock.unlock();
        }
    }

    public double getBalance() {
        // Even reads should use the lock to ensure they see the most recent value.
        lock.lock();
        try {
            return this.balance;
        } finally {
            lock.unlock();
        }
    }
}
```

### Option B: Using `AtomicReference` (Lock-Free)

A more advanced, non-blocking approach using `AtomicReference` and a dedicated `Balance` object.

```java
// A simple immutable object to hold the balance value.
public final class Balance {
    private final double value;
    public Balance(double value) { this.value = value; }
    public double getValue() { return value; }
}

// The Account entity manages its balance atomically.
public class Account {
    private final String accountNumber;
    private final AtomicReference<Balance> balance;

    public Account(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = new AtomicReference<>(new Balance(initialBalance));
    }

    public boolean withdraw(double amount) {
        while (true) {
            Balance currentBalance = balance.get();
            if (currentBalance.getValue() < amount) {
                return false; // Insufficient funds
            }
            Balance newBalance = new Balance(currentBalance.getValue() - amount);
            // Atomically update the balance IF it hasn't changed since we read it.
            if (balance.compareAndSet(currentBalance, newBalance)) {
                return true; // Success
            }
            // If we fail, another thread modified the balance. Loop and retry.
        }
    }
    
    // ... deposit would use a similar CAS loop.
}
```

---

## 3️⃣ The `Service` Template (Orchestrating Operations)

The service layer uses the thread-safe methods on the `Account` object.

```java
// ATMService handles the overall transaction flow.
public class ATMService {
    private final AccountRepository accountRepository;

    public ATMService(AccountRepository accRepo) {
        this.accountRepository = accRepo;
    }

    public boolean withdraw(String accountNumber, double amount) {
        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            // The call to account.withdraw() is internally thread-safe.
            boolean success = account.withdraw(amount);
            if (success) {
                // ... logic to dispense cash
                return true;
            }
        }
        return false;
    }
}
```

---

## 4️⃣ How to TALK Concurrency in Interviews

*   **Identify Shared State:** "The critical shared, mutable state is the `balance` of a bank `Account`. Multiple ATMs are concurrent clients that will try to read and modify this balance simultaneously."
*   **Explain the Race Condition:** "This is a textbook 'read-modify-write' race condition. Without synchronization, two ATMs could read the same initial balance, each process a withdrawal locally, and then write their new balance back. The second write would overwrite the first, effectively making one withdrawal disappear and leading to data corruption."
*   **Propose a Lock-Based Solution:** "A straightforward solution is to put a `ReentrantLock` on each `Account` object. Any method that modifies or reads the balance must first acquire the lock. This makes the entire 'check-if-sufficient-funds-then-debit' operation atomic, ensuring that once a thread starts the withdrawal process, no other thread can interfere until it's complete."
*   **Propose a Lock-Free Alternative (Advanced):** "For even higher performance and to avoid the risk of deadlocks, we can use a lock-free approach with `AtomicReference`. We model the balance as an immutable `Balance` object. The `withdraw` method enters a `compare-and-set` (CAS) loop. It reads the current balance, calculates the new balance, and then tries to atomically set the new balance *only if* the balance hasn't changed since it was read. If another thread modified it in the meantime, the CAS fails, and our thread simply retries the whole operation. This is an optimistic, non-blocking strategy."
*   **Discuss Granularity:** "It's important that the lock is fine-grained—one lock per account. A coarse-grained lock, like a single lock on the entire `ATMService`, would be a huge performance bottleneck. It would mean only one person could use any ATM in the entire world at a time. With one lock per account, transactions on different accounts can proceed in parallel without blocking each other."