package com.github.rajda;

import java.util.Scanner;

public class test {
    private static final int[] numberCustomers = {15, 15, 25, 25, 30, 30, 50, 50};
    private static final int[] numberLinks = {43, 76, 57, 86, 72, 78, 110, 143};
    private static final int iterationsNumber = 10;
    private static final int optimizationCyclesNumber = 30;

	 private static final int bandwith = 155;
	 private static final int lowerLimit = 3;
	 private static final int upperLimit = 7;

//    private static final int bandwith = 622;
//    private static final int lowerLimit = 11;
//    private static final int upperLimit = 17;

    public static void main(String[] args) {
        int howManyDifferentParameters = numberCustomers.length;
        for (int i = 0; i < howManyDifferentParameters; i++) {
            if (numberCustomers[i] == 0)
                continue;

            int maxNumberLinks = numberCustomers[i] * (numberCustomers[i] - 1) / 2;

            if (numberLinks[i] > maxNumberLinks || numberLinks[i] < 1) {
                Scanner in = new Scanner(System.in);
                while (numberLinks[i] > maxNumberLinks || numberLinks[i] < 1) {
                    System.out.print("Type the number of links between users (1<=x<=" + maxNumberLinks + "): ");
                    numberLinks[i] = Integer.parseInt(in.nextLine());
                }
                in.close();
            }

            BeeAlgorithmInIDP idp = new BeeAlgorithmInIDP(numberCustomers[i], numberLinks[i], bandwith, lowerLimit, upperLimit, iterationsNumber, optimizationCyclesNumber);
            idp.mainSteps();
        }
    }
}
