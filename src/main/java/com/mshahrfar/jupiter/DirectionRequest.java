//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

import org.apache.log4j.Logger;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.errors.ApiException;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author Pejman Ghorbanzade
 */
public class DirectionRequest {

  private static int apiQueryCounter = 0;
  private static final Logger log = Logger.getLogger(DatasetParser.class);
  private static final Config cfg = ConfigManager.get("config/main.properties");
  private static final GeoApiContext context = new GeoApiContext().setApiKey(
      cfg.getAsString("google.maps.api.key")
  );

  private final DirectionsApiRequest request = DirectionsApi.newRequest(context);
  private final Map<String, Object> info = new HashMap<String, Object>();

  /**
   *
   */
  public DirectionRequest() {
    this.request.mode(TravelMode.DRIVING);
  }

  /**
   *
   *
   * @param key
   * @param val
   */
  public void set(String key, Object val) {
    this.info.put(key, val);
  }

  /**
   *
   *
   * @return
   */
  public DirectionsResult getResult() {
    if (this.isInDb()) {
      log.info("request found in database");
      return this.getResultFromDb();
    }
    DirectionsResult result = this.getResultFromApi();
    log.info("received result from google directions api");
    this.addToDb(result);
    log.info("added google directions api result to the database");
    return result;
  }

  /**
   *
   *
   * @return
   * @throws RideException
   */
  private DirectionsResult getResultFromApi() throws RideException {
    log.info("sending request to google directions api");
    if (cfg.getAsInt("google.maps.api.cap.day") == apiQueryCounter) {
      throw new RideException("daily query cap reached");
    }
    try {
      this.request.departureTime(new DateTime(this.info.get("departure_time")));
      this.request.origin((LatLng) this.info.get("origin"));
      this.request.destination((LatLng) this.info.get("destination"));
      this.request.waypoints((LatLng[]) this.info.get("waypoints"));
      Thread.sleep(60 * 1000 / cfg.getAsInt("google.maps.api.cap.minute"));
      apiQueryCounter++;
      return this.request.await();
    } catch (ApiException | IOException | InterruptedException ex) {
      log.error(ex.getMessage());
      throw new RideException("exception thrown by google maps directions api");
    }
  }

  /**
   *
   *
   * @return
   */
  private DirectionsResult getResultFromDb() {
    return new DirectionsResult();
  }

  /**
   *
   *
   * @return
   */
  private boolean isInDb() {
    return false;
  }

  /**
   *
   *
   * @param result
   */
  public void addToDb(DirectionsResult result) {
    log.info("adding google directions api result to the database");
  }

}
