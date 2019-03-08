//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Pejman Ghorbanzade <pejman@ghorbanzade.com>
// Released under the terms of MIT License
// https://github.com/ghorbanzade/jupiter/blob/master/LICENSE
//

package com.ghorbanzade.jupiter;

import org.apache.log4j.Logger;

/**
 * This class defines a custom exception that is thrown to hint the main
 * thread that the program should be aborted.
 *
 * @author Pejman Ghorbanzade
 */
public final class FatalException extends RuntimeException {

  private static final Logger log = Logger.getLogger(FatalException.class);

  /**
   * A fatal exception updates the log file with the class name that caused it.
   *
   * @param cls the class from which this exception is thrown
   */
  public FatalException(Class<?> cls) {
    log.fatal("fatal exception in class " + cls.getSimpleName());
  }

  /**
   *
   *
   * @param msg
   */
  public FatalException(String msg) {
    super(msg);
  }

}
