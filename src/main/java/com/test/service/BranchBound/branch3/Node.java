package com.test.service.BranchBound.branch3;

import java.util.Arrays;

/**
 * A node of the state space tree
 */
class Node {
	// visited vertices
	int[] path;

	int reducedMatrix[][];

	int adj_len;

	// lower bound for all paths includeng the current path
	int cost;

	// current vertex
	int vertex;

	// number of visited vertices
	int level;

	/**
	 * A new node (i, j) corresponds to adding the edge from i
	 * to j to the existing parentPath.
	 */
	public Node(int[][] parentMatrix, int[] parentPath, int level, int i, int j) {

		path = Arrays.copyOf(parentPath, level+1);
		path[level] = j;
		vertex = j;
		this.level = level;
		adj_len = parentMatrix.length;

		reducedMatrix = new int[adj_len][];
		for (int k = 0; k < adj_len; k++) {
			reducedMatrix[k] = Arrays.copyOf(parentMatrix[k], adj_len);
		}

		// Remove no longer traversable edges
		if (level > 0) {
			for (int k = 0; k < adj_len; k++) {
				// remove outgoing edges of i
				reducedMatrix[i][k] = BranchBound.INF;
				// remove incoming edges to j
				reducedMatrix[k][j] = BranchBound.INF;
			}
		}

		//Set (j, 0) to infinity, start node is 0
		reducedMatrix[j][0] = BranchBound.INF;
	}

	public int getCost() {
		return cost;
	}

	/**
	 * Reduce each row such that it contains at least one zero vaule.
	 *
	 * @return Array with reduction values of each row.
	 */
	private int[] rowReduction() {
		final int[] row = new int[adj_len];
		Arrays.fill(row, BranchBound.INF);

		// row[i] contains minimum in row i
		for (int i = 0; i < adj_len; i++) {
			for (int j = 0; j < adj_len; j++) {
				if (reducedMatrix[i][j] < row[i]) {
					row[i] = reducedMatrix[i][j];
				}
			}
		}

		// reduce minimum value from each element of each row
		for (int i = 0; i < adj_len; i++) {
			for (int j = 0; j < adj_len; j++) {
				if (reducedMatrix[i][j] != BranchBound.INF
					&& row[i] != BranchBound.INF) {
					reducedMatrix[i][j] -= row[i];
				}
			}
		}
		return row;
	}

	/**
	 * Reduce each column such that it contains at least one zero vaule.
	 *
	 * @return Array with reduction values of each column.
	 */
	private int[] columnReduction() {
		final int[] col = new int[adj_len];
		Arrays.fill(col, BranchBound.INF);

		// col[j] contains minimum in col j
		for (int i = 0; i < adj_len; i++) {
			for (int j = 0; j < adj_len; j++) {
				if (reducedMatrix[i][j] < col[j]) {
					col[j] = reducedMatrix[i][j];
				}
			}
		}

		// reduce the minimum value from each element in each column
		for (int i = 0; i < adj_len; i++) {
			for (int j = 0; j < adj_len; j++) {
				if (reducedMatrix[i][j] != BranchBound.INF
					&& col[j] != BranchBound.INF) {
					reducedMatrix[i][j] -= col[j];
				}
			}
		}

		return col;
	}

	/**
	 * Calculate lower bound for costs of any path starting at
	 * current node. The lower bound is the sum of all row
	 * reductions.
	 */
	public int lowerBound() {
		final int[] row = rowReduction();
		final int[] col = columnReduction();
		int cost = 0;
		for (int i = 0; i < adj_len; i++) {
			if (row[i] != BranchBound.INF) {
				cost += row[i];
			}
			if (col[i] != BranchBound.INF) {
				cost += col[i];
			}
		}
		return cost;
	}

}
