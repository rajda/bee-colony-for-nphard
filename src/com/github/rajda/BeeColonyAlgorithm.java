package com.github.rajda;

import java.util.*;
import java.util.Map.Entry;

import static com.github.rajda.Helper.*;

/**
 * Bee colony algorithm solving large-scale SONET network
 *
 * @author Jacek Rajda
 */
public class BeeColonyAlgorithm {
    public static final int INITIAL_RANDOM_SOLUTIONS = 0,
            EXCHANGE_MIN_PARTITION = 1,
            EXCHANGE_TWO_CUSTOMERS = 2,
            BLANK_ENTRY = 3,
            FINISH_SOLUTION = 4;

    public static final int SCOUT_BEES_NUMBER = 10;
    public static final int SELECTED_BEST_SITES_NUMBER = 10;
    public static final int BEES_NUMBER_FOR_EACH_SITE = 10;
    public static final int PENALTY_FACTOR_FOR_BAD_SOLUTIONS = 100;

    private int partitionsNumber;
    private int totalEdgesNumber;

    private ProblemData problemData;
    private final ProblemInitData problemInitData;

    public BeeColonyAlgorithm(ProblemData problemData) {
        this.problemData = problemData;
        this.problemInitData = problemData.getProblemInitData();
        init();
    }

    private void init() {
        initialDemandMatrix();
        initialNumberOfRings();
        initialNumberOfEdges(problemInitData.getCustomersNumber());
    }

    public ProblemData getProblemData() {
        return problemData;
    }

    public ProblemInitData getProblemInitData() {
        return problemInitData;
    }

    public int getPartitionsNumber() {
        return partitionsNumber;
    }

    /**
     * Init demand matrix with random values
     */
    public void initialDemandMatrix() {
        int i, j;
        int linkId = 0;
        while (linkId != problemInitData.getLinksNumber()) {
            // random indexes of demand matrix
            i = random(0, problemInitData.getCustomersNumber() - 2);
            j = random(i + 1, problemInitData.getCustomersNumber() - 1);
            if (problemData.getDemands()[i][j] == null) {
                problemData.getDemands()[i][j] = 1.5 * random(problemInitData.getLowerLimit(), problemInitData.getUpperLimit());
                problemData.getDemands()[j][i] = problemData.getDemands()[i][j];
                linkId += 1;
            }
        }

        int cursor = 0;
        int positionOfNonzeroDemand = -1;
        for (i = 0; i < problemInitData.getCustomersNumber(); i++) {
            for (j = 0; j < problemInitData.getCustomersNumber(); j++) {
                if (j > i) {
                    positionOfNonzeroDemand += 1;
                    if (problemData.getDemands()[i][j] != null) {
                        problemData.getDemandsAsList().add(problemData.getDemands()[i][j]);
                        problemData.getPositionsOfNonzeroDemands()[cursor] = positionOfNonzeroDemand;
                        problemData.getCustomersAssignedToLink().put(cursor++, new Integer[]{i, j});
                    }
                }
            }
        }

        prn();
        prn("Demands matrix: ");
        new Show<>(problemData.getDemands());

        prn();
        prn("Non-zero cells from demands matrix: ");
        new Show<>(problemData.getDemandsAsList());

        prn();
        prn("Positions of links with non-zero demands: ");
        new Show<>(problemData.getPositionsOfNonzeroDemands());
        prn();
    }

    /**
     * Set the number of all possible edges between SCOUT_BEES_NUMBER-nodes in network
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
        for (int i = 0; i < problemInitData.getCustomersNumber(); i++) {
            for (int j = 0; j < problemInitData.getCustomersNumber(); j++) {
                if (problemData.getDemands()[i][j] != null)
                    dSum += problemData.getDemands()[i][j];
            }
        }

        /** Initial number of partitions */
        partitionsNumber = getCeilFromDouble((dSum / 2) / problemInitData.getBandwidth());

        prn();
        prn("Demands sum between customers: " + dSum / 2);
        prn();
        prn("Initial partitions number: " + partitionsNumber);
    }

    public void goThroughSteps() {
        int linkId;

        /** Initial Edges, each edge assigned to the -1st partition */
        int edge[] = new int[problemInitData.getLinksNumber()];
        Arrays.fill(edge, -1);

        /** Initial numberOfEdgesAssigned to each partition */
        int numberOfEdgesAssigned[] = new int[partitionsNumber];

        int[] assignments = new int[problemInitData.getLinksNumber()];

        linkId = 0;
        while (linkId != problemInitData.getLinksNumber()) {
            int randomEdge = random(0, problemInitData.getLinksNumber() - 1);
            if (edge[randomEdge] == -1) {
                edge[randomEdge] = linkId++;
            }
        }

        /** Initial arrangement of scout bees */
        for (int scoutBeeId = 0; scoutBeeId < SCOUT_BEES_NUMBER; scoutBeeId++) {
            Arrays.fill(numberOfEdgesAssigned, 0);

            for (linkId = 0; linkId < problemInitData.getLinksNumber(); linkId++) {
                int randomPartition = random(0, partitionsNumber - 1);
                while (numberOfEdgesAssigned[randomPartition] > totalEdgesNumber / partitionsNumber) {
                    randomPartition = random(0, partitionsNumber - 1);
                    prn(randomPartition);
                }

                assignments[edge[linkId]] = randomPartition;
                numberOfEdgesAssigned[randomPartition] = numberOfEdgesAssigned[randomPartition] + 1;
            }
            problemData.getSolutionsObjectsList().add(new Solution(assignments.clone(), countFitness(assignments)));
        }

        /** Show list of initial solutions */
        showCurrentSolutionsList(INITIAL_RANDOM_SOLUTIONS, problemData.getSolutionsObjectsList());
        long startTime = System.nanoTime();

        /** Alternating changes between types of optimization */
        for (int currentCycleId = 0; currentCycleId < problemInitData.getOptimizationCyclesNumber(); currentCycleId++) {
            optimize(OptimizeStrategyFactory.Type.EXCHANGE_MIN_PARTITION);
            optimize(OptimizeStrategyFactory.Type.EXCHANGE_TWO_CUSTOMERS);
        }

        showCurrentSolutionsList(FINISH_SOLUTION, problemData.getSolutionsObjectsList());

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
        optimizeStrategy.optimize(this, problemData);
        showCurrentSolutionsList(type, problemData.getSolutionsObjectsList());
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

        for (int i = 0; i < problemInitData.getLinksNumber(); i++) {
            listWithADMs.get(optimalSolution[i]).add(problemData.getCustomersAssignedToLink().get(i)[0]);
            listWithADMs.get(optimalSolution[i]).add(problemData.getCustomersAssignedToLink().get(i)[1]);
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
                violations += listWithVolumeCapacity.get(i) > problemInitData.getBandwidth() ? PENALTY_FACTOR_FOR_BAD_SOLUTIONS * (listWithVolumeCapacity.get(i) - problemInitData.getBandwidth()) : 0;
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

        for (int i = 0; i < problemInitData.getLinksNumber(); i++) {
            tempCapacity = listWithVolumeCapacity.get(tempSolution[i]);
            listWithVolumeCapacity.put(tempSolution[i], tempCapacity == null ? problemData.getDemandsAsList().get(i) : (tempCapacity + problemData.getDemandsAsList().get(i)));
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