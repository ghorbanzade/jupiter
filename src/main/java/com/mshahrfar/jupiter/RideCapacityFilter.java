//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Pejman Ghorbanzade <pejman@ghorbanzade.com>
// Released under the terms of MIT License
// https://github.com/ghorbanzade/jupiter/blob/master/LICENSE
//

package com.ghorbanzade.jupiter;

/**
 *
 *
 * @author Mariam Shahrabifarahani
 */
public class RideCapacityFilter implements Filter {

    private static final Config cfg = ConfigManager.get("config/main.properties");

    private int capacity;

    /**
     *
     */
    public RideCapacityFilter() {
      this.capacity = cfg.getAsInt("ride.capacity");
    }

    /**
     *
     *
     * @param capacity
     */
    public RideCapacityFilter(int capacity) {
        this.capacity = capacity;
    }

    /**
     *
     *
     * @param customer
     * @param candidate
     * @return true if the number of passengers with the given customer
     *         together with number of passengers with the given candidate
     *         do not exceed the maximum number of passengers in each ride
     */
    public boolean pass(Customer customer, Customer candidate) {
        int count1 = customer.countPassengers();
        int count2 = candidate.countPassengers();
        return (count1 + count2 < this.capacity);
    }

}
