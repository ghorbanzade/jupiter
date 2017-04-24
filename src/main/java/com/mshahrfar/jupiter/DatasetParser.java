//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

import org.apache.log4j.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.google.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

/**
 *
 *
 * @author Pejman Ghorbanzade
 * @see CustomerParser
 */
public final class DatasetParser implements CustomerParser {

    private static final Logger log = Logger.getLogger(DatasetParser.class);
    private CSVParser parser;
    private Iterator<CSVRecord> recordIterator;

    /**
     * Creates a parser to read records of the dataset in the given path.
     *
     * @param path path to the dataset
     * @throws CustomerException if we fail to open dataset with given path
     */
    public DatasetParser(Path path) throws CustomerException {
        try {
            CSVFormat format = CSVFormat.EXCEL.withFirstRecordAsHeader();
            Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
            parser = new CSVParser(reader, format);
            recordIterator = parser.iterator();
        } catch (IOException ex) {
            throw new CustomerException("failed to open customer dataset");
        }
    }

    /**
     * Checks whether dataset has any remaining record to be read.
     *
     * @return true if parser still has customers to parse
     */
    public boolean hasNext() {
        return recordIterator.hasNext();
    }

    /**
     * Reads a new record from dataset and creates a Customer object based on
     * its inforamtion.
     *
     * @return next customers in the dataset or null if none remains
     * @throws CustomerException if there are no more customers or
     *         if the record being read cannot be parsed.
     *         A good client always checks hasNext() before calling
     *         this function.
     */
    public Customer next() throws CustomerException {
        if (recordIterator.hasNext()) {
            return createCustomer(recordIterator.next());
        }
        throw new CustomerException("dataset has no more customers");
    }

    /**
     * Cleans up parser resources. Client is expected to call this method
     * when it is done with the dataset.
     */
    public void close() {
        if (!parser.isClosed()) {
            try {
                parser.close();
            } catch (IOException ex) {
                log.warn(ex.getMessage());
            }
        }
    }

    /**
     *
     *
     * @param record
     * @return
     * @throws CustomerException
     */
    private Customer createCustomer(CSVRecord record) throws CustomerException {
        Customer customer = new Customer();
        try {
            double pickupLat = Double.parseDouble(record.get("pickup_latitude"));
            double pickupLng = Double.parseDouble(record.get("pickup_longitude"));
            customer.set("pickup_location",
              new LatLng(pickupLat, pickupLng)
            );

            double dropoffLat = Double.parseDouble(record.get("dropoff_latitude"));
            double dropoffLng = Double.parseDouble(record.get("dropoff_longitude"));
            customer.set("dropoff_location",
              new LatLng(dropoffLat, dropoffLng)
            );

            // June 01 datasets use this format for pickup date
            DateFormat dateParser = new SimpleDateFormat("MM/dd/yyyyHH:mm:ss");
            // June 04 datasets use this format for pickup date
            //DateFormat dateParser = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
            String pickupTimeStr = record.get("pickup_datetime1")
                                 + record.get("pickup_datetime2");
            customer.set("pickup_time", dateParser.parse(pickupTimeStr).getTime());

            DateFormat timeParser = new SimpleDateFormat("H:mm:ss");
            customer.set("individual_ride_duration",
                timeParser.parse(record.get("dropoff_time")).getTime() -
                timeParser.parse(record.get("pickup_time")).getTime()
            );

            customer.set("passenger_count",
                Integer.parseInt(record.get("passenger_count"))
            );

            customer.set("record_number", record.getRecordNumber());

            customer.set("customer_id",
                Integer.parseInt(record.get("customer_id"))
            );

            customer.set("cluster_id",
                Integer.parseInt(record.get("cluster"))
            );

        } catch (NumberFormatException | ParseException ex) {
            throw new CustomerException(
                "invalid passenger record: " + record.getRecordNumber()
            );
        }
        return customer;
    }

}
