package com.github.rajda;

import java.util.*;
import java.util.Map.Entry;

import static com.github.rajda.Helper.*;

/**
 * Bee colony algorithm solving large-scale SONET network
 *
 * @author Jacek Rajda
 */
public class BeeAlgorithmInIDP {
    public static final int INITIAL_RANDOM_SOLUTIONS = 0,
            EXCHANGE_MIN_PARTITION = 1,
            EXCHANGE_TWO_CUSTOMERS = 2,
            BLANK_ENTRY = 3,
            FINISH_SOLUTION = 4;

    /**
     * Number of scout bees
     */
    private static final int SCOUT_BEES_NUMBER = 10;

    /**
     * Number of best sites out of n selected sites
     */
    private static final int SELECTED_BEST_SITES_NUMBER = 10;

    /**
     * Number of bees recruited for each site
     */
    private static final int BEES_NUMBER_FOR_EACH_SITE = 10;

    public static int customersNumber, linksNumber;
    private int partitionsNumber;

    /**
     * Number of all edges
     */
    private int totalEdgesNumber;

    /**
     * Number of iterations for each optimization method
     */
    private int iterationsNumber;

    /**
     * Number of optimization cycles
     */
    private int optimizationCyclesNumber;

    /**
     * Number of current cycle
     */
    private static int currentCycleId;

    /**
     * Bandwith of each partition
     */
    private int bandwith;
    private int lowerLimit, upperLimit;
    private Double[][] demands;
    private ArrayList<Double> demandsAsList;
    private Integer[] positionsOfNonzeroDemands;
    private LinkedHashMap<Integer, Integer[]> customersAssignedToLink;
    private int penaltyFactorForBadSolutions = 100;
    private ArrayList<Solution> solutionsObjectsList;

    public BeeAlgorithmInIDP(int customersNumber, int linksNumber, int bandwith, int lowerLimit, int upperLimit, int iterationsNumber, int optimizationCyclesNumber) {
        this.customersNumber = customersNumber;
        this.linksNumber = linksNumber;
        this.demands = new Double[customersNumber][customersNumber];
        this.demandsAsList = new ArrayList<>();
        this.positionsOfNonzeroDemands = new Integer[linksNumber];
        this.customersAssignedToLink = new LinkedHashMap<>();

        this.iterationsNumber = iterationsNumber;
        this.optimizationCyclesNumber = optimizationCyclesNumber;
        this.bandwith = bandwith;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        init();
    }

    private void init() {
        initialDemandMatrix();
        initialNumberOfRings();
        initialNumberOfEdges(customersNumber);
    }

    /**
     * Set the number of all possible edges between SCOUT_BEES_NUMBER-nodes in network
     *
     * @param numberOfNodes
     * @return
     */
    private void initialNumberOfEdges(int numberOfNodes) {
        totalEdgesNumber = numberOfNodes * (numberOfNodes - 1) / 2;
    }

    /**
     * Set the initial number of rings
     */
    private void initialNumberOfRings() {
        double dSum = 0;

        /**
         * Sum up values in matrix of demands
         */
        for (int i = 0; i < customersNumber; i++) {
            for (int j = 0; j < customersNumber; j++) {
                if (demands[i][j] != null)
                    dSum += demands[i][j];
            }
        }

        /**
         * Initial number of partitions
         */
        int klb = getCeilFromDouble((dSum / 2) / bandwith);
        // partitionsNumber = klb + 1;
        // partitionsNumber = klb == 1 ? 2 : klb;
        partitionsNumber = klb;

        prn();
        prn("SUM OF DEMANDS BETWEEN CUSTOMERS: " + dSum / 2);
        prn();
        prn("INITIAL NUMBER OF PARTITIONS: " + partitionsNumber);
    }

