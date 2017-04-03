//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

import org.apache.log4j.Logger;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author Pejman Ghorbanzade
 */
public final class Ride {

    private static final Logger log = Logger.getLogger(CustomerParser.class);
    private static final Config cfg = ConfigManager.get("config/main.properties");
    private static final GeoApiContext context = new GeoApiContext().setApiKey(
        cfg.getAsString("google.maps.api.key")
    );
    private final List<Customer> customers;
    private final Map<String, Long> info = new HashMap<String, Long>();

    /**
     * Creates a Ride object with an empty list of customers.
     */
    public Ride() {
        this.customers = new ArrayList<Customer>(this.capacity());
    }

    /**
     * Creates a Ride object with the given list of customers.
     *
     * @param customers list of customers to initialize the ride with.
     * @throws CustomerException if the number of given passengers are more
     *         than maximum allowed passengers in each ride.
     */
    public Ride(Customer... customers) throws CustomerException {
        this.customers = new ArrayList<Customer>(Arrays.asList(customers));
        if (this.capacity() < this.size()) {
            throw new CustomerException("number of passengers exceeds maximum ride capacity");
        }
    }

    /**
     *
     *
     * @param customers
     * @throws CustomerException if the total number of passengers in this
     *         ride exceeds the maximum number of passengers allowed
     *         in each ride.
     * @return a new Ride object that contains customers of this ride as well
     *         as the given list of customers.
     */
    public Ride with(Customer... customers) throws CustomerException {
        Ride ride = new Ride(this.customers.toArray(new Customer[this.size()]));
        ride.add(customers);
        return ride;
    }

    /**
     *
     *
     * @param customers customers to be added to the list of customers
     *        in this ride.
     * @throws CustomerException if the total number of passengers exceeds the
     *         maximum number of passengers allowed in each ride.
     */
    public void add(Customer... customers) throws CustomerException {
        int num = this.size();
        for (Customer customer: customers) {
            num += customer.countPassengers();
        }
        if (this.capacity() < num) {
            throw new CustomerException("number of passengers exceeds maximum ride capacity");
        }
        this.customers.addAll(Arrays.asList(customers));
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
        int num = 0;
        for (Customer customer: this.customers) {
            num += customer.countPassengers();
        }
        return num;
    }

    /**
     *
     *
     *
     */
    public void process() {
        DirectionsApiRequest request = DirectionsApi.newRequest(context);
        request.origin(new LatLng(40.644737, -73.781937));
        request.destination(new LatLng(40.811108, -73.957993));
        request.mode(TravelMode.DRIVING);
        try {
            DirectionsResult result = request.await();
            if (0 == result.routes.length) {
                return;
            }
            DirectionsRoute route = result.routes[0];
            long distance = 0;
            long duration = 0;
            for (DirectionsLeg leg: route.legs) {
                distance += leg.distance.inMeters;
                duration += leg.duration.inSeconds;
            }
            this.info.put("distance", distance);
            this.info.put("duration", duration);
        } catch (ApiException exp) {
            log.error(exp.getMessage());
        } catch (Exception exp) {
            log.error(exp.getMessage());
        }
    }

    /**
     *
     *
     * @return distance in meters to complete this ride
     */
    public long getDistance() {
        if (!this.info.containsKey("distance")) {
            this.process();
        }
        return this.info.get("distance");
    }

    /**
     *
     *
     * @return duration in seconds to complete this ride
     */
    public long getDuration() {
        if (!this.info.containsKey("duration")) {
            this.process();
        }
        return this.info.get("duration");
    }

}
