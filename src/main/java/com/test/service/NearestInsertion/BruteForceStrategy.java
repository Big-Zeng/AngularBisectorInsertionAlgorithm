/*
 * Copyright (c) 2015 SUN XIMENG (Nathaniel). All rights reserved.
 */

package com.test.service.NearestInsertion;

import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

class BruteForceStrategy extends Strategy {

	protected BruteForceStrategy(RoadMap rm) {
		super(rm);
	}
	
	private static final Comparator<Tour> DIST_CMP = new Comparator<Tour>(){

		@Override
		public int compare(Tour a, Tour b) {
			return Double.compare(a.getTotalDistance(), b.getTotalDistance());
		}
		
	};
	
	private static String[] addToPrefix(String[] prefix, String s){
		String[] result = new String[prefix.length + 1];
		for(int i = 0; i < prefix.length; ++i){
			result[i] = prefix[i];
		}
		result[result.length - 1] = s;
		
		return result;
	}
	
	private static String[] getRemaining(String[] array, int avoid){
		String[] result = new String[array.length - 1];
		for(int i = 0, j = 0; i < array.length; ++i){
			if(i != avoid){
				result[j] = array[i];
				++j;
			}
		}
		return result;
	}

	
	private void findAll(TreeSet<Tour> tours, String[] prefix, String[] remaining){
		if(remaining.length == 1){
			String[] all = addToPrefix(prefix, remaining[0]);
			Tour.Builder tb = new Tour.Builder(this.rm);
			for(int i = 0; i < all.length - 1; ++i){
				tb.addPair(all[i], all[i + 1]);
				tb.addPair(all[0], all[all.length - 1]);
			}
			Tour t = tb.build();
			tours.add(t);
		}
		else{
			for(int i = 0; i < remaining.length; ++i){
				findAll(tours, addToPrefix(prefix, remaining[i]), getRemaining(remaining, i));
			}
		}
	}
	

	@Override
	public Tour solve() {
		TreeSet<Tour> all = new TreeSet<Tour>(DIST_CMP);
		String[] prefix = new String[1];
		String[] cities = this.rm.getCities();
		prefix[0] = cities[0];
		this.findAll(all, prefix, Arrays.copyOfRange(cities, 1, cities.length));
		return all.first();
	}

}
