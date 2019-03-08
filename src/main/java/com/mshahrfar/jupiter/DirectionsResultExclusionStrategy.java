//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Pejman Ghorbanzade <pejman@ghorbanzade.com>
// Released under the terms of MIT License
// https://github.com/ghorbanzade/jupiter/blob/master/LICENSE
//

package com.ghorbanzade.jupiter;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 *
 * @author Pejman Ghorbanzade
 */
public class DirectionsResultExclusionStrategy implements ExclusionStrategy {

  /**
   *
   *
   * @param arg
   * @return always false
   */
  public boolean shouldSkipClass(Class<?> arg) {
    return false;
  }

  /**
   *
   *
   *
   * @param arg
   * @return true if the field should be excluded and false otherwise
   */
  public boolean shouldSkipField(FieldAttributes arg) {
    List<String> fields = new ArrayList<String>(Arrays.asList(
        "geocodedWaypoints", "summary", "copyrights", "bounds",
        "steps", "htmlInstructions", "humanReadable", "maneuver",
        "startAddress", "endAddress", "polyline", "overviewPolyline"
    ));
    for (String field: fields) {
      if (arg.getName().equals(field)) {
        return true;
      }
    }
    return false;
  }

}
