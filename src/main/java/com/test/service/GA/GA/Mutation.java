package com.test.service.GA.GA;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Used for mutating the Chromosomes.
 */
class Mutation {

    /**
     * Class cannot be instantiated, as there would be no point, since all
     * the methods are static.
     */
    private Mutation () {}

    /**
     * Selects a city and inserts it into a random place.
     * @param chromosome    The Chromosome who's cities will be swapped.
     * @param random        The Random object used for randomly selecting the cities
     * @return              the mutated Chromosome
     */
    static Chromosome insertion (Chromosome chromosome, Random random) {
        City[] cities = chromosome.getArray();
        int randomIndex = random.nextInt(cities.length);
        int randomDestination = random.nextInt(cities.length);

        if (randomIndex < randomDestination) {
            City temp = cities[randomIndex];
            for (int i = randomIndex; i < randomDestination; i++) {
                cities[i] = cities[i+1];
            }
            cities[randomDestination] = temp;
        } else {
            City temp = cities[randomIndex];
            for (int i = randomIndex; i > randomDestination; i--) {
                cities[i] = cities[i-1];
            }
            cities[randomDestination] = temp;
        }
        Chromosome some = new Chromosome(cities);

        return some;

       // return threeOpt(some,random);
        //return optimize(some);
    }

    public static Chromosome optimize(Chromosome route)
    {
        float distance = route.getDistance();

        double delta = 0;
        for (int i = 1; i < route.getWaypointCount()-4; ++i)
        {
            for (int j = i+2; j < route.getWaypointCount()-2; ++j)
            {
                for (int k = j+2; k < route.getWaypointCount(); ++k)
                {
                    // Perform the 3 way swap and test the length
                    delta += reverse_segment_if_better(route.getArray(), i, j, k);
                    if (delta >= 0) {
                        break;
                    }
                }
            }
        }

        route.regetDistance();

        return route;
}



    public static Chromosome threeOpt(Chromosome chromosome, Random random) {
        City[] cities = chromosome.getArray();
        int length = cities.length;
        int i = 1 + random.nextInt(length - 5);
        int j = i + 2;
        int bound = length - i - 2 - 2;
        if (bound > 0) {
            j = i + 2 + random.nextInt(bound);
        }
       // int j = i + 2 + random.nextInt(length - i - 2 - 2);
        int k = j + 2;
        bound = length - j - 2;
        if (bound > 0) {
             k = j + 2 + random.nextInt(length - j - 2);
        }
        return getDifCity(chromosome, i, j, k, chromosome.getDistance());
    }

    private static double reverse_segment_if_better(City[] cities, int i, int j, int k) {
        City A = cities[i - 1];
        City B = cities[i];
        City C = cities[j - 1];
        City D = cities[j];
        City E = cities[k - 1];
        City F = cities[k % cities.length];

        double d0 = distance(A, B) + distance(C, D) + distance(E, F);
        double d1 = distance(A, C) + distance(B, D) + distance(E, F);
        double d2 = distance(A, B) + distance(C, E) + distance(D, F);
        double d3 = distance(A, D) + distance(E, B) + distance(C, F);
        double d4 = distance(F, B) + distance(C, D) + distance(E, A);

        City[] newCitys = null;
        double size = 0;
        if (d0 > d1) {
            newCitys = reversed(cities, i, j);
            size = -d0 + d1;
        }
       if( d0 > d2){
           newCitys = reversed(cities, j, k);
           size = -d0 + d2;
       }
       if(d0 > d4){
           newCitys = reversed(cities, i, k);
           size = -d0 + d4;
       }
        if (d0 > d3) {
            newCitys = rebuildCitys(cities, j, i);
            size = -d0 + d3;
        }
     //   System.out.println(size);

        return size;
    }


    public static Chromosome getDifCity(Chromosome chromosome, int i, int j, int k, double oldValue) {
        City[] cities = chromosome.getArray();
        List<City> list1 = getCityList(0, i, cities);
        List<City> list2 = getCityList(i, j, cities);
        List<City> list3 = getCityList(j, k, cities);
        List<City> list4 = getCityList(k, cities.length - 1, cities);
        List<List<City>> citys = new ArrayList<>();
        citys.add(list1);
        citys.add(list2);
        citys.add(list3);
        citys.add(list4);
        Chromosome temp = sortList(citys, oldValue);

        return temp == null ? chromosome : sortList(citys, oldValue);
    }


