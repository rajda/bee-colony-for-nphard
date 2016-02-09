package com.github.rajda;

import java.util.ArrayList;

public interface Problem {
    void createInitialSolution();
    Solution optimize(Solution solution);
    Solution localSearchProbability(Solution currentSolution);
    Solution localSearchDiscover(Solution currentSolution);

    void putInPlace(Solution betterSolution, Solution worseSolution);
    Fitness countFitness(Solution solution);
    ArrayList<Solution> getSolutionsList();
}
