import java.io.*;
import java.util.*;

/**
 * WordleSolver is an interactive command-line application that helps solve
 * Wordle puzzles using a Trie-based word filtering algorithm. It suggests words
 * and updates the word list based on player feedback about letter colors
 * (green, orange, grey).
 */
public class WordleSolver {
    private static final String WORDS_FILE_PATH = "assets/english_words.txt";
    private static final int MAX_ATTEMPTS = 6;

    public static void main(String[] args) {
        boolean useStringInputMode = shouldUseStringInputMode(args);
        Trie trie = initializeTrie();
        Scanner scanner = new Scanner(System.in);
        List<String> invalidWords = new ArrayList<>();

        playGame(scanner, trie, useStringInputMode, invalidWords);

        scanner.close();
    }

    /**
     * Determines whether to use string input mode based on command-line arguments.
     * String input mode allows entering all character states at once (e.g.,
     * "12333"). Character input mode prompts for each character individually.
     *
     * @param args command-line arguments; "1" enables character input mode
     * @return true if string input mode should be used, false for character input
     *         mode
     */
    private static boolean shouldUseStringInputMode(String[] args) {
        return args.length == 0 || !args[0].equalsIgnoreCase("1");
    }

    /**
     * Initializes and loads the word Trie from the English words file.
     *
     * @return initialized Trie with words loaded from file
     */
    private static Trie initializeTrie() {
        Trie trie = new Trie();
        trie.loadFromFile(WORDS_FILE_PATH);
        return trie;
    }

    /**
     * Main game loop that runs for up to 6 attempts to solve the Wordle puzzle.
     * Handles word suggestions, user feedback, and game state updates.
     *
     * @param scanner            Scanner for reading user input
     * @param trie               Trie containing valid words
     * @param useStringInputMode whether to use string or character input mode
     * @param invalidWords       list to track words marked as invalid by the player
     */
    private static void playGame(Scanner scanner, Trie trie, boolean useStringInputMode,
            List<String> invalidWords) {
        for (int step = 1; step <= MAX_ATTEMPTS; step++) {
            System.out.println("\n--- Step " + step + " ---");

            String suggestedWord = trie.getNextWord(shouldUseAlmostFinalStrategy(step));
            if (suggestedWord == null) {
                System.out.println("No possible words found!");
                break;
            }

            System.out.println("Current word: " + suggestedWord);

            GameStepResult result = processGameStep(scanner, trie, suggestedWord, useStringInputMode, invalidWords);

            if (result.isInvalidWord) {
                step--; // Restart this step
                continue;
            }

            if (result.isSolved) {
                System.out.println("YAYY!!!");
                cleanupAfterWin(invalidWords, scanner);
                return;
            }

            trie.updateWithWordleFeedback(result.wordleWord);
        }

        handleGameNotSolvedAfterMaxAttempts(scanner, trie, invalidWords);
    }

    /**
     * Determines if the almost-final strategy should be used.
     * This strategy is activated in the last 2 attempts to narrow down
     * possibilities.
     *
     * @param currentStep the current game step (1-6)
     * @return true if this is one of the last 2 steps
     */
    private static boolean shouldUseAlmostFinalStrategy(int currentStep) {
        return currentStep >= MAX_ATTEMPTS - 1;
    }

    /**
     * Processes feedback for a single game step. Collects character states from the
     * user
     * and converts them to WordleCharacter objects.
     *
     * @param scanner            Scanner for user input
     * @param trie               Trie for word operations
     * @param word               the word being guessed
     * @param useStringInputMode whether to use string or character input mode
     * @param invalidWords       list to add word to if marked invalid
     * @return GameStepResult containing the processed feedback or invalid word flag
     */
    private static GameStepResult processGameStep(Scanner scanner, Trie trie, String word,
            boolean useStringInputMode, List<String> invalidWords) {
        String inputState = getInputState(scanner, useStringInputMode, word.length());
        List<WordleCharacter> wordleCharacters = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            int state = extractCharacterState(scanner, useStringInputMode, inputState, c, i);

            if (state == 9) {
                // User marked this word as invalid
                markWordAsInvalid(word, trie, invalidWords);
                return new GameStepResult(true, false, null);
            }

            WordleCharacter.State charState = convertStateIntToEnum(state);
            wordleCharacters.add(new WordleCharacter(c, charState));
        }

