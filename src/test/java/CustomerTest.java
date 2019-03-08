//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Pejman Ghorbanzade <pejman@ghorbanzade.com>
// Released under the terms of MIT License
// https://github.com/ghorbanzade/jupiter/blob/master/LICENSE
//

package com.ghorbanzade.jupiter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.google.maps.model.LatLng;

import java.io.IOException;
import java.nio.file.Paths;

/**
 *
 * @author Pejman Ghorbanzade
 */
public class CustomerTest {

    /**
     *
     */
    @Test(expected = CustomerException.class)
    public void makeDatasetParserWithInvalidPath() {
        CustomerParser parser = new DatasetParser(Paths.get("nonexistant.csv"));
    }

    /**
     *
     */
    @Test
    public void parseSampleCSV() {
        CustomerParser parser = new DatasetParser(Paths.get("data/sample.csv"));
        assertThat(parser.hasNext(), is(true));
        Customer customer = parser.next();
        assertThat(customer.getId(), is(1L));
        assertThat(customer.countPassengers(), is(1));
        assertThat(customer.getPickupTime(), is(1401624000000L));
        assertThat(customer.getIndividualRideDuration(), is(1200000L));
        assertThat(customer.getPickupLocation().lat, is(40.6453743));
        assertThat(customer.getDropoffLocation().lng, is(-73.9438858));
        parser.close();
        // parser should gracefully handle (and neglect) close instruction
        // if it is already closed.
        parser.close();
    }

    /**
     *
     */
    @Test
    public void checkCustomer() {
        Customer customerA = new Customer(1);
        Customer customerB = new Customer(2);
        try {
          Customer customerC = (Customer) customerA.clone();
          assertThat(customerC, is(not(1)));
          assertThat(customerC, is(customerA));
          assertThat(customerC, is(not(customerB)));
          assertThat(customerC.toString().equals("1"), is(true));
        } catch (CloneNotSupportedException ex) {
          fail("failed to clone customer");
        }
    }

}
