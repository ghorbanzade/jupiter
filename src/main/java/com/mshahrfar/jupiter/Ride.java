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
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.IOException;
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
    private final Map<String, Object> info = new HashMap<String, Object>();

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
     * @throws RideException if the number of given passengers are more
     *         than maximum allowed passengers in each ride.
     */
    public Ride(Customer... customers) throws RideException {
        this.customers = new ArrayList<Customer>(Arrays.asList(customers));
        if (this.capacity() < this.size()) {
            throw new RideException(
                "number of passengers exceeds maximum ride capacity"
            );
        }
    }

    /**
     *
     *
     * @param customers
     * @throws RideException if the total number of passengers in this
     *         ride exceeds the maximum number of passengers allowed
     *         in each ride.
     * @return a new Ride object that contains customers of this ride as well
     *         as the given list of customers.
     */
    public Ride with(Customer... customers) throws RideException {
        Customer[] customerArr = new Customer[this.customers.size()];
        Ride ride = new Ride(this.customers.toArray(customerArr));
        ride.add(customers);
        return ride;
    }

    /**
     *
     *
     * @param customers customers to be added to the list of customers
     *        in this ride.
     * @throws RideException if the total number of passengers exceeds the
     *         maximum number of passengers allowed in each ride.
     */
    public void add(Customer... customers) throws RideException {
        int num = this.size();
        for (Customer customer: customers) {
            num += customer.countPassengers();
        }
        if (this.capacity() < num) {
            throw new RideException("number of passengers exceeds maximum ride capacity");
        }
        this.customers.addAll(Arrays.asList(customers));
        // ensure that we process this ride again now that customers
        // have changed
        this.reset();
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
     * Returns the number of passengers currently reserved in this ride.
     * This value may be different than the number of customers.
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
    public void process() throws RideException {
        log.trace(String.format(
            "processing ride with %d customers", this.customers.size()
        ));
        switch (this.customers.size()) {
            case 0:
                throw new RideException(
                    "nothing to process for a ride without customers"
                );
            case 1:
                this.processDedicatedRide();
                break;
            case 2:
                this.processSharedRide();
                break;
            default:
                throw new RideException(
                    "rides with more than 2 customers not supported yet"
                );
        }
    }

    /**
     *
     */
    private void processDedicatedRide() {
        long distance = 0;
        long duration = 0;
        DirectionsApiRequest request = DirectionsApi.newRequest(context);
        request.mode(TravelMode.DRIVING);
        request.departureTime(
            this.findDepartureTime(this.customers.get(0).getPickupTime())
        );
        request.origin(this.customers.get(0).getPickupLocation());
        request.destination(this.customers.get(0).getDropoffLocation());
        DirectionsResult result = (new DirectionsFinder(cfg)).fetchResult(request);
        if (0 == result.routes.length) {
            return;
        }
        DirectionsRoute route = result.routes[0];
        for (DirectionsLeg leg: route.legs) {
            distance += leg.distance.inMeters;
            duration += leg.duration.inSeconds;
        }
        this.info.put("distance", distance);
        this.info.put("duration", duration);
    }

    /**
     *
     */
    private void processSharedRide() {
        long distance = 0;
        long duration = 0;
        long distance1 = 0;
        long duration1 = 0;
        long distance2 = 0;
        long duration2 = 0;

        DirectionsApiRequest request1 = DirectionsApi.newRequest(context);
        request1.mode(TravelMode.DRIVING);
        request1.origin(this.customers.get(0).getPickupLocation());
        request1.departureTime(
            this.findDepartureTime(this.customers.get(0).getPickupTime())
        );
        request1.destination(this.customers.get(0).getDropoffLocation());
        LatLng[] wp1 = {
            this.customers.get(1).getPickupLocation(),
            this.customers.get(1).getDropoffLocation()
        };
        request1.waypoints(wp1);

        DirectionsApiRequest request2 = DirectionsApi.newRequest(context);
        request2.mode(TravelMode.DRIVING);
        request2.origin(this.customers.get(0).getPickupLocation());
        request2.departureTime(
            this.findDepartureTime(this.customers.get(0).getPickupTime())
        );
        request2.destination(this.customers.get(1).getDropoffLocation());
        LatLng[] wp2 = {
            this.customers.get(1).getPickupLocation(),
            this.customers.get(0).getDropoffLocation()
        };
        request2.waypoints(wp2);

        DirectionsResult result1 = (new DirectionsFinder(cfg)).fetchResult(request1);

        if (0 == result1.routes.length) {
            return;
        }
        DirectionsRoute route1 = result1.routes[0];
        for (DirectionsLeg leg: route1.legs) {
            distance1 += leg.distance.inMeters;
            duration1 += leg.duration.inSeconds;
        }

        DirectionsResult result2 = (new DirectionsFinder(cfg)).fetchResult(request2);

        if (0 == result2.routes.length) {
            return;
        }
        DirectionsRoute route2 = result2.routes[0];
        for (DirectionsLeg leg: route2.legs) {
            distance2 += leg.distance.inMeters;
            duration2 += leg.duration.inSeconds;
        }

        distance = Math.min(distance1, distance2);
        duration = Math.min(duration1, duration2);

        this.info.put("distance", distance);
        this.info.put("duration", duration);
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
        return (long) this.info.get("distance");
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
        return (long) this.info.get("duration");
    }

    /**
     *
     *
     * @return
     */
    public List<Customer> getCustomers() {
        return this.customers;
    }

    /**
     *
     *
     *
     */
    @Override
    public String toString() {
        return this.info.toString();
    }

    /**
     *
     *
     *
     */
    private void reset() {
        this.info.remove("duration");
        this.info.remove("distance");
    }

    /**
     *
     *
     * @param the number of seconds since January 1, 1970, 00:00:00 GMT
     *              represented by this date
     * @return a DateTime object in the future that has the same time as
     *               the given unix time
     */
    private DateTime findDepartureTime(long epoch) {
        DateTime ret = new DateTime(epoch);
        ret = ret.withDate(LocalDate.now());
        if (ret.isBeforeNow()) {
            ret = ret.plusDays(1);
        }
        return ret;
    }

}
