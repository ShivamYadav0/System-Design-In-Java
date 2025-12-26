# 🧠 Low Level Design (LLD) – Complete Interview-Ready README

> **Goal:** Design *maintainable, extensible, testable* systems — and explain them clearly under interview pressure.

This README is a **thinking framework** for LLD, not just UML or code.

---

## 0️⃣ What Is Low Level Design (Really?)

**Low Level Design (LLD)** is:

> Designing classes, interfaces, relationships, and interactions that correctly model a real-world problem and survive change.

### ❌ LLD Is NOT

* Writing full production code
* Drawing UML without reasoning
* Memorizing patterns
* Over-engineering

### ✅ LLD IS

* Object modeling
* Responsibility assignment
* Interaction design
* Change isolation
* Clean abstractions

📌 **Interview Truth**

> Interviewers care more about *how you think* than what you write.

---

## 1️⃣ Why LLD Exists (Core Reasoning)

### Without LLD, Code Becomes

* Tightly coupled
* Hard to modify
* Fragile under change
* Impossible to test

### Root Cause

> **Requirements change. Behavior evolves. Scale grows.**

LLD exists to:

* Localize impact of change
* Reduce ripple effects
* Enable independent evolution of components

📌 **Golden Rule**

> LLD is about *future change*, not current requirements.

---

## 2️⃣ What Interviewers Expect From LLD

They are evaluating:

* Class responsibility clarity
* Boundary definition
* Abstraction quality
* Extensibility hooks
* Trade-off awareness

❌ They are NOT evaluating:

* Perfect syntax
* Design pattern count
* UML beauty

---

## 3️⃣ Core Building Blocks of LLD

### 1. Entities

* Represent real-world nouns
* Have identity + state

Examples:

* User, Order, Elevator, Vehicle

---

### 2. Value Objects

* Immutable
* No identity

Examples:

* Money, Location, TimeRange

---

### 3. Services

* Contain business logic
* Coordinate entities

Examples:

* PaymentService
* SchedulingService

---

### 4. Interfaces

* Define contracts
* Hide variability

📌 Program to interfaces, not implementations.

---

## 4️⃣ Responsibility Assignment (Most Important Skill)

### Good Responsibilities

* Small
* Cohesive
* Independent

### Bad Responsibilities

* God classes
* Utility dumping grounds
* Classes that "do everything"

📌 **Rule:** If a class has more than one reason to change — split it.

---

## 5️⃣ Identifying Change (LLD Superpower)

Ask these questions:

* What will change frequently?
* What will vary by client / feature?
* What is policy vs mechanism?

📌 **Abstract only what changes.**

Examples:

* Payment method → Strategy
* Notification channel → Observer
* Pricing rules → Policy interface

---

## 6️⃣ LLD vs HLD (Clear Boundary)

| Aspect  | HLD          | LLD             |
| ------- | ------------ | --------------- |
| Focus   | Components   | Classes         |
| Scale   | System level | Code level      |
| Concern | Architecture | Design          |
| Output  | Diagrams     | Class structure |

📌 In interviews, *never mix them*.

---

## 7️⃣ SOLID Principles (Applied, Not Memorized)

### S — Single Responsibility

One reason to change

### O — Open/Closed

Add behavior without modifying code

### L — Liskov Substitution

Subtypes must be replaceable

### I — Interface Segregation

Small, focused interfaces

### D — Dependency Inversion

Depend on abstractions

📌 SOLID is a **design smell detector**, not rules to force.

---

## 8️⃣ Common LLD Interview Problems

You should be fluent in:

* Parking Lot
* Elevator System
* Rate Limiter
* LRU Cache
* Splitwise
* File System
* Notification System

📌 Same principles apply everywhere.

---

## 9️⃣ Concurrency & Thread Safety (Modern LLD)

Interviewers may probe:

* Shared state
* Locks vs lock-free
* Thread-safe collections
* Atomicity vs visibility

📌 Always **mention** concurrency, even if you don’t fully implement it.

---

## 🔟 LLD Coding Expectations

### What to Show

* Interfaces
* Clean method names
* Extensibility points

### What to Avoid

* Full implementations
* Edge-case obsession
* Premature optimizations

---

## 1️⃣1️⃣ How to Explain Your Design (Critical)

Use this structure:

> 1. Clarify requirements
> 2. Identify entities
> 3. Identify changing behaviors
> 4. Introduce abstractions
> 5. Apply minimal patterns
> 6. Discuss trade-offs

📌 Talking clearly matters more than coding fast.

---

## 1️⃣2️⃣ LLD Interview Traps 🚨

❌ Pattern overuse
❌ God objects
❌ Static everywhere
❌ Ignoring extensibility
❌ Ignoring concurrency

---

## 1️⃣3️⃣ Mental Checklist Before Writing Code

* Who owns this behavior?
* What changes independently?
* Can I extend without modifying?
* Is this abstraction justified?

---

## 1️⃣4️⃣ How Senior Engineers Think in LLD

> "Make the *right thing easy* and the *wrong thing hard*."

* Encapsulate invariants
* Hide complexity
* Fail fast

---

## 🧠 Final Wisdom

> **LLD is not about classes.
> It’s about responsibility, change, and clarity.**

If your design reads like a story, you’re doing it right.

---

## 📚 Next Steps

* Implement 5 classic LLD problems end-to-end
* Apply design patterns *only when justified*
* Practice explaining designs out loud

---

