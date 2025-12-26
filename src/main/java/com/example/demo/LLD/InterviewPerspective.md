# 🧠 How Interviewers Judge Your LLD (Real Signals & Rejection Reasons)

> **Goal:** Help you think like the interviewer so you *avoid silent rejections*.

This README is based on **real FAANG / Big-Tech interview rubrics**, not theory.

---

## 0️⃣ The Brutal Truth (Read First)

Most LLD interviews fail **silently**.

You may hear:

> “Good discussion, we’ll get back to you.”

But the internal feedback says:

> ❌ *Design lacks ownership clarity*
> ❌ *Over-engineered*
> ❌ *Did not consider extensibility*

This guide fixes that.

---

## 1️⃣ What Interviewers Are ACTUALLY Evaluating

LLD is judged on **thinking quality**, not code volume.

Interviewers score you on 5 dimensions:

1️⃣ Problem Understanding
2️⃣ Responsibility Assignment
3️⃣ Abstraction & Extensibility
4️⃣ Trade-off Awareness
5️⃣ Communication Clarity

❌ Patterns alone do NOT score points.

---

## 2️⃣ Strong Hire vs Weak Hire (Mental Comparison)

| Weak Candidate            | Strong Candidate       |
| ------------------------- | ---------------------- |
| Starts coding immediately | Clarifies requirements |
| Uses many patterns        | Uses minimal patterns  |
| Explains *what*           | Explains *why*         |
| Writes full code          | Writes clean skeleton  |
| Silent while coding       | Thinks out loud        |

📌 Silence kills LLD interviews.

---

## 3️⃣ Top REAL Rejection Reasons 🚨

### ❌ 1. God Classes

**Feedback:**

> “One class doing too much”

Why rejected:

* No SRP
* Hard to extend

How to fix:

* Split by responsibility
* Use services

---

### ❌ 2. No Change Isolation

**Feedback:**

> “Any new feature requires modifying existing code”

Why rejected:

* Violates Open/Closed Principle

How to fix:

* Identify what changes
* Introduce interfaces / strategies

---

### ❌ 3. Pattern Overuse (Very Common)

**Feedback:**

> “Over-engineered for the problem”

Why rejected:

* Complexity without justification

How to fix:

* Use patterns only when variation exists

---

### ❌ 4. No Ownership Clarity

**Feedback:**

> “Unclear which class owns this behavior”

Why rejected:

* Leads to bugs
* Poor maintainability

How to fix:

* Explicit responsibility assignment

---

### ❌ 5. Ignoring Concurrency

**Feedback:**

> “Did not consider thread safety”

Why rejected:

* Unrealistic for production systems

How to fix:

* Mention shared state
* Talk about locks / atomics

---

### ❌ 6. Over-coding

**Feedback:**

> “Too much low-level implementation”

Why rejected:

* Missed design focus

How to fix:

* Write interfaces + skeletons

---

## 4️⃣ What Gets You a STRONG HIRE Signal ✅

Interviewers write things like:

> ✅ *Clear responsibility boundaries*
> ✅ *Extensible design*
> ✅ *Correct abstraction level*
> ✅ *Good trade-off discussion*
> ✅ *Strong communication*

---

## 5️⃣ How Interviewers Score Trade-offs

You score points when you say:

> “This adds an abstraction, but it improves extensibility.”

> “This is simpler now, but harder to extend later.”

> “We’re trading memory for concurrency safety.”

📌 Acknowledging downsides = maturity.

---

## 6️⃣ Communication Matters MORE Than Code

### ❌ Weak Communication

* Silent coding
* No explanation

### ✅ Strong Communication

* Explains before coding
* Narrates decisions
* Invites feedback

📌 Think aloud constantly.

---

## 7️⃣ Interviewer’s Internal Scorecard (Simplified)

| Area          | Pass Signal                |
| ------------- | -------------------------- |
| Requirements  | Asked clarifying questions |
| Design        | Clear abstractions         |
| Extensibility | Easy to add features       |
| Concurrency   | At least discussed         |
| Communication | Clear reasoning            |

Miss 2+ → ❌ Reject

---

## 8️⃣ How to RECOVER During an Interview

Made a mistake?

Say:

> “Let me refactor this — this responsibility belongs elsewhere.”

📌 Self-correction is a **strong positive signal**.

---

## 9️⃣ Final Interview Checklist (Memorize This)

Before ending:

* Did I explain *why*?
* Did I isolate change?
* Did I discuss trade-offs?
* Did I mention concurrency?

If yes → you’re safe.

---

## 🧠 Final Truth

> **LLD interviews are not about being right.
> They are about being *reasonable*.**

Interviewers hire engineers they trust with evolving systems.

---