        WordleWord wordleWord = new WordleWord(wordleCharacters);
        boolean isSolved = wordleWord.isSolved();
        return new GameStepResult(false, isSolved, wordleWord);
    }

    /**
     * Extracts the character state based on the input mode.
     * In string mode, parses the character from the pre-entered string.
     * In character mode, prompts the user for this specific character.
     *
     * @param scanner            Scanner for user input
     * @param useStringInputMode whether using string or character input mode
     * @param inputState         the pre-entered input string (if in string mode)
     * @param character          the character at current position
     * @param position           the position of the character in the word
     * @return integer state (1=GREEN, 2=ORANGE, 3=GREY, 9=INVALID)
     */
    private static int extractCharacterState(Scanner scanner, boolean useStringInputMode,
            String inputState, char character, int position) {
        if (useStringInputMode) {
            return inputState.charAt(position) - '0';
        } else {
            System.out.print("Enter state for '" + character + "' at position " + position
                    + " (1=GREEN, 2=ORANGE, 3=GREY, 9=INVALID WORD): ");
            return scanner.nextInt();
        }
    }

    /**
     * Converts an integer state code to the corresponding WordleCharacter.State
     * enum.
     * Valid codes: 1=GREEN, 2=ORANGE, 3=GREY, others=UNSET.
     *
     * @param stateCode integer state code from user input
     * @return corresponding WordleCharacter.State enum value
     */
    private static WordleCharacter.State convertStateIntToEnum(int stateCode) {
        switch (stateCode) {
            case 1:
                return WordleCharacter.State.GREEN;
            case 2:
                return WordleCharacter.State.ORANGE;
            case 3:
                return WordleCharacter.State.GREY;
            default:
                System.out.println("Invalid input. Assuming UNSET.");
                return WordleCharacter.State.UNSET;
        }
    }

    /**
     * Marks a word as invalid: removes it from the Trie and adds it to the invalid
     * words list.
     *
     * @param word         the word to mark as invalid
     * @param trie         Trie to remove the word from
     * @param invalidWords list to add the word to
     */
    private static void markWordAsInvalid(String word, Trie trie, List<String> invalidWords) {
        invalidWords.add(word);
        trie.deleteWord(word);
        System.out.println("Word '" + word + "' marked as invalid and deleted from Trie. Restarting step...");
    }

    /**
     * Gets user input for character states. Supports two modes:
     * - String mode: user enters all states at once (e.g., "12333")
     * - Character mode: returns empty string (states entered individually)
     *
     * @param scanner            Scanner for reading user input
     * @param useStringInputMode whether to use string input mode
     * @param wordLength         the length of the word (for validation)
     * @return input string if in string mode, empty string otherwise
     */
    private static String getInputState(Scanner scanner, boolean useStringInputMode, int wordLength) {
        if (!useStringInputMode) {
            return "";
        }

        return promptForValidInputState(scanner, wordLength);
    }

    /**
     * Repeatedly prompts user for valid input state string until valid input is
     * received.
     * Handles special cases: "1" expands to all greens, "9" means invalid word.
     *
     * @param scanner    Scanner for reading user input
     * @param wordLength expected length of input string
     * @return valid input state string
     */
    private static String promptForValidInputState(Scanner scanner, int wordLength) {
        while (true) {
            System.out.print("Enter the state for the entire word as a string (e.g., '12333' for "
                    + "GREEN, ORANGE, GREY, GREY, GREY): ");
            String inputState = scanner.nextLine().trim();

            if (inputState.length() == wordLength) {
                return inputState;
            }
            if ("9".equals(inputState)) {
                return inputState;
            }
            if ("1".equals(inputState)) {
                return "1".repeat(wordLength); // All GREEN
            }
        }
    }

    /**
     * Cleans up after successful game completion: removes invalid words from file
     * and closes scanner.
     *
     * @param invalidWords list of words to delete from file
     * @param scanner      Scanner to close
     */
    private static void cleanupAfterWin(List<String> invalidWords, Scanner scanner) {
        deleteInvalidWordsFromFile(invalidWords);
        scanner.close();
    }

    /**
     * Handles the case where the game is not solved after maximum attempts.
     * Prompts user for the solution word and adds it to the word file if new.
     *
     * @param scanner      Scanner for reading user input
     * @param trie         Trie to search for the solution word
     * @param invalidWords list of words to delete from file
     */
    private static void handleGameNotSolvedAfterMaxAttempts(Scanner scanner, Trie trie, List<String> invalidWords) {
        System.out.print("\nEnter the solution word: ");
        scanner.nextLine(); // Consume newline
        String solution = scanner.nextLine().trim().toLowerCase();

        if (!trie.search(solution)) {
            appendSolutionToFile(solution);
        }

        deleteInvalidWordsFromFile(invalidWords);
    }

    /**
     * Appends a new word to the English words file.
     *
     * @param word the word to append
     */
    private static void appendSolutionToFile(String word) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(WORDS_FILE_PATH, true))) {
            writer.println(word);
        } catch (IOException e) {
            System.err.println("Error appending to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Deletes invalid words from the English words file.
     * Reads entire file, filters out invalid words, and writes back.
     *
     * @param invalidWords list of words to delete
     */
    private static void deleteInvalidWordsFromFile(List<String> invalidWords) {
        if (invalidWords.isEmpty()) {
            return;
        }

        Set<String> invalidWordSet = new HashSet<>(invalidWords);
        List<String> validLines = readAndFilterValidLines(invalidWordSet);
        writeValidLinesToFile(validLines);
        System.out.println("Successfully deleted " + invalidWords.size() + " invalid word(s) from english_words.txt");
    }

    /**
     * Reads all lines from the words file and filters out invalid words.
     *
     * @param invalidWordSet set of invalid words to filter out
     * @return list of valid lines to keep
     */
    private static List<String> readAndFilterValidLines(Set<String> invalidWordSet) {
        List<String> validLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(WORDS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim().toLowerCase();
                if (!invalidWordSet.contains(trimmedLine)) {
                    validLines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
        return validLines;
    }

    /**
     * Writes valid lines back to the words file, replacing its previous contents.
     *
     * @param validLines lines to write to the file
     */
    private static void writeValidLinesToFile(List<String> validLines) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(WORDS_FILE_PATH))) {
            for (String line : validLines) {
                writer.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inner class to encapsulate the result of a single game step.
     * Contains information about whether the word was invalid, if the puzzle was
     * solved, and the feedback.
     */
    private static class GameStepResult {
        boolean isInvalidWord;
        boolean isSolved;
        WordleWord wordleWord;

        GameStepResult(boolean isInvalidWord, boolean isSolved, WordleWord wordleWord) {
            this.isInvalidWord = isInvalidWord;
            this.isSolved = isSolved;
            this.wordleWord = wordleWord;
        }
    }
}
