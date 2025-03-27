// Author: Anthony Deng 8264774

import java.util.*;

public class HNumbers {

    // method which gets the sum of the proper divisor
    private static int getSumOfProperDivisor(int n) {
        int sum = 0;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                if (i * i == n) {
                    sum += i;
                } else {
                    sum += i + (n / i);
                }
            }
        }

        return sum;
    }

    // finds all pairs of harmonious numbers up until limit
    private static ArrayList<int[]> findHarmoniousPairs(int limit) {
        ArrayList<Integer> duplicates = new ArrayList<Integer>();
        ArrayList<int[]> result = new ArrayList<int[]>();
        for (int i = 1; i < limit; i++) {
            int sum = getSumOfProperDivisor(i);
            if (i == getSumOfProperDivisor(sum) && i != sum) {
                if (!duplicates.contains(i)) {
                    int[] pair = new int[2];
                    pair[0] = i;
                    pair[1] = sum;
                    result.add(pair);
                    duplicates.add(sum);
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int limit = 2000000;

        ArrayList<int[]> pairs = findHarmoniousPairs(limit);
        for (int[] pair : pairs) {
            System.out.println(pair[0] + " " + pair[1]);
        }
    }
}