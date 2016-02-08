package com.github.rajda.idpProblem;

import com.github.rajda.OptimizeStrategy;
import com.github.rajda.Problem;
import com.github.rajda.Solution;

/**
 * Select and assign random edge to partition with lowest traffic
 */
public class ExchangeMinPartition implements OptimizeStrategy {

    @Override
    public Solution optimize(Problem problem, Solution solution) {
        IdpProblem idpProblem = (IdpProblem) problem;
        Solution newSolution = solution.clone();

        int user = solution.getRandomUserFromNotMinPartition();
        int minPartition = solution.getNumberOfMinPart();

        newSolution.setValueAt(user, minPartition);
        newSolution.setFitness(idpProblem.countFitness(newSolution));
        return newSolution;
    }
}