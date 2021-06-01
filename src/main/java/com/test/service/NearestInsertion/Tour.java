/*
 * Copyright (c) 2015 SUN XIMENG (Nathaniel). All rights reserved.
 */

package com.test.service.NearestInsertion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@code Tour} class represents a certain solution of a given TSP
 * @author Nathaniel
 *
 */
public class Tour {
	
	public static final class Builder{
		private HashSet<Pair> pairs = new HashSet<Pair>();
		private HashMap<String, Integer> count = new HashMap<String, Integer>();
		public HashSet<String> citiesInTour = new HashSet<String>();
		public double totalDist = 0;
		private RoadMap rm;
		
		/**
		 * The default constructor
		 * @param m A {@code RoadMap} object that this Tour belongs to
		 */
		public Builder(RoadMap m){
			this.rm = m;
			for(String s : m.getCitySet()){
				this.count.put(s, 0);
			}
		}
		
		/**
		 * Add an edge between a pair of cities to this {@code Builder}.
		 * Error check will be performed before this pair is added.
		 * The specific pair will be added if and only if: 
		 * 1) the {@code RoadMap} object contains both of the cities in the parameter list;
		 * 2) the number of edges incident on each of the cities is smaller than 2.
		 * @param a One of the cities in the pair
		 * @param b One of the cities in the pair
		 * @return The Builder object itself
		 */
		public Builder addPair(String a, String b){
			this.rm.checkCity(a);
			this.rm.checkCity(b);
			int aCount = this.count.get(a);
			int bCount = this.count.get(b);
			Pair p = new Pair(a, b);
			if(aCount < 2 && bCount < 2 && !this.pairs.contains(p)){
				this.pairs.add(p);
				this.count.put(a, aCount + 1);
				this.count.put(b, bCount + 1);
				this.totalDist += this.rm.getDistance(p);
				this.citiesInTour.add(a);
				this.citiesInTour.add(b);
			}
			return this;
		}
		
		/**
		 * Remove an edges between a pair of cities from this {@code Builder}.
		 * @param p Represents the edge in this {@code Builder}.
		 * Error check will be performed before this removal.
		 * The specific pair will be removed if and only if 
		 * the current {@code Builder} object contains such an edge.
		 * @return
		 */
		public Builder removePair(Pair p){
			if(this.pairs.contains(p)){
				this.pairs.remove(p);
				String a = p.getSmaller();
				String b = p.getLarger();
				int aCount = this.count.get(a) - 1;
				int bCount = this.count.get(b) - 1;
				this.count.put(a, aCount);
				this.count.put(b, bCount);
				if(aCount == 0){
					this.citiesInTour.remove(a);
				}
				if(bCount == 0){
					this.citiesInTour.remove(b);
				}
				this.totalDist -= this.rm.getDistance(p);
			}
			
			return this;
		}
		
		/**
		 * Get a Set view of the {@code Pair}s currently in this {@code Builder}.
		 * Modification of the returned Set will not affect the data in this {@code Builder}.
		 * @return The Set view
		 */
		public Set<Pair> getPairs(){
			return new HashSet<Pair>(this.pairs);
		}
		
		/**
		 * Get a Set view of the cities currently covered by this {@code Builder}.
		 * Modification of the returned Set will not affect the data in this {@code Builder}.
		 * @return The Set view
		 */
		public Set<String> getCities(){
			return new HashSet<String>(this.citiesInTour);
		}
		
		/**
		 * Check if the current {@code Builder} covers a city.
		 * @param city The city to check
		 * @return true if it covers; false if not.
		 */
		public boolean covers(String city){
			this.rm.checkCity(city);
			return this.citiesInTour.contains(city);
		}
		
		/**
		 * Check if the number of edges currently incident on this city equals to 2.
		 * @param city The city to check
		 * @return true if 2; false if not
		 */
		public boolean fullyCovers(String city){
			this.rm.checkCity(city);
			return this.count.get(city) == 2;
		}
		
		/**
		 * 
		 * @return number of {@code Pair}s currently in this {@code Builder}
		 */
		public int size(){
			return this.pairs.size();
		}
		
		/**
		 * Build a {@code Tour} object from the data currently contained in this {@code Builder}
		 * @return The {@code Tour} object
		 */
		public Tour build(){
			Tour t = new Tour();
			t.pairs.addAll(this.pairs);
			t.totalDist = this.totalDist;
			return t;
		}
	}	
	
	
	private HashSet<Pair> pairs;
	
	private double totalDist;
	
	private Tour(){
		this.pairs = new HashSet<Pair>();
	}
	
	/**
	 * Get the total distance of this {@code Tour}
	 * @return
	 */
	public double getTotalDistance(){
		return this.totalDist;
	}
	
	/**
	 * Get a formatted String view of this {@code Tour}
	 */
	@Override
	public String toString(){
		String s = this.pairs.toString();
		StringBuilder sb = new StringBuilder(s.length() + 25);
		sb.append(this.totalDist);
		sb.append(" : ");
		sb.append(s);
		return sb.toString();
	}
	
	/**
	 * Returns true if and only if the two {@code Tour}s specifies 
	 * logically the same solution in this TSP
	 */
	@Override
	public boolean equals(Object o){
		if(o == this){
			return true;
		}
		else if(!(o instanceof Tour)){
			return false;
		}
		Tour t = (Tour)o;
		return t.pairs.equals(this.pairs);
	}
	
	/**
	 * Any two {@code Tour} objects that equals each other will get the same hash code
	 */
	@Override
	public int hashCode(){
		return this.pairs.hashCode();
	}
}
