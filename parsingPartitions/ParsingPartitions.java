// Author: Anthony Deng

import java.util.*;

public class ParsingPartitions {

    // method which parses the lower level partitions
    private static void parsePartitions(ArrayList<String> input, boolean isVerboseMode) {
        // arraylist that holds the output
        ArrayList<String> output = new ArrayList<String>();
        // placeholder for error message
        String msg = "";

        // looping through each input line
        for (int i = 0; i < input.size(); i++) {
            String currentLine = input.get(i);
            String previousLine = "";
            String invalidCodeMsg = "# INVALID: " + currentLine;

            // starts getting previous when it is available
            if (i - 1 >= 0) {
                previousLine = input.get(i - 1);
            }

            // handling empty lines
            if (currentLine.isEmpty()) {
                // only keep one empty line
                if (!previousLine.isEmpty()) {
                    output.add(currentLine);
                }
            }
            // handling comments
            else if (currentLine.charAt(0) == '#') {
                output.add(currentLine);
            }
            // handling partitions
            else if (isPartition(currentLine)) {
                // partitions with leading whitespace are valid
                currentLine.strip();
                if (!hasLeadingZero(currentLine)) {
                    // checks if there is one or more numbers
                    String[] tempSplit = currentLine.split("\\s+");
                    // if contains ',' then more than one as regex does not apply
                    if (tempSplit.length == 1 && !tempSplit[0].contains(",")) {
                        output.add(currentLine);
                    } else {
                        // finding index of the last digit of first number
                        int lastDigitIndex = findLastDigitIndex(currentLine);

                        // if line contains "." then assumes float/decimal present
                        if (containsDecimal(currentLine)) {
                            msg = isVerboseMode ? "# INVALID - number cannot be a float/decimal:\t" + currentLine
                                    : invalidCodeMsg;
                            output.add(msg);
                        }
                        // if line contains a letter
                        else if (containsLetter(currentLine)) {
                            msg = isVerboseMode
                                    ? "# INVALID - partition cannot contain letter(s):\t" + currentLine
                                    : invalidCodeMsg;
                            output.add(msg);
                        }
                        // if line contains a negative number
                        else if (isNumberNegative(currentLine)) {
                            msg = isVerboseMode
                                    ? "# INVALID - partition cannot contain a negative number:\t" + currentLine
                                    : invalidCodeMsg;
                            output.add(msg);
                        }
                        // checks if partition is solely split by commas
                        else if (currentLine.charAt(lastDigitIndex) == ',') {
                            // comma check to find out if commas are correct in placement and amount
                            if (isValidCommaPlacement(currentLine)) {
                                output.add(sortPartition(currentLine));
                            }
                            // otherwise partition has imbalanced commas
                            else {
                                msg = isVerboseMode
                                        ? "# INVALID - partition contains imbalanced commas:\t" + currentLine
                                        : invalidCodeMsg;
                                output.add(msg);
                            }
                        }
                        // checks if partition is solely split by whitespace
                        else if (currentLine.charAt(lastDigitIndex) == ' ') {
                            // if no comma detected then partition valid and added to output
                            if (!currentLine.contains(",")) {
                                output.add(sortPartition(currentLine));
                            }
                            // if comma is detected, then partition invalid and error msg added to output
                            else {
                                msg = isVerboseMode
                                        ? "# INVALID - partition contains commas where whitespace should be:\t"
                                                + currentLine
                                        : invalidCodeMsg;
                                output.add(msg);
                            }
                        }
                    }
                }
                // if partition fails leading zero check, then error msg added to output
                else {
                    msg = isVerboseMode ? "# INVALID - number cannot be 0 or start with 0:\t" +
                            currentLine
                            : invalidCodeMsg;
                    output.add(msg);
                }
            }
            // handles lines where not covered
            else if (!currentLine.isEmpty()) {
                // if whitespace starting line, invalid
                if (currentLine.startsWith(" ")) {
                    msg = isVerboseMode ? "# INVALID - whitespace detected starting the line:\t" + currentLine
                            : invalidCodeMsg;
                }
                // separator line error message for when there are less than 3 hyphens
                else if (currentLine.startsWith("-") && currentLine.length() < 3) {
                    msg = isVerboseMode
                            ? "# INVALID - there can not be less than 3 hyphens:\t" + currentLine
                            : invalidCodeMsg;
                }
                // separator line error message for when invalid characters included
                else if (currentLine.startsWith("-") && !isSeparator(currentLine)) {
                    msg = isVerboseMode ? "#INVALID - invalid character detected in separator:\t" + currentLine
                            : invalidCodeMsg;
                }
                // if any other character not covered, invalid
                else {
                    msg = isVerboseMode ? "# INVALID - invalid character detected starting the line:\t" + currentLine
                            : invalidCodeMsg;
                }
                output.add(msg);
            }
        }

        // printing out the partition
        for (int i = 0; i < output.size(); i++) {
            System.out.println(output.get(i));
        }

    }

    // method for getting a list of higher level scenarios
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