    public void mainSteps() {
        int l;

        /**
         * Initial Edges, each edge assigned to the -1st partition
         */
        int edge[] = new int[linksNumber];
        Arrays.fill(edge, -1);

        /**
         * Initial numberOfEdgesAssigned to each partition
         */
        int numberOfEdgesAssigned[] = new int[partitionsNumber];

        int[] assignments = new int[linksNumber];
        solutionsObjectsList = new ArrayList<>();

        l = 0;
        while (l != linksNumber) {
            int randomEdge = random(0, linksNumber - 1);
            if (edge[randomEdge] == -1) {
                edge[randomEdge] = l;
                l += 1;
            }
        }

        /**
         * Initial arrangement of scout bees
         */
        for (int h = 0; h < SCOUT_BEES_NUMBER; h++) {
            Arrays.fill(numberOfEdgesAssigned, 0);

            for (l = 0; l < linksNumber; l++) {
                int randomPartition = random(0, partitionsNumber - 1);
                while (numberOfEdgesAssigned[randomPartition] > totalEdgesNumber / partitionsNumber) {
                    randomPartition = random(0, partitionsNumber - 1);
                    prn(randomPartition);
                }

                assignments[edge[l]] = randomPartition;
                numberOfEdgesAssigned[randomPartition] = numberOfEdgesAssigned[randomPartition] + 1;
            }
            solutionsObjectsList.add(new Solution(assignments.clone(), countFitness(assignments)));
        }

        /**
         * Show list of initial solutions
         */
        showCurrentSolutionsList(INITIAL_RANDOM_SOLUTIONS, solutionsObjectsList);
        long startTime = System.nanoTime();

        /**
         * Alternating changes between types of optimization
         */
        for (currentCycleId = 0; currentCycleId < optimizationCyclesNumber; currentCycleId++) {
            optimize(EXCHANGE_MIN_PARTITION);
            optimize(EXCHANGE_TWO_CUSTOMERS);
        }

        showCurrentSolutionsList(FINISH_SOLUTION, solutionsObjectsList);

        long stopTime = System.nanoTime();
        prn("OPTIMIZE TIME: " + (stopTime - startTime) / 1000000 + " ms");
        prn();
        prn("-----------------------------------------------------------------------------------------");
    }

    /**
     * Select type of optimization
     *
     * @param type EXCHANGE_MIN_PARTITION - select and assign random edge to
     *             partition with lowest traffic<br>
     *             EXCHANGE_TWO_CUSTOMERS - replace edges between two partitions
     *             partitions
     */
    private void optimize(int type) {
        int howMany;
        int t;
        int firstPartition;
        int secondPartition;
        Solution currentSolution;
        Solution testSolution;

        switch (type) {
            case EXCHANGE_MIN_PARTITION:
                prn(currentCycleId * 100 / optimizationCyclesNumber + "%");

                for (howMany = 0; howMany < iterationsNumber; howMany++) {
                    for (int i = 0; i < SCOUT_BEES_NUMBER; i++) {
                        exchangeMinPartition(solutionsObjectsList.get(i));
                    }
                }
                break;

            case EXCHANGE_TWO_CUSTOMERS:
                for (howMany = 0; howMany < iterationsNumber; howMany++) {
                    for (int j = 0; j < SELECTED_BEST_SITES_NUMBER; j++) {
                        currentSolution = solutionsObjectsList.get(j);

                        for (int i = 0; i < BEES_NUMBER_FOR_EACH_SITE; i++) {
                            testSolution = exchangeTwoCustomers(currentSolution);
                            if ((testSolution.getNumberOfADM() < currentSolution.getNumberOfADM() || testSolution.getNumberOfADM() == testSolution
                                    .getFitnessValue()) && testSolution.getFitnessValue() <= currentSolution.getFitnessValue()) {

                                if (solutionsObjectsList.remove(currentSolution))
                                    solutionsObjectsList.add(testSolution);
                            } else {
                                if (i == BEES_NUMBER_FOR_EACH_SITE - 1) { // and no modifications with improvement
                                    t = 0;
                                    firstPartition = random(0, partitionsNumber - 1);
                                    secondPartition = random(0, partitionsNumber - 1, firstPartition);
                                    int numberOfNewPartition;
                                    testSolution = currentSolution.clone();
                                    do {
                                        numberOfNewPartition = returnNumberOfNewPartitionForEdge(returnCustomers(t), testSolution, firstPartition, secondPartition);

                                        if (numberOfNewPartition != -1) {
                                            // one of two customers of the edge is in the partition
                                            testSolution.getSolution()[t] = numberOfNewPartition;

                                            int[] newParameters = countFitness(testSolution.getSolution());

                                            testSolution.setFitnessValue(newParameters[0]);
                                            testSolution.setNumberOfMinPart(newParameters[1]);
                                            testSolution.setNumberOfMaxPart(newParameters[2]);
                                            testSolution.setNumberOfADM(newParameters[3]);
                                        }

                                        t += 1;
                                    } while (t < linksNumber
                                            && returnCapacityVolumeForEachPart(testSolution.getSolution()).get(firstPartition) < bandwith
                                            && returnCapacityVolumeForEachPart(testSolution.getSolution()).get(secondPartition) < bandwith);
                                }
                            }
                            if (testSolution.getFitnessValue() < currentSolution.getFitnessValue()) {
                                i = BEES_NUMBER_FOR_EACH_SITE;
                            }
                        }// end for
                    }
                }
                break;
        }

        showCurrentSolutionsList(type, solutionsObjectsList);
    }

