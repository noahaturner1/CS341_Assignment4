package tester;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import turner.Node;

class NodeTest {

    // ---------- construction ----------

    @Test
    @DisplayName("Constructor stores the data and starts with no children")
    void constructor_storesDataAndHasNoChildren() {
        Node<String> node = new Node<>("hello");

        assertEquals("hello", node.getData());
        assertNull(node.getLeft(), "New node should have no left child");
        assertNull(node.getRight(), "New node should have no right child");
    }

    @Test
    @DisplayName("A brand-new node is a leaf")
    void constructor_newNodeIsLeaf() {
        assertTrue(new Node<>("x").isLeaf());
    }

    @Test
    @DisplayName("Constructor accepts null data without throwing")
    void constructor_nullDataAllowed() {
        Node<String> node = new Node<>(null);

        assertNull(node.getData());
        assertTrue(node.isLeaf());
    }

    // ---------- data ----------

    @Test
    @DisplayName("setData replaces the stored data")
    void setData_replacesData() {
        Node<String> node = new Node<>("old");
        node.setData("new");

        assertEquals("new", node.getData());
    }

    @Test
    @DisplayName("Node works with other Comparable types (Integer)")
    void node_worksWithIntegers() {
        Node<Integer> node = new Node<>(42);

        assertEquals(42, (int) node.getData());
        node.setData(-7);
        assertEquals(-7, (int) node.getData());
    }

    // ---------- children ----------

    @Test
    @DisplayName("setLeft stores the exact child object and makes the node a non-leaf")
    void setLeft_storesChild() {
        Node<String> parent = new Node<>("m");
        Node<String> child = new Node<>("a");

        parent.setLeft(child);

        assertSame(child, parent.getLeft());
        assertNull(parent.getRight(), "Setting left must not touch right");
        assertFalse(parent.isLeaf());
    }

    @Test
    @DisplayName("setRight stores the exact child object and makes the node a non-leaf")
    void setRight_storesChild() {
        Node<String> parent = new Node<>("m");
        Node<String> child = new Node<>("z");

        parent.setRight(child);

        assertSame(child, parent.getRight());
        assertNull(parent.getLeft(), "Setting right must not touch left");
        assertFalse(parent.isLeaf());
    }

    @Test
    @DisplayName("Node with both children is not a leaf and keeps them separate")
    void bothChildren_keptSeparate() {
        Node<String> parent = new Node<>("m");
        Node<String> left = new Node<>("a");
        Node<String> right = new Node<>("z");

        parent.setLeft(left);
        parent.setRight(right);

        assertSame(left, parent.getLeft());
        assertSame(right, parent.getRight());
        assertNotSame(parent.getLeft(), parent.getRight());
        assertFalse(parent.isLeaf());
        assertTrue(left.isLeaf());
        assertTrue(right.isLeaf());
    }

    @Test
    @DisplayName("Setting a child to null makes the node a leaf again")
    void setChildNull_restoresLeaf() {
        Node<String> parent = new Node<>("m");
        parent.setLeft(new Node<>("a"));
        parent.setRight(new Node<>("z"));

        parent.setLeft(null);
        assertFalse(parent.isLeaf(), "Right child still present");
        parent.setRight(null);

        assertNull(parent.getLeft());
        assertNull(parent.getRight());
        assertTrue(parent.isLeaf());
    }

    @Test
    @DisplayName("Nodes can be chained to form a deeper path")
    void chainedNodes_traversable() {
        Node<String> a = new Node<>("a");
        Node<String> b = new Node<>("b");
        Node<String> c = new Node<>("c");

        a.setRight(b);
        b.setRight(c);

        assertEquals("c", a.getRight().getRight().getData());
        assertTrue(c.isLeaf());
        assertFalse(a.isLeaf());
        assertFalse(b.isLeaf());
    }

    // ---------- toString ----------

    @Test
    @DisplayName("toString returns the data as a String")
    void toString_returnsData() {
        assertEquals("hello", new Node<>("hello").toString());
        assertEquals("42", new Node<>(42).toString());
    }

    @Test
    @DisplayName("toString does not throw when data is null")
    void toString_nullData() {
        assertEquals("null", new Node<String>(null).toString());
    }
}