//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

/**
 *
 *
 * @author Pejman Ghorbanzade
 */
public class ClusterFilter implements Filter {

  private static final Config cfg = ConfigManager.get("config/main.properties");

  /**
   *
   */
  public ClusterFilter() {
    // intentionally left empty
  }

  /**
   *
   *
   * @param customer
   * @param candidate
   * @return
   */
  public boolean pass(Customer customer, Customer candidate) {
      return (customer.getClusterId() == candidate.getClusterId());
  }

}
