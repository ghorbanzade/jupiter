//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

import org.apache.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author Mariam Shahrabifarahani
 * @author Pejman Ghorbanzade
 */
public class TimeWindowRule implements InputRule {

    private long limitLow;
    private long limitHigh;
    private Customer temp;
    private Customer customer;
    private CustomerParser parser;
    private List<Customer> candidates = new ArrayList<Customer>();
    private List<Filter> filters = new ArrayList<Filter>();

    private static final Logger log = Logger.getLogger(JupiterMain.class);

    /**
     *
     *
     * @param parser
     * @param limitLow
     * @param limitHigh
     */
    public TimeWindowRule(
        CustomerParser parser,
        long limitLow,
        long limitHigh
    ) {
        this.parser = parser;
        this.limitLow = limitLow * 1000;
        this.limitHigh = limitHigh * 1000;
        this.customer = this.parser.next();
        this.candidates.add(customer);
        this.rebuild();
    }

    /**
     *
     */
    private void rebuild() {
        if (null != this.temp) {
            if (this.temp.getPickupTime() < this.customer.getPickupTime() + this.limitHigh) {
                this.candidates.add(this.temp);
            } else {
                return;
            }
        }
        while (this.parser.hasNext()) {
            Customer candidate = this.parser.next();
            if (candidate.getPickupTime() < this.customer.getPickupTime() + this.limitHigh) {
                this.candidates.add(candidate);
            } else {
                this.temp = candidate;
                break;
            }
        }
        while (this.candidates.get(0).getPickupTime() < this.customer.getPickupTime() - this.limitLow) {
            this.candidates.remove(0);
        }
    }

    /**
     *
     *
     * @return
     */
    public boolean hasCustomer() {
        return (this.customer != this.candidates.get(this.candidates.size() - 1));
    }

    /**
     *
     *
     * @return
     */
    public Customer nextCustomer() {
        long lastPickupTime = this.customer.getPickupTime();
        int index = this.candidates.indexOf(this.customer);
        this.candidates.remove(index);
        this.customer = this.candidates.get(index);
        if (lastPickupTime != this.customer.getPickupTime()) {
            this.rebuild();
        }
        return this.customer;
    }

    /**
     *
     *
     * @return
     */
    public List<Customer> getCandidates() {
        List<Customer> list = new ArrayList<Customer>();
        for (Customer candidate: this.candidates) {
            if (this.customer == candidate) {
                continue;
            }
            boolean shouldAdd = true;
            for (Filter filter: filters) {
                if (!filter.pass(customer, candidate)) {
                    shouldAdd = false;
                    break;
                }
            }
            if (shouldAdd) {
                list.add(candidate);
            }
        }
        return list;
    }

    /**
     *
     *
     * @param filter
     */
    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }

    /**
     *
     *
     *
     * @param candidate
     */
    public void excludeCandidate(Customer candidate) {
        if (!this.candidates.remove(candidate)) {
            log.warn("asked to remove non-existant candidate");
        }
    }

}
