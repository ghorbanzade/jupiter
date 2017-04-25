//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

import org.apache.log4j.Logger;

import com.google.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

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
    public Customer(int id) {
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
     * @return the unix time in seconds when customer is picked-up
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
        return (int) info.get("customer_id");
    }

    /**
     *
     *
     * @return number of seconds it takes for this customer to get
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
     * @return
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
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException{
      return super.clone();
    }

}
