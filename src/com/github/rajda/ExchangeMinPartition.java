package com.github.rajda;

import java.util.ArrayList;

import static com.github.rajda.BeeAlgorithmInIDP.SCOUT_BEES_NUMBER;

/**
 * Created by Jacek on 31.01.2016.
 */
public class ExchangeMinPartition implements OptimizeStrategy {
    private BeeAlgorithmInIDP beeAlg;

    @Override
    public void optimize(BeeAlgorithmInIDP beeAlg) {
        this.beeAlg = beeAlg;
//        prn((currentCycleId / optimizationCyclesNumber) * 100 + "%");

        for (int iterationId = 0; iterationId < beeAlg.getIterationsNumber(); iterationId++) {
            for (int scoutBeeId = 0; scoutBeeId < SCOUT_BEES_NUMBER; scoutBeeId++) {
                exchangeMinPartition(beeAlg.getSolutionsObjectsList(), beeAlg.getSolutionsObjectsList().get(scoutBeeId));
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
        newSolution.setFitness(beeAlg.countFitness(newSolution.getSolution()));

        if (newSolution.getFitnessValue() <= solution.getFitnessValue()) {
            solutionsObjectsList.remove(solution);
            solutionsObjectsList.add(newSolution);
        }
        return newSolution;
    }
}
