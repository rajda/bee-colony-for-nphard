package com.github.rajda;

import java.util.Arrays;

/**
 * Created by Jacek on 02.02.2016.
 */
public class SolutionI {//implements Cloneable {
    protected int[] solution;
    protected Fitness fitness;

    public SolutionI(int[] solution, Fitness fitness) {
        this.solution = solution;
        this.fitness = fitness;
    }
//
//    public SolutionI clone() {
//        try {
//            SolutionI cloneSolution = (SolutionI) super.clone();
//            cloneSolution.solution = solution.clone();
//            return cloneSolution;
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public Fitness getFitness() {
        return fitness;
    }

    public void setFitness(Fitness fitness) {
        this.fitness = fitness;
    }

    public int[] getSolution() {
        return solution;
    }

    public String toString() {
        return fitness.getValue() + Arrays.toString(solution);
    }
}

