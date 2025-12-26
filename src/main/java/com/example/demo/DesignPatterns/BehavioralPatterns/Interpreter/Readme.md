# 🧩 Interpreter Design Pattern – Deep Dive

> **Mental model:** The Interpreter pattern provides a way to evaluate sentences in a language by building an interpreter that processes that language. It involves defining a grammatical representation for a language and an interpreter to interpret that grammar.

---

## 🔍 Problem (Realistic Scenario)

Imagine you are building a rules engine for a financial application. Users need to define custom rules for fraud detection, such as `"amount > 10000 AND (country == 'US' OR device != 'MOBILE')"`.

You need a way to parse and evaluate these rule strings. The language is simple, composed of expressions (`> 10000`), logical operators (`AND`, `OR`), and parentheses.

A naive approach would be to write a monolithic parser function with a large `if-else` or `switch` statement to handle all the possible combinations of symbols and operators. You might use regular expressions to find and replace parts of the string.

```java
// NOT a good approach
public boolean evaluate(String rule, Transaction tx) {
    // This would become an unmanageable mess of string splitting, regex, and nested ifs.
    if (rule.contains("AND")) {
        // ... split the rule and recursively call ...
    } else if (rule.contains("OR")) {
        // ... it gets complicated very fast ...
    } else if (rule.contains(">")) {
        // ... parse the amount and compare ...
    }
    // ... this is not scalable or maintainable.
}
```

This approach is problematic because:

1.  **Hard to Maintain:** The parser logic is complex and centralized. Adding a new operator (e.g., `STARTS_WITH`) would require modifying the entire monolithic function.
2.  **Not Extensible:** The grammar is hardcoded. It’s difficult to add new types of expressions without significant rework.
3.  **Complex Logic:** The code becomes hard to read and debug.

---

## ✅ Interpreter Solution

The Interpreter pattern solves this by representing the grammar of the language using a set of classes. Each rule in the grammar (e.g., a terminal symbol, a non-terminal symbol, an operator) is represented by a class.

You build an Abstract Syntax Tree (AST) of these objects. To evaluate the rule, you simply call an `interpret()` method on the top-level node of the tree, which recursively calls `interpret()` on its children.

### 🧱 Structure

For a language, you typically have `TerminalExpression` (like a number or a variable) and `NonTerminalExpression` (like addition or subtraction that combines other expressions).

```
+----------------------+
| AbstractExpression   |
| (e.g., Expression)   |
|----------------------|
| + interpret(context) |
+----------------------+
          ^
          |
+---------+---------------------+
|                               |
+-----------------------+     +---------------------------+
|  TerminalExpression   |     |   NonTerminalExpression   |
| (e.g., Number)        |     | (e.g., Add, Subtract)     |
|-----------------------|     |---------------------------|
| + interpret(context)  |     | - left:  Expression       |
+-----------------------+     | - right: Expression       |
                              | + interpret(context)      |
                              +---------------------------+
```

-   **AbstractExpression (`Expression`):** An interface or abstract class with an `interpret` method.
-   **TerminalExpression (`NumberExpression`):** Represents a literal or a variable in the grammar. It has no children.
-   **NonTerminalExpression (`AddExpression`, `AndExpression`):** Represents a composition of other expressions (e.g., `left + right`). It holds references to other `Expression` objects.
-   **Context:** An object that contains global information (like variable values) that the interpreter might need.

### ☕ Java Example

Let's implement a simple interpreter for basic math operations: `+` and `-`.
We want to parse and evaluate a string like `"5 + 10 - 3"`.

#### 1. The Expression Interface

```java
// The AbstractExpression
public interface Expression {
    int interpret();
}
```

#### 2. The Terminal Expression

This represents a number.

```java
// The TerminalExpression
public class NumberExpression implements Expression {
    private final int number;

    public NumberExpression(int number) {
        this.number = number;
    }

    public NumberExpression(String number) {
        this.number = Integer.parseInt(number.trim());
    }

    @Override
    public int interpret() {
        return this.number;
    }
}
```

#### 3. The Non-Terminal Expressions

These represent the operations.

