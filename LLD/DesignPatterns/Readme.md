# 🚀 Low Level Design (LLD) & Design Patterns – Interview-Ready Guide

> **Goal:** Think like a software engineer, not a pattern memorizer.
>
> This README captures *how interviewers expect you to reason*, *why patterns exist*, and *how to apply them cleanly under pressure*.

---

## 0️⃣ What Is a Design Pattern (Really?)

A **Design Pattern** is:

> **A proven solution to a recurring design problem in a specific context, with trade-offs.**

### ❌ What It Is NOT

* A library
* A framework
* A rule
* Boilerplate code

### ✅ What It IS

* A **mental model**
* A **shared vocabulary** ("Use Strategy here")
* A **decision-making tool**

📌 **Interview Truth**

> Interviewers don’t test *pattern syntax*.

They test:

* Why this pattern?
* Why **not** another?
* Trade-offs
* Extensibility
* Change tolerance

---

## 1️⃣ Why Design Patterns Exist (Core Reasoning)

### ❌ Code Without Patterns Leads To

* Tight coupling
* If–else explosions
* God classes
* Hard-to-test code
* Fear of change

### 🧠 Root Cause

> **Change is inevitable.**

Requirements evolve:

* New features
* New rules
* New integrations

### ✅ Patterns Help You

* Isolate change
* Program to interfaces
* Favor composition over inheritance
* Apply SOLID *naturally*

📌 **Golden Rule**

> **Patterns exist to manage change — not to look smart.**

---

## 2️⃣ Pattern Categories (Why Only These 3?)

| Category   | What Changes?      | Examples           |
| ---------- | ------------------ | ------------------ |
| Creational | Object creation    | Factory, Builder   |
| Structural | Object composition | Adapter, Decorator |
| Behavioral | Object interaction | Strategy, Observer |

📌 This classification is about **WHAT VARIES**, not how code looks.

---

## 3️⃣ How Interviewers Expect You to Think (Critical Section)

### ❌ Bad Approach

> "I’ll use Factory + Singleton + Strategy + Observer"

### ✅ Correct Approach

1. Identify **what changes**
2. Identify **who owns the change**
3. Isolate it behind an interface
4. Choose the **simplest pattern** that works

📌 Pattern selection is a *consequence*, not a starting point.

---

## 4️⃣ Most Important LLD Principles (Non‑Negotiable)

### 1. Single Responsibility Principle (SRP)

* One class → one reason to change

### 2. Open/Closed Principle (OCP)

* Add behavior without modifying existing code

### 3. Program to Interfaces

* Depend on abstractions, not implementations

### 4. Favor Composition over Inheritance

* Composition = flexible
* Inheritance = rigid

### 5. Explicit Dependencies

* No hidden creation (`new` everywhere)

---

## 5️⃣ Core Patterns You MUST Master (Interview Tier)

### 🔥 Tier‑1 (Absolute Must)

* Strategy
* Factory (Simple + Factory Method)
* Observer
* Decorator
* Singleton (with trade-offs)

### ⚡ Tier‑2 (Very Common)

* Builder
* Adapter
* Command
* Template Method

### 🧠 Tier‑3 (Situational)

* State
* Chain of Responsibility
* Proxy

📌 **If you explain Strategy, Factory, Observer well → 70% LLD problems solved.**

---

## 6️⃣ Pattern → Real Interview Mapping

| Problem               | Pattern Used        |
| --------------------- | ------------------- |
| Payment methods       | Strategy            |
| Notification system   | Observer            |
| Object creation logic | Factory             |
| Feature add-ons       | Decorator           |
| Elevator states       | State               |
| API rate limiter      | Strategy + Template |

---

## 7️⃣ Common Interview Traps 🚨

❌ Overengineering

* Using patterns when `if` is enough

❌ God Interfaces

* One interface with 10 methods

❌ Pattern Stacking

* Using multiple patterns without justification

❌ Premature Abstraction

* Abstracting things that don’t change

📌 **Rule:** Don’t abstract until variation exists or is very likely.

---

## 8️⃣ How to Answer "Why This Pattern?" (Template)

> "This part of the system is expected to change independently.
>
> Using **X pattern**, we isolate that change behind an interface.
>
> This improves extensibility and testability at the cost of an extra abstraction, which is acceptable here."

---

## 9️⃣ LLD Interview Execution Strategy (Step‑by‑Step)

1️⃣ Clarify requirements
2️⃣ Identify entities
3️⃣ Identify changing behaviors
4️⃣ Define interfaces
5️⃣ Apply minimal patterns
6️⃣ Talk about trade-offs
7️⃣ Mention extensibility hooks

📌 Silence kills interviews — explain your reasoning aloud.

---

## 🔟 Mental Checklist Before Writing Code

* What changes most often?
* What should NOT know about each other?
* Can I add a new feature without modifying old code?
* Is this abstraction justified?

---

## 🧠 Final Interview Wisdom

> **Patterns don’t make designs good.
> Good reasoning makes designs good.**

If you can *explain* your design clearly, interviewers assume you can code it.

---

## 📚 Next Steps

* Implement each Tier‑1 pattern from scratch
* Apply patterns to:

  * Elevator System
  * Rate Limiter
  * Splitwise
  * Parking Lot

---

