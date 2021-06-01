package com.test.service.BranchBound.branch1;

import com.test.model.UserAllocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Solves the traveling salesman problem using Branch and Bound by utilizing Node's
 */
public class Solver {
	int[][] distances;
	double best_cost;
	int[] best_path;
	List<UserAllocation> cities;
	public static int startIndex = 0;

	/**
	 * Constructs a new Solver and initializes distances array
	 *
	 * @param cities An ArrayList of City's
	 */
	public Solver(List<UserAllocation> cities, int[][] distances) {
		this.cities = cities;
		this.distances = distances;
	}

	/**
	 * Calculates the shortest (non-repeating) path between a series of nodes
	 *
	 * @return An array with the locations of the best path
	 */
	public int[] calculate() {
		HashSet<Integer> location_set = new HashSet<Integer>(distances.length);
		for(int i = 0; i < distances.length; i++)
			location_set.add(i);

		best_cost = findGreedyCost(0, location_set, distances);
		System.out.println("初始分支：" + best_cost);

		//UserAllocation city = cities.get(0);
		   for (UserAllocation city : cities) {
			int[] active_set = new int[distances.length];
			for(int i = 0; i < active_set.length; i++)
				active_set[i] = i;
			startIndex = city.getAreaInnerId();
			Node root = new Node(null, 0, distances, active_set, city.getAreaInnerId());
			traverse(root);
	 	  }
		int paths = 0;

		for (int i = 0; i < best_path.length-1; i++) {
			paths += distances[best_path[i]][best_path[i + 1]];
		}
		paths += distances[best_path[best_path.length - 1]][best_path[0]];

		best_cost = paths;
		System.out.println("到最内层的数量：" + index);
		index = 0;
		return best_path;
	}

	/**
	 * Get current path cost
	 *
	 * @return The cost
	 */
	public double getCost() {
		return best_cost;
	}

	/**
	 * Find the greedy cost for a set of locations
	 *
	 * @param i The current location
	 * @param location_set Set of all remaining locations
	 * @param distances The 2D array containing point distances
	 * @return The greedy cost
	 */
	private double findGreedyCost(int i, HashSet<Integer> location_set, int[][] distances) {
		if(location_set.isEmpty())
			return distances[0][i];

		location_set.remove(i);
		//System.out.println(i);

		double lowest = location_set.isEmpty() ? 0 : Double.MAX_VALUE;
		int closest = location_set.isEmpty() ? i : 0;
		for(int location : location_set) {
			double cost = distances[i][location];
			if(cost < lowest) {
				lowest = cost;
				closest = location;
			}
		}

		double value = lowest + findGreedyCost(closest, location_set, distances);
		//System.out.println(value);
		return value;
	}

	private  static  int index = 0; //记录几次到最内层

	/**
	 * Recursive method to go through the tree finding and pruning paths
	 *
	 * @param parent The root/parent node
	 */
	private void traverse(Node parent) {
		Node[] children = parent.generateChildren();

		for(Node child : children) {
			if (child.isTerminal2()) { //16ce
				index ++;
			}
			if(child.isTerminal()) {
				double cost = child.getPathCost();

				if(cost < best_cost) {
					best_cost = cost;
					best_path = child.getPath();
				}
			}
			else if(child.getLowerBound() <= best_cost) {
				traverse(child);
			}
		}
	}
}
