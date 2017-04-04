/**
 * 
 */
package com.mshahrfar.jupiter;

import java.util.List;

/**
 * @author mshah
 *
 */
public class VicinityFilter {
	private List<Customer> cands;
	private Customer customer;
	private double delta;
	/**
	 * 
	 */
	public VicinityFilter(Customer customer, List<Customer> candidates, double delta) {
		// TODO
		for (int i=0;i<candidates.size();i++){			
			if(distance(customer, candidates.get(i))>delta){
				candidates.remove(i);
			}
		}
		cands = candidates;
	}
	private double distance (Customer custA, Customer custB) {
		double lat1 = custA.getPickupLocation().lat;
		double lon1 = custA.getPickupLocation().lng;
		double lat2 = custB.getPickupLocation().lat;
		double lon2 = custB.getPickupLocation().lng;
		final int R = 6371; // Radius of the earth
		Double latDistance = Math.toRadians(lat2 - lat1);
	    Double lonDistance = Math.toRadians(lon2 - lon1);
	    Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c * 1000; // convert to meters
	    //double height = el1 - el2;
	    double height = 0;
	    distance = Math.pow(distance, 2) + Math.pow(height, 2);
	    return Math.sqrt(distance);
	}
}
