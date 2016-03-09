package com.github.rajda;

import static com.github.rajda.Helper.*;

/**
 * Artificial Bee Colony (ABC) algorithm implementation
 *
 * @author Jacek Rajda
 */
public class BeeColonyAlgorithm {
    private static final int MAXIMUM_CYCLE_COUNT = 500;

    private static final int EMPLOYED_BEES_GROUP_COUNT = 10;
    private static final int SELECTED_BEST_FOOD_SOURCE_COUNT = 10;

    private static final int EMPLOYED_BEES_PER_FOOD_SOURCE_COUNT = 10;
    private static final int ONLOOKER_BEES_PER_FOOD_SOURCE_COUNT = 1;
    private static final int SCOUT_BEES_PER_FOOD_SOURCE_COUNT = 1;

    private final Problem problem;

    public BeeColonyAlgorithm(Problem problem) {
        this.problem = problem;
        prn(problem);
    }

    /**
     * Main steps of algorithm
     */
    public void goThroughSteps() {
        /** Initialize the set of food source positions (-> initial population of solutions (C = 0)) */
        for (int employedBeeSetId = 0; employedBeeSetId < EMPLOYED_BEES_GROUP_COUNT; employedBeeSetId++) {
            /** Initialize the position of food source position (-> single solution) */
            problem.createInitialSolution();
        }

        /** Show the initial set of food source positions (-> initial population of solutions */
        showSolutionsList(problem.getSolutionsList());

        /** Go through the population optimization cycles (C = 1, 2, 3, ..., MCN) */
        for (int populationCycleId = 0; populationCycleId < MAXIMUM_CYCLE_COUNT; populationCycleId++) {

            /** Get actually best food sources (-> the best solutions) */
            for (int foodSourceId = 0; foodSourceId < SELECTED_BEST_FOOD_SOURCE_COUNT; foodSourceId++) {
                Solution currentSolution = problem.getSolutionsList().get(foodSourceId);

                for (int employedBeeId = 0; employedBeeId < EMPLOYED_BEES_PER_FOOD_SOURCE_COUNT; employedBeeId++) {
                    modify(currentSolution);
                }

                for (int onlookerBeeId = 0; onlookerBeeId < ONLOOKER_BEES_PER_FOOD_SOURCE_COUNT; onlookerBeeId++) {
                    localSearchProbability(currentSolution);
                }

                for (int scoutBeeId = 0; scoutBeeId < SCOUT_BEES_PER_FOOD_SOURCE_COUNT; scoutBeeId++) {
                    localSearchDiscovery(currentSolution);
                }
            }
        }

        /** Show the final set of food source positions (-> final population of solutions */
        showSolutionsList(problem.getSolutionsList());
    }

    /**
     * Modify current best food source to find the better one
     */
    private void modify(Solution currentSolution) {
        preferBetterFoodSource(currentSolution, problem.optimize(currentSolution));
    }

    /**
     * Prefer the food source with a better probability related to its nectar amounts
     */
    private void localSearchProbability(Solution currentSolution) {
        preferBetterFoodSource(currentSolution, problem.localSearchProbability(currentSolution));
    }

    /**
     * Send the scout bee to search area to find/localSearchDiscovery a new food source
     */
    private void localSearchDiscovery(Solution currentSolution) {
        preferBetterFoodSource(currentSolution, problem.localSearchDiscover(currentSolution));
    }

    /**
     * Replace the current food source with the new one, if it has higher nectar amount
     */
    private void preferBetterFoodSource(Solution currentFoodSource, Solution newFoodSource) {
        if (newFoodSource.betterThan(currentFoodSource)) {
            /** Memorize the new food source instead the previous one */
            problem.putInPlace(newFoodSource, currentFoodSource);
        }
    }
}