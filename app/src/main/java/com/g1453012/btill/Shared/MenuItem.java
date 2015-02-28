package com.g1453012.btill.Shared;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MenuItem {
    private final String name;
    private final GBP price;
    private int quantity = 0;
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

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

    public MenuItem(String name, GBP price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public void incrementQuantity() {
        if (quantity<10)
            this.quantity++;
    }

    public void decrementQuantity() {
        if (quantity>0)
        this.quantity--;
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
