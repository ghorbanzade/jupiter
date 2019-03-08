//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Pejman Ghorbanzade <pejman@ghorbanzade.com>
// Released under the terms of MIT License
// https://github.com/ghorbanzade/jupiter/blob/master/LICENSE
//

package com.ghorbanzade.jupiter;

import com.google.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 *
 *
 * @author Pejman Ghorbanzade
 */
public final class Customer implements Cloneable {

  private static final Logger log = Logger.getLogger(CustomerParser.class);

  private Map<String, Object> info = new HashMap<String, Object>();

  /**
   *
   *
   * @param id
   */
  public Customer(long id) {
    this.set("customer_id", id);
  }

  /**
   *
   *
   * @param key
   * @param value
   */
  public void set(String key, Object value) {
    this.info.put(key, value);
  }

  /**
   *
   *
   * @return a LatLng object representing pickup location
   *         in latitude and longitude
   */
  public LatLng getPickupLocation() {
    return (LatLng) info.get("pickup_location");
  }

  /**
   *
   *
   * @return a LatLng object representing dropoff location
   *         in latitude and longitude
   */
  public LatLng getDropoffLocation() {
    return (LatLng) info.get("dropoff_location");
  }

  /**
   *
   *
   * @return the unix time in milliseconds when customer is picked-up
   */
  public long getPickupTime() {
    return (long) info.get("pickup_time");
  }

  /**
   *
   *
   * @return a record number assigned to customer entry in the database
   */
  public long getId() {
    return (long) info.get("customer_id");
  }

  /**
   *
   *
   * @return number of milliseconds it takes for this customer to get
   *         to his destination using NYC cabs
   */
  public long getIndividualRideDuration() {
    return (long) info.get("dropoff_time") - (long) info.get("pickup_time");
  }

  /**
   *
   *
   * @return number of passengers reserved by this customer
   */
  public int countPassengers() {
    return (int) info.get("passenger_count");
  }

  /**
   *
   *
   * @return a description of this customer
   */
  @Override
  public String toString() {
    return String.format("%d", this.getId());
  }

  /**
   *
   *
   * @param obj
   * @return true if this customer has the same id as the given customer
   *         object and false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Customer) {
      Customer candidate = (Customer) obj;
      return (this.getId() == candidate.getId());
    }
    return false;
  }

  /**
   *
   *
   * @return an object identical to this customer
   * @throws CloneNotSupportedException if we fail to clone this customer
   */
  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}
