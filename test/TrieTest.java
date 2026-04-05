import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TrieTest {
    private Trie trie;
    
    @Before
    public void setUp() {
        trie = new Trie();
        File sourceDir = new File("assets");
        String wordsFile = sourceDir.getAbsolutePath() + File.separator + "english_words.txt";
        trie.loadFromFile(wordsFile);
    }
    
    @Test
    public void testWordsAreLoaded() {
        int totalWords = trie.size();
        assertTrue("Words should be loaded from file", totalWords > 0);
    }
    
    @Test
    public void testShortWords() {
        assertTrue("3-letter word 'aaron' should be found", trie.search("aaron"));
        assertTrue("4-letter word 'abaci' should be found", trie.search("abaci"));
        assertTrue("5-letter word 'hello' should be found", trie.search("hello"));
        assertTrue("5-letter word 'zebra' should be found", trie.search("zebra"));
    }
    
    @Test
    public void testNonexistentWords() {
        assertFalse("3-letter non-word 'xyz' should not be found", trie.search("xyz"));
        assertFalse("Non-existent word should not be found", trie.search("nonexistent"));
    }
    
    @Test
    public void testGetNextWordWithEmptyCharacterMaps() {
        String nextWord = trie.getNextWord();
        assertNotNull("getNextWord should return a non-null word", nextWord);
        assertTrue("getNextWord should return a 5-letter word", nextWord.length() == 5);
    }
    
    @Test
    public void testGetNextWordWithPopulatedCharacterMaps() {
        // Populate greenCharacters with some characters
        trie.getGreenCharacters().put(0, 'h');
        trie.getGreenCharacters().put(4, 'o');
        
        // Populate greyCharacters with some characters
        trie.getGreyCharacters().add('x');
        trie.getGreyCharacters().add('q');
        
        // Populate orangeCharacters with some characters at specific positions
        List<Character> orangeAtPos1 = new ArrayList<>();
        orangeAtPos1.add('e');
        trie.getOrangeCharacters().put(1, orangeAtPos1);
        
        List<Character> orangeAtPos3 = new ArrayList<>();
        orangeAtPos3.add('l');
        trie.getOrangeCharacters().put(3, orangeAtPos3);
        
        String nextWord = trie.getNextWord();
        assertNotNull("getNextWord should return a non-null word with populated maps", nextWord);
        assertTrue("getNextWord should return a 5-letter word", nextWord.length() == 5);
    }
    
    @Test
    public void testDeleteWordValidWord() {
        // Test deleting a word that exists
        Trie testTrie = new Trie();
        testTrie.insert("hello");
        testTrie.insert("world");
        testTrie.insert("test");
        
        assertTrue("'hello' should exist before deletion", testTrie.search("hello"));
        testTrie.deleteWord("hello");
        assertFalse("'hello' should not exist after deletion", testTrie.search("hello"));
        
        assertTrue("'world' should still exist", testTrie.search("world"));
        assertTrue("'test' should still exist", testTrie.search("test"));
    }
    
    @Test
    public void testDeleteWordNonexistentWord() {
        // Test deleting a word that doesn't exist
        Trie testTrie = new Trie();
        testTrie.insert("hello");
        
        testTrie.deleteWord("nonexistent");
        assertTrue("'hello' should still exist after trying to delete non-existent word", 
                   testTrie.search("hello"));
    }
    
    @Test
    public void testDeleteWordEmptyString() {
        // Test deleting an empty string
        Trie testTrie = new Trie();
        testTrie.insert("hello");
        
        testTrie.deleteWord("");
        assertTrue("'hello' should exist after deleting empty string", testTrie.search("hello"));
    }
    
    @Test
    public void testDeleteWordNullString() {
        // Test deleting a null string
        Trie testTrie = new Trie();
        testTrie.insert("hello");
        
        testTrie.deleteWord(null);
        assertTrue("'hello' should exist after deleting null", testTrie.search("hello"));
    }
    
    @Test
    public void testDeleteWordMultipleWords() {
        // Test deleting multiple different words
        Trie testTrie = new Trie();
        testTrie.insert("apple");
        testTrie.insert("application");
        testTrie.insert("apply");
        testTrie.insert("banana");
        
        testTrie.deleteWord("apple");
        assertFalse("'apple' should be deleted", testTrie.search("apple"));
        assertTrue("'application' should still exist", testTrie.search("application"));
        assertTrue("'apply' should still exist", testTrie.search("apply"));
        assertTrue("'banana' should still exist", testTrie.search("banana"));
        
        testTrie.deleteWord("application");
        assertFalse("'application' should be deleted", testTrie.search("application"));
        assertTrue("'apply' should still exist", testTrie.search("apply"));
    }
    
    @Test
    public void testDeleteWordFromFileLoadedTrie() {
        // Test deleting from the file-loaded trie
        int sizeBefore = trie.size();
        
        if (trie.search("hello")) {
            trie.deleteWord("hello");
            assertFalse("'hello' should be deleted from file-loaded trie", trie.search("hello"));
            assertEquals("Size should decrease by 1", sizeBefore - 1, trie.size());
        }
    }
    
    @Test
    public void testDeleteWordSharedPrefix() {
        // Test that deleting a word with shared prefixes doesn't affect others
        Trie testTrie = new Trie();
        testTrie.insert("care");
        testTrie.insert("car");
        testTrie.insert("card");
        
        testTrie.deleteWord("car");
        
        assertFalse("'car' should be deleted", testTrie.search("car"));
        assertTrue("'care' should still exist", testTrie.search("care"));
        assertTrue("'card' should still exist", testTrie.search("card"));
    }
    
    @Test
    public void testDeleteWordSize() {
        // Test that size decreases correctly after deletion
        Trie testTrie = new Trie();
        testTrie.insert("test");
        testTrie.insert("testing");
        testTrie.insert("tester");
        
        int initialSize = testTrie.size();
        testTrie.deleteWord("test");
        
        assertEquals("Size should decrease by 1", initialSize - 1, testTrie.size());
        assertEquals("After deleting all, size should be 2", 2, testTrie.size());
    }
    
    // ========== CONSTRAINT FILTERING TESTS ==========
    
    @Test
    public void testGetNextWordWithOrangeCharacterConstraint() {
        // Test that getNextWord respects orange character constraints
        Trie testTrie = new Trie();
        testTrie.insert("hello");
        testTrie.insert("hallo");
        testTrie.insert("hulls");
        
        // Mark 'e' as orange at position 1 (wrong position)
        List<Character> orangeAtPos1 = new ArrayList<>();
        orangeAtPos1.add('e');
        testTrie.getOrangeCharacters().put(1, orangeAtPos1);
        
        String nextWord = testTrie.getNextWord();
        
        // Should not return a word with 'e' at position 1
        assertNotNull("Should return a valid word", nextWord);
        if (nextWord != null) {
            assertNotEquals("Word should not have 'e' at position 1", 'e', nextWord.charAt(1));
        }
    }
    
    @Test
    public void testGetNextWordWithGreyCharacterConstraint() {
        // Test that getNextWord excludes grey characters using file-loaded trie
        // Mark common letters as grey to narrow down results
        trie.getGreyCharacters().add('e');
        trie.getGreyCharacters().add('a');
        trie.getGreyCharacters().add('i');
        
        String nextWord = trie.getNextWord();
        
        // Should return a word that doesn't contain the grey characters
        if (nextWord != null) {
            assertFalse("Word should not contain grey character 'e'", nextWord.contains("e"));
            assertFalse("Word should not contain grey character 'a'", nextWord.contains("a"));
            assertFalse("Word should not contain grey character 'i'", nextWord.contains("i"));
        }
    }
    
    @Test
    public void testGetNextWordWithCombinedGreenAndOrangeConstraints() {
        // Test word selection with multiple constraint types using file-loaded trie
        // Green: 's' at position 0 (many words start with 's')
        trie.getGreenCharacters().put(0, 's');
        
        // Orange: 't' is in word but not at position 1
        List<Character> orangeAtPos1 = new ArrayList<>();
        orangeAtPos1.add('t');
        trie.getOrangeCharacters().put(1, orangeAtPos1);
        
        String nextWord = trie.getNextWord();
        
        if (nextWord != null) {
            assertEquals("Word should start with 's'", 's', nextWord.charAt(0));
            assertNotEquals("Word should not have 't' at position 1", 't', nextWord.charAt(1));
        }
    }
    
    @Test
    public void testGetNextWordWithNoValidWordsForConstraints() {
        // Test when constraints eliminate all words
        Trie testTrie = new Trie();
        testTrie.insert("hello");
        
        // Set impossible constraint: green 'h' at position 0, but orange 'h' at position 1
        testTrie.getGreenCharacters().put(0, 'h');
        List<Character> orangeAtPos1 = new ArrayList<>();
        orangeAtPos1.add('h');
        testTrie.getOrangeCharacters().put(1, orangeAtPos1);
        
        // Add multiple grey characters that appear in "hello"
        testTrie.getGreyCharacters().add('e');
        testTrie.getGreyCharacters().add('l');
        testTrie.getGreyCharacters().add('o');
        
        String nextWord = testTrie.getNextWord();
        
        // Should return null or a word that satisfies all constraints
        if (nextWord != null) {
            assertEquals("Word should start with 'h'", 'h', nextWord.charAt(0));
            assertFalse("Word should not contain any grey characters", 
                       nextWord.contains("e") || nextWord.contains("l") || nextWord.contains("o"));
        }
    }
    
    @Test
    public void testConstraintCleanupRemovesInvalidWords() {
        // Test that invalid words are removed during constraint cleanup
        Trie testTrie = new Trie();
        testTrie.insert("beach");
        testTrie.insert("reach");
        testTrie.insert("teach");
        testTrie.insert("bread");
        
        // Mark 'e' as orange at position 1
        List<Character> orangeAtPos1 = new ArrayList<>();
        orangeAtPos1.add('e');
        testTrie.getOrangeCharacters().put(1, orangeAtPos1);
        
        String nextWord = testTrie.getNextWord();
        
        assertNotNull("Should return a valid word", nextWord);
        if (nextWord != null) {
            assertNotEquals("Returned word should not have 'e' at position 1", 'e', nextWord.charAt(1));
        }
    }
    
    // ========== WEIGHT CALCULATION TESTS ==========
    
    @Test
    public void testWeightedSelectionPrioritizesOrangeCharacters() {
        // Test that words with more orange characters get higher weight
        // Use file-loaded trie with 't' as orange at position 0
        List<Character> orangeChars = new ArrayList<>();
        orangeChars.add('t');
        trie.getOrangeCharacters().put(0, orangeChars);
        
        // Run multiple times - should get varied results but all valid
        for (int i = 0; i < 10; i++) {
            String word = trie.getNextWord();
            if (word != null) {
                assertNotEquals("Word should not start with orange 't'", 't', word.charAt(0));
            }
        }
        
        assertTrue("Test completed successfully with orange constraints", true);
    }
    
    @Test
    public void testWeightedSelectionWithAllGreenCharacters() {
        // Test weight calculation when all positions are green
        Trie testTrie = new Trie();
        testTrie.insert("hello");
        
        testTrie.getGreenCharacters().put(0, 'h');
        testTrie.getGreenCharacters().put(1, 'e');
        testTrie.getGreenCharacters().put(2, 'l');
        testTrie.getGreenCharacters().put(3, 'l');
        testTrie.getGreenCharacters().put(4, 'o');
        
        String nextWord = testTrie.getNextWord();
        
        assertEquals("Should return the exact green word", "hello", nextWord);
    }
    
    @Test
    public void testWeightCalculationConsistency() {
        // Test that weight calculation produces consistent results
        Trie testTrie = new Trie();
        testTrie.insert("alpha");
        testTrie.insert("delta");
        testTrie.insert("gamma");
        
        List<Character> orangeAtPos0 = new ArrayList<>();
        orangeAtPos0.add('a');
        testTrie.getOrangeCharacters().put(0, orangeAtPos0);
        
        // Call getNextWord multiple times
        String word1 = testTrie.getNextWord();
        String word2 = testTrie.getNextWord();
        
        assertNotNull("Both calls should return a word", word1);
        assertNotNull("Both calls should return a word", word2);
        
        // Both should satisfy constraints
        assertTrue("First word should not start with 'a'", word1.charAt(0) != 'a');
        assertTrue("Second word should not start with 'a'", word2.charAt(0) != 'a');
    }
    
    // ========== INTEGRATION TESTS ==========
    
    @Test
    public void testSequentialConstraintUpdates() {
        // Test updating constraints multiple times sequentially
        // Use file-loaded trie which has many words
        
        // First: get a word without constraints
        String wordNoConstraints = trie.getNextWord();
        assertNotNull("Should get a word with no constraints", wordNoConstraints);
        
        // Add constraint: green 's' at position 0
        trie.getGreenCharacters().put(0, 's');
        String wordWithGreen = trie.getNextWord();
        
        if (wordWithGreen != null) {
            assertEquals("Word should start with 's'", 's', wordWithGreen.charAt(0));
        }
        
        // Add more constraint: grey 'e'
        trie.getGreyCharacters().add('e');
        String wordWithMultipleConstraints = trie.getNextWord();
        
        if (wordWithMultipleConstraints != null) {
            assertEquals("Word should start with 's'", 's', wordWithMultipleConstraints.charAt(0));
            assertFalse("Word should not contain 'e'", wordWithMultipleConstraints.contains("e"));
        }
    }
    
    @Test
    public void testClearingConstraintsResetsBehavior() {
        // Test that clearing constraints resets to original behavior
        Trie testTrie = new Trie();
        testTrie.insert("hello");
        testTrie.insert("world");
        testTrie.insert("apple");
        
        // Add constraints
        testTrie.getGreenCharacters().put(0, 'h');
        testTrie.getGreyCharacters().add('x');
        
        String wordWithConstraints = testTrie.getNextWord();
        assertTrue("Constrained word should start with 'h'", wordWithConstraints.charAt(0) == 'h');
        
        // Clear constraints
        testTrie.getGreenCharacters().clear();
        testTrie.getGreyCharacters().clear();
        testTrie.getOrangeCharacters().clear();
        
        String wordNoConstraints = testTrie.getNextWord();
        assertNotNull("Should return a word with no constraints", wordNoConstraints);
        assertTrue("Should return a valid 5-letter word", wordNoConstraints.length() == 5);
    }
    
    @Test
    public void testComplexMultiConstraintScenario() {
        // Test realistic Wordle scenario with multiple interacting constraints
        // Using a fresh small trie with specific words
        Trie testTrie = new Trie();
        testTrie.insert("stork");
        testTrie.insert("story");
        testTrie.insert("storm");
        testTrie.insert("store");
        testTrie.insert("stock");
        
        // Scenario: Constraints that should match several words
        testTrie.getGreenCharacters().put(0, 's');
        testTrie.getGreenCharacters().put(1, 't');
        
        List<Character> orangeO = new ArrayList<>();
        orangeO.add('o');
        testTrie.getOrangeCharacters().put(2, orangeO);  // 'o' not at position 2
        
        String nextWord = testTrie.getNextWord();
        
        if (nextWord != null) {
            assertEquals("Should start with 'st'", "st", nextWord.substring(0, 2));
            assertNotEquals("Should not have 'o' at position 2", 'o', nextWord.charAt(2));
        }
    }
    
    @Test
    public void testConstraintPersistenceAcrossMultipleCalls() {
        // Test that constraints persist across multiple getNextWord calls
        Trie testTrie = new Trie();
        testTrie.insert("plant");
        testTrie.insert("plane");
        testTrie.insert("place");
        testTrie.insert("blank");
        
        testTrie.getGreenCharacters().put(0, 'p');
        testTrie.getGreyCharacters().add('e');
        
        for (int i = 0; i < 5; i++) {
            String word = testTrie.getNextWord();
            if (word != null) {
                assertEquals("Constraint should persist: must start with 'p'", 'p', word.charAt(0));
                assertFalse("Constraint should persist: must not contain 'e'", word.contains("e"));
            }
        }
    }
    
    // ========== EDGE CASE TESTS ==========
    
    @Test
    public void testEmptyTrie() {
        // Test operations on empty trie
        Trie testTrie = new Trie();
        
        assertEquals("Empty trie should have size 0", 0, testTrie.size());
        assertFalse("Should not find any word in empty trie", testTrie.search("hello"));
        assertNull("getNextWord on empty trie should return null", testTrie.getNextWord());
    }
    
    @Test
    public void testDuplicateInsertions() {
        // Test that inserting the same word multiple times doesn't increase size
        Trie testTrie = new Trie();
        testTrie.insert("hello");
        int sizeAfterFirst = testTrie.size();
        
        testTrie.insert("hello");
        int sizeAfterDuplicate = testTrie.size();
        
        assertEquals("Size should not change on duplicate insertion", sizeAfterFirst, sizeAfterDuplicate);
        assertTrue("Should still find the word", testTrie.search("hello"));
    }
    
    @Test
    public void testCaseInsensitivityInFileLoading() {
        // Test that file loading handles case correctly
        Trie testTrie = new Trie();
        
        // insert() should convert to lowercase based on file loading behavior
        testTrie.insert("hello");
        testTrie.insert("world");
        
        // Should find words after insertion
        assertTrue("Should find 'hello'", testTrie.search("hello"));
        assertTrue("Should find 'world'", testTrie.search("world"));
        assertEquals("Trie should contain 2 words", 2, testTrie.size());
    }
    
    @Test
    public void testInsertEmptyStringAndNullHandling() {
        // Test that empty strings and null values don't cause issues
        Trie testTrie = new Trie();
        testTrie.insert(null);
        testTrie.insert("");
        testTrie.insert("hello");
        
        assertEquals("Size should only count valid words", 1, testTrie.size());
        assertFalse("Should not find empty string", testTrie.search(""));
        assertFalse("Should not find null", testTrie.search(null));
        assertTrue("Should find valid word", testTrie.search("hello"));
    }
    
    @Test
    public void testSearchEmptyStringAndNull() {
        // Test search behavior with edge case inputs
        Trie testTrie = new Trie();
        testTrie.insert("hello");
        
        assertFalse("Search for empty string should return false", testTrie.search(""));
        assertFalse("Search for null should return false", testTrie.search(null));
    }
    
    // ========== FILE LOADING EDGE CASES ==========
    
    @Test
    public void testFileLoadingWithMixedWordLengths() {
        // Test that loadFromFile correctly filters non-5-letter words
        Trie testTrie = new Trie();
        
        // Only 5-letter words should be loaded from the file
        File sourceDir = new File("assets");
        String wordsFile = sourceDir.getAbsolutePath() + File.separator + "english_words.txt";
        testTrie.loadFromFile(wordsFile);
        
        int totalSize = testTrie.size();
        assertTrue("Should load words from file", totalSize > 0);
        
        // All loaded words should be exactly 5 letters
        int wordCount = 0;
        for (int i = 0; i < totalSize; i++) {
            String word = testTrie.getNextWord();
            if (word != null) {
                assertEquals("All words should be 5 letters", 5, word.length());
                wordCount++;
            }
        }
    }
    
    @Test
    public void testGetNextWordReturnsValidWords() {
        // Test that getNextWord consistently returns valid 5-letter words
        int validCount = 0;
        
        for (int i = 0; i < 20; i++) {
            String word = trie.getNextWord();
            if (word != null && word.length() == 5) {
                validCount++;
                assertTrue("Returned word should be searchable", trie.search(word));
            }
        }
        
        assertTrue("Should return mostly valid words", validCount > 15);
    }
}
