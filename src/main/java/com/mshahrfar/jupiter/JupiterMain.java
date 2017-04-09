//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

import org.apache.log4j.Logger;

import java.nio.file.Paths;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Main class of Jupiter.
 *
 * @author Mariam Shahrabifarahani
 * @author Pejman Ghorbanzade
 */
public class JupiterMain {

    private static final Logger log = Logger.getLogger(JupiterMain.class);

    /**
     *
     *
     * @param args command line arguments
     *             jupiter does not support any command line argument
     *             at the moment
     */
    public static void main(String[] args) {
        log.info("Hello from Jupiter");
        Config cfg = ConfigManager.get("config/main.properties");

        InputRule rule = new TimeWindowRule(
            new DatasetParser(Paths.get(cfg.getAsString("dataset.sample.filepath"))),
            cfg.getAsInt("time.window.bound.low"),
            cfg.getAsInt("time.window.bound.high")
        );
        rule.addFilter(new VicinityFilter(cfg.getAsInt("vicinity")));
        rule.addFilter(new RideCapacityFilter(cfg.getAsInt("ride.capacity")));

        while (rule.hasCustomer()) {
            Customer customer = rule.nextCustomer();
            List<Customer> candidates = rule.getCandidates();
            log.info(String.format(
                "customer %d: %d candidates found",
                customer.getId(), candidates.size()
            ));
            try {
                Ride ride = new Ride(customer);
                List<Ride> rides = new ArrayList<Ride>();
                for (Customer candidate: candidates) {
                    try {
                        rides.add(ride.with(candidate));
                    } catch (RideException ex) {
                        log.warn(ex.getMessage());
                        continue;
                    }
                }
                log.info(String.format(
                    "customer %d: finding best shared ride among %d rides",
                    customer.getId(), rides.size()
                ));
                //Collections.sort(rides, new DurationComparator());
                //log.info("best ride is: " + rides.get(0));
            } catch (RideException ex) {
                log.trace(ex.getMessage());
            }
        }

    }

    /**
     * Prevent instantiation from this class.
     */
    private JupiterMain() {
        // intentionally left blank
    }

}
