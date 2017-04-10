//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

import org.apache.log4j.Logger;

import com.google.maps.DirectionsApiRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;

import java.io.IOException;

/**
 *
 *
 * @author Pejman Ghorbanzade
 */
public final class DirectionsFinder {

    private Config cfg;
    private static int queryCounter = 0;
    private static final Logger log = Logger.getLogger(DatasetParser.class);

    /**
     *
     *
     * @param cfg
     */
    public DirectionsFinder(Config cfg) {
        this.cfg = cfg;
    }

    /**
     *
     *
     * @param request
     * @return
     * @throws RideException
     */
    public DirectionsResult fetchResult(DirectionsApiRequest request)
        throws RideException
    {
        if (cfg.getAsInt("google.maps.api.cap.day") == queryCounter) {
            throw new RideException("daily query cap reached");
        }
        try {
            Thread.sleep(60 * 1000 / cfg.getAsInt("google.maps.api.cap.minute"));
            queryCounter++;
            return request.await();
        } catch (ApiException | IOException | InterruptedException ex) {
            log.error(ex.getMessage());
            throw new RideException("exception thrown by google maps directions api");
        }
    }

}
