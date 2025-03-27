import java.util.*;

public class Ants {

    private static void getScenario(ArrayList<String> input) {
        ArrayList<String> scenario = new ArrayList<String>();

        for (String line : input) {
            if (line.isEmpty()) {
                outputScenario(scenario);
                scenario.clear();
            } else if (line.startsWith("#")) {
                continue;
            } else {
                scenario.add(line);
            }
        }

        // catching leftover scenario
        outputScenario(scenario);
    }

    private static String runScenario(ArrayList<String> input) {
        HashMap<String, String> dnaMap = new HashMap<String, String>();
        int moves = Integer.parseInt(input.get(input.size() - 1));
        String facing = "N";
        int x = 0;
        int y = 0;
        String val = x + " " + y;
        String dna = "";

        for (int i = 0; i < moves; i++) {
            boolean mapValExists = false;
            if (dnaMap.containsKey(val)) {
                mapValExists = true;
                val = dnaMap.get(x + " " + y);
                for (String line : input) {
                    if (String.valueOf(line.charAt(0)).equals(val)) {
                        dna = line;
                    }
                }
            } else {
                mapValExists = false;
                dna = input.get(0);
            }

            String[] dnaSplit = dna.split(" ");
            int goTo = "NESW".indexOf(facing);
            if (mapValExists) {
                dnaMap.remove(x + " " + y);
                dnaMap.put(val, String.valueOf(dnaSplit[2].charAt(goTo)));
            } else {
                dnaMap.put(val, String.valueOf(dnaSplit[2].charAt(goTo)));
            }
            // System.out.println(goTo);

            // finds the index where the ant is now facing
            facing = String.valueOf(dnaSplit[1].charAt(goTo));

            switch (facing) {
                case "N":
                    y++;
                    break;
                case "E":
                    x++;
                    break;
                case "S":
                    y--;
                    break;
                case "W":
                    x--;
                    break;
            }
        }

        return x + " " + y;
    }

    private static void outputScenario(ArrayList<String> input) {
        if (!input.isEmpty()) {
            for (String s : input) {
                System.out.println(s);
            }
            System.out.println("# " + runScenario(input) + "\n");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> lines = new ArrayList<String>();
        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }

        getScenario(lines);
        scanner.close();
    }
}
