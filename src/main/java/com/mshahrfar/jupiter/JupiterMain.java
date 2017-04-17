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

        InputRule input = new TimeWindowRule(
            new DatasetParser(Paths.get(
                cfg.getAsString("dataset.sample.filepath")
            )),
            cfg.getAsInt("time.window.bound.low"),
            cfg.getAsInt("time.window.bound.high")
        );

        input.addFilter(new VicinityFilter(
            cfg.getAsInt("vicinity")
        ));
        input.addFilter(new RideCapacityFilter(
            cfg.getAsInt("ride.capacity")
        ));

        storeRides(mongoClient, input);
    }

    /**
     *
     * @param mongoClient
     * @param input
     */
    private static void storeRides(
        MongoClient mongoClient, InputRule input
    ) {
        MongoDatabase db = mongoClient.getDatabase("jupiter");
        MongoCollection<Document> collection = db.getCollection("rides");
        db.drop();

        //for (int j = 0; j < 1 && input.hasCustomer(); j++) {
        while (input.hasCustomer()) {

            Customer customer = input.nextCustomer();

            // get the list of customers with whom this customer
            // may share his ride.
            List<Customer> candidates = input.getCandidates();

            log.info(String.format(
                "customer %d: %d candidates found",
                customer.getId(), candidates.size()
            ));

            try {

                // create a list of possible shared rides with this customer
                // and a subset of candidates
                List<Ride> rides = findCandidateRides(customer, candidates);

                // find the best ride among a list of possible rides
                Ride ride = findBestRide(customer, rides);

                log.info(String.format(
                  "customer %d: ride " + ride.getCustomers(),
                  customer.getId()
                ));

                // exclude candidates
                for (Customer rider: ride.getCustomers()) {
                  if (!rider.equals(customer)) {
                    input.excludeCandidate(rider);
                    log.trace(String.format(
                      "customer %d excluded from future consideration",
                      rider.getId()
                    ));
                  }
                }

                // store in database
                Document doc = new Document();
                doc.put("customer_id", ride.getCustomers().get(0).getId());

                List<Long> riderIds = new ArrayList<Long>();
                Iterator<Customer> it = ride.getCustomers().iterator();
                if (it.hasNext()) {
                    it.next();
                }
                it.forEachRemaining(c -> { riderIds.add(c.getId()); });
                doc.put("rider_ids", riderIds);

                doc.put("duration_total", (long) ride.get("duration"));
                doc.put("distance_total", (long) ride.get("distance"));

                List<Long> durations = (List<Long>) ride.get("durations");
                doc.put("t_p1_p2", durations.get(0));
                doc.put("t_p2_d2", durations.get(1));
                doc.put("t_d2_d1", durations.get(2));
                doc.put("t_p2_d1", durations.get(3));
                doc.put("t_d1_d2", durations.get(4));

                List<Long> distances = (List<Long>) ride.get("distances");
                doc.put("d_p1_p2", distances.get(0));
                doc.put("d_p2_d2", distances.get(1));
                doc.put("d_d2_d1", distances.get(2));
                doc.put("d_p2_d1", distances.get(3));
                doc.put("d_d1_d2", distances.get(4));

                doc.put("scenario", (int) ride.get("scenario"));

                List<Long> candidateIds = new ArrayList<Long>();
                candidates.iterator().forEachRemaining(
                    c -> { candidateIds.add(c.getId()); }
                );
                doc.put("candidates_count", candidateIds.size());
                doc.put("candidate_ids", candidateIds);
                collection.insertOne(doc);
            } catch (RideException ex) {
                log.trace(ex.getMessage());
            }
        }
    }

    /**
     *
     *
     * @param customer
     * @param candidates
     * @return
     */
    public static List<Ride> findCandidateRides(
      Customer customer, List<Customer> candidates
    ) {
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
      return rides;
    }

    /**
     *
     *
     * @param customer
     * @param rides
     * @return
     */
    private static Ride findBestRide(Customer customer, List<Ride> rides) {
      if (!rides.isEmpty()) {
        // sort possible shared rides based on total duration
        Collections.sort(rides, new DurationComparator());
        for (Ride ride: rides) {
          if ((new DurationSharePolicy(ride).pass())) {
            return ride;
          }
        }
      }
      // if there is no possible shared ride or all of them fail
      // the duration policy test, return a single-customer ride
      return new Ride(customer);
    }

    /**
     *
     *
     * @param mongoClient
     * @param input
     */
    private static void storeCandidateIds(
        MongoClient mongoClient, InputRule input
    ) {
        MongoDatabase db = mongoClient.getDatabase("jupiter");
        MongoCollection<Document> collection = db.getCollection("rides");
        db.drop();
        while (input.hasCustomer()) {
            Customer customer = input.nextCustomer();
            List<Customer> candidates = input.getCandidates();

            Document doc = new Document();
            doc.put("customer_id", customer.getId());

            List<Long> candidateIds = new ArrayList<Long>();
            candidates.iterator().forEachRemaining(
                c -> { candidateIds.add(c.getId()); }
            );
            doc.put("candidates_count", candidateIds.size());
            doc.put("candidate_ids", candidateIds);
            collection.insertOne(doc);
        }
    }

    /**
     * Prevent instantiation from this class.
     */
    private JupiterMain() {
        // intentionally left blank
    }

}
