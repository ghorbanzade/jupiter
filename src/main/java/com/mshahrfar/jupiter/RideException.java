//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Pejman Ghorbanzade <pejman@ghorbanzade.com>
// Released under the terms of MIT License
// https://github.com/ghorbanzade/jupiter/blob/master/LICENSE
//

package com.ghorbanzade.jupiter;

/**
 *
 *
 * @author Pejman Ghorbanzade
 * @see Ride
 */
public final class RideException extends RuntimeException {

  /**
   *
   *
   * @param exp explanation for why the exception is thrown
   */
  public RideException(String exp) {
    super(exp);
  }

}
