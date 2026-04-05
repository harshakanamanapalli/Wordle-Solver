import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

public class WordleWordTest {
    private List<WordleCharacter> characters;
    private WordleWord wordleWord;

    @Before
    public void setUp() {
        characters = new ArrayList<>();
    }

    @Test
    public void testIsSolvedAllGreen() {
        characters.add(new WordleCharacter('h', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('e', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('l', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('l', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('o', WordleCharacter.State.GREEN));
        wordleWord = new WordleWord(characters);
        
        assertTrue("Word with all GREEN characters should be solved", wordleWord.isSolved());
    }

    @Test
    public void testIsNotSolvedWithOrange() {
        characters.add(new WordleCharacter('h', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('e', WordleCharacter.State.ORANGE));
        characters.add(new WordleCharacter('l', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('l', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('o', WordleCharacter.State.GREEN));
        wordleWord = new WordleWord(characters);
        
        assertFalse("Word with ORANGE character should not be solved", wordleWord.isSolved());
    }

    @Test
    public void testIsNotSolvedWithGrey() {
        characters.add(new WordleCharacter('h', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('e', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('l', WordleCharacter.State.GREY));
        characters.add(new WordleCharacter('l', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('o', WordleCharacter.State.GREEN));
        wordleWord = new WordleWord(characters);
        
        assertFalse("Word with GREY character should not be solved", wordleWord.isSolved());
    }

    @Test
    public void testIsNotSolvedWithUnset() {
        characters.add(new WordleCharacter('h', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('e', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('l', WordleCharacter.State.UNSET));
        characters.add(new WordleCharacter('l', WordleCharacter.State.GREEN));
        characters.add(new WordleCharacter('o', WordleCharacter.State.GREEN));
        wordleWord = new WordleWord(characters);
        
        assertFalse("Word with UNSET character should not be solved", wordleWord.isSolved());
    }

    @Test
    public void testIsNotSolvedMixedNonGreen() {
        characters.add(new WordleCharacter('h', WordleCharacter.State.ORANGE));
        characters.add(new WordleCharacter('e', WordleCharacter.State.GREY));
        characters.add(new WordleCharacter('l', WordleCharacter.State.UNSET));
        characters.add(new WordleCharacter('l', WordleCharacter.State.ORANGE));
        characters.add(new WordleCharacter('o', WordleCharacter.State.GREY));
        wordleWord = new WordleWord(characters);
        
        assertFalse("Word with all non-GREEN characters should not be solved", wordleWord.isSolved());
    }

    @Test
    public void testIsSolvedSingleCharacter() {
        characters.add(new WordleCharacter('a', WordleCharacter.State.GREEN));
        wordleWord = new WordleWord(characters);
        
        assertTrue("Single GREEN character word should be solved", wordleWord.isSolved());
    }

    @Test
    public void testIsNotSolvedSingleCharacter() {
        characters.add(new WordleCharacter('a', WordleCharacter.State.ORANGE));
        wordleWord = new WordleWord(characters);
        
        assertFalse("Single non-GREEN character word should not be solved", wordleWord.isSolved());
    }
}
