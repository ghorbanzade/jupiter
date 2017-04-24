//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

import java.util.List;

/**
 *
 *
 * @author Mariam Shahrabifarahani
 */
public class RideCapacityFilter implements Filter {

  private static final Config cfg = ConfigManager.get("config/main.properties");

    int capacity;

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
     * @return
     */
    public boolean pass(Customer customer, Customer candidate) {
        int count1 = customer.countPassengers();
        int count2 = candidate.countPassengers();
        return (count1 + count2 < this.capacity);
    }

}
