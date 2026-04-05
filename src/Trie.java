import java.io.*;
import java.util.*;

public class Trie {
    private TrieNode root;
    private HashMap<Integer, Character> greenCharacters;
    private HashSet<Character> greyCharacters;
    private HashMap<Integer, List<Character>> orangeCharacters;
    
    public Trie() {
        root = new TrieNode();
        greenCharacters = new HashMap<>();
        greyCharacters = new HashSet<>();
        orangeCharacters = new HashMap<>();
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
    
    /**
     * Returns the next word to guess in the Wordle solver
     */
    public String getNextWord() {
        List<String> possibleWords = getPossibleWords(root, 0,5);
        cleanupOrangeMismatches(possibleWords);
        if (possibleWords.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return possibleWords.get(random.nextInt(possibleWords.size()));
    }
    
    /**
     * Recursively builds a list of possible words based on the current state of green, grey, and orange characters
     */
    private List<String> getPossibleWords(TrieNode node, int index, int length) {
    
        List<String> possibleWords = new ArrayList<>();
        if (index == length) {
            possibleWords.add("");
            return possibleWords;
        }

        if (greenCharacters.containsKey(index)) {
            char c = greenCharacters.get(index);
            List<String> subWords = getPossibleWords(node.children.get(c), index + 1, length);
            for (String subWord : subWords) {
                possibleWords.add(c + subWord);
            }
            return possibleWords; // If it's green, we must use it, so we can return immediately
        }
        

        for (char c : node.children.keySet()) {
            if (greyCharacters.contains(c)) {
                continue; // Skip if this character is grey
            }

            if (orangeCharacters.containsKey(index) && orangeCharacters.get(index).contains(c)) {
                continue; // Skip if this character is orange at this position
            }

            
            List<String> subWords = getPossibleWords(node.children.get(c), index + 1, length);
            for (String subWord : subWords) {
                possibleWords.add(c + subWord);
            }    
        }
        
        return possibleWords;
    }

    /**
     * Removes any words from the possible words list that contain orange characters in the wrong positions
     */
    private void cleanupOrangeMismatches(List<String> possibleWords) {
        Iterator<String> iterator = possibleWords.iterator();
        while (iterator.hasNext()) {
            String word = iterator.next();
            boolean remove = false;
            for (Map.Entry<Integer, List<Character>> entry : orangeCharacters.entrySet()) {
                int pos = entry.getKey();
                List<Character> chars = entry.getValue();
                if (pos < word.length() && chars.contains(word.charAt(pos))) {
                    remove = true;
                    break;
                }
            }
            if (remove) {
                iterator.remove();
            }
        }
    }
    
    public HashMap<Integer, Character> getGreenCharacters() {
        return greenCharacters;
    }
    
    public HashSet<Character> getGreyCharacters() {
        return greyCharacters;
    }
    
    public HashMap<Integer, List<Character>> getOrangeCharacters() {
        return orangeCharacters;
    }

}
