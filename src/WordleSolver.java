import java.io.*;
import java.util.*;

public class WordleSolver {
    public static void main(String[] args) {
        // Instantiate and load the Trie
        Trie trie = new Trie();
        trie.loadFromFile("assets/english_words.txt");
        
        Scanner scanner = new Scanner(System.in);
        
        // Loop for 5 steps
        for (int step = 1; step <= 5; step++) {
            System.out.println("\n--- Step " + step + " ---");
            
            // Get the next word from the trie
            String word = trie.getNextWord();
            if (word == null) {
                System.out.println("No possible words found!");
                break;
            }
            
            System.out.println("Current word: " + word);
            
            // For each character, ask the user for input (1=GREEN, 2=ORANGE, 3=GREY)
            List<WordleCharacter> wordleCharacters = new ArrayList<>();
            
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                System.out.print("Enter state for '" + c + "' at position " + i + " (1=GREEN, 2=ORANGE, 3=GREY): ");
                int state = scanner.nextInt();
                
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
            
            // Construct a WordleWord
            WordleWord wordleWord = new WordleWord(wordleCharacters);
            
            // Check if the wordle is solved
            if (wordleWord.isSolved()) {
                System.out.println("YAYY!!!");
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
        
        scanner.close();
    }
}
