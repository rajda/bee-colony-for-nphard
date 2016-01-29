package com.github.rajda;

import static com.github.rajda.Helper.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Inspiracja algorytmem pszczelim w rozwiazywaniu problemu IDP
 * 
 * @author Jacek
 * 
 */
public class BeeAlgorithmInIDP {

	private int k, klb;
	public static int numberCustomers, numberLinks;
	/** number of all edges */
	private int totalNumberOfEdges;
	/** number of iterations for each optimization method */
	private static int iterationsNumber;
	/** number of optimization cycles */ 
	private static int optimizationCyclesNumber;
	/** number of current cycle */
	private static int currentCycle;
	public static int maxTimeOfSimulation;
	/** bandwith of each partition */
	private int bandwith;
	private int lowerLimit, upperLimit;
	private Double[][] demands;
	private ArrayList<Double> demandsAsList;
	private Integer[] positionsOfNonzeroDemands;
	private LinkedHashMap<Integer, Integer[]> customersAssignedToLink;
	private int penaltyFactorForBadSolutions = 100;

	public static final int INITIAL_RANDOM_SOLUTIONS = 0, EXCHANGE_MIN_PARTITION = 1, EXCHANGE_TWO_CUSTOMERS = 2, BLANK_ENTRY = 3, FINISH_SOLUTION = 4;

	private ArrayList<Solution> solutionsObjectsList;
	public static int progressValue;

	/** number of scout bees */
	private int n = 10;

	/** number of best sites out of n selected sites */
	private int e = 10;

	/** number of bees recruited for all sites */
	private int nep = 10;

	public BeeAlgorithmInIDP(int numberCustomers, int numberLinks, int bandwith, int lowerLimit, int upperLimit, int iterationsNumber, int optimizationCyclesNumber) {
		this.numberCustomers = numberCustomers;
		this.numberLinks = numberLinks;
		this.demands = new Double[numberCustomers][numberCustomers];
		this.demandsAsList = new ArrayList<Double>();
		this.positionsOfNonzeroDemands = new Integer[numberLinks];
		this.customersAssignedToLink = new LinkedHashMap<Integer, Integer[]>();

		BeeAlgorithmInIDP.iterationsNumber = iterationsNumber;
		BeeAlgorithmInIDP.optimizationCyclesNumber = optimizationCyclesNumber;
		this.bandwith = bandwith;
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
		init();
	}

	public BeeAlgorithmInIDP() {
		init();
	}

	public BeeAlgorithmInIDP(int n, int m, int e, double ngh, int nep, int nsp, int iteration, int upperLimit, int lowerLimit, boolean integerize) {
		this.n = n;
		init();
	}

	private void init() {
		initialDemandMatrix();
		initialNumberOfRings();
		initialNumberOfEdges(numberCustomers);
	}

	/**
	 * Liczba wszystkich mozliwych polaczen pomiedzy n-wezlami w sieci
	 * 
	 * @param numberOfVertexs
	 * @return
	 */
	private void initialNumberOfEdges(int numberOfVertexs) {
		totalNumberOfEdges = numberOfVertexs * (numberOfVertexs - 1) / 2;
	}

	/**
	 * Poczatkowa liczba pierscieni
	 * 
	 * @return
	 */
	private void initialNumberOfRings() {
		double dSum = 0;

		/** Sumowanie pol z macierzy zapotrzebowan */
		for (int i = 0; i < numberCustomers; i++) {
			for (int j = 0; j < numberCustomers; j++) {
				if (demands[i][j] != null)
					dSum += demands[i][j];
			}
		}

		/** Minimalna liczba poczatkowych pierscieni */
		klb = getCeilFromDouble((dSum / 2) / bandwith);
		// k = klb + 1;
		// k = klb == 1 ? 2 : klb;
		k = klb;

		prn();
		prn("SUM OF DEMANDS BETWEEN CUSTOMERS: " + dSum / 2);
		prn();
		prn("INITIAL NUMBER OF PARTITIONS: " + k);
	}