    public void initialDemandMatrix() {
        int i, j;

        int increase = 0;
        while (increase != linksNumber) {
            // random indexes of demand matrix
            i = random(0, customersNumber - 2);
            j = random(i + 1, customersNumber - 1);

            if (demands[i][j] == null) {
                demands[i][j] = 1.5 * random(lowerLimit, upperLimit);
                demands[j][i] = demands[i][j];
                increase += 1;
            }
        }

        prn();
        prn("DEMANDS MATRIX: ");
        new Show<>(demands);

        int cursor = 0;
        int positionOfNonzeroDemand = -1;
        for (i = 0; i < customersNumber; i++) {
            for (j = 0; j < customersNumber; j++) {
                if (j > i) {
                    positionOfNonzeroDemand += 1;
                    if (demands[i][j] != null) {
                        demandsAsList.add(demands[i][j]);
                        positionsOfNonzeroDemands[cursor] = positionOfNonzeroDemand;
                        customersAssignedToLink.put(cursor, new Integer[]{i, j});
                        cursor += 1;
                    }
                }
            }
        }

        prn();
        prn("NON-ZERO CELLS FROM DEMANDS MATRIX: ");
        new Show<>(demandsAsList);

        prn();
        prn("POSITIONS OF LINKS WITH NON-ZERO DEMANDS: ");
        new Show<>(positionsOfNonzeroDemands);
        prn();
    }

    /**
     * Evaluate quality of solution
     *
     * @param optimalSolution
     * @return [0] - total fitness<br>
     * [1] - number of minimum partition<br>
     * [2] - number of maximum partition
     */
    private int[] countFitness(int[] optimalSolution) {
        int[] valuesReturn = new int[4];
        int totalFitness = 0;
        int numberADM;

        /**
         * Volume capacity for each partition
         */
        HashMap<Integer, Double> listWithVolumeCapacity = returnCapacityVolumeForEachPart(optimalSolution);

        Entry<Integer, Double> maxEntry = null;
        Entry<Integer, Double> minEntry = null;

        for (Entry<Integer, Double> entry : listWithVolumeCapacity.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
            if (minEntry == null || entry.getValue().compareTo(minEntry.getValue()) < 0) {
                minEntry = entry;
            }
        }

        /**
         * Add to each partition ids of assigned customers
         */
        ArrayList<Set<Integer>> listWithADMs = new ArrayList<>();
        for (int i = 0; i < partitionsNumber; i++) {
            listWithADMs.add(new HashSet<>());
        }

        for (int i = 0; i < linksNumber; i++) {
            listWithADMs.get(optimalSolution[i]).add(customersAssignedToLink.get(i)[0]);
            listWithADMs.get(optimalSolution[i]).add(customersAssignedToLink.get(i)[1]);
        }

        /**
         * Add to totalFitness numbers of ADMs from each partition
         */
        for (int i = 0; i < partitionsNumber; i++) {
            totalFitness += listWithADMs.get(i).size();
        }

        numberADM = totalFitness;

        /**
         * Add to totalFitness violations for each partition
         */


        for (int i = 0; i < partitionsNumber; i++) {
            Double d = listWithVolumeCapacity.get(i);
            if (d != null) {
                totalFitness += listWithVolumeCapacity.get(i) > bandwith ? penaltyFactorForBadSolutions * (listWithVolumeCapacity.get(i) - bandwith) : 0;
            }
        }

        valuesReturn[0] = totalFitness;
        valuesReturn[1] = minEntry.getKey();
        valuesReturn[2] = maxEntry.getKey();
        valuesReturn[3] = numberADM;

        return valuesReturn;
    }

