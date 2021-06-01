package com.test.service.BranchBound.branch3;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Solve Traveling Salesman problem using branch and bound
 *
 * https://www.techiedelight.com/travelling-salesman-problem-using-branch-and-bound/
 *
 * Further reading:
 * - [AN ALGORITHM FOR THE TRAVELING SALESMAN PROBLEM](http://dspace.mit.edu/bitstream/handle/1721.1/46828/algorithmfortrav00litt.pdf)
 * - [SOLVING THE TRAVELLING SALESMAN PROBLEM USING THE BRANCH AND BOUND METHOD](https://hrcak.srce.hr/file/236378)
 *
 */
public class BranchBound {

    public final static int INF = Integer.MAX_VALUE;
    public final static int NO_VERTEX = -1;
    private final static int FIRST_VERTEX = 0;

    // final shortest path
    public int[] best_path;
    // cost of the shortest path
    public int best_cost;

    // upper bound for finding paths
    public int max_cost;

    // adjacancy matrix of a complete graph
    private int[][] adjacancyMatrix;
    // length of adjacancy matrix
    private int adj_len;

    public BranchBound(int[][] adjacancyMatrix) {
        this(adjacancyMatrix, INF);
    }

    public BranchBound(int[][] adjacancyMatrix, int max_cost) {
        this.adjacancyMatrix = adjacancyMatrix;
        this.adj_len = adjacancyMatrix.length;
        this.max_cost = max_cost;
    }

    public static   void main1(int[][] adj) {

        // // cost 19?
        // int[][] adj = {
        //  {INF, 5,    INF, 6, 5,  4},
        //  {5, INF, 2, 4,  3,  INF},
        //  {INF, 2,    INF, 1, INF, INF},
        //  {6, 4,  1,  INF, 7, INF},
        //  {5, 3,  INF, 7, INF, 3},
        //  {4, INF, INF, INF, 3,   INF}
        // };

        // cost 34
//        int[][] adj = {
//            { INF, 10, 8,   9,  7 },
//            { 10, INF, 10, 5,   6 },
//            { 8,    10, INF, 8, 9 },
//            { 9,    5,  8,  INF, 6 },
//            { 7,    6,  9,  6,  INF }
//        };

        // // cost 16
        // int[][] adj = {
        //  {INF, 3,    1,  5,  8},
        //  {3, INF, 6, 7,  9},
        //  {1, 6,  INF, 4, 2},
        //  {5, 7,  4,  INF, 3},
        //  {8, 9,  2,  3,  INF}
        // };

        // // cost 8
        // int[][] adj = {
        //  {INF, 2,    1,  INF},
        //  {2, INF, 4, 3},
        //  {1, 4,  INF, 2},
        //  {INF, 3,    2,  INF}
        // };


        // // cost 12
        // int[][] adj = {
        //  {INF, 5,    4,  3},
        //  {3, INF, 8, 2},
        //  {5, 3,  INF, 9},
        //  {6, 4,  3,  INF}
        // };

        // // cost 8
        // int[][] adj = {
        //  {INF, 2,    1,  INF},
        //  {2, INF, 4, 3},
        //  {1, 4,  INF, 2},
        //  {INF, 3,    2,  INF}
        // };

        BranchBound tsp = new BranchBound(adj);
        tsp.solve();

        System.out.println("Cost: "+tsp.best_cost);
        System.out.println("Path: "+Arrays.toString(tsp.best_path));
    }

    // Function to solve Traveling Salesman Problem using Branch and Bound
    public void solve() {

        // Create a priority queue to store live nodes of search tree
        final PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparing(Node::getCost));

        // create a root node and calculate its cost;
        // path starts from first vertex
        final Node root = new Node(adjacancyMatrix, new int[]{}, 0, NO_VERTEX, FIRST_VERTEX);

        // for (int[] row:root.reducedMatrix) {
        //  System.out.println(Arrays.toString(row).toString());
        // }

        // get the lower bound of the path starting at node 0
        root.cost = root.lowerBound();

        // Add root to list of live nodes;
        queue.add(root);

        // Finds a live node with least cost, add its children to list of
        // live nodes and finally deletes it from the list
        while (!queue.isEmpty()) {

            // Find a live node with least estimated cost
            final Node min = queue.poll();

            // i stores current city number
            final int i = min.vertex;

            // not all vertices have been visited yet
            if (min.level == adj_len-1) {
                best_path = Arrays.copyOf(min.path, adj_len+1);
                // return to first vertex
                best_path[adj_len] = FIRST_VERTEX;
                best_cost = min.cost;
                return;
            }

            // do for each child of min (i, j) forms an edge in space tree
            for (int j = 0; j < adj_len; j++) {
                if (min.reducedMatrix[i][j] != INF) {
                    // create a child node and calculate its cost
                    final Node child = new Node(min.reducedMatrix, min.path, min.level + 1, i, j);

                    /* child cost =
                     *   cost of parent node +
                     *   cost of the edge(i, j) +
                     *   lower bound of the path starting at node j
                     */
                    child.cost = min.cost + min.reducedMatrix[i][j]
                               + child.lowerBound();

                    if (child.cost < max_cost) {
                        queue.add(child);
                    }
                }
            }
        }
    }
}