	public void mainSteps() {

		int l = 0;

		/** initial Edges, each edge assigned to the -1st partition */
		int edge[] = new int[numberLinks];
		Arrays.fill(edge, -1);

		/** initial numberOfEdgesAssigned to each partition */
		int numberOfEdgesAssigned[] = new int[k];

		Integer[] assignments = new Integer[numberLinks];
		solutionsObjectsList = new ArrayList<Solution>();

		l = 0;
		while (l != numberLinks) {
			int randomEdge = random(0, numberLinks - 1);
			if (edge[randomEdge] == -1) {
				edge[randomEdge] = l;
				l += 1;
			}
		}

		/** initial arrangement of scout bees */
		for (int h = 0; h < n; h++) {

			Arrays.fill(numberOfEdgesAssigned, 0);

			for (l = 0; l < numberLinks; l++) {

				int randomPartition = random(0, k - 1);
				while (numberOfEdgesAssigned[randomPartition] > totalNumberOfEdges / k) {
					randomPartition = random(0, k - 1);
					prn(randomPartition);
				}

				assignments[edge[l]] = randomPartition;
				numberOfEdgesAssigned[randomPartition] = numberOfEdgesAssigned[randomPartition] + 1;
			}

			solutionsObjectsList.add(new Solution(assignments.clone(), countFitness(assignments)));
		}

		/** show list of initial solutions */
		showCurrentSolutionsList(INITIAL_RANDOM_SOLUTIONS, solutionsObjectsList);
		long startTime = System.nanoTime();

		/** alternating changes between types of optimization */
		for (currentCycle = 0; currentCycle < optimizationCyclesNumber; currentCycle++) {
			optimize(EXCHANGE_MIN_PARTITION);
			optimize(EXCHANGE_TWO_CUSTOMERS);
		}

		showCurrentSolutionsList(FINISH_SOLUTION, solutionsObjectsList);
		
		long stopTime = System.nanoTime();
		prn("czas procesu optymalizacji: " + (stopTime - startTime) / 1000000 + " ms");
		prn();
		prn("-----------------------------------------------------------------------------------------");
	}

	/**
	 * Select type of optimization
	 * 
	 * @param type
	 *            EXCHANGE_MIN_PARTITION - select and assign random edge to
	 *            partition with lowest traffic<br>
	 *            EXCHANGE_TWO_CUSTOMERS - replace edges between two partitions
	 *            partitions
	 */
	private void optimize(int type) {

		int howMany = 0;
		int t = 0;
		int firstPartition = 0;
		int secondPartition = 0;
		Solution currentSolution = null;
		Solution testSolution = null;

		switch (type) {
		case EXCHANGE_MIN_PARTITION:
			
			prn(currentCycle*100/optimizationCyclesNumber + " %");
			
			for (howMany = 0; howMany < iterationsNumber; howMany++) {

				for (int i = 0; i < n; i++) {
					exchangeMinPartition(solutionsObjectsList.get(i));
				}
			}
			break;

		case EXCHANGE_TWO_CUSTOMERS:

			for (howMany = 0; howMany < iterationsNumber; howMany++) {

				for (int j = 0; j < e; j++) {

					currentSolution = solutionsObjectsList.get(j);

					for (int i = 0; i < nep; i++) {

						testSolution = exchangeTwoCustomers(currentSolution);
						if ((testSolution.getNumberOfADM() < currentSolution.getNumberOfADM() || testSolution.getNumberOfADM() == testSolution
								.getFitnessValue()) && testSolution.getFitnessValue() <= currentSolution.getFitnessValue()) {

							if (solutionsObjectsList.remove(currentSolution))
								solutionsObjectsList.add(testSolution);

						} else {

							if (i == nep - 1) { // and no modifications with
												// improvement

								t = 0;
								firstPartition = random(0, k - 1);
								secondPartition = random(0, k - 1, firstPartition);
								int numberOfNewPartition = -1;
								testSolution = currentSolution.clone();
								do {

									numberOfNewPartition = returnNumberOfNewPartitionForEdge(returnCustomers(t), testSolution, firstPartition,
											secondPartition);

									if (numberOfNewPartition != -1) {
										// one of two customers of the edge is
										// in
										// the partition
										testSolution.getSolution()[t] = numberOfNewPartition;

										int[] newParameters = countFitness(testSolution.getSolution());

										testSolution.setFitnessValue(newParameters[0]);
										testSolution.setNumberOfMinPart(newParameters[1]);
										testSolution.setNumberOfMaxPart(newParameters[2]);
										testSolution.setNumberOfADM(newParameters[3]);
									}

									t += 1;
								} while (t < numberLinks
										&& returnCapacityVolumeForEachPart(testSolution.getSolution()).get(firstPartition) < bandwith
										&& returnCapacityVolumeForEachPart(testSolution.getSolution()).get(secondPartition) < bandwith);
							}
						}
						if (testSolution.getFitnessValue() < currentSolution.getFitnessValue()) {
							i = nep;
						}
					}// end for
					currentSolution = null;
				}
			}

			break;
		}

		showCurrentSolutionsList(type, solutionsObjectsList);
	}

