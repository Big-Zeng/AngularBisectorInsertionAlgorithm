/*
 * Copyright (c) 2015 SUN XIMENG (Nathaniel). All rights reserved.
 */

package com.test.service.NearestInsertion;


class FarthestInsertionStrategy extends Strategy {
	
	private String a;
	private String b;
	private String c;

	protected FarthestInsertionStrategy(RoadMap rm, String a, String b, String c) {
		super(rm);
		rm.checkCity(a);
		rm.checkCity(b);
		rm.checkCity(c);
		if(a.equals(b) || a.equals(c) || b.equals(c)){
			throw new RuntimeException(a + ", " + b + ", " + c + " cannot form a triangle");
		}
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	private double distanceFrom(String city, Tour.Builder tb){
		double max = 0;
		for(String s : tb.getCities()){
			double current = this.rm.getDistance(city, s);
			if(current > max){
				max = current;
			}
		}
		return max;
	}
	
	private String findFarthestCity(Tour.Builder tb){
		String farthest = "";
		double maxDist = 0;
		for(String city : this.rm.getCitySet()){
			if(!tb.covers(city)){
				double currentDist = this.distanceFrom(city, tb);
				if(currentDist > maxDist){
					maxDist = currentDist;
					farthest = city;
				}
			}
		}
		return farthest;
	}
	
	private void insertCity(String city, Tour.Builder tb){
		Pair target = null;
		double minIncr = Double.MAX_VALUE;
		for(Pair p : tb.getPairs()){
			String a = p.getSmaller();
			String b = p.getLarger();
			double incr = this.rm.getDistance(city, a) + this.rm.getDistance(city, b) - this.rm.getDistance(p);
			if(Double.compare(incr, minIncr) < 0){
				target = p;
				minIncr = incr;
			}
		}
		tb.removePair(target);
		tb.addPair(target.getSmaller(), city);
		tb.addPair(target.getLarger(), city);
	}

	@Override
	public Tour solve() {
		Tour.Builder tb  = new Tour.Builder(this.rm);
		tb.addPair(this.a, this.b);
		tb.addPair(this.a, this.c);
		tb.addPair(this.b, this.c);
		while(tb.size() < this.rm.size()){
			String farthest = this.findFarthestCity(tb);
			this.insertCity(farthest, tb);
		}
		return tb.build();
	}
	


}
