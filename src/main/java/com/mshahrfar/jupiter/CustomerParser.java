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
 * @see DatasetParser
 */
public interface CustomerParser {

    /**
     * Checks whether parser has any remaining entries to parse.
     *
     * @return true if parser still has customers to parse
     */
    public boolean hasNext();

    /**
     * Reads a new entry and creates a Customer object based on its
     * inforamtion.
     *
     * @return next customers in the parser or null if none remains
     * @throws CustomerException if there are no more customers.
     *         A good client always checks hasNext() before calling
     *         this function.
     */
    public Customer next() throws CustomerException;

    /**
     * Cleans up parser resources. Client is expected to call this method
     * when it is done with the parser.
     */
    public void close();

}
