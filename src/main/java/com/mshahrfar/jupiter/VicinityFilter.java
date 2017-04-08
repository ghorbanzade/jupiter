//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

/**
 *
 *
 * @author Mariam Shahrabifarahani
 */
public class VicinityFilter implements Filter {

    int vicinity;

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
        Distance dist = new Distance(
            customer.getPickupLocation(),
            candidate.getPickupLocation()
        );
        return (dist.getShortestPath() < this.vicinity);
    }

}
