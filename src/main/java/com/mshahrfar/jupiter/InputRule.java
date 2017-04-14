//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

import java.util.List;

/**
 *
 * @author Mariam Shahrabifarahanai
 */
public interface InputRule {

    /**
     *
     *
     * @return
     */
    public boolean hasCustomer();

    /**
     *
     *
     * @return
     */
    public Customer nextCustomer();

    /**
     *
     *
     * @return
     */
    public List<Customer> getCandidates();

    /**
     *
     *
     * @param filter
     */
    public void addFilter(Filter filter);

    /**
     *
     *
     * @param candidate
     */
    public void excludeCandidate(Customer candidate);

}
