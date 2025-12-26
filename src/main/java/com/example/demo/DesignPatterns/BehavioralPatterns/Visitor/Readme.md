# 🧩 Visitor Design Pattern – Deep Dive

> **Mental model:** The Visitor pattern lets you add new operations to an object structure without modifying the objects themselves. It achieves this by creating an external "visitor" object that can "visit" each element in the structure and perform an operation.

---

## 🔍 Problem (Realistic Scenario)

Imagine you have a complex object structure, such as a hierarchical document model composed of different types of nodes (e.g., `Paragraph`, `Heading`, `Image`). This structure is stable and you don't want to change it often.

```java
// The object structure
interface DocumentNode {}
class HeadingNode implements DocumentNode {}
class ParagraphNode implements DocumentNode {}
class ImageNode implements DocumentNode {}

List<DocumentNode> document = Arrays.asList(new HeadingNode(), new ParagraphNode(), new ImageNode());
```

Now, you need to add various operations to be performed on this structure. For example:

1.  **HTML Export:** Convert each node into its HTML representation.
2.  **Text Extraction:** Extract only the plain text from the document, ignoring images.
3.  **Markdown Conversion:** Convert the document to Markdown format.

A naive approach would be to add a new method to each class in the object structure for every new operation.

```java
// NOT a good approach
interface DocumentNode {
    void toHtml();      // Operation 1
    String toText();    // Operation 2
    String toMarkdown(); // Operation 3
}

class HeadingNode implements DocumentNode {
    public void toHtml() { /* ... */ }
    public String toText() { /* ... */ }
    public String toMarkdown() { /* ... */ }
}
// ... and so on for every class
```

This approach is terrible for several reasons:

1.  **Violation of Single Responsibility Principle:** The node classes (`HeadingNode`, `ParagraphNode`) are no longer just responsible for representing the document part; they are also responsible for HTML conversion, text extraction, etc.
2.  **Violation of Open/Closed Principle:** Every time you want to add a *new operation* (e.g., "PDF Export"), you have to modify *every single class* in your document structure. This is brittle and error-prone.
3.  **Code Bloat:** The node classes become cluttered with unrelated logic.

---

## ✅ Visitor Solution

The Visitor pattern moves the operational logic into a separate class called a `Visitor`. To make this work, each element in the object structure must have a method (commonly named `accept`) that takes a visitor object as an argument. The element then calls the visitor's method that corresponds to its own class.

This technique, known as **double dispatch**, allows you to achieve different behavior based on both the type of the element and the type of the visitor.

### 🧱 Structure

```
+------------------+         +------------------+       +--------------------+
|      Client      | uses    | ObjectStructure  |       | Element (Interface)| 
+------------------+         | (e.g., Document) |       |--------------------|
                             +------------------+       | + accept(Visitor)  |
                                      |               +--------------------+
                                      |                        ^
                                      | (contains)             | (implements)
                             +--------+------------------------+
                             |                                 |
                     +-----------------+               +-----------------+
                     | ConcreteElementA|               | ConcreteElementB|
                     | (e.g., Heading) |               | (e.g., Paragraph)|
                     +-----------------+               +-----------------+


+--------------------+         +-----------------------+
| Visitor (Interface)|         |   ConcreteVisitorA    |
|--------------------|         | (e.g., HtmlExporter)  |
| + visit(ElementA)  |         |-----------------------|
| + visit(ElementB)  |         | + visit(Heading) {}   |
+--------------------+         | + visit(Paragraph) {} |
           ^                   +-----------------------+
           | (implements)
           |                   +-----------------------+
           +-------------------|   ConcreteVisitorB    |
                               | (e.g., TextExtractor) |
                               |-----------------------|
                               | + visit(Heading) {}   |
                               | + visit(Paragraph) {} |
                               +-----------------------+
```

### ☕ Java Example

#### 1. The Element Interface and Concrete Elements

The key is the `accept` method, which enables the double dispatch.

