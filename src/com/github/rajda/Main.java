package com.github.rajda;

import com.github.rajda.idpProblem.IdpProblem;
import com.github.rajda.idpProblem.IdpProblemInitData;

class Main {
    private static final int[] numberCustomers = {15, 15, 25, 25, 30, 30, 50, 50};
    private static final int[] numberLinks = {43, 76, 57, 86, 72, 78, 110, 143};

    /* Low-demand case */
    private static final int bandwidth = 155;
    private static final int lowerLimit = 3;
    private static final int upperLimit = 7;

    /* High-demand case */
//    private static final int bandwidth = 622;
//    private static final int lowerLimit = 11;
//    private static final int upperLimit = 17;

    public static void main(String[] args) {
        int howManyDifferentParameters = numberCustomers.length;
        for (int i = 0; i < howManyDifferentParameters; i++) {
            IdpProblemInitData idpProblemInitData = new IdpProblemInitData(numberCustomers[i], numberLinks[i], bandwidth, lowerLimit, upperLimit);
            Problem problem = new IdpProblem(idpProblemInitData);
            BeeColonyAlgorithm bca = new BeeColonyAlgorithm(problem);
            bca.goThroughSteps();
        }
    }
}
