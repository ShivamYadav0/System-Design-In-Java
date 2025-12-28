# 🧩 Chain of Responsibility Pattern – Deep Dive

> **Mental model:** The Chain of Responsibility pattern creates a chain of processing objects. A request enters the chain and is passed along from object to object until it is handled.

---

## 🔍 Problem (Realistic Scenario)

Imagine you are building a request processing system for a web server. Incoming HTTP requests need to go through several checks before they can be handled by the main application logic:

1.  **Authentication:** Is the user logged in? Does the request have a valid API key?
2.  **Authorization:** Does the logged-in user have permission to access this specific resource?
3.  **Caching:** Is the response for this request already in the cache? If so, serve it directly without further processing.
4.  **Logging:** Log the details of the incoming request for analytics.

A naive approach would be to have one massive method that performs all these checks sequentially:

```java
public class RequestProcessor {
    public void handle(HttpRequest request) {
        // 1. Authentication
        if (!authenticate(request)) {
            // return 401 Unauthorized
            return;
        }

        // 2. Authorization
        if (!authorize(request)) {
            // return 403 Forbidden
            return;
        }

        // 3. Caching
        if (isCached(request)) {
            // return cached response
            return;
        }

        // 4. Logging
        log(request);

        // Finally, process the request
        dispatchToController(request);
    }
    // ... private helper methods for authenticate, authorize, etc.
}
```

This approach has several drawbacks:

-   **Monolithic and Inflexible:** The processing logic is hardcoded. What if you want to reorder the checks, or add a new one (e.g., rate limiting)? You would have to modify this core method.
-   **Violates Single Responsibility Principle:** The `RequestProcessor` class knows about authentication, authorization, caching, and logging. Its responsibility is not singular.
-   **Hard to Reuse:** The individual checking logic (e.g., authentication) is tightly coupled to the `RequestProcessor` and cannot be easily reused elsewhere.

---

## ✅ Chain of Responsibility Solution

The Chain of Responsibility pattern solves this by turning each processing step into a standalone object called a "handler." Each handler contains a reference to the next handler in the chain. When a request arrives, it is passed to the first handler. The handler decides whether to process the request or to pass it along to the next handler in the chain (or both).

### 🧱 Structure

```
+-----------------+
|     Client      |
+-----------------+
        |
        V
+-----------------+
|  Handler (Interface) |
|-----------------|
| + setNext(Handler):void |
| + handle(Request):void  |
+-----------------+
        ^
        | (implements)
+-----------------+-----------------+-----------------+
|                 |                 |                 |
+-----------------+ +-----------------+ +-----------------+
| ConcreteHandlerA| | ConcreteHandlerB| | ConcreteHandlerC|
| (AuthHandler)   | | (AuthzHandler)  | | (CacheHandler)  |
| - next: Handler |-|-> - next: Handler |-|-> - next: Handler |
+-----------------+ +-----------------+ +-----------------+
```

-   **Handler:** An interface that declares the common method for handling requests (e.g., `handle(request)`) and a method for setting the next handler in the chain (`setNext`).
-   **ConcreteHandler:** Implements the `Handler` interface. It decides if it can handle the request. If it can, it does so. If not, it passes the request to the next handler in the chain.
-   **Client:** Creates the chain of handlers and initiates the request by passing it to the first handler.

### ☕ Java Example

This is the classic structure for middleware in web frameworks like Jakarta EE (Filters) or Express.js.

#### 1. The Handler Interface

```java
// The Handler Interface
public abstract class Middleware {
    private Middleware next;

    // Builds the chain
    public Middleware linkWith(Middleware next) {
        this.next = next;
        return next;
    }

    // Subclasses must implement this method
    public abstract boolean check(HttpRequest request);

    // Runs check on the next object in chain or ends traversing if we're in last object in chain
    protected boolean checkNext(HttpRequest request) {
        if (next == null) {
            return true;
        }
        return next.check(request);
    }
}
```

#### 2. Concrete Handlers