        // for-loop:
        // 1) filters out empty partitions
        // 2) checks if it's an invalid partition
        for (int i = 0; i < partitions.size(); i++) {
            ArrayList<String> scenario = partitions.get(i);
            int countEmpty = 0;
            int countInvalid = 0;
            for (int j = 0; j < scenario.size(); j++) {
                String currentLine = scenario.get(j);
                if (currentLine.isEmpty() || currentLine.isBlank()) {
                    countEmpty++;
                }

                // counts the number of invalid lines (anything that is not a valid partition)
                if (!currentLine.isEmpty() && !Character.isDigit(currentLine.charAt(0))
                        || currentLine.isBlank()
                        || isNumberNegative(currentLine)
                        || hasLeadingZero(currentLine)
                        || containsDecimal(currentLine)
                        || containsLetter(currentLine)
                        || (currentLine.contains(",")
                                && isPartition(currentLine)
                                && !isValidCommaPlacement(currentLine))) {
                    countInvalid++;
                }

                // decrements the number of invalid lines if partition with leading whitespace
                if (currentLine.startsWith(" ") && isPartition(currentLine)) {
                    countInvalid--;
                }
            }

            // if all lines in the scenario are empty, then skip
            boolean isScenarioEmpty = countEmpty == scenario.size();
            if (!isScenarioEmpty) {
                // only add scenario if not empty
                partitionResult.add(scenario);
            }

            // if number of invalid lines same as scenario size then invalid scenario
            boolean isScenarioInvalid = countInvalid == scenario.size();
            if (isScenarioInvalid) {
                scenario.add(0, "# INVALID SCENARIO");
            }
        }

        return partitionResult;
    }

    // method for sorting the partition into non-ascending order
    private static String sortPartition(String input) {
        String[] numbers = input.trim().split("[,\\s]+");
        int[] partition = new int[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            partition[i] = Integer.parseInt(numbers[i]); // converting each number into an integer
        }

        // sorting partition numbers into non-increasing order
        Arrays.sort(partition);
        StringBuilder sortedPartition = new StringBuilder();
        for (int j = partition.length - 1; j >= 0; j--) {
            // appending each number in correct standard format
            sortedPartition.append(partition[j]).append(" ");
        }

        return sortedPartition.toString();
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

    // method that checks if a number has a leading zero
    private static boolean hasLeadingZero(String input) {
        boolean containsLeadingZero = false;
        if (input.contains("0")) {
            String[] tempSplit = input.split("\\s+");
            for (String s : tempSplit) { // loop used to find numbers where not first in partition
                if (s.startsWith("0")) {
                    containsLeadingZero = true;
                }
            }
        }

        return containsLeadingZero;
    }

    // method that checks if a number is negative or not
    private static boolean isNumberNegative(String input) {
        boolean isNegative = false;
        if (!input.isEmpty() && (Character.isDigit(input.charAt(0)) || input.charAt(0) == '-')) {
            if (input.contains("-")) {
                isNegative = true;
            }
        }

        return isNegative;
    }

    // method that checks if input is a partition or not
    private static boolean isPartition(String input) {
        boolean isAPartition = false;
        if (!input.isEmpty()) {
            for (int i = 0; i < input.length(); i++) {
                if (Character.isDigit(input.charAt(i))) {
                    isAPartition = true;
                    break;
                }
            }
        }
        return isAPartition;
    }

    // method which checks if the number and position of commas is correct or not
    private static boolean isValidCommaPlacement(String input) {
        boolean isValid = false;
        String tempLine = input.replaceAll("\\s*,\\s*", " , ");
        // e.g for a correct partition, array will look like this: [1, ',' , 2, ',' , 3]
        String[] tempArray = tempLine.trim().split(" ");
        // if consecutive commas, empty space inserted in between
        if (!tempArray[tempArray.length - 1].contains(",")) {
            for (int j = 1; j <= tempArray.length - 1; j += 2) {
                // empty space can then be used to find consecutive commas
                if (tempArray[j].equals(",") && !tempArray[j - 1].isEmpty()) {
                    isValid = true;
                } else {
                    isValid = false;
                    break;
                }
            }
        } else {
            isValid = false;
        }

        return isValid;
    }

    // method which checks if partition contains a double/float
    private static boolean containsDecimal(String input) {
        boolean hasDecimal = false;
        if (input.contains(".")) {
            hasDecimal = true;
        }

        return hasDecimal;
    }

    // method which checks if partition contains a letter
    private static boolean containsLetter(String input) {
        boolean hasLetter = false;
        if (input.matches(".*[a-zA-Z]+.*")) {
            hasLetter = true;
        }

        return hasLetter;
    }

    // method which finds the index of the last digit
    private static int findLastDigitIndex(String input) {
        boolean isDigit = true;
        int index = 0;
        while (isDigit) {
            if (!Character.isDigit(input.charAt(index))) {
                isDigit = false;
                break;
            } else {
                index++;
            }
        }

        return index;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> lines = new ArrayList<String>();
        boolean isVerboseMode = false;

        // checks cmd line for verbose mode
        for (String arg : args) {
            if (arg.equals("-v")) {
                isVerboseMode = true;
                break;
            }
        }
        // reading lines in from file
        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }

        // sorting and parsing partitions
        String separator = "--------";
        ArrayList<ArrayList<String>> partitions = getPartitions(lines);
        for (int i = 0; i < partitions.size(); i++) {
            ArrayList<String> partition = partitions.get(i);
            parsePartitions(partition, isVerboseMode);

            // write separator between partitions (not for the last one)
            if (i != partitions.size() - 1) {
                System.out.println(separator);
            }
        }

        scanner.close();
    }
}