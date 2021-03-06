//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Pejman Ghorbanzade <pejman@ghorbanzade.com>
// Released under the terms of MIT License
// https://github.com/ghorbanzade/jupiter/blob/master/LICENSE
//

package com.ghorbanzade.jupiter;

import com.google.maps.DirectionsApiRequest;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 *
 *
 * @author Pejman Ghorbanzade
 */
public final class Ride {

  private static final Logger log = Logger.getLogger(CustomerParser.class);
  private static final Config cfg = ConfigManager.get("config/main.properties");
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
   * @return a new Ride object that contains customers of this ride as
   *         well as the given list of customers.
   * @throws RideException if the total number of passengers in this
   *         ride exceeds the maximum number of passengers allowed
   *         in each ride.
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
   * @throws RideException
   */
  public void process() throws RideException {
    log.trace(String.format(
        "processing ride with %d customers: %s",
        this.customers.size(),
        this.customers.toString()
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
    log.trace(String.format(
        "total duration of this ride: %d",
        (long) this.info.get("duration")
    ));
  }

  /**
   *
   *
   * @throws RideException
   */
  private void processDedicatedRide() throws RideException {
    long distance = 0;
    long duration = 0;

    DirectionRequest request = new DirectionRequest();
    request.set("departure_time",
        this.findDepartureTime(this.customers.get(0).getPickupTime())
    );
    request.set("origin", this.customers.get(0).getPickupLocation());
    request.set("destination", this.customers.get(0).getDropoffLocation());

    DirectionsResult result = request.getResult();

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
    DirectionRequest[] requests = new DirectionRequest[2];
    long[] totalDurations = new long[2];
    long[] totalDistances = new long[2];

    // Case 1: P1 -> P2 -> D2 -> D1

    requests[0] = new DirectionRequest();
    requests[0].set("origin", this.customers.get(0).getPickupLocation());
    requests[0].set("destination",
        this.customers.get(0).getDropoffLocation()
    );
    requests[0].set("departure_time",
        this.findDepartureTime(this.customers.get(0).getPickupTime())
    );
    LatLng[] wp1 = {
        this.customers.get(1).getPickupLocation(),
        this.customers.get(1).getDropoffLocation()
    };
    requests[0].set("waypoints", wp1);

    // Case 2: P1 -> P2 -> D1 -> D2

    requests[1] = new DirectionRequest();
    requests[1].set("origin", this.customers.get(0).getPickupLocation());
    requests[1].set("destination",
        this.customers.get(1).getDropoffLocation()
    );
    requests[1].set("departure_time",
        this.findDepartureTime(this.customers.get(0).getPickupTime())
    );
    LatLng[] wp2 = {
        this.customers.get(1).getPickupLocation(),
        this.customers.get(0).getDropoffLocation()
    };
    requests[1].set("waypoints", wp2);

    int minIndex = 0; // dirty way to obtain scenario
    long minDuration = Long.MAX_VALUE;
    long minDistance = Long.MAX_VALUE;
    long[][] legsDurations = new long[2][3];
    long[][] legsDistances = new long[2][3];
    DirectionsResult[] results = new DirectionsResult[2];
    for (int i = 0; i < results.length; i++) {
      results[i] = requests[i].getResult();
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
   * @return
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
   * If given datetime is in the future, it will be set as the departure
   * time. If given datetime is in the past, we will set the departure time
   * to the the earliest datetime in the future that has the same day and
   * same time as the given datetime.
   *
   * @param tin unix time in milliseconds representing customer pickup time
   * @return unix time in milliseconds representing customer departure time
   */
  private long findDepartureTime(long tin) {
    DateTime ret = new DateTime(tin);
    if (ret.isBeforeNow()) {
      int day = ret.getDayOfWeek();
      ret = ret.withDate(LocalDate.now());
      while (ret.getDayOfWeek() != day) {
        ret = ret.plusDays(1);
      }
    }
    return ret.getMillis();
  }

}
