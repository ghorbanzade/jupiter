//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Pejman Ghorbanzade <pejman@ghorbanzade.com>
// Released under the terms of MIT License
// https://github.com/ghorbanzade/jupiter/blob/master/LICENSE
//

package com.ghorbanzade.jupiter;

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
        long duration = (long) this.ride.get("duration");
        double factor = cfg.getAsDouble("late.factor");
        for (Customer customer: this.ride.getCustomers()) {
            if (customer.getIndividualRideDuration() * factor < duration) {
                return false;
            }
        }
        return true;
    }

}
