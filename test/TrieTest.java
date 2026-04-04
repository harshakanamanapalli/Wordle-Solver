import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;

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
}
