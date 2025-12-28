# 🧩 Decorator Design Pattern – Deep Dive

> **Mental model:** The Decorator pattern allows you to dynamically attach new behaviors or responsibilities to an object without altering its original code. It is a flexible alternative to subclassing for extending functionality.

---

## 🔍 Problem (Realistic Scenario)

Imagine you are building a notification system. You start with a simple `Notifier` interface that sends a basic email notification.

```java
public interface Notifier {
    void send(String message);
}

public class EmailNotifier implements Notifier {
    @Override
    public void send(String message) {
        System.out.println("Sending email with message: " + message);
    }
}
```

Soon, the requirements change. You need to add more notification channels, such as SMS and Slack. Crucially, users should be able to choose any combination of these channels. For example, a user might want to receive notifications via:
- Email only
- Email and SMS
- Email, SMS, and Slack
- Slack and Email

If you use inheritance, you would need to create a subclass for every possible combination:
- `EmailAndSmsNotifier`
- `EmailAndSlackNotifier`
- `EmailSmsAndSlackNotifier`
- ...and so on.

This leads to a **class explosion**, making the system rigid and difficult to maintain. Every time a new notification channel is added, the number of required classes grows exponentially.

---

## ✅ Decorator Solution

The Decorator pattern solves this by wrapping the original object (`EmailNotifier`) with "decorator" objects. Each decorator adds a new responsibility (like sending an SMS or Slack message) and then delegates the call to the wrapped object.

### 🧱 Structure

```
+-------------------+
|     Component     | (Notifier)
|-------------------|
|    operation()    |
+-------------------+
        ^
        |
+-------------------+      +----------------------+
| ConcreteComponent |      |      Decorator       | (Abstract)
| (EmailNotifier)   |      |----------------------|
+-------------------+      | - component: Component|
                           |----------------------|
                           | + operation()        |
                           +----------------------+
                                     ^
                                     |
           +-------------------------+-------------------------+
           |                         |                         |
+---------------------+   +---------------------+   +---------------------+
| ConcreteDecoratorA  |   | ConcreteDecoratorB  |   | ConcreteDecoratorC  |
| (SmsDecorator)      |   | (SlackDecorator)    |   | (Etc...)            |
+---------------------+   +---------------------+   +---------------------+
```

- **Component:** The common interface for both the object being decorated and the decorators themselves (`Notifier`).
- **ConcreteComponent:** The original object to which we want to add functionality (`EmailNotifier`).
- **Decorator:** An abstract class that implements the `Component` interface and holds a reference to a `Component` object. It delegates calls to the wrapped component.
- **ConcreteDecorator:** Concrete classes that extend the `Decorator`. Each one adds a specific piece of functionality before or after delegating the call to the wrapped object (`SmsDecorator`, `SlackDecorator`).

### ☕ Java Example

Let's apply this to our notification system.

#### 1. Component Interface

```java
public interface Notifier {
    void send(String message);
}
```

#### 2. Concrete Component

```java
public class EmailNotifier implements Notifier {
    @Override
    public void send(String message) {
        System.out.println("Sending email with message: '" + message + "'");
    }
}
```

#### 3. Abstract Decorator

```java
public abstract class NotifierDecorator implements Notifier {
    protected final Notifier wrappedNotifier;

    public NotifierDecorator(Notifier notifier) {
        this.wrappedNotifier = notifier;
    }

    @Override
    public void send(String message) {
        wrappedNotifier.send(message); // Delegate the call
    }
}
```

#### 4. Concrete Decorators

```java
// Concrete Decorator for SMS
public class SmsDecorator extends NotifierDecorator {
    public SmsDecorator(Notifier notifier) {
        super(notifier);
    }

    @Override
    public void send(String message) {
        super.send(message); // First, do the original notification
        sendSms(message);    // Then, add the new functionality
    }

    private void sendSms(String message) {
        System.out.println("Sending SMS with message: '" + message + "'");
    }
}

// Concrete Decorator for Slack
public class SlackDecorator extends NotifierDecorator {
    public SlackDecorator(Notifier notifier) {
        super(notifier);
    }

    @Override
    public void send(String message) {
        super.send(message); // Delegate
        sendSlack(message);  // Add new behavior
    }

    private void sendSlack(String message) {
        System.out.println("Sending Slack message: '" + message + "'");
    }
}
```

#### 5. Client Code

Now, the client can dynamically compose the desired notification chain at runtime.

```java
public class NotificationSystem {
    public static void main(String[] args) {
        // Start with a basic email notifier
        Notifier notifier = new EmailNotifier();

        // Let's create a notifier that sends email and SMS
        System.out.println("--- Email and SMS Notifier ---");
        Notifier emailAndSms = new SmsDecorator(notifier);
        emailAndSms.send("Your package has shipped!");
        
        System.out.println("
--- Email, SMS, and Slack Notifier ---");
        // Now, let's decorate it further with Slack
        Notifier allChannels = new SlackDecorator(emailAndSms);
        allChannels.send("Your critical alert has been triggered!");

        System.out.println("
--- Email and Slack Notifier ---");
        // You can also compose different combinations easily
        Notifier emailAndSlack = new SlackDecorator(new EmailNotifier());
        emailAndSlack.send("Team meeting starts in 15 minutes.");
    }
}
```

---

## ✔ When to Use the Decorator Pattern

- **Dynamically Add Responsibilities:** When you want to add functionality to objects at runtime without affecting other objects.
- **Avoid Feature Bloat in Superclasses:** When using inheritance would create an unmanageable number of subclasses to account for every possible combination of features.
- **When a Class's Functionality Should Be Extensible:** When you design a class that you expect others to extend with new features in ways you can't predict.

## 💡 Key Insight

- The decorator **is-a** component (it implements the same interface).
- The decorator **has-a** component (it wraps another component).
This is the secret to its flexibility and composability.

## 🆚 Decorator vs. Other Patterns

- **Decorator vs. Proxy:** A Decorator adds or alters the functionality of an object, whereas a Proxy controls access to it. The intent is different: a decorator is about adding behavior, while a proxy is about managing the object's lifecycle or access.
- **Decorator vs. Adapter:** A Decorator does not change the interface, it just adds responsibilities. An Adapter's primary purpose is to convert one interface to another.

---

## 🚀 Next Steps

- Explore the **Proxy Pattern** to understand how to control access to an object.
- Understand the **Composite Pattern**, which allows you to treat a group of objects in the same way as a single object instance.
