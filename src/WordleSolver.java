import java.io.*;
import java.util.*;

public class WordleSolver {
    public static void main(String[] args) {
        // Instantiate and load the Trie
        Trie trie = new Trie();
        trie.loadFromFile("assets/english_words.txt");
        
        Scanner scanner = new Scanner(System.in);
        List<String> invalidWords = new ArrayList<>();
        
        // Loop for 6 steps
        for (int step = 1; step <= 6; step++) {
            System.out.println("\n--- Step " + step + " ---");
            
            // Get the next word from the trie
            String word = trie.getNextWord();
            if (word == null) {
                System.out.println("No possible words found!");
                break;
            }
            
            System.out.println("Current word: " + word);
            
            // For each character, ask the user for input (1=GREEN, 2=ORANGE, 3=GREY, 9=INVALID WORD)
            List<WordleCharacter> wordleCharacters = new ArrayList<>();
            boolean invalidWord = false;
            
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                System.out.print("Enter state for '" + c + "' at position " + i + " (1=GREEN, 2=ORANGE, 3=GREY, 9=INVALID WORD): ");
                int state = scanner.nextInt();
                
                if (state == 9) {
                    // Mark the entire word as invalid
                    invalidWord = true;
                    invalidWords.add(word);
                    trie.deleteWord(word);
                    System.out.println("Word '" + word + "' marked as invalid and deleted from Trie. Restarting step...");
                    step--; // Restart this step
                    break;
                }
                
                WordleCharacter.State charState;
                switch (state) {
                    case 1:
                        charState = WordleCharacter.State.GREEN;
                        break;
                    case 2:
                        charState = WordleCharacter.State.ORANGE;
                        break;
                    case 3:
                        charState = WordleCharacter.State.GREY;
                        break;
                    default:
                        System.out.println("Invalid input. Assuming UNSET.");
                        charState = WordleCharacter.State.UNSET;
                }
                
                wordleCharacters.add(new WordleCharacter(c, charState));
            }
            
            if (invalidWord) {
                continue; // Skip to next iteration (which restarts the same step)
            }
            
            // Construct a WordleWord
            WordleWord wordleWord = new WordleWord(wordleCharacters);
            
            // Check if the wordle is solved
            if (wordleWord.isSolved()) {
                System.out.println("YAYY!!!");
                deleteInvalidWordsFromFile(invalidWords);
                scanner.close();
                return;
            }
            
            // Update the trie data structures
            trie.updateWithWordleFeedback(wordleWord);
        }
        
        // If not solved after 5 steps, ask the user for the solution
        System.out.print("\nEnter the solution word: ");
        scanner.nextLine(); // Consume the newline
        String solution = scanner.nextLine().trim().toLowerCase();
        
        // Search for the solution in the trie
        if (!trie.search(solution)) {
            // If it doesn't exist, append it to the english_words.txt file
            try (PrintWriter writer = new PrintWriter(new FileWriter("assets/english_words.txt", true))) {
                writer.println(solution);
            } catch (IOException e) {
                System.err.println("Error appending to file: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        deleteInvalidWordsFromFile(invalidWords);
        scanner.close();
    }
    
    /**
     * Deletes invalid words from the english_words.txt file
     */
    private static void deleteInvalidWordsFromFile(List<String> invalidWords) {
        if (invalidWords.isEmpty()) {
            return;
        }
        
        String filePath = "assets/english_words.txt";
        Set<String> invalidWordSet = new HashSet<>(invalidWords);
        
        try {
            // Read all lines from the file
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmedLine = line.trim().toLowerCase();
                    // Keep the line if it's not in the invalid words set
                    if (!invalidWordSet.contains(trimmedLine)) {
                        lines.add(line);
                    }
                }
            }
            
            // Write all remaining lines back to the file
            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
                for (String line : lines) {
                    writer.println(line);
                }
            }
            
            System.out.println("Successfully deleted " + invalidWords.size() + " invalid word(s) from english_words.txt");
        } catch (IOException e) {
            System.err.println("Error deleting invalid words from file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
