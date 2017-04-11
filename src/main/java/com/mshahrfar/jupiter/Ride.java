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
import java.util.NoSuchElementException;

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
     *
     *
     * @param customer
     */
    public Ride(Customer customer) {
        this.customers = new ArrayList<Customer>();
        this.customers.add(customer);
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
            throw new RideException(
                "number of passengers exceeds maximum ride capacity"
            );
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
        if (null != result.routes && 0 != result.routes.length) {
            DirectionsRoute route = result.routes[0];
            for (DirectionsLeg leg: route.legs) {
                distance += leg.distance.inMeters;
                duration += leg.duration.inSeconds;
            }
        }

        List<Long> durations = new ArrayList<Long>();
        durations.add((long) 0); // t_p1_p2
        durations.add((long) 0); // t_p2_d2
        durations.add((long) 0); // t_d2_d1
        durations.add((long) 0); // t_p2_d1
        durations.add((long) 0); // t_d1_d2
        this.info.put("durations", durations);

        List<Long> distances = new ArrayList<Long>();
        distances.add((long) 0); // d_p1_p2
        distances.add((long) 0); // d_p2_d2
        distances.add((long) 0); // d_d2_d1
        distances.add((long) 0); // d_p2_d1
        distances.add((long) 0); // d_d1_d2
        this.info.put("distances", distances);

        this.info.put("duration", duration);
        this.info.put("distance", distance);
        this.info.put("scenario", 0);
    }

    /**
     *
     */
    private void processSharedRide() {

        DirectionsApiRequest[] requests = new DirectionsApiRequest[2];
        DirectionsResult[] results = new DirectionsResult[2];
        long[] totalDurations = new long[2];
        long[] totalDistances = new long[2];

        // Case 1: P1 -> P2 -> D2 -> D1
        requests[0] = DirectionsApi.newRequest(context);
        requests[0].mode(TravelMode.DRIVING);

        requests[0].origin(this.customers.get(0).getPickupLocation());
        requests[0].departureTime(
            this.findDepartureTime(this.customers.get(0).getPickupTime())
        );
        requests[0].destination(this.customers.get(0).getDropoffLocation());
        LatLng[] wp1 = {
            this.customers.get(1).getPickupLocation(),
            this.customers.get(1).getDropoffLocation()
        };
        requests[0].waypoints(wp1);

        // Case 2: P1 -> P2 -> D1 -> D2
        requests[1] = DirectionsApi.newRequest(context);
        requests[1].mode(TravelMode.DRIVING);

        requests[1].origin(this.customers.get(0).getPickupLocation());
        requests[1].departureTime(
            this.findDepartureTime(this.customers.get(0).getPickupTime())
        );
        requests[1].destination(this.customers.get(1).getDropoffLocation());
        LatLng[] wp2 = {
            this.customers.get(1).getPickupLocation(),
            this.customers.get(0).getDropoffLocation()
        };
        requests[1].waypoints(wp2);

        int minIndex = 0; // dirty way to obtain scenario
        long minDuration = Long.MAX_VALUE;
        long minDistance = Long.MAX_VALUE;
        long[][] legsDurations = new long[2][3];
        long[][] legsDistances = new long[2][3];
        for (int i = 0; i < results.length; i++) {
            results[i] = (new DirectionsFinder(cfg)).fetchResult(requests[i]);
            if (null != results[i].routes && 0 != results[i].routes.length) {
                DirectionsRoute route = results[i].routes[0];
                for (int j = 0; j < route.legs.length; j++) {
                    legsDurations[i][j] = route.legs[j].duration.inSeconds;
                    legsDistances[i][j] = route.legs[j].distance.inMeters;
                }
            }
            // temporary solution to get scenario
            long totalDuration = Arrays.stream(legsDurations[i]).sum();
            if (totalDuration < minDuration) {
                minDuration = totalDuration;
                minIndex = i;
            }
            minDistance = Math.min(
                minDistance,
                Arrays.stream(legsDistances[i]).sum()
            );
        }

        List<Long> durations = new ArrayList<Long>();
        durations.add(legsDurations[0][0]); // t_p1_p2
        durations.add(legsDurations[0][1]); // t_p2_d2
        durations.add(legsDurations[0][2]); // t_d2_d1
        durations.add(legsDurations[1][1]); // t_p2_d1
        durations.add(legsDurations[1][2]); // t_d1_d2
        this.info.put("durations", durations);

        List<Long> distances = new ArrayList<Long>();
        distances.add(legsDistances[0][0]); // d_p1_p2
        distances.add(legsDistances[0][1]); // d_p2_d2
        distances.add(legsDistances[0][2]); // d_d2_d1
        distances.add(legsDistances[1][1]); // d_p2_d1
        distances.add(legsDistances[1][2]); // d_d1_d2
        this.info.put("distances", distances);

        this.info.put("duration", minDuration);
        this.info.put("distance", minDistance);
        this.info.put("scenario", minIndex);
    }

    /**
     *
     *
     * @return distance in meters to complete this ride
     * @deprecated
     */
    public long getDistance() {
        return (long) this.info.get("distance");
    }

    /**
     *
     *
     * @return duration in seconds to complete this ride
     * @deprecated
     */
    public long getDuration() {
        return (long) this.get("duration");
    }

    /**
     *
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        if (!this.info.containsKey(key)) {
            this.process();
        }
        if (!this.info.containsKey(key)) {
            throw new NoSuchElementException(
                "key `" + key + "` does not exist in ride"
            );
        }
        return this.info.get(key);
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
        this.info.remove("scenario");
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
