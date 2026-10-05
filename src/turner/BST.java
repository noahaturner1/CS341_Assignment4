package turner;

import java.util.ArrayList;
import java.util.List;

/**
 * A binary search tree. Duplicate values are ignored.
 *
 * @param <T> the type of data stored; must be comparable
 */
public class BST<T extends Comparable<T>> {

    private Node<T> root;
    private int size;

    public BST() {
        this.root = null;
        this.size = 0;
    }

    /**
     * Inserts a value into the tree.
     *
     * @return true if the value was added, false if it was already present
     */
    public boolean insert(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot insert null into the BST");
        }
        if (root == null) {
            root = new Node<>(value);
            size++;
            return true;
        }

        Node<T> current = root;
        while (true) {
            int cmp = value.compareTo(current.getData());
            if (cmp == 0) {
                return false; // duplicate
            } else if (cmp < 0) {
                if (current.getLeft() == null) {
                    current.setLeft(new Node<>(value));
                    size++;
                    return true;
                }
                current = current.getLeft();
            } else {
                if (current.getRight() == null) {
                    current.setRight(new Node<>(value));
                    size++;
                    return true;
                }
                current = current.getRight();
            }
        }
    }

    /**
     * Returns true if the value exists in the tree.
     */
    public boolean contains(T value) {
        if (value == null) {
            return false;
        }
        Node<T> current = root;
        while (current != null) {
            int cmp = value.compareTo(current.getData());
            if (cmp == 0) {
                return true;
            }
            current = (cmp < 0) ? current.getLeft() : current.getRight();
        }
        return false;
    }

    /**
     * Returns all values in sorted (in-order) order.
     */
    public List<T> inOrder() {
        List<T> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    private void inOrder(Node<T> node, List<T> result) {
        if (node == null) {
            return;
        }
        inOrder(node.getLeft(), result);
        result.add(node.getData());
        inOrder(node.getRight(), result);
    }

    /**
     * Height of the tree: -1 for an empty tree, 0 for a single node.
     */
    public int height() {
        return height(root);
    }

    private int height(Node<T> node) {
        if (node == null) {
            return -1;
        }
        return 1 + Math.max(height(node.getLeft()), height(node.getRight()));
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void clear() {
        root = null;
        size = 0;
    }

    public Node<T> getRoot() {
        return root;
    }
}