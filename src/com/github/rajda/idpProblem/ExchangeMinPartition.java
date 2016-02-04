package com.github.rajda.idpProblem;

import com.github.rajda.OptimizeStrategy;
import com.github.rajda.Problem;
import com.github.rajda.Solution;

/**
 * Select and assign random edge to partition with lowest traffic
 */
public class ExchangeMinPartition implements OptimizeStrategy {
    /**
     * Local search: assigned a customer to partition with lowest amount
     * @param problem
     * @param solution
     * @return
     */
    @Override
    public Solution optimize(Problem problem, Solution solution) {
        IdpProblem idpProblem = (IdpProblem) problem;
        Solution newSolution = solution.clone();

        int user = solution.getRandomUserFromNotMinPartition();
        int minPartition = solution.getNumberOfMinPart();

        newSolution.getSolution()[user] = minPartition;
        newSolution.setFitness(idpProblem.countFitness(newSolution.getSolution()));
        return newSolution;
    }
}