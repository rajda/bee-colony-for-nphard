package com.github.rajda.idpProblem;

import com.github.rajda.Fitness;
import com.github.rajda.OptimizeStrategy;
import com.github.rajda.Problem;
import com.github.rajda.Solution;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.rajda.Helper.*;

/**
 * Intraring Synchronous Optical Network Design Problem
 */
public class IdpProblem implements Problem {
    private static final int PENALTY_FACTOR_FOR_BAD_SOLUTIONS = 100;

    private final IdpProblemInitData initData;
    private final Double[][] demandMatrix;
    private final ArrayList<Double> demandsAsList;
    private final Integer[] positionsOfNonzeroDemands;
    private final LinkedHashMap<Integer, Integer[]> customersAssignedToLink;
    private final ArrayList<Solution> solutionsList;
    private int partitionsNumber;
    private int totalEdgesNumber;
    private int[] numberOfEdgesAssigned;
    private int[] assignments;
    private int[] edge;

    public IdpProblem(IdpProblemInitData initData) {
        this.initData = initData;
        this.demandMatrix = new Double[initData.getCustomersNumber()][initData.getCustomersNumber()];
        this.demandsAsList = new ArrayList<>();
        this.positionsOfNonzeroDemands = new Integer[initData.getLinksNumber()];
        this.customersAssignedToLink = new LinkedHashMap<>();
        this.solutionsList = new ArrayList<>();
        init();
    }

    private void init() {
        initialDemandMatrix();
        initialNumberOfRings();
        initialNumberOfEdges(initData.getCustomersNumber());
        initialProblem();
    }

    private void initialProblem() {
        /** Initial Edges, each edge assigned to the -1st partition */
        this.edge = new int[initData.getLinksNumber()];
        Arrays.fill(edge, -1);

        /** Initial numberOfEdgesAssigned to each partition */
        this.numberOfEdgesAssigned = new int[partitionsNumber];
        this.assignments = new int[initData.getLinksNumber()];

        int linkId = 0;
        while (linkId != initData.getLinksNumber()) {
            int randomEdge = random(0, initData.getLinksNumber() - 1);
            if (edge[randomEdge] == -1) {
                edge[randomEdge] = linkId++;
            }
        }
    }

    private void initialDemandMatrix() {
        int i, j;
        int linkId = 0;
        // random assignment links between customers
        while (linkId != initData.getLinksNumber()) {
            // random indexes of demand matrix
            i = random(0, initData.getCustomersNumber() - 2);
            j = random(i + 1, initData.getCustomersNumber() - 1);
            if (demandMatrix[i][j] == null) {
                demandMatrix[i][j] = 1.5 * random(initData.getLowerLimit(), initData.getUpperLimit());
                demandMatrix[j][i] = demandMatrix[i][j];
                linkId += 1;
            }
        }

        int cursor = 0;
        int positionOfNonzeroDemand = -1;
        for (i = 0; i < initData.getCustomersNumber(); i++) {
            for (j = 0; j < initData.getCustomersNumber(); j++) {
                if (j > i) {
                    positionOfNonzeroDemand += 1;
                    if (demandMatrix[i][j] != null) {
                        demandsAsList.add(demandMatrix[i][j]);
                        positionsOfNonzeroDemands[cursor] = positionOfNonzeroDemand;
                        customersAssignedToLink.put(cursor++, new Integer[]{i, j});
                    }
                }
            }
        }
    }

    /**
     * Set the initial number of rings
     */
    private void initialNumberOfRings() {
        double dSum = 0;

        /** Sum up values in matrix of demandMatrix */
        for (int i = 0; i < initData.getCustomersNumber(); i++) {
            for (int j = 0; j < initData.getCustomersNumber(); j++) {
                if (demandMatrix[i][j] != null)
                    dSum += demandMatrix[i][j];
            }
        }

        /** Initial number of partitions */
        partitionsNumber = getCeilFromDouble((dSum / 2) / initData.getBandwidth());
    }

    /**
     * Set the number of all possible edges between SCOUT_BEES_NUMBER-nodes in network
     */
    private void initialNumberOfEdges(int numberOfNodes) {
        totalEdgesNumber = numberOfNodes * (numberOfNodes - 1) / 2;
    }

    public IdpProblemInitData getInitData() {
        return initData;
    }

    public int getPartitionsNumber() {
        return partitionsNumber;
    }

    public LinkedHashMap<Integer, Integer[]> getCustomersAssignedToLink() {
        return customersAssignedToLink;
    }

    @Override
    public ArrayList<Solution> getSolutionsList() {
        return new ArrayList<>(solutionsList);
    }

    @Override
    public void putInPlace(Solution betterSolution, Solution worseSolution) {
        if (solutionsList.remove(worseSolution)) {
            solutionsList.add(betterSolution);
        }
    }

