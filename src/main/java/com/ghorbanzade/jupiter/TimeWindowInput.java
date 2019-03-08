//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Pejman Ghorbanzade <pejman@ghorbanzade.com>
// Released under the terms of MIT License
// https://github.com/ghorbanzade/jupiter/blob/master/LICENSE
//

package com.ghorbanzade.jupiter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 *
 *
 * @author Pejman Ghorbanzade
 */
public class TimeWindowInput implements CustomerInput {

  private Customer temp;
  private Customer prev;
  private Customer customer;
  private final long limitLow;
  private final long limitHigh;
  private final CustomerParser parser;
  private final List<Customer> candidates = new ArrayList<Customer>();
  private final List<Filter> filters = new ArrayList<Filter>();

  private static final Logger log = Logger.getLogger(JupiterMain.class);

  /**
   *
   *
   * @param parser
   * @param boundLow
   * @param boundHigh
   */
  public TimeWindowInput(
      CustomerParser parser,
      long boundLow,
      long boundHigh
  ) {
    this.parser = parser;
    this.customer = this.parser.next();
    this.limitLow = boundLow * 1000;
    this.limitHigh = boundHigh * 1000;
    this.candidates.add(customer);
    this.rebuild();
  }

  /**
   *
   */
  private void rebuild() {
    if (null != this.temp) {
      if (this.temp.getPickupTime() - this.customer.getPickupTime() < this.limitHigh) {
        this.candidates.add(this.temp);
        this.temp = null;
      } else {
        return;
      }
    }
    while (this.parser.hasNext()) {
      Customer candidate = this.parser.next();
      if (candidate.getPickupTime() - this.customer.getPickupTime() < this.limitHigh) {
        this.candidates.add(candidate);
      } else {
        this.temp = candidate;
        break;
      }
    }
    while (this.candidates.get(0).getPickupTime() + this.limitLow < this.customer.getPickupTime()) {
      this.candidates.remove(0);
    }
  }

  /**
   * Note that we cannot use parser.hasNext() here because parser may not
   * have additional records to read but local temp variable may contain
   * a customer.
   *
   * @return true if there is still a customer in parser
   */
  public boolean hasCustomer() {
    if (null != this.temp) {
      return true;
    }
    return (null != this.customer);
  }

  /**
   *
   *
   * @return a customer to be handled
   * @throws CustomerException
   */
  public Customer nextCustomer() throws CustomerException {
    try {
      this.prev = (Customer) this.customer.clone();
    } catch (CloneNotSupportedException ex) {
      throw new CustomerException("failed to clone customer");
    }
    this.customer = null;
    if (!this.candidates.isEmpty()) {
      if (this.candidates.contains(this.customer)) {
        int index = this.candidates.indexOf(this.customer);
        this.candidates.remove(index);
        this.customer = this.candidates.get(index);
      } else {
        this.customer = this.candidates.remove(0);
      }
      if (this.prev.getPickupTime() != this.customer.getPickupTime()) {
        this.rebuild();
      }
    }
    return prev;
  }

  /**
   *
   *
   * @return list of customers who may be able to share the ride with
   *         current customer
   */
  public List<Customer> getCandidates() {
    List<Customer> list = new ArrayList<Customer>();
    for (Customer candidate: this.candidates) {
      if (this.prev == candidate) {
        continue;
      }
      boolean shouldAdd = true;
      for (Filter filter: this.filters) {
        if (!filter.pass(this.prev, candidate)) {
          shouldAdd = false;
          break;
        }
      }
      if (shouldAdd) {
        list.add(candidate);
      }
    }
    return list;
  }

  /**
   * Registers a filter to be applied to list of candidates given for any
   * customer.
   *
   * @param filter the filter to be registered and applied to the list
   *        of candidates
   */
  public void addFilter(Filter filter) {
    this.filters.add(filter);
  }

  /**
   * Excludes a given customer from future consideration.
   *
   * @param candidate the customer to be excluded for future consideration
   */
  public void excludeCandidate(Customer candidate) {
    if (!this.candidates.remove(candidate)) {
      log.warn("asked to remove non-existant candidate");
    }
  }

}
