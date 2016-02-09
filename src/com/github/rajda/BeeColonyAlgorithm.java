package com.github.rajda;

import static com.github.rajda.Helper.showCurrentSolutionsList;

/**
 * Artificial Bee Colony (ABC) algorithm implementation
 *
 * @author Jacek Rajda
 */
public class BeeColonyAlgorithm {
    public static final int INITIAL_RANDOM_SOLUTIONS = 0, BLANK_ENTRY = 1, FINISH_SOLUTION = 2;
    private static final int MAXIMUM_CYCLE_NUMBER = 300;

    private static final int EMPLOYED_BEES_GROUPS_NUMBER = 10;
    private static final int SELECTED_BEST_FOOD_SOURCES_NUMBER = 10;

    private static final int EMPLOYED_BEES_NUMBER_PER_FOOD_SOURCE = 10;
    private static final int ONLOOKER_BEES_NUMBER_PER_FOOD_SOURCE = 1;
    private static final int SCOUT_BEES_NUMBER_PER_FOOD_SOURCE = 1;

    private final Problem problem;

    public BeeColonyAlgorithm(Problem problem) {
        this.problem = problem;
    }

    public void goThroughSteps() {
        /** Initialize the set of foot source positions (-> initial population of solutions (C = 0)) */
        for (int eBeeSetId = 0; eBeeSetId < EMPLOYED_BEES_GROUPS_NUMBER; eBeeSetId++) {
            /** Initialize the position of foot source position (-> single solution) */
            problem.createInitialSolution();
        }

        /** Show the initial set of foot source positions (-> initial population of solutions */
        showCurrentSolutionsList(INITIAL_RANDOM_SOLUTIONS, problem.getSolutionsList());

        /** Go through the population optimization cycles (C = 1, 2, 3, ..., MCN) */
        for (int populationCycleId = 0; populationCycleId < MAXIMUM_CYCLE_NUMBER; populationCycleId++) {

            /** Get actually best foot sources (-> the best solutions) */
            for (int foodSourceId = 0; foodSourceId < SELECTED_BEST_FOOD_SOURCES_NUMBER; foodSourceId++) {
                Solution currentSolution = problem.getSolutionsList().get(foodSourceId);

                /** Modify current best food sources to find the better ones */
                for (int i = 0; i < EMPLOYED_BEES_NUMBER_PER_FOOD_SOURCE; i++) {
                    Solution solutionAfterOptimization = problem.optimize(currentSolution);
                    currentSolution = preferBetterFoodSource(solutionAfterOptimization, currentSolution);
                }

                /** Prefer the food sources with a better probability related to their nectar amounts */
                for (int oBeeId = 0; oBeeId < ONLOOKER_BEES_NUMBER_PER_FOOD_SOURCE; oBeeId++) {
                    Solution solutionAfterOptimization = problem.localSearchProbability(currentSolution);
                    currentSolution = preferBetterFoodSource(solutionAfterOptimization, currentSolution);
                }

                /** Send the scout bee to search area to find/discover a new food source */
                for (int sBeeId = 0; sBeeId < SCOUT_BEES_NUMBER_PER_FOOD_SOURCE; sBeeId++) {
                    Solution solutionAfterOptimization = problem.localSearchDiscover(currentSolution);
                    currentSolution = preferBetterFoodSource(solutionAfterOptimization, currentSolution);
                }
            }
        }

        showCurrentSolutionsList(FINISH_SOLUTION, problem.getSolutionsList());
    }

    private Solution preferBetterFoodSource(Solution newFoodSource, Solution currentFoodSource) {
        /** If the nectar amount in the new food source is higher than in the current one */
        if (newFoodSource.betterThan(currentFoodSource)) {
            /** Memorize the new food source instead the previous one */
            problem.putInPlace(newFoodSource, currentFoodSource);
            return newFoodSource;
        } else {
            return currentFoodSource;
        }
    }
}