package com.github.rajda;

import com.github.rajda.idpProblem.IdpProblem;
import com.github.rajda.idpProblem.IdpProblemInitData;

import static com.github.rajda.Helper.showCurrentSolutionsList;

/**
 * Artificial Bee Colony (ABC) algorithm implementation
 *
 * @author Jacek Rajda
 */
public class BeeColonyAlgorithm {
    public static final int INITIAL_RANDOM_SOLUTIONS = 0, BLANK_ENTRY = 1, FINISH_SOLUTION = 2;
    public static final int MAXIMUM_CYCLE_NUMBER = 300;

    public static final int EMPLOYED_BEES_GROUPS_NUMBER = 10;
    public static final int SELECTED_BEST_FOOD_SOURCES_NUMBER = 10;

    public static final int EMPLOYED_BEES_NUMBER_PER_FOOD_SOURCE = 10;
    public static final int ONLOOKER_BEES_NUMBER_PER_FOOD_SOURCE = 1;
    public static final int SCOUT_BEES_NUMBER_PER_FOOD_SOURCE = 1;

    private IdpProblem problem;

    public BeeColonyAlgorithm(Problem problem) {
        this.problem = (IdpProblem) problem;
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
        for (int problemOptCycleId = 0; problemOptCycleId < MAXIMUM_CYCLE_NUMBER; problemOptCycleId++) {

            /** Get actually best foot sources (the best solutions) */
            for (int bestSiteId = 0; bestSiteId < SELECTED_BEST_FOOD_SOURCES_NUMBER; bestSiteId++) {
                Solution currentSolution = problem.getSolutionsList().get(bestSiteId);

                /** Modify current best food sources to find the better ones */
                for (int i = 0; i < EMPLOYED_BEES_NUMBER_PER_FOOD_SOURCE; i++) {
                    Solution solutionAfterOptimization = problem.optimize(currentSolution);

                    /** If the nectar amount in new food source is higher than in previous one */
                    if (solutionAfterOptimization.betterThan(currentSolution)) {
                        /** Memorize the new food source instead the previous one */
                        problem.putInPlace(solutionAfterOptimization, currentSolution);
                        break;
                    }
                }

                /** Prefer the food sources with a better probability related to their nectar amounts */
                for (int oBeeId = 0; oBeeId < ONLOOKER_BEES_NUMBER_PER_FOOD_SOURCE; oBeeId++) {
                    Solution solutionAfterOptimization = problem.doSomething(currentSolution);
                    if (solutionAfterOptimization.betterThan(currentSolution)) {
                        problem.putInPlace(solutionAfterOptimization, currentSolution);
                        break;
                    }
                }

                /** Send the scout bee to search area to find/discover a new food source */
                for (int oBeeId = 0; oBeeId < SCOUT_BEES_NUMBER_PER_FOOD_SOURCE; oBeeId++) {
                    Solution solutionAfterOptimization = problem.minPartitionOptimize(currentSolution);
                    if (solutionAfterOptimization.betterThan(currentSolution)) {
                        problem.putInPlace(solutionAfterOptimization, currentSolution);
                    }
                }
            }
        }

        showCurrentSolutionsList(FINISH_SOLUTION, problem.getSolutionsList());
    }
}