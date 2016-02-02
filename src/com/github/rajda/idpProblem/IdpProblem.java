package com.github.rajda.idpProblem;

import com.github.rajda.*;

import java.util.*;

import static com.github.rajda.Helper.*;

/**
 * Intraring Synchronous Optical Network Design Problem
 */
public class IdpProblem implements Problem {
    private IdpProblemInitData idpProblemInitData;
    private Double[][] demandMatrix;
    private ArrayList<Double> demandsAsList;
    private Integer[] positionsOfNonzeroDemands;
    private LinkedHashMap<Integer, Integer[]> customersAssignedToLink;
    private ArrayList<Solution> solutionsList;
    private int partitionsNumber;
    private int totalEdgesNumber;

    public IdpProblem(IdpProblemInitData idpProblemInitData) {
        this.idpProblemInitData = idpProblemInitData;
        this.demandMatrix = new Double[idpProblemInitData.getCustomersNumber()][idpProblemInitData.getCustomersNumber()];
        this.demandsAsList = new ArrayList<>();
        this.positionsOfNonzeroDemands = new Integer[idpProblemInitData.getLinksNumber()];
        this.customersAssignedToLink = new LinkedHashMap<>();
        this.solutionsList = new ArrayList<>();
        init();
    }

    private void init() {
        initialDemandMatrix();
        initialNumberOfRings();
        initialNumberOfEdges(idpProblemInitData.getCustomersNumber());
    }

