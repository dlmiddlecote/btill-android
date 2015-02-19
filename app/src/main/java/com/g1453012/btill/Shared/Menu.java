package com.g1453012.btill.Shared;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Menu implements Iterable<MenuItem> {
    private ArrayList<MenuItem> items = new ArrayList<MenuItem>();

    public Menu(ArrayList<MenuItem> items) {
        this.items = items;
    }

    public Menu(){}

    @Override
    public Iterator<MenuItem> iterator() {
        return items.iterator();
    }


    @Override
    public String toString() {
        return "Menu{" + items + '}';
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public void add(MenuItem item) {
        items.add(item);
    }

    public MenuItem get(int position) {
        return items.get(position);
    }

    public int size() {
        return items.size();
    }
}