    /**
     * Count volume capacity for each partition from tempSolution
     *
     * @param tempSolution
     * @return
     */
    private synchronized HashMap<Integer, Double> returnCapacityVolumeForEachPart(int[] tempSolution) {
        /**
         * Initialization map
         */
        HashMap<Integer, Double> listWithVolumeCapacity = new HashMap<>();

        /**
         * Count capacity volume for each partition
         */
        Double tempCapacity;

        for (int i = 0; i < linksNumber; i++) {
            tempCapacity = listWithVolumeCapacity.get(tempSolution[i]);
            listWithVolumeCapacity.put(tempSolution[i], tempCapacity == null ? demandsAsList.get(i) : (tempCapacity + demandsAsList.get(i)));
        }

        /**
         * In case of too big number of partitions and any edge is assigned to one of them
         */
        for (int i = 0; i < partitionsNumber; i++) {
            if (!listWithVolumeCapacity.containsKey(i)) {
                listWithVolumeCapacity.put(i, 0.0);
            }
        }
        return listWithVolumeCapacity;
    }

    /**
     * Local search: exchange two customers between two partitions
     *
     * @param solution
     */
    private synchronized Solution exchangeTwoCustomers(Solution solution) {
        Solution newSolution = solution.clone();
        int firstPartition = newSolution.getSolution()[random(0, linksNumber - 1)];
        int secondPartition;

        do {
            secondPartition = newSolution.getSolution()[random(0, linksNumber - 1)];
        } while (secondPartition == firstPartition);

        int firstUser = solution.getRandomUser(firstPartition);
        int secondUser = solution.getRandomUser(secondPartition);

        newSolution.getSolution()[firstUser] = secondPartition;
        newSolution.getSolution()[secondUser] = firstPartition;

        int[] newParameters = countFitness(newSolution.getSolution());
        newSolution.setFitnessValue(newParameters[0]);
        newSolution.setNumberOfMinPart(newParameters[1]);
        newSolution.setNumberOfMaxPart(newParameters[2]);
        newSolution.setNumberOfADM(newParameters[3]);
        return newSolution;
    }

    /**
     * Local search: assigned a customer to partition with lowest amount
     *
     * @param solution
     */
    private synchronized Solution exchangeMinPartition(Solution solution) {
        Solution newSolution = solution.clone();
        int minPartition = solution.getNumberOfMinPart();

        int user = solution.getRandomUserFromNotMinPartition();

        newSolution.getSolution()[user] = minPartition;

        int[] newParameters = countFitness(newSolution.getSolution());
        newSolution.setFitnessValue(newParameters[0]);
        newSolution.setNumberOfMinPart(newParameters[1]);
        newSolution.setNumberOfMaxPart(newParameters[2]);
        newSolution.setNumberOfADM(newParameters[3]);

        if (newSolution.getFitnessValue() <= solution.getFitnessValue()) {
            solutionsObjectsList.remove(solution);
            solutionsObjectsList.add(newSolution);
        }
        return newSolution;
    }

    /**
     * Appoint partition index for an edge between the two users
     *
     * @param edge
     * @return
     */
    private synchronized Integer[] returnCustomers(Integer edge) {
        for (Entry<Integer, Integer[]> entry : customersAssignedToLink.entrySet()) {
            if (edge.equals(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private synchronized int returnNumberOfEdge(Integer[] customers) {
        for (Entry<Integer, Integer[]> entry : customersAssignedToLink.entrySet()) {
            if (customers[0].equals(entry.getValue()[0]) && customers[1].equals(entry.getValue()[1])) {
                return entry.getKey();
            }
        }
        return -1;
    }

    private synchronized int returnNumberOfNewPartitionForEdge(Integer[] customers, Solution solution, int firstPartition, int secondPartition) {
        int numberOfFirstCustomer = customers[0];
        int numberOfSecondCustomer = customers[1];
        int numberOfTempEdge;

        for (int i = 0; i < customersNumber; i++) {
            if (i > numberOfFirstCustomer) {
                numberOfTempEdge = returnNumberOfEdge(new Integer[]{numberOfFirstCustomer, i});
                if (numberOfTempEdge != -1) {
                    // if current edge has a positive value
                    if (solution.getSolution()[numberOfTempEdge] == firstPartition) {
                        return firstPartition;
                    } else if (solution.getSolution()[numberOfTempEdge] == secondPartition) {
                        return secondPartition;
                    }
                }
            }
        }

        for (int i = 0; i < customersNumber; i++) {
            if (i < numberOfSecondCustomer) {
                numberOfTempEdge = returnNumberOfEdge(new Integer[]{i, numberOfSecondCustomer});

                if (numberOfTempEdge != -1) {
                    // if current edge has a positive value
                    if (solution.getSolution()[numberOfTempEdge] == firstPartition) {
                        return firstPartition;
                    } else if (solution.getSolution()[numberOfTempEdge] == secondPartition) {
                        return secondPartition;
                    }
                }
            }
        }
        return -1;
    }
}
