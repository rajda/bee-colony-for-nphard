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
 *
 * Each customer (node in network) is connected to one or a few rings.
 * If two customers want to communicate with each other,
 * they have to be connected to the same ring.
 *
 * There is ADM (Add-Drop Multiplexer) between customer and ring.
 * Because ADM is the most expensive device in IDP network,
 * we have to do our best to minimize the number of them
 * to minimize cost of build/exploitation IDP network as well.
 *
 */
public class IdpProblem implements Problem {
    private static final int PENALTY_FACTOR_FOR_BAD_SOLUTIONS = 100;

    private final IdpProblemInitData initData;
    private final Double[][] demandMatrix;
    private final ArrayList<Double> demandsAsList;
    private final ArrayList<Integer[]> customersAssignedToLink;
    private final ArrayList<Solution> solutionsList;
    private int partitionCount;
    private int totalEdgeCount;
    private int[] assignedEdgeCount;
    private int[] edge;

    public IdpProblem(IdpProblemInitData initData) {
        this.initData = initData;
        this.demandMatrix = new Double[initData.getCustomerCount()][initData.getCustomerCount()];
        this.demandsAsList = new ArrayList<>();
        this.customersAssignedToLink = new ArrayList<>();
        this.solutionsList = new ArrayList<>();
        init();
    }

    private void init() {
        initDemandMatrix();
        initCustomersAssignment();
        initPartitionCount();
        initEdgeCount();
        initProblem();
    }

    /**
     * Demand matrix defines required capacity
     * between randomly selected customers (nodes)
     */
    private void initDemandMatrix() {
        int i, j, linkIndex = 0;
        // random assignment links between customers
        while (linkIndex != initData.getLinkCount()) {
            // random indexes of demand matrix
            i = random(0, initData.getCustomerCount() - 2);
            j = random(i + 1, initData.getCustomerCount() - 1);
            if (demandMatrix[i][j] == null) {
                demandMatrix[i][j] = 1.5 * random(initData.getLowerLimit(), initData.getUpperLimit());
                demandMatrix[j][i] = demandMatrix[i][j];
                linkIndex += 1;
            }
        }
    }

    /**
     * Link customers based on demand matrix
     */
    private void initCustomersAssignment() {
        int cursor = 0;
        for (int i = 0; i < initData.getCustomerCount(); i++) {
            for (int j = i + 1; j < initData.getCustomerCount(); j++) {
                if (demandMatrix[i][j] != null) {
                    demandsAsList.add(demandMatrix[i][j]);
                    customersAssignedToLink.add(new Integer[]{i, j});
                }
            }
        }
    }

    /**
     * Set the initial number of partitions
     */
    private void initPartitionCount() {
        double demandsSum = 0;

        /** Sum up demand capacities in demandMatrix */
        for (int i = 0; i < initData.getCustomerCount(); i++) {
            for (int j = 0; j < initData.getCustomerCount(); j++) {
                if (demandMatrix[i][j] != null)
                    demandsSum += demandMatrix[i][j];
            }
        }

        /** Initial number of partitions */
        partitionCount = getCeilFromDouble((demandsSum / 2) / initData.getBandwidth());
    }

    /**
     * Set the number of all possible edges between customers (nodes) in network
     */
    private void initEdgeCount() {
        int customerCount = initData.getCustomerCount();
        totalEdgeCount = customerCount * (customerCount - 1) / 2;
    }

    private void initProblem() {
        /** Initial Edges, each edge assigned to the -1st partition */
        this.edge = new int[initData.getLinkCount()];
        Arrays.fill(edge, -1);

        /** Initial assignedEdgeCount to each partition */
        this.assignedEdgeCount = new int[partitionCount];

        int linkId = 0;
        while (linkId != initData.getLinkCount()) {
            int randomEdge = random(0, initData.getLinkCount() - 1);
            if (edge[randomEdge] == -1) {
                edge[randomEdge] = linkId++;
            }
        }
    }

    public IdpProblemInitData getInitData() {
        return initData;
    }

    public int getPartitionCount() {
        return partitionCount;
    }

    public ArrayList<Integer[]> getCustomersAssignedToLink() {
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
        Arrays.fill(assignedEdgeCount, 0);
        int[] assignments = new int[initData.getLinkCount()];

        for (int linkId2 = 0; linkId2 < initData.getLinkCount(); linkId2++) {
            int randomPartition = random(0, partitionCount - 1);
            while (assignedEdgeCount[randomPartition] > totalEdgeCount / partitionCount) {
                randomPartition = random(0, partitionCount - 1);
                prn(randomPartition);
            }

            assignments[edge[linkId2]] = randomPartition;
            assignedEdgeCount[randomPartition] = assignedEdgeCount[randomPartition] + 1;
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
     */
    @Override
    public Fitness countFitness(Solution solutionI) {
        int admCount = 0;
        int violations = 0;
        int totalFitness = 0;
        int minPartitionId;
        int maxPartitionId;
        IdpSolution solution = (IdpSolution) solutionI;

        /** Volume capacity for each partition */
        HashMap<Integer, Double> capacityVolumeMap = getCapacityVolumeForEachPart(solution);
        List<Integer> partitionsNumberSortedByValue = capacityVolumeMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        minPartitionId = partitionsNumberSortedByValue.get(0);
        maxPartitionId = partitionsNumberSortedByValue.get(partitionCount - 1);

        /** Add to each partition ids of assigned customers */
        ArrayList<Set<Integer>> listWithADMs = new ArrayList<>();
        for (int i = 0; i < partitionCount; i++) {
            listWithADMs.add(new HashSet<>());
        }

        for (int i = 0; i < initData.getLinkCount(); i++) {
            listWithADMs.get(solution.getValueAt(i)).add(customersAssignedToLink.get(i)[0]);
            listWithADMs.get(solution.getValueAt(i)).add(customersAssignedToLink.get(i)[1]);
        }

        /** Add to totalFitness numbers of ADMs from each partition */
        for (int i = 0; i < partitionCount; i++) {
            admCount += listWithADMs.get(i).size();
        }
        totalFitness += admCount;

        /** Add to totalFitness violations for each partition */
        for (int i = 0; i < partitionCount; i++) {
            Double d = capacityVolumeMap.get(i);
            if (d != null) {
                violations += capacityVolumeMap.get(i) > initData.getBandwidth() ? PENALTY_FACTOR_FOR_BAD_SOLUTIONS * (capacityVolumeMap.get(i) - initData.getBandwidth()) : 0;
            }
        }
        totalFitness += violations;
        return new IdpFitness(totalFitness, minPartitionId, maxPartitionId, admCount);
    }

    /**
     * Count volume capacity for each partition from tempSolution
     */
    public HashMap<Integer, Double> getCapacityVolumeForEachPart(IdpSolution tempSolution) {
        /** Initialization map */
        HashMap<Integer, Double> capacityVolumeMap = new HashMap<>();

        /** Count capacity volume for each partition */
        IntStream.range(0, demandsAsList.size())
                .forEach(demandId -> capacityVolumeMap.compute(tempSolution.getValueAt(demandId),
                            (k, v) -> v == null ? demandsAsList.get(demandId) : v + demandsAsList.get(demandId)));

        /** In case of too big number of partitions and any edge is assigned to one of them */
        for (int i = 0; i < partitionCount; i++) {
            capacityVolumeMap.putIfAbsent(i, 0.0);
        }
        return capacityVolumeMap;
    }

    @Override
    public String toString() {
        return initData.toString();
    }
}