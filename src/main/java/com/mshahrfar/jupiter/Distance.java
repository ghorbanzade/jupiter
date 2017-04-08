//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

import com.google.maps.model.LatLng;

/**
 *
 *
 * @author   Pejman Ghorbanzade
 */
public class Distance {

    private LatLng src;
    private LatLng dst;

    private static final int EARTH_RADIUS = 6371; // radius of the earth

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
        double latDistance = Math.toRadians(dst.lat - src.lat);
        double lonDistance = Math.toRadians(dst.lng - src.lng);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(src.lat)) * Math.cos(Math.toRadians(dst.lat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c * 1000; // convert to meters
    }

}
