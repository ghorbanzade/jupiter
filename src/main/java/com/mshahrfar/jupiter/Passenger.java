//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import com.google.maps.model.LatLng;

/**
 *
 *
 * @author Pejman Ghorbanzade
 */
public final class Passenger {

    private static final Logger log = Logger.getLogger(PassengerParser.class);

    private LatLng pickupLocation;
    private LatLng dropoffLocation;

    /**
     *
     *
     * @param record
     * @throws PassengerException
     */
    public Passenger(CSVRecord record) throws PassengerException {
        try {
            double pickupLat = Double.parseDouble(record.get("pickup_latitude"));
            double pickupLng = Double.parseDouble(record.get("pickup_longitude"));
            double dropoffLat = Double.parseDouble(record.get("dropoff_latitude"));
            double dropoffLng = Double.parseDouble(record.get("dropoff_longitude"));
            this.pickupLocation = new LatLng(pickupLat, pickupLng);
            this.dropoffLocation = new LatLng(dropoffLat, dropoffLng);
        } catch (NumberFormatException ex) {
            throw new PassengerException("failed to parse passenger record");
        }
    }

    /**
     *
     *
     * @return
     */
    public LatLng getPickupLocation() {
        return this.pickupLocation;
    }

    /**
     *
     *
     * @return
     */
    public LatLng getDropoffLocation() {
        return this.dropoffLocation;
    }

}
