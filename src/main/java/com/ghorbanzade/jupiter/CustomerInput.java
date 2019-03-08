//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Pejman Ghorbanzade <pejman@ghorbanzade.com>
// Released under the terms of MIT License
// https://github.com/ghorbanzade/jupiter/blob/master/LICENSE
//

package com.ghorbanzade.jupiter;

import java.util.List;

/**
 *
 * @author Mariam Shahrabifarahanai
 */
public interface CustomerInput {

  /**
   *
   *
   * @return true if input has a customer that has not been handled yet and
   *         false otherwise
   */
  public boolean hasCustomer();

  /**
   *
   *
   * @return
   */
  public Customer nextCustomer();

  /**
   *
   *
   * @return
   */
  public List<Customer> getCandidates();

  /**
   *
   *
   * @param filter
   */
  public void addFilter(Filter filter);

  /**
   *
   *
   * @param candidate
   */
  public void excludeCandidate(Customer candidate);

}
