/*
 * Copyright (c) 2015 SUN XIMENG (Nathaniel). All rights reserved.
 */

package com.test.service.NearestInsertion;

/**
 * The {@code Strategy} class represent a specific strategy 
 * to solve a given TSP problem
 * @author Nathaniel
 */
public abstract class Strategy {
	
	
	
	/**
	 * A {@code RoadMap} object that this strategy works on
	 */
	protected RoadMap rm;

	/**
	 * The only constructor.
	 * @param rm A {@code Strategy} object to be directly assigned to the RoadMap rm attribute
	 */
	public Strategy(RoadMap rm) {
		this.rm = rm;
	}
	
	/**
	 * Every child class must provide an implementation to solve this TSP problem
	 * @return A {@code Tour} object that represents the solution of this strategy
	 */
	public abstract Tour solve();
	
	/**
	 * Get a built-in strategy to solve this TSP using brute force
	 * @param rm A {@code RoadMap} object that this strategy works on
	 * @return A {@code Tour} object that represents the solution of this strategy
	 */
	public static Strategy bruteForce(RoadMap rm){
		return new BruteForceStrategy(rm);
	}
	
	/**
	 * Get a built-in strategy to solve this TSP using Nearest Neighbor Heuristic
	 * @param rm A {@code RoadMap} object that this strategy works on
	 * @param start The city this strategy starts with
	 * @return A {@code Tour} object that represents the solution of this strategy
	 */
	public static Strategy nearestNeighbor(RoadMap rm, String start){
		return new NearestNeighborStrategy(rm, start);
	}
	
	/**
	 * Get a built-in strategy to solve this TSP using Farthest Insertion Heuristic
	 * @param rm  A {@code RoadMap} object that this strategy works on
	 * @param a One of the three cities to form a triangle that this strategy starts with
	 * @param b One of the three cities to form a triangle that this strategy starts with
	 * @param c One of the three cities to form a triangle that this strategy starts with
	 * @return A {@code Tour} object that represents the solution of this strategy
	 */
	public static Strategy farthestInsertion(RoadMap rm, String a, String b, String c){
		return new FarthestInsertionStrategy(rm, a, b, c);
	}

}
