package com.g1453012.btill.Shared;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MenuItem {
    private final String name;
    private final GBP price;
    private int quantity = 0;

    public String getName() {
        return name;
    }

    public GBP getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public MenuItem(String name, GBP price) {
        this.name = name;
        this.price = price;
    }

    @Override public String toString() {
        return "MenuItem{" + name + ", " + price + '}';
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
