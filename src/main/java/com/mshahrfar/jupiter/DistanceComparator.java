//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

import java.io.Serializable;
import java.util.Comparator;

/**
* This class presents a method to compare two rides based on total
* distance of each.
*
* @author   Pejman Ghorbanzade
* @see      DurationComparator
* @see      Ride
*/
public class DistanceComparator implements Comparator<Ride>, Serializable {

    /**
     * Comparison rule for comparing two Ride objects. In this comparator, a
     * ride is considered as favorable if its total distance is less than
     * that of the other.
     *
     * @param ride1 the ride we are comparing against
     * @param ride2 the ride we are comparing with
     */
    @Override
    public int compare(Ride ride1, Ride ride2) {
        long dist1 = (long) ride1.get("distance");
        long dist2 = (long) ride2.get("distance");
        return Long.compare(dist1, dist2);
    }

}