    @Override
    public void createInitialSolution() {
        Arrays.fill(numberOfEdgesAssigned, 0);

        for (int linkId2 = 0; linkId2 < initData.getLinksNumber(); linkId2++) {
            int randomPartition = random(0, partitionsNumber - 1);
            while (numberOfEdgesAssigned[randomPartition] > totalEdgesNumber / partitionsNumber) {
                randomPartition = random(0, partitionsNumber - 1);
                prn(randomPartition);
            }

            assignments[edge[linkId2]] = randomPartition;
            numberOfEdgesAssigned[randomPartition] = numberOfEdgesAssigned[randomPartition] + 1;
        }

        IdpSolution initialSolution = new IdpSolution(assignments.clone());
        initialSolution.setFitness(countFitness(initialSolution));
        solutionsList.add(initialSolution);
    }

    @Override
    public IdpSolution optimize(Solution solution) {
        return executeStrategy(IdpOptimizeStrategyFactory.Type.EXCHANGE_TWO_CUSTOMERS, solution);
    }

    @Override
    public IdpSolution localSearchProbability(Solution solution) {
        return executeStrategy(IdpOptimizeStrategyFactory.Type.EXCHANGE_ACCORDING_PROB, solution);
    }

    @Override
    public IdpSolution localSearchDiscover(Solution solution) {
        return executeStrategy(IdpOptimizeStrategyFactory.Type.EXCHANGE_MIN_PARTITION, solution);
    }

    private IdpSolution executeStrategy(IdpOptimizeStrategyFactory.Type type, Solution solution) {
        OptimizeStrategy optimizeStrategy = IdpOptimizeStrategyFactory.getStrategy(type);
        return (IdpSolution) optimizeStrategy.optimize(this, solution);
    }

    /**
     * Evaluate quality of solution
     *
     * @param solutionI
     * @return [0] - total fitness<br>
     * [1] - number of minimum partition<br>
     * [2] - number of maximum partition
     */
    @Override
    public Fitness countFitness(Solution solutionI) {
        int numberADM = 0;
        int violations = 0;
        int totalFitness = 0;
        int minPartitionNumber;
        int maxPartitionNumber;
        IdpSolution solution = (IdpSolution) solutionI;

        /** Volume capacity for each partition */
        HashMap<Integer, Double> capacityVolumeMap = getCapacityVolumeForEachPart(solution);
        List<Integer> partitionsNumberSortedByValue = capacityVolumeMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        minPartitionNumber = partitionsNumberSortedByValue.get(0);
        maxPartitionNumber = partitionsNumberSortedByValue.get(partitionsNumber - 1);

        /** Add to each partition ids of assigned customers */
        ArrayList<Set<Integer>> listWithADMs = new ArrayList<>();
        for (int i = 0; i < partitionsNumber; i++) {
            listWithADMs.add(new HashSet<>());
        }

        for (int i = 0; i < initData.getLinksNumber(); i++) {
            listWithADMs.get(solution.getValueAt(i)).add(customersAssignedToLink.get(i)[0]);
            listWithADMs.get(solution.getValueAt(i)).add(customersAssignedToLink.get(i)[1]);
        }

        /** Add to totalFitness numbers of ADMs from each partition */
        for (int i = 0; i < partitionsNumber; i++) {
            numberADM += listWithADMs.get(i).size();
        }
        totalFitness += numberADM;

        /** Add to totalFitness violations for each partition */
        for (int i = 0; i < partitionsNumber; i++) {
            Double d = capacityVolumeMap.get(i);
            if (d != null) {
                violations += capacityVolumeMap.get(i) > initData.getBandwidth() ? PENALTY_FACTOR_FOR_BAD_SOLUTIONS * (capacityVolumeMap.get(i) - initData.getBandwidth()) : 0;
            }
        }
        totalFitness += violations;
        return new IdpFitness(totalFitness, minPartitionNumber, maxPartitionNumber, numberADM);
    }

    /**
     * Count volume capacity for each partition from tempSolution
     *
     * @param tempSolution
     * @return
     */
    public HashMap<Integer, Double> getCapacityVolumeForEachPart(IdpSolution tempSolution) {
        /** Initialization map */
        HashMap<Integer, Double> capacityVolumeMap = new HashMap<>();

        /** Count capacity volume for each partition */
        IntStream.range(0, demandsAsList.size())
                .forEach(demandId -> capacityVolumeMap.compute(tempSolution.getValueAt(demandId),
                            (k, v) -> v == null ? demandsAsList.get(demandId) : v + demandsAsList.get(demandId)));

        /** In case of too big number of partitions and any edge is assigned to one of them */
        for (int i = 0; i < partitionsNumber; i++) {
            capacityVolumeMap.putIfAbsent(i, 0.0);
        }
        return capacityVolumeMap;
    }
}