package com.github.rajda;

import java.util.ArrayList;

import static com.github.rajda.BeeColonyAlgorithm.SCOUT_BEES_NUMBER;

/**
 * Created by Jacek on 31.01.2016.
 */
public class ExchangeMinPartition implements OptimizeStrategy {
    private BeeColonyAlgorithm beeColonyAlgorithm;
    private ProblemData problemData;

    @Override
    public void optimize(BeeColonyAlgorithm beeColonyAlgorithm, ProblemData problemData) {
        this.beeColonyAlgorithm = beeColonyAlgorithm;
        this.problemData = problemData;
//        prn((currentCycleId / optimizationCyclesNumber) * 100 + "%");

        for (int iterationId = 0; iterationId < problemData.getProblemInitData().getIterationsNumber(); iterationId++) {
            for (int scoutBeeId = 0; scoutBeeId < SCOUT_BEES_NUMBER; scoutBeeId++) {
                exchangeMinPartition(problemData.getSolutionsObjectsList(), problemData.getSolutionsObjectsList().get(scoutBeeId));
            }
        }
    }

    /**
     * Local search: assigned a customer to partition with lowest amount
     *
     * @param solutionsObjectsList
     * @param solution
     */
    private Solution exchangeMinPartition(ArrayList<Solution> solutionsObjectsList, Solution solution) {
        Solution newSolution = solution.clone();
        int minPartition = solution.getNumberOfMinPart();

        int user = solution.getRandomUserFromNotMinPartition();

        newSolution.getSolution()[user] = minPartition;
        newSolution.setFitness(beeColonyAlgorithm.countFitness(newSolution.getSolution()));

        if (newSolution.getFitnessValue() <= solution.getFitnessValue()) {
            solutionsObjectsList.remove(solution);
            solutionsObjectsList.add(newSolution);
        }
        return newSolution;
    }
}
