import java.io.*;

public class Trie {
    private TrieNode root;
    
    public Trie() {
        root = new TrieNode();
    }
    
    /**
     * Inserts a word into the trie
     */
    public void insert(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }
        
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            current.children.putIfAbsent(c, new TrieNode());
            current = current.children.get(c);
        }
        current.isEndOfWord = true;
    }
    
    /**
     * Searches for a word in the trie
     */
    public boolean search(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }
        
        TrieNode node = findNode(word);
        return node != null && node.isEndOfWord;
    }
    
    /**
     * Finds the node corresponding to a word prefix
     */
    private TrieNode findNode(String prefix) {
        TrieNode current = root;
        for (char c : prefix.toCharArray()) {
            if (!current.children.containsKey(c)) {
                return null;
            }
            current = current.children.get(c);
        }
        return current;
    }
    
    /**
     * Loads all words from a file into the trie
     */
    public void loadFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String word;
            while ((word = reader.readLine()) != null) {
                word = word.trim().toLowerCase();
                if (!word.isEmpty()) {
                    insert(word);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading words from file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Returns the total number of words in the trie
     */
    public int size() {
        return countWords(root);
    }
    
    private int countWords(TrieNode node) {
        int count = 0;
        if (node.isEndOfWord) {
            count = 1;
        }
        for (TrieNode child : node.children.values()) {
            count += countWords(child);
        }
        return count;
    }
}
