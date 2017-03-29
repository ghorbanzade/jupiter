//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

import org.apache.log4j.Logger;

// should be moved to another class
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

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

        String apiKey = cfg.getAsString("google.map.api.key");
        // context is expensive and should be declared as static var
        GeoApiContext context = new GeoApiContext().setApiKey(apiKey);
        DirectionsApiRequest request = DirectionsApi.newRequest(context);
        request.origin(new LatLng(40.644737, -73.781937));
        request.destination(new LatLng(40.811108, -73.957993));
        request.mode(TravelMode.DRIVING);
        try {
            DirectionsResult result = request.await();
            for (DirectionsRoute route: result.routes) {
                long distance = 0;
                long duration = 0;
                for (DirectionsLeg leg: route.legs) {
                    duration += leg.duration.inSeconds;
                    distance += leg.distance.inMeters;
                }
                log.info("total duration is " + duration);
                log.info("total distance is " + distance);
            }
        } catch (ApiException exp) {
            log.error(exp.getMessage());
        } catch (Exception exp) {
            log.error(exp.getMessage());
        }

    }

    /**
     * Prevent instantiation from this class.
     */
    private JupiterMain() {
        // intentionally left blank
    }

}