```java
// A NonTerminalExpression for Addition
public class AddExpression implements Expression {
    private final Expression left;
    private final Expression right;

    public AddExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int interpret() {
        return left.interpret() + right.interpret();
    }
}

// A NonTerminalExpression for Subtraction
public class SubtractExpression implements Expression {
    private final Expression left;
    private final Expression right;

    public SubtractExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int interpret() {
        return left.interpret() - right.interpret();
    }
}
```

#### 4. The Parser (Client Code)

The client is responsible for parsing the input string and building the Abstract Syntax Tree.

```java
import java.util.Stack;

public class Application {
    public static Expression parse(String expressionStr) {
        Stack<Expression> stack = new Stack<>();
        String[] tokens = expressionStr.split(" ");

        for (String token : tokens) {
            if (token.equals("+")) {
                Expression right = stack.pop();
                Expression left = stack.pop();
                stack.push(new AddExpression(left, right));
            } else if (token.equals("-")) {
                Expression right = stack.pop();
                Expression left = stack.pop();
                stack.push(new SubtractExpression(left, right));
            } else {
                stack.push(new NumberExpression(token));
            }
        }
        // In a real implementation, you'd need to handle operator precedence (e.g. using two stacks - Shunting-yard algorithm)
        // This simple example assumes left-to-right evaluation.
        // A better input for this parser would be Reverse Polish Notation: "10 5 + 3 -"

        // To make "5 + 10 - 3" work, we can adjust the parsing logic.
        // Let's re-parse for a simple left-to-right evaluation.

        // Assume expression is "5 + 10"
        Expression left = new NumberExpression(tokens[0]); // 5
        for(int i = 1; i < tokens.length; i += 2) {
            String operator = tokens[i];
            Expression right = new NumberExpression(tokens[i+1]);
            if (operator.equals("+")) {
                left = new AddExpression(left, right);
            } else if (operator.equals("-")) {
                left = new SubtractExpression(left, right);
            }
        }
        return left;
    }

    public static void main(String[] args) {
        String expressionStr = "5 + 10 - 3"; // Should evaluate to 12

        // The parser builds the Abstract Syntax Tree
        // The tree would look like: Subtract( Add(5, 10), 3 )
        Expression expression = parse(expressionStr);

        // Interpret the expression
        int result = expression.interpret();
        System.out.println("Result of '" + expressionStr + "' is: " + result);

    }
}
```
*Note: The parser logic in a real-world scenario is the most complex part and often involves algorithms like Shunting-yard to handle operator precedence and parentheses correctly. The example above uses a simplified parser for clarity.* 

---

## ✔ When to Use the Interpreter Pattern

-   **Simple Language:** When you need to interpret a simple language and can represent sentences in that language as an Abstract Syntax Tree.
-   **Extensible Grammar:** When the grammar is relatively simple, but you expect to add new operators or expressions later. Adding a new `MultiplyExpression` class is much cleaner than modifying a giant parser function.
-   **Common Interface:** When you have many different variations of operations that can be composed together.

## ❌ When to Avoid

-   **Complex Grammars:** The pattern is not suitable for complex grammars. For these, using a dedicated parser generator tool like ANTLR or JavaCC is a much better approach. The number of classes can become unmanageable.
-   **Performance-Critical Applications:** The pattern involves creating many small objects, which might not be the most performant solution for high-throughput systems.

## 💡 Interview Line

> **“The Interpreter pattern is used to define a grammar for a simple language and provide an interpreter to evaluate expressions in that language. You model the problem as a language and build an Abstract Syntax Tree of objects to represent an expression. Evaluation is done by calling an `interpret` method on the tree. It’s great for simple rule engines or query languages, but for complex grammars, a parser generator is better.”**

---

## 🚀 Next Steps

-   Explore the **Composite Pattern**. The Abstract Syntax Tree in the Interpreter pattern is a classic example of a Composite structure.
-   Look into parser generator tools like **ANTLR**. They automate the process of creating a lexer, parser, and AST from a formal grammar definition, effectively implementing a much more robust version of the Interpreter pattern for you.
