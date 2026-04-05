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
}
