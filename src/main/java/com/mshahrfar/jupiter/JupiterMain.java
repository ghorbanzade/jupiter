//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import org.apache.log4j.Logger;

import org.bson.Document;

import java.nio.file.Paths;
import java.nio.file.Path;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
     * @param args command line arguments. Jupiter does not support any
     *             command line argument at the moment.
     */
    public static void main(String[] args) {
        log.info("Hello from Jupiter");
        Config cfg = ConfigManager.get("config/main.properties");

        MongoClient mongoClient = new MongoClient("localhost" , 27017);
        MongoDatabase db = mongoClient.getDatabase("jupiter");
        MongoCollection<Document> collection = db.getCollection("result");
        db.drop();

        InputRule rule = new TimeWindowRule(
            new DatasetParser(Paths.get(
                cfg.getAsString("dataset.sample.filepath")
            )),
            cfg.getAsInt("time.window.bound.low"),
            cfg.getAsInt("time.window.bound.high")
        );

        rule.addFilter(new VicinityFilter(
            cfg.getAsInt("vicinity")
        ));
        rule.addFilter(new RideCapacityFilter(
            cfg.getAsInt("ride.capacity")
        ));

        //while (rule.hasCustomer()) {
        for (int j = 0; j < 3 && rule.hasCustomer(); j++) {
            Customer customer = rule.nextCustomer();
            List<Customer> candidates = rule.getCandidates();

            log.info(String.format(
                "customer %d: %d candidates found",
                customer.getId(), candidates.size()
            ));
            try {
                Ride ride = new Ride(customer);
                List<Ride> rides = new ArrayList<Ride>();
                for (Customer candidate: candidates) {
                    try {
                        rides.add(ride.with(candidate));
                    } catch (RideException ex) {
                        log.warn(ex.getMessage());
                        continue;
                    }
                }
                Ride ri = null;
                if (!rides.isEmpty()) {
                    log.info(String.format(
                        "customer %d: finding best shared ride among %d rides",
                        customer.getId(), rides.size()
                    ));
                    Collections.sort(rides, new DurationComparator());
                    log.info(String.format(
                        "customer %d: found best shared ride candidate",
                        customer.getId()
                    ));
                    if ((new DurationSharePolicy(rides.get(0))).pass()) {
                        ri = rides.get(0);
                        log.info(String.format(
                            "customer %d: ride will be shared",
                            customer.getId()
                        ));
                        for (Customer rider: rides.get(0).getCustomers()) {
                            if (!rider.equals(customer)) {
                                rule.excludeCandidate(rider);
                                log.warn(String.format(
                                    "customer %d excluded from future considerations",
                                    rider.getId()
                                ));
                            }
                        }
                    }
                } else {
                    ri = new Ride(customer);
                    log.warn(String.format(
                        "customer %d: no shared ride candidate exists",
                        customer.getId()
                    ));
                }

                Document doc = new Document();
                doc.put("customer_id", ri.getCustomers().get(0).getId());

                List<Long> company = new ArrayList<Long>();
                Iterator<Customer> it = ri.getCustomers().iterator();
                if (it.hasNext()) {
                    it.next();
                }
                it.forEachRemaining(c -> { company.add(c.getId()); });
                doc.put("candidate_ids", company);

                doc.put("duration_total", ri.getDuration());
                doc.put("distance_total", ri.getDistance());
                collection.insertOne(doc);

            } catch (RideException ex) {
                log.trace(ex.getMessage());
            }
        }

    }

    /**
     * Prevent instantiation from this class.
     */
    private JupiterMain() {
        // intentionally left blank
    }

}
