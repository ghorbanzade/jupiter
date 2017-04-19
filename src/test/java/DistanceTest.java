//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.google.maps.model.LatLng;

/**
 *
 * @author Pejman Ghorbanzade
 */
public class DistanceTest {

  /**
   *
   */
  @Test
  public void calculateDirectDistance() {
    LatLng point1 = new LatLng(40.83954620, -73.94101715);
    LatLng point2 = new LatLng(40.77891159, -73.95391846);
    Distance dist = new Distance(point1, point2);
    assertThat(dist.getShortestPath(), is(closeTo(6829, 1)));
  }

  /**
   *
   */
  @Test
  public void checkVicinityFilter() {
    Customer customerA = new Customer();
    Customer customerB = new Customer();
    customerA.set("pickup_location", new LatLng(40.83954620, -73.94101715));
    customerB.set("pickup_location", new LatLng(40.77891159, -73.95391846));
    Filter filter = new VicinityFilter(7000);
    assertThat(filter.pass(customerA, customerB), is(true));
  }

}
