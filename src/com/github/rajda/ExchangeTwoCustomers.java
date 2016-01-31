package com.github.rajda;

import java.util.Map;

import static com.github.rajda.Helper.random;
import static com.github.rajda.BeeAlgorithmInIDP.*;

/**
 * Created by Jacek on 31.01.2016.
 */
public class ExchangeTwoCustomers implements OptimizeStrategy {
    private BeeAlgorithmInIDP beeAlg;

    @Override
    public void optimize(BeeAlgorithmInIDP beeAlg) {
        this.beeAlg = beeAlg;
        int howMany;
        Solution currentSolution;
        Solution testSolution;
        int firstPartition;
        int secondPartition;
        for (howMany = 0; howMany < beeAlg.iterationsNumber; howMany++) {
            for (int bestSiteId = 0; bestSiteId < SELECTED_BEST_SITES_NUMBER; bestSiteId++) {
                currentSolution = beeAlg.solutionsObjectsList.get(bestSiteId);

                for (int i = 0; i < BEES_NUMBER_FOR_EACH_SITE; i++) {
                    testSolution = exchangeTwoCustomers(currentSolution);
                    if ((testSolution.getNumberOfADM() < currentSolution.getNumberOfADM() || testSolution.getNumberOfADM() == testSolution
                            .getFitnessValue()) && testSolution.getFitnessValue() <= currentSolution.getFitnessValue()) {

                        if (beeAlg.solutionsObjectsList.remove(currentSolution))
                            beeAlg.solutionsObjectsList.add(testSolution);
                    } else {
                        if (i == BEES_NUMBER_FOR_EACH_SITE - 1) { // and no modifications with improvement
                            int t = 0;
                            firstPartition = random(0, beeAlg.partitionsNumber - 1);
                            secondPartition = random(0, beeAlg.partitionsNumber - 1, firstPartition);
                            int numberOfNewPartition;
                            testSolution = currentSolution.clone();
                            do {
                                numberOfNewPartition = getNumberOfNewPartitionForEdge(getCustomers(t), testSolution, firstPartition, secondPartition);

                                if (numberOfNewPartition != -1) {
                                    // one of two customers of the edge is in the partition
                                    testSolution.getSolution()[t] = numberOfNewPartition;
                                    testSolution.setFitness(beeAlg.countFitness(testSolution.getSolution()));
                                }
                            } while (++t < beeAlg.linksNumber
                                    && beeAlg.getCapacityVolumeForEachPart(testSolution.getSolution()).get(firstPartition) < beeAlg.bandwidth
                                    && beeAlg.getCapacityVolumeForEachPart(testSolution.getSolution()).get(secondPartition) < beeAlg.bandwidth);
                        }
                    }
                    if (testSolution.getFitnessValue() < currentSolution.getFitnessValue()) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Local search: exchange two customers between two partitions
     *
     * @param solution
     */
    private Solution exchangeTwoCustomers(Solution solution) {
        Solution newSolution = solution.clone();
        int firstPartition = newSolution.getSolution()[random(0, beeAlg.linksNumber - 1)];
        int secondPartition;

        do {
            secondPartition = newSolution.getSolution()[random(0, beeAlg.linksNumber - 1)];
        } while (secondPartition == firstPartition);

        int firstUser = solution.getRandomUser(firstPartition);
        int secondUser = solution.getRandomUser(secondPartition);

        newSolution.getSolution()[firstUser] = secondPartition;
        newSolution.getSolution()[secondUser] = firstPartition;
        newSolution.setFitness(beeAlg.countFitness(newSolution.getSolution()));
        return newSolution;
    }

    private int getNumberOfNewPartitionForEdge(Integer[] customers, Solution solution, int firstPartition, int secondPartition) {
        int numberOfFirstCustomer = customers[0];
        int numberOfSecondCustomer = customers[1];
        int numberOfTempEdge;

        for (int i = 0; i < beeAlg.customersNumber; i++) {
            if (i > numberOfFirstCustomer) {
                numberOfTempEdge = getNumberOfEdge(new Integer[]{numberOfFirstCustomer, i});
                int assignedPartition = assignPartitionToEdge(solution, firstPartition, secondPartition, numberOfTempEdge);
                if (assignedPartition != -1) return assignedPartition;
            }
        }

        for (int i = 0; i < beeAlg.customersNumber; i++) {
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
        for (Map.Entry<Integer, Integer[]> entry : beeAlg.customersAssignedToLink.entrySet()) {
            if (edge.equals(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private int getNumberOfEdge(Integer[] customers) {
        for (Map.Entry<Integer, Integer[]> entry : beeAlg.customersAssignedToLink.entrySet()) {
            if (customers[0].equals(entry.getValue()[0]) && customers[1].equals(entry.getValue()[1])) {
                return entry.getKey();
            }
        }
        return -1;
    }

    private int assignPartitionToEdge(Solution solution, int firstPartition, int secondPartition, int numberOfTempEdge) {
        if (numberOfTempEdge != -1) {
            // if current edge has a positive value
            if (solution.getSolution()[numberOfTempEdge] == firstPartition) {
                return firstPartition;
            } else if (solution.getSolution()[numberOfTempEdge] == secondPartition) {
                return secondPartition;
            }
        }
        return -1;
    }
}