    public void initialDemandMatrix() {
        int i, j;
        int linkId = 0;
        // random assignment links between customers
        while (linkId != idpProblemInitData.getLinksNumber()) {
            // random indexes of demand matrix
            i = random(0, idpProblemInitData.getCustomersNumber() - 2);
            j = random(i + 1, idpProblemInitData.getCustomersNumber() - 1);
            if (demandMatrix[i][j] == null) {
                demandMatrix[i][j] = 1.5 * random(idpProblemInitData.getLowerLimit(), idpProblemInitData.getUpperLimit());
                demandMatrix[j][i] = demandMatrix[i][j];
                linkId += 1;
            }
        }

        int cursor = 0;
        int positionOfNonzeroDemand = -1;
        for (i = 0; i < idpProblemInitData.getCustomersNumber(); i++) {
            for (j = 0; j < idpProblemInitData.getCustomersNumber(); j++) {
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
//
//        prn();
//        prn("Demands matrix: ");
//        new Helper.Show<>(demandMatrix);
//
//        prn();
//        prn("Non-zero cells from demandMatrix matrix: ");
//        new Helper.Show<>(demandsAsList);
//
//        prn();
//        prn("Positions of links with non-zero demandMatrix: ");
//        new Helper.Show<>(positionsOfNonzeroDemands);
//        prn();
    }

    /**
     * Set the initial number of rings
     */
    private void initialNumberOfRings() {
        double dSum = 0;

        /** Sum up values in matrix of demandMatrix */
        for (int i = 0; i < idpProblemInitData.getCustomersNumber(); i++) {
            for (int j = 0; j < idpProblemInitData.getCustomersNumber(); j++) {
                if (demandMatrix[i][j] != null)
                    dSum += demandMatrix[i][j];
            }
        }

        /** Initial number of partitions */
        partitionsNumber = getCeilFromDouble((dSum / 2) / idpProblemInitData.getBandwidth());
//
//        prn();
//        prn("Demands sum between customers: " + dSum / 2);
//        prn();
//        prn("Initial partitions number: " + partitionsNumber);
    }

    /**
     * Set the number of all possible edges between SCOUT_BEES_NUMBER-nodes in network
     */
    private void initialNumberOfEdges(int numberOfNodes) {
        totalEdgesNumber = numberOfNodes * (numberOfNodes - 1) / 2;
    }

    public IdpProblemInitData getIdpProblemInitData() {
        return idpProblemInitData;
    }

    public int getPartitionsNumber() {
        return partitionsNumber;
    }

    public LinkedHashMap<Integer, Integer[]> getCustomersAssignedToLink() {
        return customersAssignedToLink;
    }

    @Override
    public ArrayList<Solution> getSolutionsList() {
        return solutionsList;
    }

    @Override
    public void createInitialSolutions() {
        /** Initial Edges, each edge assigned to the -1st partition */
        int edge[] = new int[idpProblemInitData.getLinksNumber()];
        Arrays.fill(edge, -1);

        /** Initial numberOfEdgesAssigned to each partition */
        int numberOfEdgesAssigned[] = new int[partitionsNumber];

        int[] assignments = new int[idpProblemInitData.getLinksNumber()];

        int linkId = 0;
        while (linkId != idpProblemInitData.getLinksNumber()) {
            int randomEdge = random(0, idpProblemInitData.getLinksNumber() - 1);
            if (edge[randomEdge] == -1) {
                edge[randomEdge] = linkId++;
            }
        }

        Arrays.fill(numberOfEdgesAssigned, 0);

        for (int linkId2 = 0; linkId2 < idpProblemInitData.getLinksNumber(); linkId2++) {
            int randomPartition = random(0, partitionsNumber - 1);
            while (numberOfEdgesAssigned[randomPartition] > totalEdgesNumber / partitionsNumber) {
                randomPartition = random(0, partitionsNumber - 1);
                prn(randomPartition);
            }

            assignments[edge[linkId2]] = randomPartition;
            numberOfEdgesAssigned[randomPartition] = numberOfEdgesAssigned[randomPartition] + 1;
        }
        solutionsList.add(new Solution(assignments.clone(), countFitness(assignments)));
    }

    @Override
    public void optimize() {
        optimize(IdpOptimizeStrategyFactory.Type.EXCHANGE_MIN_PARTITION);
        optimize(IdpOptimizeStrategyFactory.Type.EXCHANGE_TWO_CUSTOMERS);
    }

    public void optimize(IdpOptimizeStrategyFactory.Type type) {
        OptimizeStrategy optimizeStrategy = IdpOptimizeStrategyFactory.getStrategy(type);
        optimizeStrategy.optimize(this);
        showCurrentSolutionsList(type);
    }

    public void showCurrentSolutionsList(IdpOptimizeStrategyFactory.Type type) {
        Collections.sort(solutionsList, (o1, o2) -> Integer.compare(o1.getFitnessValue(), o2.getFitnessValue()));

        switch (type) {
            case EXCHANGE_MIN_PARTITION:
//                prn(EXCHANGE_MIN_PARTITION);
                break;
            case EXCHANGE_TWO_CUSTOMERS:
//                prn(EXCHANGE_TWO_CUSTOMERS);
                break;
        }
    }

    /**
     * Evaluate quality of solution
     *
     * @param solution
     * @return [0] - total fitness<br>
     * [1] - number of minimum partition<br>
     * [2] - number of maximum partition
     */
    @Override
    public Fitness countFitness(int[] solution) {
        int numberADM = 0;
        int violations = 0;
        int totalFitness = 0;

        /** Volume capacity for each partition */
        HashMap<Integer, Double> listWithVolumeCapacity = getCapacityVolumeForEachPart(solution);

        Map.Entry<Integer, Double> maxEntry = null;
        Map.Entry<Integer, Double> minEntry = null;

        for (Map.Entry<Integer, Double> entry : listWithVolumeCapacity.entrySet()) {
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

        for (int i = 0; i < idpProblemInitData.getLinksNumber(); i++) {
            listWithADMs.get(solution[i]).add(customersAssignedToLink.get(i)[0]);
            listWithADMs.get(solution[i]).add(customersAssignedToLink.get(i)[1]);
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
                violations += listWithVolumeCapacity.get(i) > idpProblemInitData.getBandwidth() ? BeeColonyAlgorithm.PENALTY_FACTOR_FOR_BAD_SOLUTIONS * (listWithVolumeCapacity.get(i) - idpProblemInitData.getBandwidth()) : 0;
            }
        }
        totalFitness += violations;
        return new IdpFitness(totalFitness, minEntry.getKey(), maxEntry.getKey(), numberADM);
    }

    @Override
    public ProblemInitData getProblemInitData() {
        return idpProblemInitData;
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

        for (int i = 0; i < idpProblemInitData.getLinksNumber(); i++) {
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
