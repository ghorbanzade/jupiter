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

        Path path = Paths.get(cfg.getAsString("dataset.sample.filepath"));
        try {
            CustomerParser parser = new CustomerParser(path);
            Customer customer = parser.next();
            Customer candidateA = parser.next();
            Customer candidateB = parser.next();
            Customer candidateC = parser.next();
            List<Customer> candidates = new ArrayList<Customer>();
            candidates.add(candidateA);
            candidates.add(candidateB);
            candidates.add(candidateC);

            Ride ride = new Ride(customer);
            List<Ride> rides = new ArrayList<Ride>();
            for (Customer candidate: candidates) {
                try {
                    rides.add(ride.with(candidate));
                } catch (RideException exp) {
                    log.warn(exp.getMessage());
                    continue;
                }
            }
            Collections.sort(rides, new DistanceComparator());
            log.info("best ride is: " + rides.get(0));
            parser.close();
        } catch (CustomerException ex) {
            log.error("failed to create customer parser");
        }

    }

    /**
     * Prevent instantiation from this class.
     */
    private JupiterMain() {
        // intentionally left blank
    }

}
