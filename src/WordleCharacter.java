public class WordleCharacter {
    public enum State {
        GREEN,
        ORANGE,
        GREY,
        UNSET,
    }

    private char letter;
    private State state;

    public WordleCharacter(char letter, State state) {
        this.letter = letter;
        this.state = state;
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
