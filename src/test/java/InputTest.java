//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Pejman Ghorbanzade <pejman@ghorbanzade.com>
// Released under the terms of MIT License
// https://github.com/ghorbanzade/jupiter/blob/master/LICENSE
//

package com.ghorbanzade.jupiter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 *
 * @author Pejman Ghorbanzade
 */
public class InputTest {

  /**
   *
   */
  @Test
  public void testTimeWindowInputBasics() {
    CustomerParser parser = new DatasetParser(Paths.get("data/sample.csv"));
    CustomerInput input = new TimeWindowInput(parser, 5, 5);
    assertThat(input.hasCustomer(), is(true));
    Customer customer = input.nextCustomer();
    assertThat(customer.getId(), is(1L));
    List<Long> candidateIds = new ArrayList<Long>();
    input.getCandidates().iterator().forEachRemaining(
        c -> candidateIds.add(c.getId())
    );
    assertThat(candidateIds, is(Arrays.asList(2L, 3L, 4L, 5L, 6L, 7L)));
    //assertThat(input.nextCustomer().getId(), is(2L));
    parser.close();
  }

  /**
   *
   */
  @Test
  public void testFilter() {
    CustomerParser parser = new DatasetParser(Paths.get("data/sample.csv"));
    CustomerInput input = new TimeWindowInput(parser, 120, 120);
    input.addFilter(new VicinityFilter(17000));
    Customer customer = input.nextCustomer();
    List<Long> ids = new ArrayList<Long>();
    input.getCandidates().iterator().forEachRemaining(
        c -> ids.add(c.getId())
    );
    assertThat(ids, is(Arrays.asList(20L)));
    parser.close();
  }

  /**
   *
   */
  @Test
  public void testExcludeCandidate() {
    CustomerParser parser = new DatasetParser(Paths.get("data/sample.csv"));
    CustomerInput input = new TimeWindowInput(parser, 120, 120);
    input.addFilter(new VicinityFilter(17000));
    input.excludeCandidate(new Customer(20L));
    // input should gracefully handle exclusion of the same customer
    // for a second time
    input.excludeCandidate(new Customer(20L));
    Customer customer = input.nextCustomer();
    List<Long> ids = new ArrayList<Long>();
    input.getCandidates().iterator().forEachRemaining(
        c -> ids.add(c.getId())
    );
    assertThat(ids, is((new ArrayList<Customer>())));
    parser.close();
  }

}
