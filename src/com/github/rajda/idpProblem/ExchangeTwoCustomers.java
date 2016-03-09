package com.github.rajda.idpProblem;

import com.github.rajda.OptimizeStrategy;
import com.github.rajda.Problem;
import com.github.rajda.Solution;

import static com.github.rajda.Helper.random;

/**
 * Replace edges between two partitions
 */
public class ExchangeTwoCustomers implements OptimizeStrategy {

    @Override
    public Solution optimize(Problem problem, Solution solution) {
        IdpProblem idpProblem = (IdpProblem) problem;
        IdpSolution newSolution = (IdpSolution) solution.clone();
        int firstPartition = newSolution.getValueAt(random(0, idpProblem.getInitData().getLinkCount() - 1));
        int secondPartition = newSolution.getValueAt(random(0, idpProblem.getInitData().getLinkCount() - 1, firstPartition));

        int firstUserId = newSolution.getRandomUser(firstPartition);
        int secondUserId = newSolution.getRandomUser(secondPartition);

        newSolution.setValueAt(firstUserId, secondPartition);
        newSolution.setValueAt(secondUserId, firstPartition);
        newSolution.setFitness(idpProblem.countFitness(newSolution));
        return newSolution;
    }
}
