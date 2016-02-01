package com.github.rajda;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Jacek on 01.02.2016.
 */
public class ProblemData {
    private ProblemInitData problemInitData;
    private Double[][] demands;
    private ArrayList<Double> demandsAsList;
    private Integer[] positionsOfNonzeroDemands;
    private LinkedHashMap<Integer, Integer[]> customersAssignedToLink;
    private ArrayList<Solution> solutionsObjectsList;

    public ProblemData(ProblemInitData problemInitData) {
        this.problemInitData = problemInitData;
        this.demands = new Double[problemInitData.getCustomersNumber()][problemInitData.getCustomersNumber()];
        this.demandsAsList = new ArrayList<>();
        this.positionsOfNonzeroDemands = new Integer[problemInitData.getLinksNumber()];
        this.customersAssignedToLink = new LinkedHashMap<>();
        this.solutionsObjectsList = new ArrayList<>();
    }

    public ProblemInitData getProblemInitData() {
        return problemInitData;
    }

    public Double[][] getDemands() {
        return demands;
    }

    public ArrayList<Double> getDemandsAsList() {
        return demandsAsList;
    }

    public Integer[] getPositionsOfNonzeroDemands() {
        return positionsOfNonzeroDemands;
    }

    public LinkedHashMap<Integer, Integer[]> getCustomersAssignedToLink() {
        return customersAssignedToLink;
    }

    public ArrayList<Solution> getSolutionsObjectsList() {
        return solutionsObjectsList;
    }
}
