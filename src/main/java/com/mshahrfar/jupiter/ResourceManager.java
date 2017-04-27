//
// Jupiter: A Ride-Sharing Network Generation and Analysis Application
// Copyright 2017 Mariam Shahrabifarahani <mshahrfar@gmail.com>
// Released under the terms of MIT License
// https://github.com/mshahrfar/jupiter/blob/master/LICENSE
//

package com.mshahrfar.jupiter;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author Pejman Ghorbanzade
 * @see Config
 */
public final class ResourceManager {

    private static final Map<String, Closeable> resources =
        new HashMap<String, Closeable>();

    public static void add(String key, Closeable value) {
        if (!resources.containsKey(key)) {
            resources.put(key, value);
        }
    }

    public static Object get(String key) {
        return resources.get(key);
    }

}

