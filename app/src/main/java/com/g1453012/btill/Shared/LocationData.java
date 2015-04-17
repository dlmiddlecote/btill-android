package com.g1453012.btill.Shared;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by dlmiddlecote on 20/03/15.
 */
public class LocationData {

    private Map<String, Double> locations;
    private Date time;

    public LocationData(TreeMap<String, Double> treeMap) {
        locations = treeMap;
    }

    public void add(String address, double distance) {
        locations.put(address, distance);
    }

    public void setTime(long currentMilliseconds) {
        time = new Date(currentMilliseconds);
    }

    public Date getTime() {
        return time;
    }

    public double getDistance(String address) {
        return locations.get(address);
    }

    public int size() {
        return locations.size();
    }

    public void clear() {
        locations.clear();
    }

}
