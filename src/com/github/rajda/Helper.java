package com.github.rajda;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

public class Helper {
    public static int getCeilFromDouble(double value) {
        return (int) Math.ceil(value);
    }

    /**
     * Random value from the range except referenced value
     *
     * @param low - the lowest
     * @param high - the highest
     * @param referenceValue - undesirable output
     * @return value inclusive within boundary except referenceValue
     */
    public static int random(int low, int high, int referenceValue) {
        int randomValue;
        do {
            randomValue = random(low, high);
        } while (randomValue == referenceValue);
        return randomValue;
    }

    /**
     * Random value from the range
     *
     * @param low - the lowest
     * @param high - the highest
     * @return value inclusive within boundary
     */
    public static int random(int low, int high) {
        return ThreadLocalRandom.current().nextInt(low, high + 1);
    }

    public static void showSolutionsList(ArrayList<Solution> solutionsObjectsList) {
        Collections.sort(solutionsObjectsList, (o1, o2) -> Integer.compare(o1.getFitness().getValue(), o2.getFitness().getValue()));
        solutionsObjectsList.forEach(Helper::prn);
        prn();
    }

    public static void prn(Object obj) {
        System.out.println(obj);
    }

    public static void prn() {
        System.out.println();
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
