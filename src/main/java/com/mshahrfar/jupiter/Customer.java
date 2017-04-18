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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author Pejman Ghorbanzade
 */
public final class Customer {

    private static final Logger log = Logger.getLogger(CustomerParser.class);

    private Map<String, Object> info = new HashMap<String, Object>();

    /**
     *
     *
     * @param record
     * @throws CustomerException
     */
    public Customer(CSVRecord record) throws CustomerException {
        this.initialize(record);
    }

    /**
     *
     *
     * @param record
     * @throws CustomerException
     */
    private void initialize(CSVRecord record) throws CustomerException {
        try {

            double pickupLat = Double.parseDouble(record.get("pickup_latitude"));
            double pickupLng = Double.parseDouble(record.get("pickup_longitude"));
            info.put("pickup_location", new LatLng(pickupLat, pickupLng));

            double dropoffLat = Double.parseDouble(record.get("dropoff_latitude"));
            double dropoffLng = Double.parseDouble(record.get("dropoff_longitude"));
            info.put("dropoff_location", new LatLng(dropoffLat, dropoffLng));
            // June 01 datasets use this format for pickup date
            DateFormat dateParser = new SimpleDateFormat("MM/dd/yyyyHH:mm:ss");
            // June 04 datasets use this format for pickup date
            //DateFormat dateParser = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
            String pickupTimeStr = record.get("pickup_datetime1")
                                 + record.get("pickup_datetime2");
            info.put("pickup_time", dateParser.parse(pickupTimeStr).getTime());

            DateFormat timeParser = new SimpleDateFormat("H:mm:ss");
            info.put("individual_ride_duration",
                timeParser.parse(record.get("dropoff_time")).getTime() -
                timeParser.parse(record.get("pickup_time")).getTime()
            );

            info.put("passenger_count",
                Integer.parseInt(record.get("passenger_count"))
            );

            info.put("record_number", record.getRecordNumber());

            info.put("customer_id",
                Integer.parseInt(record.get("customer_id"))
            );

        } catch (NumberFormatException | ParseException ex) {
            throw new CustomerException(
                "invalid passenger record: " + record.getRecordNumber()
            );
        }
    }

    /**
     *
     *
     * @return a LatLng object representing pickup location in latitude and longitude
     */
    public LatLng getPickupLocation() {
        return (LatLng) info.get("pickup_location");
    }

    /**
     *
     *
     * @return a LatLng object representing dropoff location in latitude and longitude
     */
    public LatLng getDropoffLocation() {
        return (LatLng) info.get("dropoff_location");
    }

    /**
     *
     *
     * @return a Date object representing pickup time
     */
    public long getPickupTime() {
        return (long) info.get("pickup_time");
    }

    /**
     *
     *
     * @return a record number assigned to customer entry in the database
     */
    public long getId() {
        return (int) info.get("customer_id");
    }

    /**
     *
     *
     * @return number of seconds it takes for this customer to get
     *         to his destination using NYC cabs
     */
    public long getIndividualRideDuration() {
        return (long) info.get("individual_ride_duration");
    }

    /**
     *
     *
     * @return number of passengers reserved by this customer
     */
    public int countPassengers() {
        return (int) info.get("passenger_count");
    }

    /**
     *
     *
     * @return a description of this customer
     */
    @Override
    public String toString() {
        return String.format("%d", this.getId());
    }

    /**
     *
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Customer) {
            Customer candidate = (Customer) obj;
            return (this.getId() == candidate.getId());
        }
        return false;
    }

}
