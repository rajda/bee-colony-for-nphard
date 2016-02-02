package com.github.rajda.idpProblem;

import com.github.rajda.idpProblem.IdpProblem;
import com.github.rajda.OptimizeStrategy;
import com.github.rajda.Problem;
import com.github.rajda.Solution;

import java.util.ArrayList;

import static com.github.rajda.BeeColonyAlgorithm.SCOUT_BEES_NUMBER;

/**
 * Select and assign random edge to partition with lowest traffic
 */
public class ExchangeMinPartition implements OptimizeStrategy {
    private IdpProblem idpProblem;

    @Override
    public void optimize(Problem idpProblem) {
        this.idpProblem = (IdpProblem) idpProblem;
        this.idpProblem = (IdpProblem) idpProblem;
//        prn((currentCycleId / optimizationCyclesNumber) * 100 + "%");

        for (int iterationId = 0; iterationId < idpProblem.getProblemInitData().getIterationsNumber(); iterationId++) {
            for (int scoutBeeId = 0; scoutBeeId < SCOUT_BEES_NUMBER; scoutBeeId++) {
                exchangeMinPartition(idpProblem.getSolutionsList(), idpProblem.getSolutionsList().get(scoutBeeId));
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
        int user = solution.getRandomUserFromNotMinPartition();

        int minPartition = solution.getNumberOfMinPart();
        newSolution.getSolution()[user] = minPartition;
        newSolution.setFitness(idpProblem.countFitness(newSolution.getSolution()));

        if (newSolution.getFitnessValue() <= solution.getFitnessValue()) {
            solutionsObjectsList.remove(solution);
            solutionsObjectsList.add(newSolution);
        }
        return newSolution;
    }
}
