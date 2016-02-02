package com.github.rajda;

import static com.github.rajda.Helper.*;

/**
 * Bee colony algorithm solving large-scale SONET network
 *
 * @author Jacek Rajda
 */
public class BeeColonyAlgorithm {
    public static final int INITIAL_RANDOM_SOLUTIONS = 0, BLANK_ENTRY = 1, FINISH_SOLUTION = 2;
    public static final int SCOUT_BEES_NUMBER = 10;
    public static final int SELECTED_BEST_SITES_NUMBER = 10;
    public static final int BEES_NUMBER_FOR_EACH_SITE = 10;
    public static final int PENALTY_FACTOR_FOR_BAD_SOLUTIONS = 100;

    private Problem problem;
    private final ProblemInitData problemInitData;

    public BeeColonyAlgorithm(Problem problem) {
        this.problem = problem;
        this.problemInitData = problem.getProblemInitData();
    }

    public void goThroughSteps() {
        /** Initial arrangement of scout bees */
        for (int scoutBeeId = 0; scoutBeeId < SCOUT_BEES_NUMBER; scoutBeeId++) {
            problem.createInitialSolutions();
        }

        /** Show list of initial solutions */
        showCurrentSolutionsList(INITIAL_RANDOM_SOLUTIONS, problem.getSolutionsList());
//        long startTime = System.nanoTime();

        /** Alternating changes between types of optimization */
        for (int currentCycleId = 0; currentCycleId < problemInitData.getOptimizationCyclesNumber(); currentCycleId++) {
            problem.optimize();
        }

        showCurrentSolutionsList(FINISH_SOLUTION, problem.getSolutionsList());
//
//        long stopTime = System.nanoTime();
//        prn("OPTIMIZE TIME: " + (stopTime - startTime) / 1000000 + " ms");
//        prn();
//        prn("-----------------------------------------------------------------------------------------");
    }
}