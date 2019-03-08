//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Pejman Ghorbanzade <pejman@ghorbanzade.com>
// Released under the terms of MIT License
// https://github.com/ghorbanzade/jupiter/blob/master/LICENSE
//

package com.ghorbanzade.jupiter;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 *
 *
 * @author Pejman Ghorbanzade
 * @see Config
 */
public final class ResourceManager {

  private static final Logger log = Logger.getLogger(DatasetParser.class);
  private static final Map<String, Closeable> resources =
      new HashMap<String, Closeable>();

  /**
   *
   *
   * @param key
   * @param value
   */
  public static void put(String key, Closeable value) {
    resources.putIfAbsent(key, value);
  }

  /**
   *
   *
   * @param key
   */
  public static Object get(String key) {
    return resources.get(key);
  }

  /**
   *
   *
   */
  public static void close() {
    resources.forEach((k, v) -> {
      try {
        log.info("closing resource " + k);
        v.close();
      } catch (IOException ex) {
        log.warn("failed to close resource " + k);
      }
    });
  }

}
