package com.github.rajda.idpProblem;

import com.github.rajda.OptimizeStrategy;
import com.github.rajda.Problem;
import com.github.rajda.Solution;

import java.util.Map;

import static com.github.rajda.Helper.random;

/**
 * Select and assign edge to partition according to probability
 */
public class ExchangeAccordingProb implements OptimizeStrategy {
    private IdpProblem idpProblem;

    @Override
    public IdpSolution optimize(Problem problem, Solution solution) {
        idpProblem = (IdpProblem) problem;
        IdpSolution newSolution = (IdpSolution) solution.clone();

        int t = 0;
        int firstPartition = random(0, idpProblem.getPartitionsNumber() - 1);
        int secondPartition = random(0, idpProblem.getPartitionsNumber() - 1, firstPartition);
        int numberOfNewPartition;
        do {
            numberOfNewPartition = getNumberOfNewPartitionForEdge(getCustomers(t), newSolution, firstPartition, secondPartition);

            if (numberOfNewPartition != -1) {
                // one of two customers of the edge is in the partition
                newSolution.setValueAt(t, numberOfNewPartition);
                newSolution.setFitness(idpProblem.countFitness(newSolution));
            }
        } while (++t < idpProblem.getInitData().getLinksNumber()
                && idpProblem.getCapacityVolumeForEachPart(newSolution).get(firstPartition) < idpProblem.getInitData().getBandwidth()
                && idpProblem.getCapacityVolumeForEachPart(newSolution).get(secondPartition) < idpProblem.getInitData().getBandwidth());

        return newSolution;
    }

    private int getNumberOfNewPartitionForEdge(Integer[] customers, IdpSolution solution, int firstPartition, int secondPartition) {
        int numberOfFirstCustomer = customers[0];
        int numberOfSecondCustomer = customers[1];
        int numberOfTempEdge;

        for (int i = 0; i < idpProblem.getInitData().getCustomersNumber(); i++) {
            if (i > numberOfFirstCustomer) {
                numberOfTempEdge = getNumberOfEdge(new Integer[]{numberOfFirstCustomer, i});
                int assignedPartition = assignPartitionToEdge(solution, firstPartition, secondPartition, numberOfTempEdge);
                if (assignedPartition != -1) return assignedPartition;
            }
        }

        for (int i = 0; i < idpProblem.getInitData().getCustomersNumber(); i++) {
            if (i < numberOfSecondCustomer) {
                numberOfTempEdge = getNumberOfEdge(new Integer[]{i, numberOfSecondCustomer});
                int assignedPartition = assignPartitionToEdge(solution, firstPartition, secondPartition, numberOfTempEdge);
                if (assignedPartition != -1) return assignedPartition;
            }
        }
        return -1;
    }

    /**
     * Appoint partition index for an edge between the two users
     *
     * @param edge
     * @return
     */
    private Integer[] getCustomers(Integer edge) {
        for (Map.Entry<Integer, Integer[]> entry : idpProblem.getCustomersAssignedToLink().entrySet()) {
            if (edge.equals(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private int getNumberOfEdge(Integer[] customers) {
        for (Map.Entry<Integer, Integer[]> entry : idpProblem.getCustomersAssignedToLink().entrySet()) {
            if (customers[0].equals(entry.getValue()[0]) && customers[1].equals(entry.getValue()[1])) {
                return entry.getKey();
            }
        }
        return -1;
    }

    private int assignPartitionToEdge(IdpSolution solution, int firstPartition, int secondPartition, int numberOfTempEdge) {
        if (numberOfTempEdge != -1) {
            // if current edge has a positive value
            if (solution.getValueAt(numberOfTempEdge) == firstPartition) {
                return firstPartition;
            } else if (solution.getValueAt(numberOfTempEdge) == secondPartition) {
                return secondPartition;
            }
        }
        return -1;
    }
}
