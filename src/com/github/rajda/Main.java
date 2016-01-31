package com.github.rajda;

public class Main {
    private static final int[] numberCustomers = {15, 15, 25, 25, 30, 30, 50, 50};
    private static final int[] numberLinks = {43, 76, 57, 86, 72, 78, 110, 143};
    private static final int iterationsNumber = 1000;
    private static final int optimizationCyclesNumber = 30;

	 private static final int bandwidth = 155;
	 private static final int lowerLimit = 3;
	 private static final int upperLimit = 7;

//    private static final int bandwidth = 622;
//    private static final int lowerLimit = 11;
//    private static final int upperLimit = 17;

    public static void main(String[] args) {
        int howManyDifferentParameters = numberCustomers.length;
        for (int i = 0; i < howManyDifferentParameters; i++) {
            BeeAlgorithmInIDP idp = new BeeAlgorithmInIDP(numberCustomers[i], numberLinks[i], bandwidth, lowerLimit, upperLimit, iterationsNumber, optimizationCyclesNumber);
            idp.mainSteps();
        }
    }
}