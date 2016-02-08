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
        Solution newSolution = solution.clone();
        int firstPartition = newSolution.getValueAt(random(0, idpProblem.getInitData().getLinksNumber() - 1));
        int secondPartition = newSolution.getValueAt(random(0, idpProblem.getInitData().getLinksNumber() - 1, firstPartition));

        int firstUser = solution.getRandomUser(firstPartition);
        int secondUser = solution.getRandomUser(secondPartition);

        newSolution.setValueAt(firstUser, secondPartition);
        newSolution.setValueAt(secondUser, firstPartition);
        newSolution.setFitness(idpProblem.countFitness(newSolution));
        return newSolution;
    }
}