	public void initialDemandMatrix() {
		int i = 0, j = 0;

		int increase = 0;
		while (increase != numberLinks) {

			// random indexes of demand matrix
			i = random(0, numberCustomers - 2);
			j = random(i + 1, numberCustomers - 1);

			if (demands[i][j] == null) {
				demands[i][j] = 1.5 * random(lowerLimit, upperLimit);
				demands[j][i] = demands[i][j];
				increase += 1;
			}
		}

		prn();
		prn("DEMANDS MATRIX: ");
		new Show<Double>(demands);

		int cursor = 0;
		int positionOfNonzeroDemand = -1;
		for (i = 0; i < numberCustomers; i++) {
			for (j = 0; j < numberCustomers; j++) {
				if (j <= i) {
					continue;
				} else {
					positionOfNonzeroDemand += 1;

					if (demands[i][j] == null) {
						continue;
					} else {
						demandsAsList.add(demands[i][j]);
						positionsOfNonzeroDemands[cursor] = positionOfNonzeroDemand;
						// customersAssignedToLink[cursor][0] = i;
						// customersAssignedToLink[cursor][1] = j;
						customersAssignedToLink.put(cursor, new Integer[] { i, j });
						cursor += 1;
					}
				}
			}
		}

		prn();
		prn("NON-ZERO CELLS FROM DEMANDS MATRIX: ");
		new Show<Double>(demandsAsList);

		prn();
		prn("POSITIONS OF LINKS WITH NON-ZERO DEMANDS: ");
		new Show<Integer>(positionsOfNonzeroDemands);
		prn();

//		prn("CUSTOMERS CONNECTED LINKS WITH NON-ZERO DEMAND: " + customersAssignedToLink.size());
//		new Show<Integer>(customersAssignedToLink);
	}

