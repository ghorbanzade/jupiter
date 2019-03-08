//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Pejman Ghorbanzade <pejman@ghorbanzade.com>
// Released under the terms of MIT License
// https://github.com/ghorbanzade/jupiter/blob/master/LICENSE
//

package com.ghorbanzade.jupiter;

import java.util.List;

/**
 *
 *
 * @author Mariam Shahrabifarahani
 */
public interface Filter {

    /**
     *
     *
     * @param customer
     * @param candidate
     * @return
     */
    public boolean pass(Customer customer, Customer candidate);

}
