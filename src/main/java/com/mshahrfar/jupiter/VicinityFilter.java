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
public class VicinityFilter implements Filter {

  private static final Config cfg = ConfigManager.get("config/main.properties");

    int vicinity;

  /**
   *
   */
  public VicinityFilter() {
    this.vicinity = cfg.getAsInt("vicinity");
  }

  /**
   *
   *
   * @param vicinity
   */
    public VicinityFilter(int vicinity) {
        this.vicinity = vicinity;
    }

    /**
     *
     *
     * @param customer
     * @param candidate
     * @return
     */
    public boolean pass(Customer customer, Customer candidate) {
        Distance dist = new Distance(
            customer.getPickupLocation(),
            candidate.getPickupLocation()
        );
        return (dist.getShortestPath() < this.vicinity);
    }

}
