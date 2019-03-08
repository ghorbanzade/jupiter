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
 * @see CustomerParser
 * @see Customer
 */
public final class CustomerException extends RuntimeException {

  /**
   *
   *
   * @param ex explanation for why the exception is thrown
   */
  public CustomerException(String ex) {
    super(ex);
  }

}
