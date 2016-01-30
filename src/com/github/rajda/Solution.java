package com.github.rajda;

import java.util.Arrays;

import static com.github.rajda.Helper.random;

public class Solution implements Cloneable {
    private Integer[] solution;
    private int solutionSize;
    private int fitnessValue;
    private int numberADM;
    private int numberOfMinPart;
    private int numberOfMaxPart;

    public Solution(Integer[] solution, int[] parameters) {
        this.solution = solution;
        this.solutionSize = solution.length;
        this.fitnessValue = parameters[0];
        this.numberOfMinPart = parameters[1];
        this.numberOfMaxPart = parameters[2];
        this.numberADM = parameters[3];
    }

    public Solution clone() {
        try {
            Solution cloneSolution = (Solution) super.clone();
            cloneSolution.solution = solution.clone();
            return cloneSolution;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setFitnessValue(int fitnessValue) {
        this.fitnessValue = fitnessValue;
    }

    public Integer getFitnessValue() {
        return fitnessValue;
    }

    public void setNumberOfMinPart(int numberOfMinPart) {
        this.numberOfMinPart = numberOfMinPart;
    }

    public void setNumberOfMaxPart(int numberOfMaxPart) {
        this.numberOfMaxPart = numberOfMaxPart;
    }

    public int getNumberOfMinPart() {
        return numberOfMinPart;
    }

    public int getNumberOfMaxPart() {
        return numberOfMaxPart;
    }

    public void setNumberOfADM(int numberADM) {
        this.numberADM = numberADM;
    }

    public int getNumberOfADM() {
        return numberADM;
    }

    public Integer[] getSolution() {
        return solution;
    }

    public int getRandomUserFromNotMinPartition() {
        int indexOfUser = random(0, solutionSize - 1);

        while (solution[indexOfUser] == getNumberOfMinPart()) {
            indexOfUser = random(0, solutionSize - 1);
        }
        return indexOfUser;
    }

    public int getRandomUser(int partition) {
        int indexOfUser = random(0, solutionSize - 1);

        while (solution[indexOfUser] != partition) {
            indexOfUser = random(0, solutionSize - 1);
        }
        return indexOfUser;
    }

    public String toString() {
        return getFitnessValue() + ", " + getNumberOfMinPart() + ", " + getNumberOfMaxPart() + " : " + Arrays.toString(solution);
    }
}
