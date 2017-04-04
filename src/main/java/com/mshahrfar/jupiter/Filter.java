package com.mshahrfar.jupiter;

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
