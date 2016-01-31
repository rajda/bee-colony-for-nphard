package com.github.rajda;

import java.util.Arrays;

import static com.github.rajda.Helper.random;

public class Solution implements Cloneable {
    private int[] solution;
    private int solutionSize;
    private IdpFitness idpFitness;

    public Solution(int[] solution, Fitness fitness) {
        this.solution = solution;
        this.solutionSize = solution.length;
        this.idpFitness = (IdpFitness) fitness;
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

    public int getFitnessValue() {
        return idpFitness.getValue();
    }

    public int getNumberOfMinPart() {
        return idpFitness.getMinPartitionNumber();
    }

    public int getNumberOfMaxPart() {
        return idpFitness.getMaxPartitionNumber();
    }

    public int getNumberOfADM() {
        return idpFitness.getAdmNumber();
    }

    public int[] getSolution() {
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

    public void setFitness(Fitness idpFitness) {
        this.idpFitness = (IdpFitness) idpFitness;
    }
}
