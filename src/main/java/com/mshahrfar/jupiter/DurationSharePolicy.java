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
 * @author Pejman Ghorbanzade
 */
public class DurationSharePolicy implements SharePolicy {

    private static final Config cfg = ConfigManager.get("config/main.properties");
    private final Ride ride;

    /**
     *
     *
     * @param ride
     */
    public DurationSharePolicy(Ride ride) {
        this.ride = ride;
    }

    /**
     *
     *
     * @return
     */
    public boolean pass() {
        for (Customer customer: this.ride.getCustomers()) {
            if (
              customer.getIndividualRideDuration() *
              cfg.getAsDouble("late.factor") < this.ride.getDuration()
            ) {
                return false;
            }
        }
        return true;
    }

}
