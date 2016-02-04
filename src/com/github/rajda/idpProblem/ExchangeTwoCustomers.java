package com.github.rajda.idpProblem;

import com.github.rajda.OptimizeStrategy;
import com.github.rajda.Problem;
import com.github.rajda.Solution;

import static com.github.rajda.Helper.random;

/**
 * Replace edges between two partitions
 */
public class ExchangeTwoCustomers implements OptimizeStrategy {
    /**
     * Local search: exchange two customers between two partitions
     *
     * @param solution
     */
    @Override
    public Solution optimize(Problem problem, Solution solution) {
        IdpProblem idpProblem = (IdpProblem) problem;
        Solution newSolution = solution.clone();
        int firstPartition = newSolution.getSolution()[random(0, idpProblem.getIdpProblemInitData().getLinksNumber() - 1)];
        int secondPartition = newSolution.getSolution()[random(0, idpProblem.getIdpProblemInitData().getLinksNumber() - 1, firstPartition)];

        int firstUser = solution.getRandomUser(firstPartition);
        int secondUser = solution.getRandomUser(secondPartition);

        newSolution.getSolution()[firstUser] = secondPartition;
        newSolution.getSolution()[secondUser] = firstPartition;
        newSolution.setFitness(idpProblem.countFitness(newSolution.getSolution()));
        return newSolution;
    }
}
