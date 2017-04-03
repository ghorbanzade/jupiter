//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

import org.apache.log4j.Logger;

/**
 *
 *
 * @author Pejman Ghorbanzade
 * @see Ride
 */
public final class RideException extends RuntimeException {

    private static final Logger log = Logger.getLogger(RideException.class);

    /**
     *
     *
     * @param ex explanation for why the exception is thrown
     */
    public RideException(String ex) {
        log.error(ex);
    }

}
