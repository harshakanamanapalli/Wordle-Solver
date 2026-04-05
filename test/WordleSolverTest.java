import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class WordleSolverTest {
    private Trie trie;
    
    @Before
    public void setUp() {
        trie = new Trie();
        File sourceDir = new File("assets");
        String wordsFile = sourceDir.getAbsolutePath() + File.separator + "english_words.txt";
        trie.loadFromFile(wordsFile);
    }
    
    @Test
    public void testUpdateWithWordleFeedbackAllGreen() {
        // Create a WordleWord with all GREEN characters
        List<WordleCharacter> characters = new ArrayList<>();
        characters.add(new WordleCharacter('h', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('e', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('l', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('l', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('o', WordleCharacter.State.GREEN));
        
        WordleWord wordleWord = new WordleWord(characters);
        
        // Update the trie with the feedback
        trie.updateWithWordleFeedback(wordleWord);
        
        // Verify that all green characters are stored correctly
        HashMap<Integer, Character> greenCharacters = trie.getGreenCharacters();
        assertEquals("Position 0 should have 'h'", Character.valueOf('h'), greenCharacters.get(0));
        assertEquals("Position 1 should have 'e'", Character.valueOf('e'), greenCharacters.get(1));
        assertEquals("Position 2 should have 'l'", Character.valueOf('l'), greenCharacters.get(2));
        assertEquals("Position 3 should have 'l'", Character.valueOf('l'), greenCharacters.get(3));
        assertEquals("Position 4 should have 'o'", Character.valueOf('o'), greenCharacters.get(4));
    }
    
    @Test
    public void testUpdateWithWordleFeedbackAllGrey() {
        // Create a WordleWord with all GREY characters
        List<WordleCharacter> characters = new ArrayList<>();
        characters.add(new WordleCharacter('x', WordleCharacter.State.GREY));
        characters.add(new WordleCharacter('y', WordleCharacter.State.GREY));
        characters.add(new WordleCharacter('z', WordleCharacter.State.GREY));
        characters.add(new WordleCharacter('q', WordleCharacter.State.GREY));
        characters.add(new WordleCharacter('w', WordleCharacter.State.GREY));
        
        WordleWord wordleWord = new WordleWord(characters);
        
        // Update the trie with the feedback
        trie.updateWithWordleFeedback(wordleWord);
        
        // Verify that all grey characters are stored correctly
        HashSet<Character> greyCharacters = trie.getGreyCharacters();
        assertTrue("'x' should be in grey characters", greyCharacters.contains('x'));
        assertTrue("'y' should be in grey characters", greyCharacters.contains('y'));
        assertTrue("'z' should be in grey characters", greyCharacters.contains('z'));
        assertTrue("'q' should be in grey characters", greyCharacters.contains('q'));
        assertTrue("'w' should be in grey characters", greyCharacters.contains('w'));
    }
    
    @Test
    public void testUpdateWithWordleFeedbackAllOrange() {
        // Create a WordleWord with all ORANGE characters
        List<WordleCharacter> characters = new ArrayList<>();
        characters.add(new WordleCharacter('h', WordleCharacter.State.ORANGE));
        characters.add(new WordleCharacter('e', WordleCharacter.State.ORANGE));
        characters.add(new WordleCharacter('l', WordleCharacter.State.ORANGE));
        characters.add(new WordleCharacter('l', WordleCharacter.State.ORANGE));
        characters.add(new WordleCharacter('o', WordleCharacter.State.ORANGE));
        
        WordleWord wordleWord = new WordleWord(characters);
        
        // Update the trie with the feedback
        trie.updateWithWordleFeedback(wordleWord);
        
        // Verify that all orange characters are stored correctly
        HashMap<Integer, List<Character>> orangeCharacters = trie.getOrangeCharacters();
        assertTrue("Position 0 should have 'h'", orangeCharacters.get(0).contains('h'));
        assertTrue("Position 1 should have 'e'", orangeCharacters.get(1).contains('e'));
        assertTrue("Position 2 should have 'l'", orangeCharacters.get(2).contains('l'));
        assertTrue("Position 3 should have 'l'", orangeCharacters.get(3).contains('l'));
        assertTrue("Position 4 should have 'o'", orangeCharacters.get(4).contains('o'));
    }
    
    @Test
    public void testUpdateWithWordleFeedbackMixed() {
        // Create a WordleWord with mixed states
        List<WordleCharacter> characters = new ArrayList<>();
        characters.add(new WordleCharacter('h', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('e', WordleCharacter.State.ORANGE));
        characters.add(new WordleCharacter('l', WordleCharacter.State.GREY));
        characters.add(new WordleCharacter('l', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('o', WordleCharacter.State.ORANGE));
        
        WordleWord wordleWord = new WordleWord(characters);
        
        // Update the trie with the feedback
        trie.updateWithWordleFeedback(wordleWord);
        
        // Verify green characters
        HashMap<Integer, Character> greenCharacters = trie.getGreenCharacters();
        assertEquals("Position 0 should have 'h'", Character.valueOf('h'), greenCharacters.get(0));
        assertEquals("Position 3 should have 'l'", Character.valueOf('l'), greenCharacters.get(3));
        
        // Verify grey characters
        HashSet<Character> greyCharacters = trie.getGreyCharacters();
        assertTrue("'l' should be in grey characters", greyCharacters.contains('l'));
        
        // Verify orange characters
        HashMap<Integer, List<Character>> orangeCharacters = trie.getOrangeCharacters();
        assertTrue("Position 1 should have 'e'", orangeCharacters.get(1).contains('e'));
        assertTrue("Position 4 should have 'o'", orangeCharacters.get(4).contains('o'));
    }
    
    @Test
    public void testUpdateWithWordleFeedbackMultipleUpdates() {
        // First update with some feedback
        List<WordleCharacter> firstCharacters = new ArrayList<>();
        firstCharacters.add(new WordleCharacter('h', WordleCharacter.State.GREEN));
        firstCharacters.add(new WordleCharacter('e', WordleCharacter.State.GREY));
        firstCharacters.add(new WordleCharacter('l', WordleCharacter.State.GREY));
        firstCharacters.add(new WordleCharacter('l', WordleCharacter.State.GREY));
        firstCharacters.add(new WordleCharacter('o', WordleCharacter.State.ORANGE));
        
        WordleWord firstWord = new WordleWord(firstCharacters);
        trie.updateWithWordleFeedback(firstWord);
        
        // Second update with more feedback
        List<WordleCharacter> secondCharacters = new ArrayList<>();
        secondCharacters.add(new WordleCharacter('h', WordleCharacter.State.GREEN));
        secondCharacters.add(new WordleCharacter('a', WordleCharacter.State.GREEN));
        secondCharacters.add(new WordleCharacter('n', WordleCharacter.State.ORANGE));
        secondCharacters.add(new WordleCharacter('d', WordleCharacter.State.GREEN));
        secondCharacters.add(new WordleCharacter('y', WordleCharacter.State.GREEN));
        
        WordleWord secondWord = new WordleWord(secondCharacters);
        trie.updateWithWordleFeedback(secondWord);
        
        // Verify the combined feedback
        HashMap<Integer, Character> greenCharacters = trie.getGreenCharacters();
        assertEquals("Position 0 should have 'h'", Character.valueOf('h'), greenCharacters.get(0));
        assertEquals("Position 1 should have 'a'", Character.valueOf('a'), greenCharacters.get(1));
        assertEquals("Position 3 should have 'd'", Character.valueOf('d'), greenCharacters.get(3));
        assertEquals("Position 4 should have 'y'", Character.valueOf('y'), greenCharacters.get(4));
        
        HashSet<Character> greyCharacters = trie.getGreyCharacters();
        assertTrue("'e' should be in grey characters", greyCharacters.contains('e'));
        assertTrue("'l' should be in grey characters", greyCharacters.contains('l'));
        
        HashMap<Integer, List<Character>> orangeCharacters = trie.getOrangeCharacters();
        assertTrue("Position 4 should have 'o' in orange", orangeCharacters.get(4).contains('o'));
        assertTrue("Position 2 should have 'n' in orange", orangeCharacters.get(2).contains('n'));
    }
    
    @Test
    public void testUpdateWithWordleFeedbackDoesNotAffectSearch() {
        // Add some feedback to the trie
        List<WordleCharacter> characters = new ArrayList<>();
        characters.add(new WordleCharacter('h', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('e', WordleCharacter.State.GREY));
        characters.add(new WordleCharacter('l', WordleCharacter.State.ORANGE));
        characters.add(new WordleCharacter('l', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('o', WordleCharacter.State.ORANGE));
        
        WordleWord wordleWord = new WordleWord(characters);
        trie.updateWithWordleFeedback(wordleWord);
        
        // Verify that the search functionality still works
        assertTrue("'hello' should still be searchable", trie.search("hello"));
        assertTrue("'zebra' should still be searchable", trie.search("zebra"));
        assertFalse("'xyz' should still not be found", trie.search("xyz"));
    }
    
    @Test
    public void testUpdateWithWordleFeedbackEmptyStates() {
        // Create a WordleWord with UNSET characters (not tested in updateWithWordleFeedback)
        List<WordleCharacter> characters = new ArrayList<>();
        characters.add(new WordleCharacter('h', WordleCharacter.State.UNSET));
        characters.add(new WordleCharacter('e', WordleCharacter.State.UNSET));
        characters.add(new WordleCharacter('l', WordleCharacter.State.UNSET));
        characters.add(new WordleCharacter('l', WordleCharacter.State.UNSET));
        characters.add(new WordleCharacter('o', WordleCharacter.State.UNSET));
        
        WordleWord wordleWord = new WordleWord(characters);
        
        // Update the trie with the feedback (should not add anything)
        trie.updateWithWordleFeedback(wordleWord);
        
        // Verify that nothing was added
        HashMap<Integer, Character> greenCharacters = trie.getGreenCharacters();
        HashSet<Character> greyCharacters = trie.getGreyCharacters();
        HashMap<Integer, List<Character>> orangeCharacters = trie.getOrangeCharacters();
        
        assertTrue("No green characters should be added", greenCharacters.isEmpty());
        assertTrue("No grey characters should be added", greyCharacters.isEmpty());
        assertTrue("No orange characters should be added", orangeCharacters.isEmpty());
    }
}