```java
// The Element interface
interface DocumentPart {
    void accept(DocumentVisitor visitor);
}

// Concrete Element A
class Heading implements DocumentPart {
    public final String text;
    public Heading(String text) { this.text = text; }

    @Override
    public void accept(DocumentVisitor visitor) {
        visitor.visit(this); // Double dispatch: calls the correct method on the visitor
    }
}

// Concrete Element B
class Paragraph implements DocumentPart {
    public final String text;
    public Paragraph(String text) { this.text = text; }

    @Override
    public void accept(DocumentVisitor visitor) {
        visitor.visit(this); // Double dispatch
    }
}
```

#### 2. The Visitor Interface

It must have a `visit` method for each concrete element type.

```java
// The Visitor interface
interface DocumentVisitor {
    void visit(Heading heading);
    void visit(Paragraph paragraph);
}
```

#### 3. Concrete Visitor Implementations

Each operation becomes its own visitor class.

```java
// Concrete Visitor 1: HTML Export
class HtmlExporter implements DocumentVisitor {
    private StringBuilder sb = new StringBuilder();

    @Override
    public void visit(Heading heading) {
        sb.append("<h1>").append(heading.text).append("</h1>\n");
    }

    @Override
    public void visit(Paragraph paragraph) {
        sb.append("<p>").append(paragraph.text).append("</p>\n");
    }

    public String getHtml() { return sb.toString(); }
}

// Concrete Visitor 2: Plain Text Extraction
class TextExtractor implements DocumentVisitor {
    private StringBuilder sb = new StringBuilder();

    @Override
    public void visit(Heading heading) {
        sb.append(heading.text).append("\n");
    }

    @Override
    public void visit(Paragraph paragraph) {
        sb.append(paragraph.text).append("\n");
    }

    public String getText() { return sb.toString(); }
}
```

#### 4. Client Code

The client creates a structure, creates a visitor, and passes the visitor to the structure.

```java
import java.util.Arrays;
import java.util.List;

public class Application {
    public static void main(String[] args) {
        // The object structure is a list of DocumentPart objects
        List<DocumentPart> document = Arrays.asList(
            new Heading("The Visitor Pattern"),
            new Paragraph("A pattern to separate algorithms from the objects they operate on.")
        );

        // Use the HtmlExporter visitor to get HTML
        HtmlExporter htmlExporter = new HtmlExporter();
        for (DocumentPart part : document) {
            part.accept(htmlExporter);
        }
        System.out.println("--- HTML Output ---");
        System.out.println(htmlExporter.getHtml());

        // Use the TextExtractor visitor to get plain text
        TextExtractor textExtractor = new TextExtractor();
        for (DocumentPart part : document) {
            part.accept(textExtractor);
        }
        System.out.println("--- Plain Text Output ---");
        System.out.println(textExtractor.getText());
    }
}
```

---

## ✔ When to Use the Visitor Pattern

-   **Stable Object Structure, New Operations:** The primary use case is when you have a stable set of classes (the elements) but you anticipate needing to add many new operations that work on these classes.
-   **Avoiding Class Pollution:** When you want to keep your data-centric classes clean from operations that don't belong there.
-   **Complex Operations:** When an operation needs to perform different actions on different classes within the structure.

## ❌ When to Avoid

-   **Unstable Object Structure:** The biggest drawback of the Visitor pattern is that if you need to add a new `ConcreteElement` class, you must update the `Visitor` interface and *every single concrete visitor class*. This makes the element hierarchy difficult to change.

## 💡 Interview Line

> **“The Visitor pattern lets you add new operations to an existing object structure without modifying the classes of that structure. It’s ideal when the object structure is stable, but you need to frequently add new functions that operate on it. It works by using a technique called double dispatch to apply the correct logic for each element type.”**

---

## 🚀 Next Steps

-   Explore the **Composite Pattern**. Visitor is very often used to perform operations on a tree-like structure built using the Composite pattern.
-   Contrast this with the **Strategy Pattern**, which is about swapping out entire algorithms, whereas Visitor is about applying an external operation to a set of different but related classes.
