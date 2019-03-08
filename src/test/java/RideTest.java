//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Pejman Ghorbanzade <pejman@ghorbanzade.com>
// Released under the terms of MIT License
// https://github.com/ghorbanzade/jupiter/blob/master/LICENSE
//

package com.ghorbanzade.jupiter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 *
 * @author Pejman Ghorbanzade
 */
public class RideTest {

  @Rule
  public ExpectedException exp = ExpectedException.none();

  /**
   *
   */
  @Test
  public void rideThenOneCustomerFewPassengers() {
    Customer customer = new Customer(1);
    customer.set("passenger_count", 3);
    Ride ride = new Ride();
    ride.add(customer);
  }

  /**
   *
   */
  @Test(expected = RideException.class)
  public void rideThenOneCustomerManyPassengers() {
    Customer customer = new Customer(1);
    customer.set("passenger_count", 6);
    Ride ride = new Ride();
    ride.add(customer);
  }

  /**
   *
   */
  @Test
  public void rideOneCustomerFewPassengers() {
    Customer customer = new Customer(1);
    customer.set("passenger_count", 3);
    Ride ride = new Ride(customer);
  }

  /**
   *
   */
  @Test
  public void rideOneCustomerManyPassengers() {
    Customer customer = new Customer(1);
    customer.set("passenger_count", 6);
    Ride ride = new Ride(customer);
  }

  /**
   *
   */
  @Test
  public void rideTwoCustomersFewPassengers() {
    Customer[] customers = new Customer[2];
    customers[0] = new Customer(1);
    customers[1] = new Customer(2);
    customers[0].set("passenger_count", 2);
    customers[1].set("passenger_count", 2);
    Ride ride = new Ride(customers);
    assertThat(ride.getCustomers(), is(Arrays.asList(customers)));
  }

  /**
   *
   */
  @Test(expected = RideException.class)
  public void rideTwoCustomersManyPassengers() {
    Customer[] customers = new Customer[2];
    customers[0] = new Customer(1);
    customers[1] = new Customer(2);
    customers[0].set("passenger_count", 2);
    customers[1].set("passenger_count", 3);
    Ride ride = new Ride(customers);
  }

  /**
   *
   */
  @Test
  public void rideWithTwoCustomerFewPassengers() {
    Customer customerA = new Customer(1);
    Customer customerB = new Customer(2);
    customerA.set("passenger_count", 2);
    customerB.set("passenger_count", 2);
    Ride ride1 = new Ride(customerA);
    Ride ride2 = ride1.with(customerB);
  }

}
