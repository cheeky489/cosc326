import java.util.*;

// donan928 denan895
// improved version which has way less complexity and computation which should improve the overall efficiency of the program
public class findAnagrams {
    private static ArrayList<String> words = new ArrayList<String>();
    private static ArrayList<String> dict = new ArrayList<String>();
    private static ArrayList<String> output = new ArrayList<String>();

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        while (scan.hasNextLine()) {
            String line = scan.nextLine().toLowerCase();
            if (line.isEmpty()) {
                break;
            }
            words.add(line);
        }
        while (scan.hasNextLine()) {
            String line = scan.nextLine().toLowerCase();
            dict.add(line);
        }
        dict.sort((string1, string2) -> {
            if (string1.length() != string2.length()) {
                return string2.length() - string1.length();
            } else {
                return string1.compareTo(string2);
            }
        });
        for (String word : words) {
            output.clear();
            generateBestAnagram(word, 0);
            System.out.print(word + ": ");
            for (int x = output.size() - 1; x >= 0; x--) {
                System.out.print(output.get(x) + (x != 0 ? " " : "\n"));
            }
            if (output.isEmpty()) {
                System.out.println();
            }
        }
        scan.close();
    }

    // recursion but less complexity compared to previous version
    private static String generateBestAnagram(String input, int index) {
        for (int x = index; x < dict.size(); x++) {
            String remainderOfInput = remainingLetterSearch(input, dict.get(x));
            if (remainderOfInput != null) {
                String restOfAnagram = remainderOfInput.isEmpty() ? dict.get(x)
                        : generateBestAnagram(remainderOfInput, x);
                if (restOfAnagram != null) {
                    output.add(dict.get(x));
                    return dict.get(x);
                }
            }
        }
        return null;
    }

    // finds remaining
    private static String remainingLetterSearch(String word, String wordDict) {
        // removes all punctuation and whitespace and sets it to lowercase
        word = word.replaceAll("[\\p{Punct}\\s]", "").toLowerCase();
        wordDict = wordDict.replaceAll("[\\p{Punct}\\s]", "").toLowerCase();
        if (wordDict.length() > word.length()) {
            return null;
        }
        StringBuilder remaining = new StringBuilder(word);
        for (char c : wordDict.toCharArray()) {
            if (!removeChar(remaining, c)) {
                return null;
            }
        }
        return remaining.toString();
    }

    // removes character from string
    private static boolean removeChar(StringBuilder string, char c) {
        for (int x = 0; x < string.length(); x++) {
            if (string.charAt(x) == c) {
                string.deleteCharAt(x);
                return true;
            }
        }
        return false;
    }
}