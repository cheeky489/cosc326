import java.util.*;

public class OneDimensionSolitaire {
    public static void main(String[] args) {
        int numPegs = Integer.parseInt(args[0]);
        ArrayList<String> winningStates = findWinningStates(numPegs);
        int winningStatesCount = 0;
        for (String state : winningStates) {
            System.out.println(state);
            winningStatesCount++;
        }
        System.out.println("\nThere are " + winningStatesCount + " winning states for " + numPegs + " starting pegs");
    }

    public static ArrayList<String> findWinningStates(int numPegs) {
        String startingState = "O".repeat(numPegs * 2) + "I" + "O".repeat(numPegs * 2);
        Queue<String> stateQueue = new LinkedList<>();
        stateQueue.add(startingState);
        ArrayList<String> winningList = new ArrayList<>(); // Final list of winning states
        HashSet<String> foundWinningStates = new HashSet<>(); // Initial set of winning states
        HashSet<String> beenToStates = new HashSet<>(); // Set of already visited to states

        while (!stateQueue.isEmpty()) {
            String state = stateQueue.poll();

            // Checks for duplicate or reversed states
            if (checkDuplicateOrReverse(state, beenToStates)) {
                continue;
            }
            // Checks if state "I" matches numPegs
            if (state.chars().filter(ch -> ch == 'I').count() == numPegs) {
                // Check if it's not a duplicate or reverse state before adding to winning list
                if (!checkDuplicateOrReverse(state, foundWinningStates)) {
                    winningList.add(state);
                }
            } else {
                // Generates possible moves for state and adds to stateQueue
                for (String move : generateMovesForState(state)) {
                    stateQueue.add(move);
                }
            }
        }
        return winningList; // Return the list of winning states
    }

    public static boolean checkDuplicateOrReverse(String state, HashSet<String> stateList) {
        int firstI = state.indexOf('I');
        int lastI = state.lastIndexOf('I');
        // Substring of state
        String stateSubstring = state.substring(firstI, lastI + 1);
        // Reversed state
        String reversedState = new StringBuilder(stateSubstring).reverse().toString();
        // Check if either the state or reversed state has already been visited
        if (stateList.contains(stateSubstring) || stateList.contains(reversedState)) {
            return true;
        } else {
            // If not visited before, add both the state and its reverse to the set of
            // visited substrings
            stateList.add(stateSubstring);
            stateList.add(reversedState);
            return false;
        }
    }

    public static ArrayList<String> generateMovesForState(String state) {
        ArrayList<String> moveList = new ArrayList<>();
        for (int i = 0; i < state.length() - 2; i++) {
            if (state.substring(i, i + 3).equals("IOO")) { // Replaces IOO with OII
                moveList.add(state.substring(0, i) + "OII" + state.substring(i + 3));
            } else if (state.substring(i, i + 3).equals("OOI")) { // Replaces OOI with IIO
                moveList.add(state.substring(0, i) + "IIO" + state.substring(i + 3));
            }
        }
        return moveList;
    }

}
