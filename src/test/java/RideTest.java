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

import org.junit.rules.ExpectedException;
import org.junit.Test;
import org.junit.Rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public void RideThenOneCustomerFewPassengers() {
        Customer customer = new Customer(1);
        customer.set("passenger_count", 3);
        Ride ride = new Ride();
        ride.add(customer);
    }

    /**
     *
     */
    @Test(expected = RideException.class)
    public void RideThenOneCustomerManyPassengers() {
        Customer customer = new Customer(1);
        customer.set("passenger_count", 6);
        Ride ride = new Ride();
        ride.add(customer);
    }

    /**
     *
     */
    @Test
    public void RideOneCustomerFewPassengers() {
        Customer customer = new Customer(1);
        customer.set("passenger_count", 3);
        Ride ride = new Ride(customer);
    }

    /**
     *
     */
    @Test
    public void RideOneCustomerManyPassengers() {
        Customer customer = new Customer(1);
        customer.set("passenger_count", 6);
        Ride ride = new Ride(customer);
    }

    /**
     *
     */
    @Test
    public void RideTwoCustomersFewPassengers() {
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
    public void RideTwoCustomersManyPassengers() {
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
    public void RideWithTwoCustomerFewPassengers() {
        Customer customerA = new Customer(1);
        Customer customerB = new Customer(2);
        customerA.set("passenger_count", 2);
        customerB.set("passenger_count", 2);
        Ride ride1 = new Ride(customerA);
        Ride ride2 = ride1.with(customerB);
    }

}
