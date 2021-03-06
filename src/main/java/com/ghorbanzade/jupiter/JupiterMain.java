//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Pejman Ghorbanzade <pejman@ghorbanzade.com>
// Released under the terms of MIT License
// https://github.com/ghorbanzade/jupiter/blob/master/LICENSE
//

package com.ghorbanzade.jupiter;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.lang.ReflectiveOperationException;
import java.lang.reflect.Constructor;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import org.bson.Document;

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

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      ResourceManager.close();
    }));

    MongoClient mongoClient = new MongoClient(new MongoClientURI(
        cfg.getAsString("mongo.client.uri"),
        MongoClientOptions.builder().serverSelectionTimeout(
            cfg.getAsInt("mongo.client.timeout")
        )
    ));
    ResourceManager.put("mongo_client", mongoClient);

    CustomerInput input = new TimeWindowInput(
        new DatasetParser(Paths.get(
            cfg.getAsString("dataset.sample.filepath")
        )),
        cfg.getAsInt("time.window.bound.low"),
        cfg.getAsInt("time.window.bound.high")
    );

    String filters = cfg.getAsString("filters");
    List<String> items = Arrays.asList(filters.split("\\s*,\\s*"));
    for (String item: items) {
      String filter = String.format(
          "com.ghorbanzade.jupiter.%sFilter", item
      );
      try {
        Class<?> cls = Class.forName(filter);
        Constructor<?> constructor = cls.getConstructor();
        Filter instance = (Filter) constructor.newInstance();
        input.addFilter(instance);
        log.info("applied filter " + filter);
      } catch (ClassNotFoundException ex) {
        log.error("class does not exist for filter: " + filter);
      } catch (ReflectiveOperationException ex) {
        log.error("failed to instantiate filter: " + filter);
      }
    }

    storeRides(input);
    //storeCandidateIds(input);
    //storeSingleCustomerRides(input);
  }

  /**
   *
   *
   * @param input
   */
  private static void storeRides(CustomerInput input) {
    MongoClient client = (MongoClient) ResourceManager.get("mongo_client");
    MongoDatabase db = client.getDatabase("jupiter");
    MongoCollection<Document> collection = db.getCollection("rides");
    collection.drop();

    while (input.hasCustomer()) {

      Customer customer = input.nextCustomer();

      // get the list of customers with whom this customer
      // may share his ride.
      List<Customer> candidates = input.getCandidates();

      log.info("customer: " + customer);
      log.info("candidates: " + candidates);

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
        it.forEachRemaining(c -> {
          riderIds.add(c.getId());
        });
        doc.put("rider_ids", riderIds);

        doc.put("duration_total", (long) ride.get("duration"));
        doc.put("distance_total", (long) ride.get("distance"));

        @SuppressWarnings("unchecked")
        List<Long> durations = (List<Long>) ride.get("durations");
        doc.put("t_p1_p2", durations.get(0));
        doc.put("t_p2_d2", durations.get(1));
        doc.put("t_d2_d1", durations.get(2));
        doc.put("t_p2_d1", durations.get(3));
        doc.put("t_d1_d2", durations.get(4));

        @SuppressWarnings("unchecked")
        List<Long> distances = (List<Long>) ride.get("distances");
        doc.put("d_p1_p2", distances.get(0));
        doc.put("d_p2_d2", distances.get(1));
        doc.put("d_d2_d1", distances.get(2));
        doc.put("d_p2_d1", distances.get(3));
        doc.put("d_d1_d2", distances.get(4));

        doc.put("scenario", (int) ride.get("scenario"));

        List<Long> candidateIds = new ArrayList<Long>();
        candidates.iterator().forEachRemaining(c -> {
          candidateIds.add(c.getId());
        });
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
  private static List<Ride> findCandidateRides(
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
   * @param input
   */
  private static void storeCandidateIds(CustomerInput input) {
    MongoClient client = (MongoClient) ResourceManager.get("mongo_client");
    MongoDatabase db = client.getDatabase("jupiter");
    MongoCollection<Document> collection = db.getCollection("candidates");
    collection.drop();
    while (input.hasCustomer()) {
      Customer customer = input.nextCustomer();
      List<Customer> candidates = input.getCandidates();

      Document doc = new Document();
      doc.put("customer_id", customer.getId());

      List<Long> candidateIds = new ArrayList<Long>();
      candidates.iterator().forEachRemaining(c -> {
        candidateIds.add(c.getId());
      });
      doc.put("candidates_count", candidateIds.size());
      doc.put("candidate_ids", candidateIds);
      collection.insertOne(doc);
    }
  }

  /**
   *
   *
   * @param input
   */
  private static void storeSingleCustomerRides(CustomerInput input) {
    MongoClient client = (MongoClient) ResourceManager.get("mongo_client");
    MongoDatabase db = client.getDatabase("jupiter");
    MongoCollection<Document> collection = db.getCollection("single_rides");
    collection.drop();
    while (input.hasCustomer()) {
      Customer customer = input.nextCustomer();
      Ride ride = new Ride(customer);
      Document doc = new Document();
      doc.put("customer_id", customer.getId());
      doc.put("google_distance", ride.get("distance"));
      doc.put("google_duration", ride.get("duration"));
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
