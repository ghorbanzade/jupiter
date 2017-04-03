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
public final class CustomerParser {

    private static final Logger log = Logger.getLogger(CustomerParser.class);
    private static CSVParser parser;
    private static Iterator<CSVRecord> recordIterator;

    /**
     * Creates a parser to read records of the dataset in the given path.
     *
     * @param path path to the dataset
     * @throws CustomerException if we fail to open dataset with given path
     */
    public CustomerParser(Path path) throws CustomerException {
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
     * @throws CustomerException if there are no more customers.
     *         A good client always checks hasNext() before calling
     *         this function.
     */
    public Customer next() throws CustomerException {
        if (recordIterator.hasNext()) {
            return new Customer(recordIterator.next());
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

}