	/**
	 * Evaluate quality of solution
	 * 
	 * @param optimalSolution
	 * @return [0] - total fitness<br>
	 *         [1] - number of minimum partition<br>
	 *         [2] - number of maximum partition
	 */
	private int[] countFitness(Integer[] optimalSolution) {

		int[] valuesReturn = new int[4];
		int totalFitness = 0;
		int numberADM = 0;

		/** Volume capacity for each partition */
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

		/** Add to each partition ids of assigned customers */
		ArrayList<Set<Integer>> listWithADMs = new ArrayList<Set<Integer>>();
		for (int i = 0; i < k; i++) {
			listWithADMs.add(new HashSet<Integer>());
		}

		for (int i = 0; i < numberLinks; i++) {
			listWithADMs.get(optimalSolution[i]).add(customersAssignedToLink.get(i)[0]);
			listWithADMs.get(optimalSolution[i]).add(customersAssignedToLink.get(i)[1]);
		}

		/** Add to totalFitness numbers of ADMs from each partition */
		for (int i = 0; i < k; i++) {
			totalFitness += listWithADMs.get(i).size();
		}

		numberADM = totalFitness;

		/** Add to totalFitness violations for each partition */
		for (int i = 0; i < k; i++) {
			Double d = listWithVolumeCapacity.get(i);
			if (d == null)
				totalFitness += 0;
			else
				totalFitness += listWithVolumeCapacity.get(i) > bandwith ? penaltyFactorForBadSolutions * (listWithVolumeCapacity.get(i) - bandwith)
						: 0;
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
	private synchronized HashMap<Integer, Double> returnCapacityVolumeForEachPart(Integer[] tempSolution) {

		/** Initialization map */
		HashMap<Integer, Double> listWithVolumeCapacity = new HashMap<Integer, Double>();

		/** Count capacity volume for each partition */
		Double tempCapacity = 0d;

		for (int i = 0; i < numberLinks; i++) {

			tempCapacity = listWithVolumeCapacity.get(tempSolution[i]);
			if (tempCapacity == null) {
				listWithVolumeCapacity.put(tempSolution[i], demandsAsList.get(i));
			} else {
				listWithVolumeCapacity.put(tempSolution[i], tempCapacity + demandsAsList.get(i));
			}
		}

		// w przypadku, gdyby sie okazalo, ze jest za duza liczba pierscieni i
		// do ktoregos z nich nie jest przypisana zadna krawedz
		for (int i = 0; i < k; i++) {
			if (!listWithVolumeCapacity.containsKey(i)) {
				listWithVolumeCapacity.put(i, 0d);
			}
		}

		// prn("CAPACITY VOLUME FOR EACH PARTITION: ");
		// prn(listWithVolumeCapacity.values());

		return listWithVolumeCapacity;
	}

	/**
	 * Local search: exchange two customers between two partitions
	 * 
	 * @param solution
	 */
	private synchronized Solution exchangeTwoCustomers(Solution solution) {
		Solution newSolution = solution.clone();
		int firstPartition = newSolution.getSolution()[random(0, numberLinks - 1)];
		int secondPartition = 0;

		do {
			secondPartition = newSolution.getSolution()[random(0, numberLinks - 1)];
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

		// prn("stare: " + solution);
		// prn("nowe: " + newSolution);

		// if (newSolution.getFitnessValue() <= solution.getFitnessValue()) {
		// // prn(newSolution.getFitnessValue() + " < " +
		// // solution.getFitnessValue());
		// solutionsObjectsList.remove(solution);
		// solutionsObjectsList.add(newSolution);
		// //return true;
		// }
		// return false;

		return newSolution;
	}

	/**
	 * Local search: assigned a customer to partition with lowest amount
	 * 
	 * @param solution
	 */
	private synchronized Solution exchangeMinPartition(Solution solution) {

		// prn("exchangeMinPartition");

		Solution newSolution = solution.clone();
		int minPartition = solution.getNumberOfMinPart();

		int user = solution.getRandomUserFromNotMinPartition();

		newSolution.getSolution()[user] = minPartition;

		int[] newParameters = countFitness(newSolution.getSolution());

		newSolution.setFitnessValue(newParameters[0]);
		newSolution.setNumberOfMinPart(newParameters[1]);
		newSolution.setNumberOfMaxPart(newParameters[2]);
		newSolution.setNumberOfADM(newParameters[3]);

		// prn("stare: " + solution);
		// prn("nowe: " + newSolution);

		if (newSolution.getFitnessValue() <= solution.getFitnessValue()) {
			// prn(newSolution.getFitnessValue() + " < " +
			// solution.getFitnessValue());
			solutionsObjectsList.remove(solution);
			solutionsObjectsList.add(newSolution);
			// return true;
		}
		// return false;

		return newSolution;
	}

	/**
	 * Wyznacza numer partycji dla krawedzi laczacej dwoch konkretnych
	 * uzytkownikow
	 * 
	 * @param customers
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

	/**
	 * Wyznacza numer partycji dla krawedzi laczacej dwoch konkretnych
	 * uzytkownikow
	 * 
	 * @param customers
	 * @return
	 */
	private synchronized int returnNumberOfEdge(Integer[] customers) {

		for (Entry<Integer, Integer[]> entry : customersAssignedToLink.entrySet()) {
			if (customers[0].equals(entry.getValue()[0]) && customers[1].equals(entry.getValue()[1])) {
				return entry.getKey();
			}
		}
		return -1;
	}

	private synchronized int returnNumberOfNewPartitionForEdge(Integer[] customers, Solution solution, int firstPartition, int secondPartition) {

		int numberOfFirstCust = customers[0];
		int numberOfSecondCust = customers[1];
		int numberOfTempEdge = -1;

		// prn("first: ");

		for (int i = 0; i < numberCustomers; i++) {
			if (i <= numberOfFirstCust) {
				continue;
			} else {
				// edge = (numberOfFirstCust, i)
				numberOfTempEdge = returnNumberOfEdge(new Integer[] { numberOfFirstCust, i });

				if (numberOfTempEdge != -1) {
					// czyli dla naszej macierzy uslug, dana krawedz ma dodatnia
					// wartosc

					if (solution.getSolution()[numberOfTempEdge] == firstPartition) {
						return firstPartition;
					} else if (solution.getSolution()[numberOfTempEdge] == secondPartition) {
						return secondPartition;
					} else {
						continue;
					}
				} else {
					continue;
				}
			}
		}

		// prn("second: ");

		for (int i = 0; i < numberCustomers; i++) {
			if (i >= numberOfSecondCust) {
				continue;
			} else {
				// edge = (i, numberOfSecondCust)
				numberOfTempEdge = returnNumberOfEdge(new Integer[] { i, numberOfSecondCust });

				if (numberOfTempEdge != -1) {
					// czyli dla naszej macierzy uslug, dana krawedz ma dodatnia
					// wartosc

					if (solution.getSolution()[numberOfTempEdge] == firstPartition) {
						return firstPartition;
					} else if (solution.getSolution()[numberOfTempEdge] == secondPartition) {
						return secondPartition;
					} else {
						continue;
					}
				} else {
					continue;
				}
			}
		}

		return -1;
	}

	Comparator<Double> comparator = new Comparator<Double>() {

		@Override
		public int compare(Double o1, Double o2) {
			return o1.compareTo(o2);
		}

	};
}
