package com.test.service.GA.GA;


import com.test.model.UserAllocation;

import java.util.List;
import java.util.Random;

import static com.test.service.GA.GA.Population.fromDataSet;


public class Preset {

    private Preset() {}

    public static int[][] diss;

    public static GeneticAlgorithm getDefaultGA(List<UserAllocation> userAllocations, int[][] dis) {
        diss = dis;
        Random random = new Random();
        long seed = random.nextLong();
      // System.out.println("Seed: " + seed);
        Random r = new Random();
        r.setSeed(seed);

        // Parameters.
        int popSize = 500;      // Size of the population.
        int maxGen = 2000;      // Number of generations to run.
        double crossoverRate = 0.95;     // Odds that crossover will occur.
        double mutationRate = 0.55;     // Odds that mutation will occur.

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();

        geneticAlgorithm.setPopulation(fromDataSet(popSize, userAllocations, r));
        //geneticAlgorithm.setPopulation(Population.getRandomPopulation(100, popSize, r));
        geneticAlgorithm.setMaxGen(maxGen);
        geneticAlgorithm.setK(3); //Binary tournament
        geneticAlgorithm.setElitismValue(1);
        geneticAlgorithm.setCrossoverRate(crossoverRate);
        geneticAlgorithm.setMutationRate(mutationRate);
        geneticAlgorithm.setRandom(r);
        geneticAlgorithm.forceUniqueness(false);
        geneticAlgorithm.setLocalSearchRate(0.00);
        geneticAlgorithm.setCrossoverType(GeneticAlgorithm.CrossoverType.UNIFORM_ORDER);
        geneticAlgorithm.setMutationType(GeneticAlgorithm.MutationType.INSERTION);

        return geneticAlgorithm;
    }

}
