/*
 * Copyright (c) 2015 SUN XIMENG (Nathaniel). All rights reserved.
 */

package com.test.service.NearestInsertion;

import java.util.*;

class NearestNeighborStrategy extends Strategy {

	private String start;
	
	protected NearestNeighborStrategy(RoadMap rm, String start) {
		super(rm);
		this.rm.checkCity(start);
		this.start = start;
	}
	
	private String findNearestNeighbor(String city, LinkedList<String> unvisited){
		double shortest = Double.MAX_VALUE;
		String nearest = null;
		for(String s : unvisited){
			double current = this.rm.getDistance(city, s);
			if(Double.compare(current, shortest) < 0){
				nearest = s;
				shortest = current;
			}
		}
		return nearest;
	}

	@Override
	public Tour solve() {
		Tour.Builder tb = new Tour.Builder(this.rm);
		List<String> myData = new ArrayList<>();
		myData.add(this.start);
		String current = this.start;

		LinkedList<String> unvisited = new LinkedList<String>(this.rm.getCitySet());
		unvisited.remove(current);

		while(!unvisited.isEmpty()){
			String nearest = this.findNearestNeighbor(current, unvisited);
			myData.add(nearest);
			tb.addPair(current, nearest);
			current = nearest;
			unvisited.remove(current);
		}
		tb.addPair(current, start);
		myData.add(start);
		String currentStr = "";
		double value = 0;
		for (String s : myData) {
			if (currentStr != "") {
				value += rm.getD(new Pair(currentStr, s, 1));
			}
			currentStr = s;

		}
		//System.out.println(value);
		tb.totalDist = value;
		return tb.build();
	}

}
