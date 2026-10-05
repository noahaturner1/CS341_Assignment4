package tester;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import turner.Dictionary;

class DictionaryTest {

    // ---------- helpers ----------

    private static void assertAllValid(Dictionary d, String... words) {
        for (String w : words) {
            assertTrue(d.isValidWord(w), "Expected VALID: \"" + w + "\"");
        }
    }

    private static void assertAllInvalid(Dictionary d, String... words) {
        for (String w : words) {
            assertFalse(d.isValidWord(w), "Expected INVALID: \"" + w + "\"");
        }
    }

    /** Letters-only word unique to n (base-26), so generated words never collide. */
    private static String wordFor(int n) {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append((char) ('a' + (n % 26)));
            n /= 26;
        } while (n > 0);
        return sb.toString();
    }

    // ---------- construction ----------

    @Test
    @DisplayName("Default constructor makes an empty dictionary: every lookup is false")
    void defaultConstructor_isEmpty() {
        Dictionary d = new Dictionary();

        assertAllInvalid(d, "hello", "a", "the", "");
    }

    @Test
    @DisplayName("Null paragraph does not throw and yields an empty dictionary")
    void constructor_nullParagraph() {
        assertDoesNotThrow(() -> {
            new Dictionary(null);
        });

        Dictionary d = new Dictionary(null);
        assertAllInvalid(d, "hello", "null", "");
    }

    @Test
    @DisplayName("Empty and whitespace-only paragraphs yield an empty dictionary")
    void constructor_emptyAndWhitespaceParagraphs() {
        for (String paragraph : new String[] {"", " ", "     ", "\t", "\n", "\t\n\r\n  "}) {
            Dictionary d = new Dictionary(paragraph);
            assertAllInvalid(d, "hello", "a", "");
        }
    }

    @Test
    @DisplayName("Punctuation-and-digit-only paragraph yields an empty dictionary")
    void constructor_noLettersParagraph() {
        Dictionary d = new Dictionary("... !!! ??? --- ''' 123 4567 ,;:");

        assertAllInvalid(d, "hello", "123", "4567", "...", "'", "---");
    }

    @Test
    @DisplayName("Single-word paragraph")
    void constructor_singleWord() {
        Dictionary d = new Dictionary("hello");

        assertAllValid(d, "hello");
        assertAllInvalid(d, "hell", "hellos", "ello", "world");
    }

    @Test
    @DisplayName("Single-letter words are stored")
    void constructor_singleLetterWords() {
        Dictionary d = new Dictionary("I am a b c");

        assertAllValid(d, "i", "I", "am", "a", "b", "c");
        assertAllInvalid(d, "d", "z");
    }

    // ---------- every word stored and retrievable ----------

    @Test
    @DisplayName("Every word in the paragraph (first, middle, and LAST) is stored and found")
    void everyWordStored_includingFirstAndLast() {
        Dictionary d = new Dictionary("The quick brown fox jumps over the lazy dog");

        assertAllValid(d, "The", "quick", "brown", "fox", "jumps", "over", "lazy", "dog");
        assertAllValid(d, "the");   // repeated word, lowercase form
        assertAllValid(d, "dog");   // last word has no trailing delimiter: must still be flushed
    }

    @Test
    @DisplayName("Words not in the paragraph are rejected, including prefixes, extensions, and near-misses")
    void absentWordsRejected() {
        Dictionary d = new Dictionary("The quick brown fox jumps over the lazy dog");

        assertAllInvalid(d,
                "cat", "quickly", "quic", "jump", "jumped", "do", "dogs", "ov",
                "fo", "brow", "lazydog", "thee", "t", "a");
    }

    @Test
    @DisplayName("Realistic multi-sentence paragraph: every distinct word found, other words rejected")
    void realisticParagraph() {
        Dictionary d = new Dictionary(
                "The quick brown fox jumps over the lazy dog. Then the dog, annoyed, chased the fox!");

        assertAllValid(d, "the", "quick", "brown", "fox", "jumps", "over",
                "lazy", "dog", "then", "annoyed", "chased");
        assertAllValid(d, "dog.", "fox!", "annoyed,");   // edge punctuation on input is ignored
        assertAllInvalid(d, "cat", "jump", "chase", "annoy", "quickly", "fox!x");
    }

    // ---------- case ----------

    @Test
    @DisplayName("Lookup is case-insensitive when the paragraph has capitals")
    void caseInsensitive_capitalsInParagraph() {
        Dictionary d = new Dictionary("Hello World");

        assertAllValid(d, "hello", "HELLO", "Hello", "hElLo", "world", "WORLD", "World");
    }

    @Test
    @DisplayName("Lookup is case-insensitive when the paragraph is lowercase")
    void caseInsensitive_lowercaseParagraph() {
        Dictionary d = new Dictionary("hello world");

        assertAllValid(d, "HELLO", "Hello", "WORLD", "wOrLd");
    }

    @Test
    @DisplayName("ALL-CAPS paragraph is searchable in any case")
    void caseInsensitive_allCapsParagraph() {
        Dictionary d = new Dictionary("HELLO WORLD");

        assertAllValid(d, "hello", "Hello", "HELLO", "world");
    }

    // ---------- whitespace ----------

    @Test
    @DisplayName("Whitespace around the input word is ignored")
    void inputWhitespaceTrimmed() {
        Dictionary d = new Dictionary("hello");

        assertAllValid(d, "  hello  ", "\thello\n", "hello ", " hello", "\n\nhello\r\n");
    }

    @Test
    @DisplayName("Spaces, tabs, newlines, and runs of whitespace all separate words in the paragraph")
    void paragraphWhitespaceVariants() {
        Dictionary d = new Dictionary("one  two\tthree\nfour\r\nfive   six");

        assertAllValid(d, "one", "two", "three", "four", "five", "six");
        assertAllInvalid(d, "", " ", "onetwo", "two\tthree");
    }

    @Test
    @DisplayName("Leading and trailing whitespace in the paragraph does not lose or invent words")
    void paragraphLeadingTrailingWhitespace() {
        Dictionary d = new Dictionary("   alpha beta   ");

        assertAllValid(d, "alpha", "beta");
        assertAllInvalid(d, "", " ");
    }

    // ---------- punctuation ----------

    @Test
    @DisplayName("Punctuation attached to input words is ignored")
    void inputEdgePunctuationIgnored() {
        Dictionary d = new Dictionary("hello world");

        assertAllValid(d,
                "hello.", "hello!", "hello,", "hello?!", "(hello)", "\"hello\"",
                "...hello...", "[world]", "world;", "-world-", "hello:");
    }

    @Test
    @DisplayName("Punctuation in the paragraph does not become part of stored words")
    void paragraphPunctuationStripped() {
        Dictionary d = new Dictionary(
                "Hello, world! This is a test. Really? Yes; (maybe) \"quoted\" text: done.");

        assertAllValid(d, "hello", "world", "this", "is", "a", "test", "really",
                "yes", "maybe", "quoted", "text", "done");
        assertAllInvalid(d, "hello,", "world!", "x");
    }

    @Test
    @DisplayName("Punctuation with no space after it still separates words")
    void punctuationSeparatesWordsWithoutSpace() {
        Dictionary d = new Dictionary("end.Start;next,more");

        assertAllValid(d, "end", "start", "next", "more");
        assertAllInvalid(d, "endstart", "end.start");
    }

    // ---------- apostrophes ----------

    @Test
    @DisplayName("Contractions are stored whole, and are not matched by their pieces")
    void apostrophes_contractions() {
        Dictionary d = new Dictionary("Don't can't won't it's");

        assertAllValid(d, "don't", "can't", "won't", "it's", "DON'T", "Don't");
        assertAllInvalid(d, "dont", "don", "t", "cant", "can", "wont", "won", "its", "it", "s");
    }

    @Test
    @DisplayName("Quote marks around a word are stripped; apostrophes inside words are kept")
    void apostrophes_quotesVersusContractions() {
        Dictionary d = new Dictionary("She said 'hello' to me and don't stop");

        assertAllValid(d, "hello", "'hello'", "don't", "don't.", "'don't'", "she", "said", "to", "me", "and", "stop");
    }

    @Test
    @DisplayName("A lone apostrophe is never a word")
    void apostrophes_loneApostropheInvalid() {
        Dictionary d = new Dictionary("it's ' '' 'x");

        assertAllInvalid(d, "'", "''", "'''");
        assertAllValid(d, "x");
    }

    // ---------- internal junk ----------

    @Test
    @DisplayName("Input with characters inside the word (hyphen, space, digit, underscore) is rejected")
    void internalJunkRejected() {
        Dictionary d = new Dictionary("hello world well-known");

        assertAllInvalid(d, "hel-lo", "hello world", "he1llo", "hello_world", "hel lo", "he.llo");
    }

    @Test
    @DisplayName("Hyphenated paragraph words are split into their parts")
    void hyphenatedWordsSplit() {
        Dictionary d = new Dictionary("hello world well-known");

        assertAllValid(d, "well", "known");
        assertAllInvalid(d, "well-known", "wellknown");
    }

    @Test
    @DisplayName("Digits in the paragraph act as separators and are never stored")
    void digitsActAsSeparators() {
        Dictionary d = new Dictionary("abc123def route66");

        assertAllValid(d, "abc", "def", "route");
        assertAllInvalid(d, "123", "66", "abc123def", "route66x");
    }

    // ---------- null / blank input ----------

    @Test
    @DisplayName("Null, empty, blank, and symbol-only input return false without throwing")
    void nullAndBlankInput() {
        Dictionary d = new Dictionary("hello");

        assertDoesNotThrow(() -> {
            d.isValidWord(null);
        });
        assertFalse(d.isValidWord(null));
        assertAllInvalid(d, "", " ", "   ", "\t", "\n", "\t\n ", "...", "'", "''", "123", "!?", "-");
    }

    // ---------- duplicates ----------

    @Test
    @DisplayName("Repeated words (any case) are handled without error and remain findable")
    void duplicatesHandled() {
        Dictionary d = new Dictionary("the the The THE the.");

        assertAllValid(d, "the", "The", "THE");
        assertAllInvalid(d, "th", "thee");
    }

    // ---------- unicode ----------

    @Test
    @DisplayName("Accented letters are treated as letters, case-insensitively")
    void accentedLetters() {
        Dictionary d = new Dictionary("Caf\u00e9 na\u00efve");

        assertAllValid(d, "caf\u00e9", "CAF\u00c9", "Caf\u00e9", "na\u00efve", "NA\u00cfVE");
        assertAllInvalid(d, "cafe", "naive");
    }

    // ---------- loadParagraph ----------

    @Test
    @DisplayName("loadParagraph replaces the old dictionary rather than adding to it")
    void loadParagraph_replacesOldContents() {
        Dictionary d = new Dictionary("alpha beta");
        assertAllValid(d, "alpha", "beta");

        d.loadParagraph("gamma delta");

        assertAllValid(d, "gamma", "delta");
        assertAllInvalid(d, "alpha", "beta");
    }

    @Test
    @DisplayName("loadParagraph(null) and loadParagraph(\"\") clear the dictionary")
    void loadParagraph_nullAndEmptyClear() {
        Dictionary d = new Dictionary("alpha beta");
        d.loadParagraph(null);
        assertAllInvalid(d, "alpha", "beta");

        d.loadParagraph("gamma");
        assertAllValid(d, "gamma");
        d.loadParagraph("");
        assertAllInvalid(d, "gamma");
    }

    @Test
    @DisplayName("Default constructor followed by loadParagraph works")
    void defaultConstructorThenLoad() {
        Dictionary d = new Dictionary();
        assertAllInvalid(d, "hello");

        d.loadParagraph("Hello, world.");

        assertAllValid(d, "hello", "world");
        assertAllInvalid(d, "hell", "worlds");
    }

    @Test
    @DisplayName("Reloading the same paragraph is stable")
    void loadParagraph_sameParagraphTwice() {
        Dictionary d = new Dictionary("one two three");
        d.loadParagraph("one two three");

        assertAllValid(d, "one", "two", "three");
        assertAllInvalid(d, "four");
    }

    @Test
    @DisplayName("Paragraph constructor and default-constructor-plus-load behave identically")
    void constructorMatchesLoad() {
        String paragraph = "It's a dog-eat-dog world, isn't it? Yes!";
        Dictionary viaConstructor = new Dictionary(paragraph);
        Dictionary viaLoad = new Dictionary();
        viaLoad.loadParagraph(paragraph);

        String[] probes = {"it's", "a", "dog", "eat", "world", "isn't", "it", "yes",
                "dog-eat-dog", "its", "isnt", "", "cat"};
        for (String p : probes) {
            assertEquals(viaConstructor.isValidWord(p), viaLoad.isValidWord(p),
                    "Mismatch for \"" + p + "\"");
        }
    }

    // ---------- order independence & repeat lookups ----------

    @Test
    @DisplayName("Word order in the paragraph does not change which words are found")
    void orderIndependence() {
        Dictionary forward = new Dictionary("alpha bravo charlie delta echo");
        Dictionary backward = new Dictionary("echo delta charlie bravo alpha");
        Dictionary scrambled = new Dictionary("charlie echo alpha delta bravo");

        String[] present = {"alpha", "bravo", "charlie", "delta", "echo"};
        String[] absent = {"foxtrot", "alph", "bravos", "", "a"};
        for (Dictionary d : new Dictionary[] {forward, backward, scrambled}) {
            assertAllValid(d, present);
            assertAllInvalid(d, absent);
        }
    }

    @Test
    @DisplayName("Repeated lookups give the same answer (lookups do not change state)")
    void repeatedLookupsConsistent() {
        Dictionary d = new Dictionary("hello world");

        for (int i = 0; i < 5; i++) {
            assertTrue(d.isValidWord("hello"));
            assertFalse(d.isValidWord("goodbye"));
        }
    }

    // ---------- scale ----------

    @Test
    @DisplayName("1000 words in alphabetical order (worst-case tree shape): all found, none invented")
    void largeParagraph_ascendingOrder() {
        List<String> words = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            words.add(wordFor(i));
        }
        Collections.sort(words);

        Dictionary d = new Dictionary(String.join(" ", words));

        for (String w : words) {
            assertTrue(d.isValidWord(w), "Expected VALID: " + w);
        }
        for (int i = 1000; i < 1100; i++) {
            assertFalse(d.isValidWord(wordFor(i)), "Expected INVALID: " + wordFor(i));
        }
    }

    @Test
    @DisplayName("1000 words in reverse alphabetical order: all found, none invented")
    void largeParagraph_descendingOrder() {
        List<String> words = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            words.add(wordFor(i));
        }
        Collections.sort(words, Collections.reverseOrder());

        Dictionary d = new Dictionary(String.join(" ", words));

        for (String w : words) {
            assertTrue(d.isValidWord(w), "Expected VALID: " + w);
        }
        for (int i = 1000; i < 1100; i++) {
            assertFalse(d.isValidWord(wordFor(i)), "Expected INVALID: " + wordFor(i));
        }
    }

    @Test
    @DisplayName("Very long word is stored whole and not matched by shorter or longer versions")
    void veryLongWord() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append('a');
        }
        String longWord = sb.toString();
        Dictionary d = new Dictionary("start " + longWord + " end");

        assertTrue(d.isValidWord(longWord));
        assertFalse(d.isValidWord(longWord.substring(1)), "one letter shorter");
        assertFalse(d.isValidWord(longWord + "a"), "one letter longer");
        assertAllValid(d, "start", "end");
    }
}