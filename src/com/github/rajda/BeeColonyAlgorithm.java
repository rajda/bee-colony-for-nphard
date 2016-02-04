package com.github.rajda;

import com.github.rajda.idpProblem.IdpProblem;
import com.github.rajda.idpProblem.IdpProblemInitData;

import static com.github.rajda.Helper.showCurrentSolutionsList;

/**
 * Bee colony algorithm implementation
 *
 * @author Jacek Rajda
 */
public class BeeColonyAlgorithm {
    public static final int INITIAL_RANDOM_SOLUTIONS = 0, BLANK_ENTRY = 1, FINISH_SOLUTION = 2;
    public static final int SCOUT_BEES_NUMBER = 10;
    public static final int SELECTED_BEST_SITES_NUMBER = 10;
    public static final int BEES_NUMBER_FOR_EACH_SITE = 10;

    private IdpProblem problem;
    private final IdpProblemInitData problemInitData;

    public BeeColonyAlgorithm(Problem problem) {
        this.problem = (IdpProblem) problem;
        this.problemInitData = (IdpProblemInitData) problem.getProblemInitData();
    }

    public void goThroughSteps() {
        /** Initial food sources for all employed bees */

        /** Initial arrangement of scout bees */
        for (int scoutBeeId = 0; scoutBeeId < SCOUT_BEES_NUMBER; scoutBeeId++) {
            problem.createInitialSolutions();
        }

        /** Show list of initial solutions */
        showCurrentSolutionsList(INITIAL_RANDOM_SOLUTIONS, problem.getSolutionsList());

        /** Go through optimization process */
        for (int problemOptCycleId = 0; problemOptCycleId < problemInitData.getOptimizationCyclesNumber(); problemOptCycleId++) {
            for (int problemIterationId = 0; problemIterationId < problemInitData.getIterationsNumber(); problemIterationId++) {

                /** Get actually best sites */
                for (int bestSiteId = 0; bestSiteId < SELECTED_BEST_SITES_NUMBER; bestSiteId++) {
                    Solution currentSolution = problem.getSolutionsList().get(bestSiteId);

                    /** Modify current best site to find the better one */
                    for (int i = 0; i < BEES_NUMBER_FOR_EACH_SITE; i++) {
                        //TODO: exchange two customers
                        Solution solutionAfterOptimization = problem.optimize(currentSolution);

                        if (solutionAfterOptimization.betterThan(currentSolution)) {
                            problem.putInPlace(solutionAfterOptimization, currentSolution);
                            break;
                        } else if (isLastBee(i)) { // and no modifications with improvement
                            //TODO: exchange - improve solution in some way
                            solutionAfterOptimization = problem.doSomething(currentSolution);
                            if (solutionAfterOptimization.betterThan(currentSolution)) {
                                problem.putInPlace(solutionAfterOptimization, currentSolution);
                                break;
                            }
                        }
                    }
                }
                for (int scoutBeeId = 0; scoutBeeId < SCOUT_BEES_NUMBER; scoutBeeId++) {
                    Solution currentSolution = problem.getSolutionsList().get(scoutBeeId);

                    //TODO: exchange min partitions
                    Solution solutionAfterOptimization = problem.minPartitionOptimize(currentSolution);
                    if (solutionAfterOptimization.betterThan(currentSolution)) {
                        problem.putInPlace(solutionAfterOptimization, currentSolution);
                    }
                }
            }
        }

        showCurrentSolutionsList(FINISH_SOLUTION, problem.getSolutionsList());
    }

    private boolean isLastBee(int beeId) {
        return beeId == BEES_NUMBER_FOR_EACH_SITE - 1;
    }
}