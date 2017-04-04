//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

import java.util.List;

/**
 *
 *
 * @author Mariam Shahrabifarahani
 */
public class VicinityFilter implements Filter {

    int vicinity;
    private static final int EARTH_RADIUS = 6371; // radius of the earth

    public VicinityFilter(int vicinity) {
        this.vicinity = vicinity;
    }

    /**
     *
     *
     * @param customer
     * @param candidate
     * @return
     */
    public boolean pass(Customer customer, Customer candidate) {
        double lat1 = customer.getPickupLocation().lat;
        double lng1 = customer.getPickupLocation().lng;
        double lat2 = candidate.getPickupLocation().lat;
        double lng2 = candidate.getPickupLocation().lng;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c * 1000; // convert to meters
        return (distance < vicinity);
    }

}
