package turner;

import java.util.List;
public class Dictionary {

    private BST<String> words = new BST<>();

    public Dictionary() {
    	
    }
    
    public Dictionary(String paragraph) {
    	words.clear();
        if (paragraph == null) {
            return; // empty dictionary
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paragraph.length(); i++) {
            char c = paragraph.charAt(i);
            if (Character.isLetter(c) || c == '\'') {
                sb.append(c);
            } else {
                addWord(sb);
            }
        }
        addWord(sb); // flush the final word
    }

    private void addWord(StringBuilder sb) {
        String w = normalize(sb.toString());
        if (!w.isEmpty()) {
            words.insert(w);
        }
        sb.setLength(0);
    }

    public boolean isValidWord(String word) {
        String cleaned = normalize(word);
        if (cleaned.isEmpty()) {
            return false;
        }
        // reject anything with junk in the middle ("hel-lo", "hello world", "abc123")
        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            if (!Character.isLetter(c) && c != '\'') {
                return false;
            }
        }
        return words.contains(cleaned);
    }

    // lowercases, trims, and strips non-letters from both ends only
    private static String normalize(String s) {
        if (s == null) {
            return "";
        }
        String t = s.trim().toLowerCase();
        int start = 0;
        int end = t.length();
        while (start < end && !Character.isLetter(t.charAt(start))) {
            start++;
        }
        while (end > start && !Character.isLetter(t.charAt(end - 1))) {
            end--;
        }
        return t.substring(start, end);
    }
    public void loadParagraph(String paragraph) {
        words.clear();   // replace any previous dictionary
        if (paragraph == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paragraph.length(); i++) {
            char c = paragraph.charAt(i);
            if (Character.isLetter(c) || c == '\'') {
                sb.append(c);
            } else {
                addWord(sb);
            }
        }
        addWord(sb); // flush the final word
    }
    public List<String> getWords() {
        return words.inOrder();   // sorted, no duplicates
    }

    public int size() {
        return words.size();
    }
}