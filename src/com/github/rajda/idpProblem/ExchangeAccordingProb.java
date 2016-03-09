package com.github.rajda.idpProblem;

import com.github.rajda.OptimizeStrategy;
import com.github.rajda.Problem;
import com.github.rajda.Solution;

import java.util.ArrayList;
import java.util.Arrays;

import static com.github.rajda.Helper.random;

/**
 * Select and assign edge to partition according to probability
 */
public class ExchangeAccordingProb implements OptimizeStrategy {
    private IdpProblem idpProblem;
    private IdpSolution newSolution;

    @Override
    public IdpSolution optimize(Problem problem, Solution solution) {
        idpProblem = (IdpProblem) problem;
        newSolution = (IdpSolution) solution.clone();

        int linkId = 0;
        int firstPartition = random(0, idpProblem.getPartitionCount() - 1);
        int secondPartition = random(0, idpProblem.getPartitionCount() - 1, firstPartition);
        int newPartitionId;
        do {
            newPartitionId = getNewPartitionIdForLink(getCustomers(linkId), firstPartition, secondPartition);

            if (newPartitionId != -1) {
                // one of two customers of the edge is in the partition
                newSolution.setValueAt(linkId, newPartitionId);
                newSolution.setFitness(idpProblem.countFitness(newSolution));
            }
        } while (++linkId < idpProblem.getInitData().getLinkCount()
                && idpProblem.getCapacityVolumeForEachPart(newSolution).get(firstPartition) < idpProblem.getInitData().getBandwidth()
                && idpProblem.getCapacityVolumeForEachPart(newSolution).get(secondPartition) < idpProblem.getInitData().getBandwidth());

        return newSolution;
    }

    private int getNewPartitionIdForLink(Integer[] customers, int firstPartition, int secondPartition) {
        int firstCustomerId = customers[0];
        int secondCustomerId = customers[1];
        int assignedPartition = getNewPartitionIdForLinkForFirstCustomer(firstPartition, secondPartition, firstCustomerId);
        if (assignedPartition == -1) {
            assignedPartition = getNewPartitionIdForLinkForSecondCustomer(firstPartition, secondPartition, secondCustomerId);
        }
        return assignedPartition;
    }

    private int getNewPartitionIdForLinkForFirstCustomer(int firstPartition, int secondPartition, int firstCustomerId) {
        int tempLinkId;
        int assignedPartition = -1;
        for (int i = firstCustomerId + 1; i < idpProblem.getInitData().getCustomerCount(); i++) {
            tempLinkId = getLinkId(new Integer[]{firstCustomerId, i});
            assignedPartition = assignPartitionToLink(firstPartition, secondPartition, tempLinkId);
            if (assignedPartition != -1) break;
        }
        return assignedPartition;
    }

    private int getNewPartitionIdForLinkForSecondCustomer(int firstPartition, int secondPartition, int secondCustomerId) {
        int tempLinkId;
        int assignedPartition = -1;
        for (int i = 0; i < idpProblem.getInitData().getCustomerCount() && i < secondCustomerId; i++) {
            tempLinkId = getLinkId(new Integer[]{i, secondCustomerId});
            assignedPartition = assignPartitionToLink(firstPartition, secondPartition, tempLinkId);
            if (assignedPartition != -1) break;
        }
        return assignedPartition;
    }

    /**
     * Get customers connected by link
     */
    private Integer[] getCustomers(Integer link) {
        return idpProblem.getCustomersAssignedToLink().get(link);
    }

    /**
     * Get link id between two customers
     */
    private int getLinkId(Integer[] customers) {
        Integer customerMinId = Arrays.stream(customers).mapToInt(i -> i).min().getAsInt();
        Integer customerMaxId = Arrays.stream(customers).mapToInt(i -> i).max().getAsInt();
        int linkId = -1;

        ArrayList<Integer[]> customersAssignedToLink = idpProblem.getCustomersAssignedToLink();
        for (int i = 0; i < customersAssignedToLink.size(); i++) {
            if (customerMinId.equals(customersAssignedToLink.get(i)[0]) && customerMaxId.equals(customersAssignedToLink.get(i)[1])) {
                linkId = i;
                break;
            }
        }

        return linkId;
    }

    private int assignPartitionToLink(int firstPartition, int secondPartition, int linkId) {
        if (linkId != -1) {
            // if current edge has a positive value
            if (newSolution.getValueAt(linkId) == firstPartition) {
                return firstPartition;
            } else if (newSolution.getValueAt(linkId) == secondPartition) {
                return secondPartition;
            }
        }
        return -1;
    }
}