Each processing step is a separate class.

```java
// Concrete Handler for Authentication
public class AuthMiddleware extends Middleware {
    public boolean check(HttpRequest request) {
        System.out.println("AuthMiddleware: Checking authentication...");
        if (!request.isAuthenticated()) {
            System.out.println("Authentication failed!");
            return false; // Stop the chain
        }
        return checkNext(request); // Pass to the next handler
    }
}

// Concrete Handler for Authorization
public class AuthzMiddleware extends Middleware {
    public boolean check(HttpRequest request) {
        System.out.println("AuthzMiddleware: Checking authorization...");
        if (!request.isAuthorized()) {
            System.out.println("Authorization failed!");
            return false; // Stop the chain
        }
        return checkNext(request); // Pass to the next handler
    }
}

// Concrete Handler for Caching
public class CacheMiddleware extends Middleware {
    public boolean check(HttpRequest request) {
        System.out.println("CacheMiddleware: Checking cache...");
        if (request.isCached()) {
            System.out.println("Response served from cache!");
            return false; // Stop the chain, but it's a "good" stop
        }
        return checkNext(request);
    }
}
```

#### 3. Client Code

The client builds the chain and initiates the request.

```java
public class Application {
    public static void main(String[] args) {
        // Build the chain of middleware
        Middleware middleware = new AuthMiddleware();
        middleware.linkWith(new AuthzMiddleware())
                  .linkWith(new CacheMiddleware());

        // A request that should pass all checks
        HttpRequest goodRequest = new HttpRequest(true, true, false);
        System.out.println("--- Processing good request ---");
        boolean goodResult = middleware.check(goodRequest);
        if (goodResult) {
            System.out.println("Request processed successfully!");
        }

        System.out.println();

        // A request that will fail authentication
        HttpRequest badAuthRequest = new HttpRequest(false, true, false);
        System.out.println("--- Processing bad auth request ---");
        middleware.check(badAuthRequest);
    }
}

// Dummy HttpRequest class for the example
class HttpRequest {
    private boolean authenticated;
    private boolean authorized;
    private boolean cached;
    public HttpRequest(boolean auth, boolean authz, boolean cached) {
        this.authenticated = auth; this.authorized = authz; this.cached = cached;
    }
    public boolean isAuthenticated() { return authenticated; }
    public boolean isAuthorized() { return authorized; }
    public boolean isCached() { return cached; }
}
```

---

## ✔ When to Use Chain of Responsibility

-   **Decoupling Request Senders and Receivers:** When you want to decouple the object that makes a request from the objects that process it. The sender doesn't need to know which object in the chain will ultimately handle the request.
-   **Multiple Handlers:** When more than one object can handle a request, and the handler is not known beforehand. The handler is determined automatically at runtime.
-   **Flexible Ordering:** When you want to be able to specify the order of handlers dynamically.

## 🆚 Chain of Responsibility vs. Decorator

-   **Handling:** In a Chain of Responsibility, handlers can choose **not** to pass the request on. They can decide to handle it and stop the chain. In the Decorator pattern, all decorators typically get a chance to add their functionality.
-   **Structure:** A Chain of Responsibility is usually a simple linked list (A -> B -> C). A Decorator encloses or "wraps" a component, and the execution flow goes all the way in to the core component and then unwraps back out (A(B(C()))).

## 💡 Interview Line

> **“The Chain of Responsibility pattern is ideal for creating a pipeline of processing objects, like middleware in a web framework. It lets a request pass through a chain of handlers, allowing any handler to process it or pass it to the next, which decouples the sender from the receivers and makes the processing flow highly flexible.”**

---

## 🚀 Next Steps

-   Explore the **Command Pattern**, which also deals with decoupling senders and receivers, but does so by encapsulating a request as an object.
-   Look at the source code of a web framework like **Jakarta EE (Servlet Filters)** or **Spring Security (FilterChain)** to see real-world, industrial-strength implementations of this pattern.
