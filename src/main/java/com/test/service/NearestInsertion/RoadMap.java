/*
 * Copyright (c) 2015 SUN XIMENG (Nathaniel). All rights reserved.
 */

package com.test.service.NearestInsertion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * The {@code RoadMap} class represents the logical map in a TSP. 
 * Cities are represented by {@code String}s, and distances are 
 * represented by {@code double}s
 * @author nathaniel
 *
 */
public class RoadMap {
	
	private HashSet<String> cities;
	
	private HashMap<Pair, Integer> map;
	
	/**
	 * Construct from distance matrices. Error checking will be performed before 
	 * an object is created. A {@code RuntimeException} will be thrown if
	 * any of the parameters is illegal.
	 * @param cities Names of cities in this map
	 * @param dists The distance matrix, following the order of cities
	 */
	public RoadMap(String[] cities, int[][] dists){
		if(cities.length != dists.length || cities.length != dists[dists.length - 1].length){
			throw new RuntimeException("Illegal distance matrix dimensions");
		}
		this.initialize();
		this.cities.addAll(Arrays.asList(cities));
		if(this.cities.size() != cities.length){
			throw new RuntimeException("Duplicates found in names of cities");
		}
		for (int i = 0; i < cities.length; i++) {
			for (int i1 = 0; i1 < cities.length; i1++) {
				this.map.put(new Pair(cities[i], cities[i1],1), dists[i][i1]);
			}
		}

		/*for(int i = 0; i < dists.length - 1; ++i){
			for(int j = i + 1; j < dists.length; ++j){
				this.map.put(new Pair(cities[i], cities[j],1), dists[i][j]);
			}
		}*/
	}
	
	/**
	 * Construct from a mapping between {@code Pair}s of cities and distances
	 * @param cities Names of cities in this map
	 * @param map The pair-distance map
	 */
	public RoadMap(String[] cities, Map<Pair, Integer> map){
		if(cities.length * (cities.length - 1) / 2 != map.size()){
			throw new RuntimeException("Illegal number of pairs");
		}
		this.initialize();
		this.cities.addAll(Arrays.asList(cities));
		if(this.cities.size() != cities.length){
			throw new RuntimeException("Duplicates found in names of cities");
		}
		for(Pair p : map.keySet()){
			if(!this.cities.contains(p.getSmaller()) || !this.cities.contains(p.getLarger())){
				throw new RuntimeException("Illegal pair : " + p);
			}
		}
		this.map.putAll(map);
		
	}
	
	protected void checkCity(String s){
		if(!this.hasCity(s)){
			throw new RuntimeException("This map does not contain such a city: \"" + s + "\"");
		}
	}
	
	/**
	 * Get an array view of the cities in this {@code RoadMap}
	 * @return The {@code String[]}
	 */
	public String[] getCities(){
		return this.cities.toArray(new String[this.cities.size()]);
	}
	
	protected HashSet<String> getCitySet(){
		return this.cities;
	}
	
	/**
	 * Get the distance between a {@code Pair} of cities. 
	 * {@code RuntimeException} will be thrown if the {@code Pair} 
	 * is not contained in this {@code RoadMap}
	 * @param p The {@code Pair} of cities
	 * @return The distance in between
	 */
	public double getDistance(Pair p){
		this.checkCity(p.getSmaller());
		this.checkCity(p.getLarger());
		return this.map.get(p);
	}

	public double getD(Pair pair) {
		return this.map.get(pair);

	}
	
	/**
	 * Get the distance between two cities.
	 * {@code RuntimeException} will be thrown if any of the two
	 * parameters is not contained in this {@code RoadMap}
	 * @param a The name of a city
	 * @param b The name of a city
	 * @return The distance in between
	 */
	public double getDistance(String a, String b){
		this.checkCity(a);
		this.checkCity(b);
		return this.map.get(new Pair(a, b, 1));
	}
	
	/**
	 * Check if this {@code RoadMap} contains a city of the given name
	 * @param city The name to check
	 * @return true if it contains; false if not
	 */
	public boolean hasCity(String city){
		return this.cities.contains(city);
	}
	
	private void initialize(){
		this.cities = new HashSet<String>();
		this.map = new HashMap<Pair, Integer>();
	}
	
	/**
	 * Get the number of cities in this {@code RoadMap}
	 * @return The number of cities in this {@code RoadMap}
	 */
	public int size(){
		return this.cities.size();
	}
	
		

}
