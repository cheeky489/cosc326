// Author: Anthony Deng 8264774

import java.util.*;

public class Pathfinding {

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

    // method which takes care of the start and target partitions
    public static void parsePartitions(ArrayList<String> input) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> firstPartition = new ArrayList<Integer>();
        ArrayList<Integer> lastPartition = new ArrayList<Integer>();
        int startIndex = 0;
        int endIndex = input.size() - 1;

        // turning String input into array for sorting and reversing
        for (int i = 0; i < input.size() - 1; i++) {
            for (String n : input.get(startIndex).split(" ")) {
                firstPartition.add(Integer.parseInt(n));
            }
            for (String n : input.get(endIndex).split(" ")) {
                lastPartition.add(Integer.parseInt(n));
            }
        }

        // sorting the partitions
        Collections.sort(firstPartition);
        Collections.reverse(firstPartition);
        Collections.sort(lastPartition);
        Collections.reverse(lastPartition);

        // checks if a path can be found
        if (isTransformable(firstPartition, lastPartition)) {
            // finding the shortest path between the two partitions
            result = findShortestPath(firstPartition, lastPartition);

            // prints out the result of the current partition
            System.out.println("# Moves required: " + (result.size() - 1));
            for (ArrayList<Integer> partition : result) {
                for (Integer number : partition) {
                    System.out.print(number + " ");
                }
                System.out.println();
            }
        } else {
            System.out.println("# No solution possible");
            System.out.println(input.get(0));
            System.out.println(input.get(input.size() - 1));
        }
    }

    // method which checks if the sum of elements in start is equal to the sum of
    // elements in target
    private static boolean isTransformable(ArrayList<Integer> start, ArrayList<Integer> target) {
        boolean result = true;
        int sum1 = start.stream().mapToInt(Integer::intValue).sum();
        int sum2 = target.stream().mapToInt(Integer::intValue).sum();
        if (sum1 != sum2) {
            result = false;
        }

        return result;
    }

    // method which finds the shortest path from first partition to the last
    private static ArrayList<ArrayList<Integer>> findShortestPath(ArrayList<Integer> start, ArrayList<Integer> target) {
        Queue<ArrayList<Integer>> queue = new LinkedList<>();
        Set<ArrayList<Integer>> visited = new HashSet<>();
        Map<ArrayList<Integer>, ArrayList<Integer>> parentMap = new HashMap<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            ArrayList<Integer> currPartition = queue.poll();

            // get next partitions
            ArrayList<ArrayList<Integer>> nextPartitions = getNextPartitions(currPartition);
            for (ArrayList<Integer> nextPartition : nextPartitions) {
                // checks if next partition is unique or not
                if (!visited.contains(nextPartition)) {
                    queue.add(nextPartition);
                    visited.add(nextPartition);
                    parentMap.put(nextPartition, currPartition);
                }
            }

            // checks if curr is the last partition that we want
            if (currPartition.equals(target)) {
                ArrayList<ArrayList<Integer>> reconstructedPath = new ArrayList<ArrayList<Integer>>();

                // loops through the parent map
                while (currPartition != null) {
                    // add unique partitions to path
                    reconstructedPath.add(0, currPartition);
                    currPartition = parentMap.get(currPartition);
                }

                // returns list of lists of partitions as shortest path
                return reconstructedPath;
            }
        }

        // if no path found, return null
        return null;
    }

    // method that finds the next partitions after a given partition
    private static ArrayList<ArrayList<Integer>> getNextPartitions(ArrayList<Integer> input) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        for (int row = 0; row < input.get(0); row++) {
            ArrayList<Integer> next = new ArrayList<>(input);
            int elementsRemoved = 0;

            // iterate over elements and remove those greater than or equal to row
            for (int col = 0; col < next.size(); col++) {
                if (next.get(col) > row) {
                    next.set(col, next.get(col) - 1);
                    elementsRemoved++;
                } else {
                    // as elements are sorted, break once we find one not greater than row
                    break;
                }
            }
            if (elementsRemoved > 0) {
                // remove zeros
                next.removeIf(num -> num == 0);
                // insert new row at the correct position
                int newRow = elementsRemoved;
                int insertPos = 0;
                while (insertPos < next.size() && next.get(insertPos) >= newRow) {
                    insertPos++;
                }
                next.add(insertPos, newRow);

                // add to result if partition is unique
                if (!result.contains(next)) {
                    result.add(next);
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> lines = new ArrayList<String>();
        // reading lines in from file
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // skips comments and blank lines
            if (line.startsWith("#") && line.length() > 0) {
                continue;
            }
            lines.add(line);
        }

        // sorting and parsing partitions
        String separator = "--------";
        ArrayList<ArrayList<String>> partitions = getPartitions(lines);
        for (int i = 0; i < partitions.size(); i++) {
            ArrayList<String> partition = partitions.get(i);
            parsePartitions(partition);

            // write separator between partitions (not for the last one)
            if (i != partitions.size() - 1) {
                System.out.println(separator);
            }
        }

        scanner.close();
    }
}