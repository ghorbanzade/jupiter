//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

import org.apache.log4j.Logger;

import com.google.maps.model.LatLng;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author Pejman Ghorbanzade
 */
public final class Ride {

    private static final Logger log = Logger.getLogger(PassengerParser.class);
    private static final Config cfg = ConfigManager.get("config/main.properties");
    private final List<Passenger> passengers;

    /**
     * Creates a Ride object with an empty list of passengers.
     */
    public Ride() {
        this.passengers = new ArrayList<Passenger>(this.capacity());
    }

    /**
     * Creates a Ride object with the given list of passengers.
     *
     * @param passengers list of passengers to initialize the ride with.
     * @throws PassengerException if the number of given passengers are more
     *         than maximum allowed passengers in each ride.
     */
    public Ride(Passenger... passengers) throws PassengerException {
        this.passengers = Arrays.asList(passengers);
        if (this.capacity() < this.size()) {
            throw new PassengerException("number of passengers exceeds maximum ride capacity");
        }
    }

    /**
     *
     *
     * @param passengers
     * @throws PassengerException if the total number of passengers in the
     *         resulting list exceeds the maximum number of passengers allowed
     *         in each ride.
     * @return a new Ride object that contains passengers of this ride as well
     *         as the given list of passengers.
     */
    public Ride with(Passenger... passengers) throws PassengerException {
        Ride ride = new Ride(this.passengers.toArray(new Passenger[this.size()]));
        ride.add(passengers);
        return ride;
    }

    /**
     *
     *
     * @param passengers passengers to be added to the list of passengers
     *        in this ride.
     * @throws PassengerException if the total number of passengers exceeds the
     *         maximum number of passengers allowed in each ride.
     */
    public void add(Passenger... passengers) throws PassengerException {
        List<Passenger> list = Arrays.asList(passengers);
        if (this.capacity() < this.size() + list.size()) {
            throw new PassengerException("number of passengers exceeds maximum ride capacity");
        }
        this.passengers.addAll(list);
    }

    /**
     *
     *
     * @return maximum number of passengers allowed in each ride
     */
    public int capacity() {
        return cfg.getAsInt("ride.capacity");
    }

    /**
     *
     *
     * @return current number of passengers in this ride
     */
    public int size() {
        return this.passengers.size();
    }

}
