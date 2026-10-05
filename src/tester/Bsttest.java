package tester;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import turner.BST;
import turner.Node;

class BSTTest {

    private BST<Integer> tree;

    @BeforeEach
    void setUp() {
        tree = new BST<>();
    }

    private void insertAll(int... values) {
        for (int v : values) {
            tree.insert(v);
        }
    }

    // ---------- empty tree ----------

    @Test
    @DisplayName("A new tree is empty in every observable way")
    void newTree_isEmpty() {
        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());
        assertEquals(-1, tree.height());
        assertNull(tree.getRoot());
        assertTrue(tree.inOrder().isEmpty());
        assertFalse(tree.contains(5));
    }

    // ---------- insert ----------

    @Test
    @DisplayName("First insert becomes the root")
    void insert_firstValueBecomesRoot() {
        assertTrue(tree.insert(10));

        assertFalse(tree.isEmpty());
        assertEquals(1, tree.size());
        assertEquals(0, tree.height());
        assertEquals(10, (int) tree.getRoot().getData());
        assertTrue(tree.getRoot().isLeaf());
    }

    @Test
    @DisplayName("Inserting null throws and leaves the tree unchanged")
    void insert_nullThrows() {
        assertThrows(IllegalArgumentException.class, () -> tree.insert(null));

        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());
    }

    @Test
    @DisplayName("Inserting null into a populated tree does not corrupt it")
    void insert_nullIntoPopulatedTree() {
        insertAll(5, 3, 8);

        assertThrows(IllegalArgumentException.class, () -> tree.insert(null));

        assertEquals(3, tree.size());
        assertEquals(Arrays.asList(3, 5, 8), tree.inOrder());
    }

    @Test
    @DisplayName("Smaller values go left, larger values go right, at every level")
    void insert_placesNodesCorrectly() {
        insertAll(50, 30, 70, 20, 40, 60, 80);

        Node<Integer> root = tree.getRoot();
        assertEquals(50, (int) root.getData());
        assertEquals(30, (int) root.getLeft().getData());
        assertEquals(70, (int) root.getRight().getData());
        assertEquals(20, (int) root.getLeft().getLeft().getData());
        assertEquals(40, (int) root.getLeft().getRight().getData());
        assertEquals(60, (int) root.getRight().getLeft().getData());
        assertEquals(80, (int) root.getRight().getRight().getData());

        assertFalse(root.isLeaf());
        assertTrue(root.getLeft().getLeft().isLeaf());
        assertTrue(root.getLeft().getRight().isLeaf());
        assertTrue(root.getRight().getLeft().isLeaf());
        assertTrue(root.getRight().getRight().isLeaf());
    }

    @Test
    @DisplayName("Each successful insert returns true and grows size by one")
    void insert_returnsTrueAndGrowsSize() {
        int[] values = {50, 30, 70, 20, 40, 60, 80};
        for (int i = 0; i < values.length; i++) {
            assertTrue(tree.insert(values[i]), "Insert of " + values[i] + " should succeed");
            assertEquals(i + 1, tree.size());
        }
    }

    @Test
    @DisplayName("Duplicate insert returns false and does not change size or contents")
    void insert_duplicateRejected() {
        assertTrue(tree.insert(5));
        assertFalse(tree.insert(5));

        assertEquals(1, tree.size());
        assertEquals(Arrays.asList(5), tree.inOrder());
    }

    @Test
    @DisplayName("Duplicate of a non-root value is rejected and the structure is unchanged")
    void insert_duplicateDeepInTree() {
        insertAll(50, 30, 70, 20, 40);
        int heightBefore = tree.height();

        assertFalse(tree.insert(40));
        assertFalse(tree.insert(20));
        assertFalse(tree.insert(50));

        assertEquals(5, tree.size());
        assertEquals(heightBefore, tree.height());
        assertEquals(Arrays.asList(20, 30, 40, 50, 70), tree.inOrder());
    }

    @Test
    @DisplayName("Negative numbers, zero, and integer extremes are ordered correctly")
    void insert_negativesZeroAndExtremes() {
        insertAll(0, -5, 5, -10, Integer.MAX_VALUE, Integer.MIN_VALUE);

        assertEquals(
                Arrays.asList(Integer.MIN_VALUE, -10, -5, 0, 5, Integer.MAX_VALUE),
                tree.inOrder());
        assertTrue(tree.contains(Integer.MIN_VALUE));
        assertTrue(tree.contains(Integer.MAX_VALUE));
        assertTrue(tree.contains(0));
    }

    // ---------- contains ----------

    @Test
    @DisplayName("contains finds every inserted value")
    void contains_findsEveryInsertedValue() {
        int[] values = {50, 30, 70, 20, 40, 60, 80};
        insertAll(values);

        for (int v : values) {
            assertTrue(tree.contains(v), v + " should be found");
        }
    }

    @Test
    @DisplayName("contains rejects values that were never inserted")
    void contains_rejectsAbsentValues() {
        insertAll(50, 30, 70, 20, 40, 60, 80);

        int[] absent = {10, 25, 35, 45, 55, 65, 75, 85, 100, 0, -1};
        for (int v : absent) {
            assertFalse(tree.contains(v), v + " should not be found");
        }
    }

    @Test
    @DisplayName("contains(null) returns false instead of throwing")
    void contains_nullReturnsFalse() {
        assertFalse(tree.contains(null), "Empty tree");
        insertAll(1, 2, 3);
        assertFalse(tree.contains(null), "Populated tree");
    }

    @Test
    @DisplayName("contains works on a single-node tree")
    void contains_singleNode() {
        tree.insert(7);

        assertTrue(tree.contains(7));
        assertFalse(tree.contains(6));
        assertFalse(tree.contains(8));
    }

    // ---------- inOrder ----------

    @Test
    @DisplayName("inOrder returns values sorted regardless of insertion order")
    void inOrder_isSorted() {
        insertAll(50, 30, 70, 20, 40, 60, 80);

        assertEquals(Arrays.asList(20, 30, 40, 50, 60, 70, 80), tree.inOrder());
    }

    @Test
    @DisplayName("Different insertion orders of the same values give the same inOrder result")
    void inOrder_independentOfInsertionOrder() {
        BST<Integer> other = new BST<>();
        insertAll(1, 2, 3, 4, 5);
        for (int v : new int[] {5, 3, 1, 4, 2}) {
            other.insert(v);
        }

        assertEquals(tree.inOrder(), other.inOrder());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), other.inOrder());
    }

    @Test
    @DisplayName("Modifying the list returned by inOrder does not affect the tree")
    void inOrder_returnsIndependentList() {
        insertAll(3, 1, 2);

        List<Integer> snapshot = tree.inOrder();
        snapshot.clear();

        assertEquals(3, tree.size());
        assertEquals(Arrays.asList(1, 2, 3), tree.inOrder());
    }

    // ---------- height ----------

    @Test
    @DisplayName("Height of a perfectly balanced 7-node tree is 2")
    void height_balanced() {
        insertAll(50, 30, 70, 20, 40, 60, 80);

        assertEquals(2, tree.height());
    }

    @Test
    @DisplayName("Height of a two-node tree is 1")
    void height_twoNodes() {
        insertAll(10, 5);

        assertEquals(1, tree.height());
    }

    @Test
    @DisplayName("Ascending insertion degenerates into a chain: height = n - 1")
    void height_ascendingChain() {
        insertAll(1, 2, 3, 4, 5);

        assertEquals(4, tree.height());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), tree.inOrder());
    }

    @Test
    @DisplayName("Descending insertion degenerates into a chain: height = n - 1")
    void height_descendingChain() {
        insertAll(5, 4, 3, 2, 1);

        assertEquals(4, tree.height());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), tree.inOrder());
    }

    // ---------- clear ----------

    @Test
    @DisplayName("clear empties the tree completely")
    void clear_emptiesTree() {
        insertAll(50, 30, 70);

        tree.clear();

        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());
        assertEquals(-1, tree.height());
        assertNull(tree.getRoot());
        assertTrue(tree.inOrder().isEmpty());
        assertFalse(tree.contains(50));
        assertFalse(tree.contains(30));
        assertFalse(tree.contains(70));
    }

    @Test
    @DisplayName("A cleared tree can be reused, and old values can be reinserted")
    void clear_treeIsReusable() {
        insertAll(1, 2, 3);
        tree.clear();

        assertTrue(tree.insert(2), "Previously stored value should insert again");
        assertTrue(tree.insert(9));

        assertEquals(2, tree.size());
        assertEquals(Arrays.asList(2, 9), tree.inOrder());
        assertFalse(tree.contains(1));
    }

    @Test
    @DisplayName("clear on an empty tree is harmless")
    void clear_emptyTree() {
        assertDoesNotThrow(() -> tree.clear());

        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());
    }

    // ---------- Strings (what Dictionary actually stores) ----------

    @Test
    @DisplayName("String tree sorts alphabetically and finds every word")
    void strings_sortedAndFound() {
        BST<String> words = new BST<>();
        for (String w : new String[] {"banana", "apple", "cherry", "date", "elderberry"}) {
            assertTrue(words.insert(w));
        }

        assertEquals(
                Arrays.asList("apple", "banana", "cherry", "date", "elderberry"),
                words.inOrder());
        for (String w : new String[] {"banana", "apple", "cherry", "date", "elderberry"}) {
            assertTrue(words.contains(w), w + " should be found");
        }
    }

    @Test
    @DisplayName("String lookups are exact: prefixes, extensions, and similar words are not matches")
    void strings_exactMatchOnly() {
        BST<String> words = new BST<>();
        words.insert("apple");

        assertFalse(words.contains("app"), "prefix");
        assertFalse(words.contains("apples"), "extension");
        assertFalse(words.contains("appl"), "truncation");
        assertFalse(words.contains("aple"), "typo");
        assertFalse(words.contains(""), "empty string");
        assertTrue(words.contains("apple"));
    }

    @Test
    @DisplayName("BST itself is case-sensitive (Dictionary is responsible for lowercasing)")
    void strings_caseSensitive() {
        BST<String> words = new BST<>();

        assertTrue(words.insert("apple"));
        assertTrue(words.insert("Apple"), "Different case is a different value to the BST");

        assertEquals(2, words.size());
        assertEquals(Arrays.asList("Apple", "apple"), words.inOrder());
        assertFalse(words.contains("APPLE"));
    }

    @Test
    @DisplayName("Empty string is a legal value")
    void strings_emptyString() {
        BST<String> words = new BST<>();

        assertTrue(words.insert(""));
        assertTrue(words.contains(""));
        assertEquals(1, words.size());
    }

    // ---------- scale ----------

    @Test
    @DisplayName("1000 shuffled values: all stored, all found, near-misses rejected, output sorted")
    void largeShuffledInput() {
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            values.add(i * 3); // multiples of 3, so i*3+1 is never present
        }
        List<Integer> sorted = new ArrayList<>(values);
        Collections.shuffle(values, new Random(42));

        for (int v : values) {
            assertTrue(tree.insert(v), v + " should insert");
        }

        assertEquals(1000, tree.size());
        for (int i = 0; i < 1000; i++) {
            assertTrue(tree.contains(i * 3), (i * 3) + " should be found");
            assertFalse(tree.contains(i * 3 + 1), (i * 3 + 1) + " should not be found");
        }
        assertEquals(sorted, tree.inOrder());
    }
}