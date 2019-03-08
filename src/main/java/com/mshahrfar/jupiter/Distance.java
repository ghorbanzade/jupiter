//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Pejman Ghorbanzade <pejman@ghorbanzade.com>
// Released under the terms of MIT License
// https://github.com/ghorbanzade/jupiter/blob/master/LICENSE
//

package com.ghorbanzade.jupiter;

import com.google.maps.model.LatLng;

/**
 *
 *
 * @author   Pejman Ghorbanzade
 */
public class Distance {

  private LatLng src;
  private LatLng dst;

  private static final int EARTH_RADIUS = 6371000; // radius of the earth

  /**
   *
   *
   * @param src
   * @param dst
   */
  public Distance(LatLng src, LatLng dst) {
    this.src = src;
    this.dst = dst;
  }

  /**
   *
   *
   * @return
   */
  public double getShortestPath() {
    double dLat = Math.toRadians(dst.lat - src.lat);
    double dLng = Math.toRadians(dst.lng - src.lng);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(src.lat)) * Math.cos(Math.toRadians(dst.lat))
            * Math.sin(dLng / 2) * Math.sin(dLng / 2);
    double ret = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)) * EARTH_RADIUS;
    return ret;
  }

}
