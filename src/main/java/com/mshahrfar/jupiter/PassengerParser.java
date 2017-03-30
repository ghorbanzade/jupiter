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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Date;

/**
 *
 *
 * @author Pejman Ghorbanzade
 */
public final class PassengerParser {

    private static final Logger log = Logger.getLogger(PassengerParser.class);
    private static Iterator<CSVRecord> recordIterator;

    /**
     * Creates a parser to read records of the dataset in the given path.
     *
     * @param path path to the dataset
     * @throws PassengerException if we fail to open dataset with given path
     */
    public PassengerParser(Path path) throws PassengerException {
        try {
            CSVFormat format = CSVFormat.EXCEL.withFirstRecordAsHeader();
            Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
            CSVParser parser = new CSVParser(reader, format);
            recordIterator = parser.iterator();
        } catch (IOException ex) {
            throw new PassengerException("failed to open passenger dataset");
        }
    }

    /**
     * Checks whether dataset has any remaining record to be read.
     *
     * @return true if parser still has passengers to parse
     */
    public boolean hasNext() {
        return recordIterator.hasNext();
    }

    /**
     * Reads a new record from dataset and creates a Passenger object based on
     * its inforamtion.
     *
     * @return next passenger in the dataset or null if none remains
     * @throws PassengerException if there are no more passengers.
     *         A good client always checks hasNext() before calling
     *         this function.
     */
    public Passenger next() throws PassengerException {
        if (recordIterator.hasNext()) {
            return new Passenger(recordIterator.next());
        }
        throw new PassengerException("dataset has no more passengers");
    }

}
