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
}
