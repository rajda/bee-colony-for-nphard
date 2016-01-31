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
    public static final int SCOUT_BEES_NUMBER = 10;

    /**
     * Number of best sites out of n selected sites
     */
    public static final int SELECTED_BEST_SITES_NUMBER = 10;

    /**
     * Number of bees recruited for each site
     */
    public static final int BEES_NUMBER_FOR_EACH_SITE = 10;

    public int customersNumber, linksNumber;
    public int partitionsNumber;

    /**
     * Number of all edges
     */
    public int totalEdgesNumber;

    /**
     * Number of iterations for each optimization method
     */
    public int iterationsNumber;

    /**
     * Number of optimization cycles
     */
    public int optimizationCyclesNumber;

    /**
     * Number of current cycle
     */
    public static int currentCycleId;

    /**
     * Bandwith of each partition
     */
    public int bandwidth;
    public int lowerLimit, upperLimit;
    public Double[][] demands;
    public ArrayList<Double> demandsAsList;
    public Integer[] positionsOfNonzeroDemands;
    public LinkedHashMap<Integer, Integer[]> customersAssignedToLink;
    public int penaltyFactorForBadSolutions = 100;
    public ArrayList<Solution> solutionsObjectsList;

    public BeeAlgorithmInIDP(int customersNumber, int linksNumber, int bandwidth, int lowerLimit, int upperLimit, int iterationsNumber, int optimizationCyclesNumber) {
        this.customersNumber = customersNumber;
        this.linksNumber = linksNumber;
        this.demands = new Double[customersNumber][customersNumber];
        this.demandsAsList = new ArrayList<>();
        this.positionsOfNonzeroDemands = new Integer[linksNumber];
        this.customersAssignedToLink = new LinkedHashMap<>();

        this.iterationsNumber = iterationsNumber;
        this.optimizationCyclesNumber = optimizationCyclesNumber;
        this.bandwidth = bandwidth;
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

        /** Sum up values in matrix of demands */
        for (int i = 0; i < customersNumber; i++) {
            for (int j = 0; j < customersNumber; j++) {
                if (demands[i][j] != null)
                    dSum += demands[i][j];
            }
        }

        /** Initial number of partitions */
        partitionsNumber = getCeilFromDouble((dSum / 2) / bandwidth);

        prn();
        prn("Demands sum between customers: " + dSum / 2);
        prn();
        prn("Initial partitions number: " + partitionsNumber);
    }

    public void mainSteps() {
        int linkId;

        /** Initial Edges, each edge assigned to the -1st partition */
        int edge[] = new int[linksNumber];
        Arrays.fill(edge, -1);

        /** Initial numberOfEdgesAssigned to each partition */
        int numberOfEdgesAssigned[] = new int[partitionsNumber];

        int[] assignments = new int[linksNumber];
        solutionsObjectsList = new ArrayList<>();

        linkId = 0;
        while (linkId != linksNumber) {
            int randomEdge = random(0, linksNumber - 1);
            if (edge[randomEdge] == -1) {
                edge[randomEdge] = linkId++;
            }
        }

        /** Initial arrangement of scout bees */
        for (int scoutBeeId = 0; scoutBeeId < SCOUT_BEES_NUMBER; scoutBeeId++) {
            Arrays.fill(numberOfEdgesAssigned, 0);

            for (linkId = 0; linkId < linksNumber; linkId++) {
                int randomPartition = random(0, partitionsNumber - 1);
                while (numberOfEdgesAssigned[randomPartition] > totalEdgesNumber / partitionsNumber) {
                    randomPartition = random(0, partitionsNumber - 1);
                    prn(randomPartition);
                }

                assignments[edge[linkId]] = randomPartition;
                numberOfEdgesAssigned[randomPartition] = numberOfEdgesAssigned[randomPartition] + 1;
            }
            solutionsObjectsList.add(new Solution(assignments.clone(), countFitness(assignments)));
        }

        /** Show list of initial solutions */
        showCurrentSolutionsList(INITIAL_RANDOM_SOLUTIONS, solutionsObjectsList);
        long startTime = System.nanoTime();

        /** Alternating changes between types of optimization */
        for (currentCycleId = 0; currentCycleId < optimizationCyclesNumber; currentCycleId++) {
            optimize(OptimizeStrategyFactory.Type.EXCHANGE_MIN_PARTITION);
            optimize(OptimizeStrategyFactory.Type.EXCHANGE_TWO_CUSTOMERS);
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
    private void optimize(OptimizeStrategyFactory.Type type) {
        OptimizeStrategy optimizeStrategy = OptimizeStrategyFactory.getStrategy(type);
        optimizeStrategy.optimize(this);
//        showCurrentSolutionsList(type, solutionsObjectsList);
    }

    public void initialDemandMatrix() {
        int i, j;

        int linkId = 0;
        while (linkId != linksNumber) {
            // random indexes of demand matrix
            i = random(0, customersNumber - 2);
            j = random(i + 1, customersNumber - 1);

            if (demands[i][j] == null) {
                demands[i][j] = 1.5 * random(lowerLimit, upperLimit);
                demands[j][i] = demands[i][j];
                linkId += 1;
            }
        }

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
        prn("Demands matrix: ");
        new Show<>(demands);

        prn();
        prn("Non-zero cells from demands matrix: ");
        new Show<>(demandsAsList);

        prn();
        prn("Positions of links with non-zero demands: ");
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
    public Fitness countFitness(int[] optimalSolution) {
        int numberADM = 0;
        int violations = 0;
        int totalFitness = 0;

        /** Volume capacity for each partition */
        HashMap<Integer, Double> listWithVolumeCapacity = getCapacityVolumeForEachPart(optimalSolution);

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

        /** Add to each partition ids of assigned customers */
        ArrayList<Set<Integer>> listWithADMs = new ArrayList<>();
        for (int i = 0; i < partitionsNumber; i++) {
            listWithADMs.add(new HashSet<>());
        }

        for (int i = 0; i < linksNumber; i++) {
            listWithADMs.get(optimalSolution[i]).add(customersAssignedToLink.get(i)[0]);
            listWithADMs.get(optimalSolution[i]).add(customersAssignedToLink.get(i)[1]);
        }

        /** Add to totalFitness numbers of ADMs from each partition */
        for (int i = 0; i < partitionsNumber; i++) {
            numberADM += listWithADMs.get(i).size();
        }
        totalFitness += numberADM;

        /** Add to totalFitness violations for each partition */
        for (int i = 0; i < partitionsNumber; i++) {
            Double d = listWithVolumeCapacity.get(i);
            if (d != null) {
                violations += listWithVolumeCapacity.get(i) > bandwidth ? penaltyFactorForBadSolutions * (listWithVolumeCapacity.get(i) - bandwidth) : 0;
            }
        }
        totalFitness += violations;

        return new IdpFitness(totalFitness, minEntry.getKey(), maxEntry.getKey(), numberADM);
    }

    /**
     * Count volume capacity for each partition from tempSolution
     *
     * @param tempSolution
     * @return
     */
    public HashMap<Integer, Double> getCapacityVolumeForEachPart(int[] tempSolution) {
        /** Initialization map */
        HashMap<Integer, Double> listWithVolumeCapacity = new HashMap<>();

        /** Count capacity volume for each partition */
        Double tempCapacity;

        for (int i = 0; i < linksNumber; i++) {
            tempCapacity = listWithVolumeCapacity.get(tempSolution[i]);
            listWithVolumeCapacity.put(tempSolution[i], tempCapacity == null ? demandsAsList.get(i) : (tempCapacity + demandsAsList.get(i)));
        }

        /** In case of too big number of partitions and any edge is assigned to one of them */
        for (int i = 0; i < partitionsNumber; i++) {
            if (!listWithVolumeCapacity.containsKey(i)) {
                listWithVolumeCapacity.put(i, 0.0);
            }
        }
        return listWithVolumeCapacity;
    }
}