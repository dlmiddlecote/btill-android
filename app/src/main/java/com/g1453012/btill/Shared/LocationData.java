package com.g1453012.btill.Shared;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by dlmiddlecote on 20/03/15.
 */
public class LocationData {

    private Map<String, Double> locations;

    public LocationData(TreeMap<String, Double> treeMap) {
        locations = treeMap;
    }

    public void add(String address, double distance) {
        locations.put(address, distance);
    }

    public double getDistance(String address) {
        return locations.get(address);
    }

}
