package com.test.service.BranchBound.branch3;

import java.util.LinkedList;
import java.util.List;

public class Data {

	public List<int[][]> completeGraphs;
	public int[][] collegeGraph;
	final static int INF = Integer.MAX_VALUE;

	public Data() {

		completeGraphs = new LinkedList<>();

		/* minimum cost: 3
		 */
		completeGraphs.add(new int[][] {
		    { INF,   0,   0,   0},
		    {   0, INF,   1, INF},
		    {   0,   1, INF,   2},
		    {   0, INF,   2, INF}
		});

		/* minimum cost: 1355?
		 * source: https://github-oce.ubs.net/JVI/cc2-polyglot
		 */
		completeGraphs.add(new int[][] {
		    { INF,   0,   0,   0,   0,   0,   0,   0},
		    {   0, INF, 412, INF, 316, 400, INF, INF},
		    {   0, 412, INF, 141, INF, INF, INF, INF},
		    {   0, INF, 141, INF, 223, INF, 219, 144},
		    {   0, 316, INF, 223, INF, INF, 142, INF},
		    {   0, 400, INF, INF, INF, INF, 200, INF},
		    {   0, INF, INF, 219, 142, 200, INF, 222},
		    {   0, INF, INF, 144, INF, INF, 222, INF}
		});

		/* minimum cost: 1355?
		 */
		completeGraphs.add(new int[][] {
		    { INF,   0,   0,   0,   0,   0,   0,   0},
		    {   0, INF, 412, INF, 316, 400, INF, INF},
		    {   0, 412, INF, 141, INF, INF, INF, INF},
		    {   0, INF, 141, INF, 223, INF, 219, 144},
		    {   0, 316, INF, 223, INF, INF, 142, INF},
		    {   0, 400, INF, INF, INF, INF, 200, INF},
		    {   0, INF, INF, 219, 142, 200, INF, INF},
		    {   0, INF, INF, 144, INF, INF, INF, INF}
		});

		/* minimum cost: does not exist
		 */
		completeGraphs.add(new int[][] {
		    { INF,   0,   0,   0,   0,   0,   0,   0},
		    {   0, INF, 412, INF, 316, 400, INF, INF},
		    {   0, 412, INF, 141, INF, INF, INF, INF},
		    {   0, INF, 141, INF, 223, INF, 219, INF},
		    {   0, 316, INF, 223, INF, INF, 142, INF},
		    {   0, 400, INF, INF, INF, INF, 200, INF},
		    {   0, INF, INF, 219, 142, 200, INF, INF},
		    {   0, INF, INF, INF, INF, INF, INF, INF}
		});

		/* minimum cost: 339
		 * minimal path: H-A-C-B-H
		 * source: http://www.ams.org/publicoutreach/feature-column/fcarc-tsp
		 */
		completeGraphs.add(new int[][] {
		    {INF,  80,  69,  50},
		    { 80, INF, 140, 100},
		    { 69, 140, INF,  90},
		    { 50, 100,  90, INF}
		});
		
		/* minimum cost: 80
		 * minimal path: 0-1-3-2-0
		 * source: https://www.geeksforgeeks.org/traveling-salesman-problem-using-branch-and-bound-2/
		 */
		completeGraphs.add(new int[][] {
		   { 0, 10, 15, 20},
		   {10,  0, 35, 25},
		   {15, 35,  0, 30},
		   {20, 25, 30,  0}
		});

		/* minimum cost: 29
		 * minimal path: 1-2-4-5-3-1
		 * source: http://www.mafy.lut.fi/study/DiscreteOpt/DOSLID5.pdf (Example 5.5)
		 */
		completeGraphs.add(new int[][] {
		   { 0,  8,  4,  9,  9},
		   { 8,  0,  6,  7, 10},
		   { 4,  6,  0,  5,  6},
		   { 9,  7,  5,  0,  4},
		   { 9, 10,  6,  4,  0}
		});

		/* minimum cost: 32
		 * minimal path: E-A-C-B-D-E
		 * source: Example 1 (http://www.jlmartin.faculty.ku.edu/courses/math105-F14/chapter6-part6.pdf)
		 */
		completeGraphs.add(new int[][] {
		   {  0, 12, 10, 19,  8},
		   { 12,  0,  3,  7,  2},
		   { 10,  3,  0,  6, 20},
		   { 19,  7,  6,  0,  4},
		   {  8,  2, 20,  4,  0}
		});

		/* minimum cost: 46
		 * minimal path: D-A-E-C-B-D
		 * source: Example 2 (http://www.jlmartin.faculty.ku.edu/courses/math105-F14/chapter6-part6.pdf)
		 */
		completeGraphs.add(new int[][] {
		   {  0, 14, 15,  4,  9},
		   { 14,  0, 18,  5, 13},
		   { 15, 18,  0, 19, 10},
		   {  4,  5, 19,  0, 12},
		   {  9, 13, 10, 12,  0}
		});

		/* minimum cost: 76
		 * minimal path: F-B-C-A-E-D-F
		 * source: Example 4 (http://www.jlmartin.faculty.ku.edu/courses/math105-F14/chapter6-part6.pdf)
		 */
		completeGraphs.add(new int[][] {
		   {  0, 12, 29, 22, 13, 24},
		   { 12,  0, 19,  3, 25,  6},
		   { 29, 19,  0, 21, 23, 28},
		   { 22,  3, 21,  0,  4,  5},
		   { 13, 25, 23,  4,  0, 16},
		   { 24,  6, 28,  5, 16,  0}
		});

		/* minimum cost: 64
		 * minimal path: A-C-B-D-A
		 * source: Example 5 (http://www.jlmartin.faculty.ku.edu/courses/math105-F14/chapter6-part6.pdf)
		 */
		completeGraphs.add(new int[][] {
		   {  0, 12, 14, 17},
		   { 12,  0, 15, 18},
		   { 14, 15,  0, 29},
		   { 17, 18, 29,  0}
		});

		/* minimal cost: 9275?
		 *
		 * source: https://www.mathematics.pitt.edu/sites/default/files/TSP.pdf
		 */
		collegeGraph = new int[][] {
			{    0,  648, 2625,  549, 2185, 1898, 1458, 1752, 1963,  427, 1743, 1817, 1899, 1060, 1148, 2084,  732, 1095, 1725, 2524},
			{  648,    0, 2363,  481, 2129, 2030, 1641, 1594, 1638,  557, 1214, 1492, 1710, 1126,  825, 1861,  811, 1195, 1375, 2262},
			{ 2625, 2362,    0, 1965,  669, 1274, 1541,  920,  744, 2172, 1623,  875,  720, 1595, 3085,  543, 3113, 1734, 1111,  103},
			{  549,  481, 1965,    0, 1667, 1605, 1194, 1132, 1242,  431,  963, 1096, 1280,  664, 1249, 1464, 1276,  799,  979, 1866},
			{ 2185, 2129,  669, 1667,    0,  621,  906,  541,  643, 1733, 1504,  733,  459, 1187, 2880,  479, 2791, 1169,  932,  566},
			{ 1898, 2030, 1274, 1605,  621,    0,  443,  662,  978, 1482, 1669,  925,  839, 1007, 2855,  929, 2541,  843, 1107, 1172},
			{ 1458, 1641, 1541, 1194,  906,  443,    0,  754, 1106, 1074, 1447,  976,  968,  638, 2477, 1148, 2132,  435, 1027, 1442},
			{ 1752, 1594,  920, 1132,  541,  662,  754,    0,  347, 1293, 1015,  261,  209,  724, 2319,  389, 2346,  841,  443,  818},
			{ 1963, 1638,  744, 1242,  643,  978, 1106,  347,    0, 1511,  961,  170,  183,  970, 2361,  287, 2389, 1124,  388,  688},
			{  427,  557, 2172,  431, 1733, 1482, 1074, 1293, 1511,    0, 1318, 1363, 1447,  585, 1378, 1631, 1063,  641, 1275, 2071},
			{ 1743, 1214, 1623,  963, 1504, 1669, 1447, 1015,  961, 1318,    0,  813, 1078,  918, 1571, 1182, 1882, 1147,  582, 1583},
			{ 1817, 1492,  875, 1096,  733,  925,  976,  261,  170, 1363,  813,    0,  271,  826, 2215,  373, 2242,  979,  241,  774},
			{ 1899, 1710,  720, 1280,  459,  839,  968,  209,  183, 1447, 1078,  271,    0,  882, 2453,  192, 2408, 1049,  504,  621},
			{ 1060, 1126, 1595,  664, 1187, 1007,  638,  724,  970,  585,  918,  826,  882,    0, 1891, 1066, 1667,  251,  798, 1495},
			{ 1148,  825, 3085, 1249, 2880, 2855, 2477, 2319, 2361, 1378, 1571, 2215, 2453, 1891,    0, 2583,  559, 2042, 2108, 2984},
			{ 2084, 1861,  543, 1464,  479,  929, 1148,  389,  287, 1631, 1182,  373,  192, 1066, 2583,    0, 2612, 1220,  611,  442},
			{  732,  811, 3113, 1276, 2791, 2541, 2132, 2346, 2389, 1063, 1882, 2242, 2408, 1667,  559, 2612,    0, 1701, 2125, 3012},
			{ 1095, 1195, 1734,  799, 1169,  843,  435,  841, 1124,  641, 1147,  979, 1049,  251, 2042, 1220, 1701,    0,  977, 1634},
			{ 1725, 1375, 1111,  979,  932, 1107, 1027,  443,  388, 1275,  582,  241,  504,  798, 2108,  611, 2125,  977,    0, 1011},
			{ 2524, 2262,  103, 1866,  566, 1172, 1442,  818,  688, 2071, 1583,  774,  621, 1495, 2984,  442, 3012, 1634, 1011,    0}
		};
	}
}
