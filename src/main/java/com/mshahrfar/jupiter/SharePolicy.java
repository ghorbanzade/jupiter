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
 */
public interface SharePolicy {

    /**
     *
     *
     * @return true if the ride complies with policy implementing this
     *         interface
     */
    public boolean pass();

}
