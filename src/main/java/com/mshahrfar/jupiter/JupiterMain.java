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
            CustomerParser customers = new CustomerParser(path);
            Customer customerA = customers.next();
            Customer customerB = customers.next();
            Ride ride = new Ride(customerA);
            log.info(ride.with(customerB).getDuration());
            customers.close();
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
