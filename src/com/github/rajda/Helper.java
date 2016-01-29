package com.github.rajda;

import java.util.*;
import java.util.Map.Entry;

public class Helper {

    private static final String RANDOM_SOLUTIONS = "RANDOM SOLUTIONS: ";
    private static final String EXCHANGE_MIN_PARTITION = "AFTER ASSIGNED EDGES TO MINUMUM PARTITION: ";
    private static final String EXCHANGE_TWO_CUSTOMERS = "AFTER REPLACE EDGES BETWEEN PARTITIONS: ";
    private static final String FINISH_SOLUTION_1 = "FINISH SOLUTION, numberCustomers: ";
    private static final String FINISH_SOLUTION_2 = ", numberLinks: ";

    /**
     * Wyznaczenie cechy gornej z danej liczby
     *
     * @param value - liczba double
     * @return
     */
    public static int getCeilFromDouble(double value) {
        return (value == (int) value) ? (int) value : (int) value + 1;
    }

    /**
     * Random value from the range
     *
     * @param low - the lowest
     * @param high - the highest
     * @return
     */
    public static int random(int low, int high) {
        Random generator = new Random();
        return (generator.nextInt(high - low + 1) + low);
    }

    /**
     * Random value from the range except referenced value
     *
     * @param low - the lowest
     * @param high - the highest
     * @param referenceValue
     * @return
     */
    public static int random(int low, int high, int referenceValue) {
        Random generator = new Random();
        int randomValue;

        do {
            randomValue = (generator.nextInt(high - low + 1) + low);
        } while (randomValue == referenceValue);

        return randomValue;
    }

    public static void prn(Object obj) {
        if (!obj.equals("empty"))
            System.out.println(obj);
    }

    public static void prn() {
        System.out.println();
    }

    public static void showCurrentSolutionsList(int type, ArrayList<Solution> solutionsObjectsList) {
        Collections.sort(solutionsObjectsList, new Solution.SolutionComparator());

        switch (type) {
            case BeeAlgorithmInIDP.BLANK_ENTRY:
                break;
//		case BeeAlgorithmInIDP.INITIAL_RANDOM_SOLUTIONS:
//			prn(RANDOM_SOLUTIONS);
//			break;
//		case BeeAlgorithmInIDP.EXCHANGE_MIN_PARTITION:
//			prn(EXCHANGE_MIN_PARTITION);
//			break;
//		case BeeAlgorithmInIDP.EXCHANGE_TWO_CUSTOMERS:
//			prn(EXCHANGE_TWO_CUSTOMERS);
//			break;
            case BeeAlgorithmInIDP.FINISH_SOLUTION:
                prn(FINISH_SOLUTION_1 + BeeAlgorithmInIDP.numberCustomers + FINISH_SOLUTION_2 + BeeAlgorithmInIDP.numberLinks + ": ");
                for (int s = 0; s < solutionsObjectsList.size(); s++) {
                    prn(solutionsObjectsList.get(s));
                }
                break;
        }
    }

    static class Show<T> {
        private static final int LIST = 0;
        private static final int TABLE = 1;
        private static final int MATRIX = 2;
        private static final int MAP = 3;

        public T[] table;
        public T[][] matrix;
        public ArrayList<T> list;
        public LinkedHashMap<T, T[]> map;

        public Show(T[][] matrix) {
            this.matrix = matrix;
            show(MATRIX);
        }

        public Show(T[] table) {
            this.table = table;
            show(TABLE);
        }

        public Show(ArrayList<T> list) {
            this.list = list;
            show(LIST);
        }

        private void show(int type) {
            String chain = "";
            chain += "[";

            switch (type) {
                case LIST:
                    for (T t : list)
                        chain += t + ", ";
                    break;
                case MAP:
                    for (Entry<T, T[]> t : map.entrySet()) {
                        table = t.getValue();
                        show(TABLE);
                    }
                    break;
                case TABLE:
                    for (T t : table)
                        chain += t + ", ";
                    break;
                case MATRIX:
                    for (T[] row : matrix) {
                        prn(Arrays.toString(row));
                    }
            }

            try {
                chain = chain.substring(0, chain.length() - 2);
                chain += "]";
                prn(chain);
            } catch (StringIndexOutOfBoundsException e) {
                // e.printStackTrace();
            }
        }
    }
}
