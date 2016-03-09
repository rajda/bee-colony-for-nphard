package com.github.rajda.idpProblem;

import com.github.rajda.OptimizeStrategy;
import com.github.rajda.Problem;
import com.github.rajda.Solution;

/**
 * Select and assign random edge to partition with lowest traffic
 */
public class ExchangeMinPartition implements OptimizeStrategy {

    @Override
    public IdpSolution optimize(Problem problem, Solution solution) {
        IdpProblem idpProblem = (IdpProblem) problem;
        IdpSolution newSolution = (IdpSolution) solution.clone();

        int user = newSolution.getRandomUserFromNotMinPartition();
        int minPartition = newSolution.getMinPartitionId();

        newSolution.setValueAt(user, minPartition);
        newSolution.setFitness(idpProblem.countFitness(newSolution));
        return newSolution;
    }
}