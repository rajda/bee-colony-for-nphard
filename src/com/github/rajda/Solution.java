package com.github.rajda;

import java.util.Arrays;

public class Solution implements Cloneable {
    protected int[] values;
    protected int valuesLength;
    protected Fitness fitness;

    public Solution(int[] values) {
        this.values = values;
        this.valuesLength = values.length;
    }

    public Solution clone() {
        try {
            Solution cloneSolution = (Solution) super.clone();
            cloneSolution.values = values.clone();
            return cloneSolution;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Fitness getFitness() {
        return fitness;
    }

    public void setFitness(Fitness fitness) {
        this.fitness = fitness;
    }

    public boolean betterThan(Solution comparedWith) {
        return this.getFitness().getValue() < comparedWith.getFitness().getValue();
    }

    public int getValueAt(int valueId) {
        return values[valueId];
    }

    public void setValueAt(int valueId, int newValue) {
        values[valueId] = newValue;
    }

    public String toString() {
        return fitness.getValue() + " " + Arrays.toString(values);
    }
}

