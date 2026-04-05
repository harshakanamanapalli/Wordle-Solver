import java.util.List;

public class WordleWord {
    private List<WordleCharacter> characters;

    public WordleWord(List<WordleCharacter> characters) {
        this.characters = characters;
    }

    public List<WordleCharacter> getCharacters() {
        return characters;
    }

    public void setCharacters(List<WordleCharacter> characters) {
        this.characters = characters;
    }

    public boolean isSolved() {
        for (WordleCharacter character : characters) {
            if (character.getState() != WordleCharacter.State.GREEN) {
                return false;
            }
        }
        return true;
    }
}
