import java.util.*;

public class Playing {

    private static HashSet<ArrayList<Integer>> happyPositions = new HashSet<ArrayList<Integer>>();
    private static HashSet<ArrayList<Integer>> sadPositions = new HashSet<ArrayList<Integer>>();
    private static HashSet<ArrayList<Integer>> visited = new HashSet<ArrayList<Integer>>();
    private static ArrayList<Integer> startPosition;
    private static List<ArrayList<Integer>> targetPartitions;

    // method that gets separate partitions
    private static ArrayList<ArrayList<String>> getPartitions(ArrayList<String> input) {
        ArrayList<ArrayList<String>> partitionResult = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> partitions = new ArrayList<ArrayList<String>>();
        ArrayList<String> partition = new ArrayList<String>();

        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);

            if (partition == null) {
                // need a new partition
                partition = new ArrayList<String>();
            }

            // check if the line is a separator
            if (isSeparator(line)) {
                partitions.add(partition);
                // end the current partition
                partition = null;
            }
            // when we have found the last partition
            else if (i == input.size() - 1) {
                partition.add(line);
                partitions.add(partition);
            } else {
                partition.add(line);
            }
        }

        // for-loop filters out empty partitions
        for (int i = 0; i < partitions.size(); i++) {
            ArrayList<String> scenario = partitions.get(i);
            int countEmpty = 0;
            for (int j = 0; j < scenario.size(); j++) {
                String currentLine = scenario.get(j);
                if (currentLine.isEmpty() || currentLine.isBlank()) {
                    countEmpty++;
                }
            }

            // if all lines in the scenario are empty, then skip
            boolean isScenarioEmpty = countEmpty == scenario.size();
            if (!isScenarioEmpty) {
                // only add scenario if not empty
                partitionResult.add(scenario);
            }
        }

        return partitionResult;
    }

    // method for finding separators
    private static boolean isSeparator(String input) {
        boolean isValidSeparator = true;
        // makes sure separator is at least length 3
        if (input.length() >= 3) {
            for (int i = 0; i < input.length(); i++) {
                // checks if line contains invalid character
                if (input.charAt(i) != '-') {
                    isValidSeparator = false;
                    break;
                }
            }
        } else {
            isValidSeparator = false;
        }
        return isValidSeparator;
    }

    private static ArrayList<Integer> convertToIntegerList(String line) {
        String[] parts = line.split(" ");
        ArrayList<Integer> partition = new ArrayList<>();
        for (String p : parts) {
            partition.add(Integer.parseInt(p));
        }
        return partition;
    }

    // method which takes care of the output
    public static String getResult(String start, ArrayList<String> partitions) {
        startPosition = convertToIntegerList(start);
        targetPartitions = new ArrayList<>();
        for (String s : partitions) {
            targetPartitions.add(convertToIntegerList(s));
        }

        String result = determineGameOutcome(startPosition, targetPartitions);
        StringBuilder outcome = new StringBuilder(start + "\n\n");
        for (String s : partitions) {
            outcome.append(s).append("\n");
        }
        return outcome + result;
    }

    // method which takes care of the partitions
    private static String determineGameOutcome(ArrayList<Integer> start, List<ArrayList<Integer>> targets) {
        Queue<ArrayList<Integer>> queue = new LinkedList<>();
        for (ArrayList<Integer> target : targets) {
            queue.add(target);
            sadPositions.add(target);
        }

        while (!queue.isEmpty()) {
            ArrayList<Integer> curr = queue.poll();
            visited.add(curr);
            // checking sad moves
            if (sadPositions.contains(curr)) {
                for (ArrayList<Integer> move : getAllBackwardMoves(curr)) { // getAllBackwordsMoves(dir, curr)
                    if (!visited.contains(move) && !sadPositions.contains(move)) {
                        happyPositions.add(move);
                        queue.add(move);
                    }
                }
            } else {
                // checking happy moves
                for (ArrayList<Integer> move : getAllBackwardMoves(curr)) {
                    if (!visited.contains(move)) {
                        if (allLeadToHappy(move)) {
                            sadPositions.add(move);
                            queue.add(move);
                        } else if (anyLeadToSad(move)) {
                            happyPositions.add(move);
                            queue.add(move);
                        }
                    }
                }
            }
        }

        if (visited.contains(start)) {
            if (happyPositions.contains(start)) {
                return "# WIN";
            } else {
                return "# LOSE";
            }
        } else {
            return "# DRAW";
        }
    }

    // method to check if any forward move leads to a sad position
    private static boolean anyLeadToSad(ArrayList<Integer> partition) {
        for (ArrayList<Integer> move : getAllForwardMoves(partition)) {
            if (sadPositions.contains(move)) {
                return true;
            }
        }
        return false;
    }

    // method to check if all forward moves lead to a happy position
    private static boolean allLeadToHappy(ArrayList<Integer> partition) {
        for (ArrayList<Integer> move : getAllForwardMoves(partition)) {
            if (!happyPositions.contains(move)) {
                return false;
            }
        }
        return true;
    }

    // method to get all forward moves
    private static ArrayList<ArrayList<Integer>> getAllForwardMoves(ArrayList<Integer> partition) {
        HashSet<ArrayList<Integer>> forwardMoves = new HashSet<>();
        for (int i : potentialForwardMoves(partition)) {
            forwardMoves.add(runForwardMove(partition, i));
        }
        return new ArrayList<>(forwardMoves);
    }

    // method to get potential forward moves
    private static int[] potentialForwardMoves(ArrayList<Integer> partition) {
        int[] moves = new int[partition.get(0)];
        for (int i = 0; i < moves.length; i++) {
            moves[i] = i + 1;
        }
        return moves;
    }

    // method which runs a forward move
    private static ArrayList<Integer> runForwardMove(ArrayList<Integer> partition, int colToMove) {
        int rowSize = 0;
        ArrayList<Integer> newPartition = new ArrayList<>();
        for (int i : partition) {
            if (i >= colToMove) {
                rowSize++;
                if (i - 1 != 0) {
                    newPartition.add(i - 1);
                }
            } else {
                newPartition.add(i);
            }
        }
        int index = 0;
        while (index < newPartition.size() && newPartition.get(index) >= rowSize) {
            index++;
        }
        newPartition.add(index, rowSize);
        Collections.sort(newPartition, Collections.reverseOrder());
        return newPartition;
    }

    // method to get all backward moves
    private static ArrayList<ArrayList<Integer>> getAllBackwardMoves(ArrayList<Integer> partition) {
        HashSet<ArrayList<Integer>> backwardMoves = new HashSet<>();
        for (int i : potentialBackwardMoves(partition)) {
            backwardMoves.add(runBackwardMove(partition, i));
        }
        return new ArrayList<>(backwardMoves);
    }

    // method to get all potential backward moves
    private static int[] potentialBackwardMoves(ArrayList<Integer> partition) {
        int[] moves = new int[partition.size()];
        for (int i = 0; i < moves.length; i++) {
            moves[i] = i;
        }
        return moves;
    }

    // method which runs a backward move
    private static ArrayList<Integer> runBackwardMove(ArrayList<Integer> partition, int colToMove) {
        ArrayList<Integer> newPartition = new ArrayList<>(partition);
        int amountToAdd = newPartition.get(colToMove);
        newPartition.remove(colToMove);
        for (int i = 0; i < amountToAdd; i++) {
            if (i >= newPartition.size()) {
                newPartition.add(1);
            } else {
                newPartition.set(i, newPartition.get(i) + 1);
            }
        }
        Collections.sort(newPartition, Collections.reverseOrder());
        return newPartition;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> lines = new ArrayList<String>();
        // reading lines in from file
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // skips comments and blank lines
            if ((line.startsWith("#") && line.length() > 0) || line.isEmpty()) {
                continue;
            }
            lines.add(line);
        }

        // sorting and parsing partitions
        String separator = "--------";
        ArrayList<ArrayList<String>> partitions = getPartitions(lines);
        for (int i = 0; i < partitions.size(); i++) {
            // resets sets for new partitions
            happyPositions.clear();
            sadPositions.clear();
            visited.clear();

            ArrayList<String> partition = partitions.get(i);

            // separate the input
            String start = partition.get(0);
            ArrayList<String> targets = new ArrayList<>(partition.subList(1, partition.size()));

            // get and print the result
            String result = getResult(start, targets);
            System.out.println(result);

            // write separator between partitions (not for the last one)
            if (i != partitions.size() - 1) {
                System.out.println(separator);
            }
        }

        scanner.close();
    }
}