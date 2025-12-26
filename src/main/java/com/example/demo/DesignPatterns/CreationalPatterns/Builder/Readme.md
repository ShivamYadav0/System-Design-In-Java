# 🧩 Builder Design Pattern – Deep Dive

> **Mental model:** The Builder pattern separates the construction of a complex object from its representation, so that the same construction process can create different representations.

---

## 🔍 Problem (Realistic Scenario)

Imagine you are creating a `HttpClient` class. A `HttpClient` can have many configuration options:

- `method`: (Required) The HTTP method (GET, POST, etc.).
- `url`: (Required) The URL to connect to.
- `headers`: (Optional) A map of request headers.
- `body`: (Optional) The request body.
- `connectTimeout`: (Optional) Connection timeout in milliseconds.
- `readTimeout`: (Optional) Read timeout in milliseconds.

How would you construct an instance of this `HttpClient`? 

#### Approach 1: Telescoping Constructors

You could create multiple constructors with different combinations of parameters:

```java
// NOT a good approach
new HttpClient("POST", "https://api.example.com");
new HttpClient("POST", "https://api.example.com", headers);
new HttpClient("POST", "https://api.example.com", headers, body);
new HttpClient("POST", "https://api.example.com", headers, body, 5000, 10000);
```
**Problem:** This is hard to read (what does `5000` mean?), error-prone (mixing up timeout values), and a nightmare to maintain. This is known as the **telescoping constructor** anti-pattern.

#### Approach 2: JavaBeans Pattern (Setters)

You could create a default constructor and provide setters for each optional parameter.

```java
// Also NOT a good approach
HttpClient client = new HttpClient();
client.setMethod("POST");
client.setUrl("https://api.example.com");
client.setHeaders(headers);
client.setBody(body);
```
**Problem:** The object is in an **inconsistent state** during its construction. What if the user forgets to set the required `url`? Furthermore, the object is not **immutable**. Its state can be changed after it has been created, which is not ideal for an object like a client configuration.

---

## ✅ Builder Solution

The Builder pattern solves this by using a dedicated `Builder` object to construct the final object step by step. This approach provides a fluent, readable API and ensures the final object is immutable and consistently configured.

### 🧱 Structure

```
+----------------+       +-------------------+       +----------------+
|     Client     |------>|      Builder      |------>|     Product    |
| (e.g., MainApp)|       | (e.g., HttpClientBuilder)|
+----------------+       +-------------------+       +----------------+
```

- **Product:** The complex object being built (e.g., `HttpClient`).
- **Builder:** An interface or abstract class that defines the steps for building the `Product`.
- **ConcreteBuilder:** Implements the `Builder` and keeps track of the representation it's creating. It provides a way to retrieve the final product.

In modern Java, the Builder is often implemented as a **static nested class** inside the `Product` class itself.

### ☕ Java Example (Static Nested Builder)

This is the most common and effective way to implement the Builder pattern in Java.

#### 1. The Product Class

The `HttpClient` class has a private constructor that takes a `Builder` as its argument. All its fields are `final`, making it immutable.

```java
import java.util.Map;

public final class HttpClient {
    private final String method;
    private final String url;
    private final Map<String, String> headers;
    private final String body;
    private final int connectTimeout;
    private final int readTimeout;

    // Constructor is private and takes a Builder
    private HttpClient(Builder builder) {
        this.method = builder.method;
        this.url = builder.url;
        this.headers = builder.headers;
        this.body = builder.body;
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
    }

    // Getters for all fields...

    // The static nested Builder class
    public static class Builder {
        // Required parameters
        private final String method;
        private final String url;

        // Optional parameters - initialized to default values
        private Map<String, String> headers = Map.of();
        private String body = null;
        private int connectTimeout = 10_000;
        private int readTimeout = 10_000;

        public Builder(String method, String url) {
            this.method = method;
            this.url = url;
        }

        // Setter-like methods that return the builder for chaining
        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder connectTimeout(int timeout) {
            this.connectTimeout = timeout;
            return this;
        }

        public Builder readTimeout(int timeout) {
            this.readTimeout = timeout;
            return this;
        }

        // The final build() method that creates the Product
        public HttpClient build() {
            // Here you could add validation logic before creating the object
            if (url == null || method == null) {
                throw new IllegalStateException("Method and URL cannot be null");
            }
            return new HttpClient(this);
        }
    }
}
```

#### 2. The Client Code

The client code is now highly readable, fluent, and less error-prone.

```java
public class Application {
    public static void main(String[] args) {
        // Construct a complex HttpClient object using the builder
        HttpClient client = new HttpClient.Builder("POST", "https://api.example.com/data")
            .headers(Map.of("Authorization", "Bearer token"))
            .body("{ \"key\": \"value\" }")
            .connectTimeout(5000)
            .readTimeout(5000)
            .build();

        // The resulting client object is immutable and fully configured.
        System.out.println("Client configured for URL: " + client.getUrl());
    }
}
```

---

## ✔ When to Use the Builder Pattern

- **Complex Constructors:** When an object has a large number of constructor parameters, especially if many are optional.
- **Immutability:** When you want to create an immutable object but it requires complex initialization logic.
- **Readability:** When you want to create a fluent, readable API for object creation.
- **Multi-step Construction:** When the object needs to be constructed in multiple steps with some validation logic before it's finalized.

## 🆚 Builder vs. Abstract Factory

- **Focus:** Builder focuses on constructing a **single, complex object** step by step. Abstract Factory focuses on creating **families of related simple objects**.
- **Process:** Builder provides a step-by-step API for the client to direct the construction. Abstract Factory provides a one-shot method to get a family of objects.

## 💡 Interview Line

> **“The Builder pattern is my go-to solution for creating complex objects with many optional parameters. It provides a fluent, readable API, avoids the telescoping constructor anti-pattern, and helps create immutable, fully-initialized objects.”**

---

## 🚀 Next Steps

- Explore the **Prototype Pattern**, which lets you create new objects by copying existing ones, which can be an alternative to construction.
- Understand the **Fluent Interface** pattern, which is a broader concept that the Builder pattern often implements.
