/*
 * Copyright (c) 2015 SUN XIMENG (Nathaniel). All rights reserved.
 */

package com.test.service.NearestInsertion;

/**
 * The {@code Pair} class represents a pair of cities as an logical "edge" of a graph
 * @author Nathaniel
 *
 */

public class Pair {

	private String a;
	private String b;

	/**
	 * @param a A city
	 * @param b Another city
	 */
	public Pair(String a, String b) {

		int compare = a.compareTo(b);
		if (compare < 0) {
			this.a = a;
			this.b = b;
		} else if (compare > 0) {
			this.a = b;
			this.b = a;
		} else {
			throw new RuntimeException("Identical city names : " + a);
		}
	}

	public Pair(String a, String b, int flag) {
		this.a = a;
		this.b = b;

	}
	/**
	 * The copy constructor. It creates a new object with the same contents as parameter p.
	 * @param p The {@code Pair} object to copy
	 */
	public Pair(Pair p){
		this.a = p.a;
		this.b = p.b;
	}
	
	/**
	 * Returns true if two {@code Pair} objects represent logically the same edge
	 * in this TSP
	 */
	@Override
	public boolean equals(Object o){
		if(o == this){
			return true;
		}
		else if(!(o instanceof Pair)){
			return false;
		}
		Pair p = (Pair)o;
		return p.a.equals(this.a) && p.b.equals(this.b);
	}
	
	public String getSmaller(){
		return this.a;
	}
	
	public String getLarger(){
		return this.b;
	}
	
	/**
	 * Any two {@code Pair}s that equal each other will get the same hash code
	 */
	@Override
	public int hashCode(){
		int h = 17;
		h = h * 31 + this.a.hashCode();
		h = h * 31 + this.b.hashCode();
		return h;
	}
	
	/**
	 * Get a formatted {@code String} that represents this {@code Pair}
	 */
	@Override
	public String toString(){
		return "{" + this.a + "," + this.b + "}";
	}


}