    public static Chromosome sortList(List<List<City>> cities, double oldValue) {
        double temp = oldValue;
        Chromosome chromosome = null;
        for (int i = 0; i < cities.size(); i++) {
            for (int i1 = i + 1; i1 < cities.size(); i1++) {
                for (int i2 = 0; i2 < cities.size(); i2++) {
                    if (i2 != i1 && i2 != i) {
                        for (int j = 1; j < 4; j++) {
                            if (j != i1 && j != i2 && j != i) {
                                Chromosome somew = makeUpList(cities.get(i),
                                        cities.get(i1), cities.get(i2), cities.get(j));
                                if (somew.getDistance() < temp) {
                                    chromosome = somew;
                                    temp = somew.getDistance();
                                  //  System.out.println(chromosome.toString());
                                }
                            }
                        }
                    }
                }
            }
        }
        return chromosome;

        /*List<City> citys = new ArrayList<>();
        Chromosome somew = makeUpList(A, B, C, D);
        for (int i = 0; i < 4; i++) {
            Chromosome somew = makeUpList(A, B, C, D);

        }
        if (somew.getDistance() < oldValue) {


        }*/

    }

    public static Chromosome makeUpList(List<City> A, List<City> B, List<City> C, List<City> D) {
        List<City> citys = new ArrayList<>();
        citys.addAll(A);
        citys.addAll(B);
        citys.addAll(C);
        citys.addAll(D);
        City[] datas = citys.toArray(new City[citys.size()]);
        return new Chromosome(datas);

    }



    /**
     * 返回城市集合
     * @param i
     * @param j
     * @param cities
     * @return
     */
    public  static List<City> getCityList(int i, int j,City[] cities) {
        List<City> list2 = new ArrayList<>();
        for (int l = i; l < j; l++) {
            list2.add(cities[l]);
        }
        if (cities.length - 1 == j && i != cities.length - 1) {
            list2.add(cities[cities.length - 1]);
        }

        return list2;
    }


    public static City[] reversed(City[] cities, int i, int j) {
        List<City> cities1 = new ArrayList<>();
        try {
            for (int k = i; k <= j; k++) {
                cities1.add(cities[k]);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        Collections.reverse(cities1);

        for (int k = i, z = 0; k <= j; k++, z++) {
            cities[k] = cities1.get(z);
        }

        return cities;
    }

    public static City[] rebuildCitys(City[] cities, int j,int i ) {
        List<City> cities1 = new ArrayList<>();
        for (int k = 0; k < i; k++) {
            cities1.add(cities[k]);
        }

        for (int z = j; z < cities.length; z++) {
            cities1.add(cities[z]);
        }

        for (int k = i; k < j; k++) {
            cities1.add(cities[k]);
        }

        return  cities1.toArray(new City[cities1.size()]);
    }


    private static double distance(City cityA, City cityB) {
        return Preset.diss[Integer.valueOf(cityA.getName())][Integer.valueOf(cityB.getName())];
    }



    private static City[] Swap(int i, int j, City[] tour) {
        int size = tour.length;
        City[] newerTour = new City[tour.length];

        for (int c = 0; c <= i - 1; c++) {
            newerTour[c] = tour[c];
        }
        int change = 0;
        for (int d = i; d <= j; d++) {
            newerTour[d] = tour[d - change];
            change++;
        }
        for (int e = j + 1; e < size; e++) {
            newerTour[e] = tour[e];
        }
        return newerTour;

    }

    /**
     * Swaps two randomly selected cities.
     * @param chromosome    The Chromosome who's cities will be swapped.
     * @param random        The Random object used for randomly selecting the cities
     * @return              the mutated Chromosome
     */
    static Chromosome reciprocalExchange (Chromosome chromosome, Random random) {
        City[] cities = chromosome.getArray();
        int l = cities.length;
        swap(cities, random.nextInt(l), random.nextInt(l));
        return new Chromosome(cities);
    }

    /**
     * Pick a subset of Cities and randomly re-arrange them.
     * @param chromosome    The Chromosome who's cities will be swapped.
     * @param random        The Random object used for randomly selecting the cities
     * @return              the mutated Chromosome
     */
    static Chromosome scrambleMutation (Chromosome chromosome, Random random) {

        /**
         * The subset Cities include wrapping.
         * Example: if there is a Chromosome with 10 cities and randomIndexStart is 8
         * and randomIndexEnd is 2, that means that the subset will include the cities
         * at indexes 8, 9, 1, and 2.
         */

        City[] cities = chromosome.getArray();
        int randomIndexStart = random.nextInt(cities.length);
        int randomIndexEnd = random.nextInt(cities.length);

        for (int i = randomIndexStart; i%cities.length != randomIndexEnd; i++) {
            int r = random.nextInt(Math.abs(i%cities.length - randomIndexEnd));
            swap(cities, i%cities.length, (i+r)%cities.length);
        }

        return new Chromosome(cities);
    }

    /**
     * Helper method for swapping two Cities in a Chromosome to change the tour.
     * @param array     the array of Cities to do the swap in
     * @param i         the index of the first City
     * @param j         the index of the second City
     */
    private static void swap (City[] array, int i, int j) {
        City temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

}


