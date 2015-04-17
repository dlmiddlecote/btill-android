package com.g1453012.btill.Shared;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Menu implements Iterable<MenuItem> {
    private String restaurantName;
    private List<MenuItem> items = new ArrayList<MenuItem>();
    private ArrayList<String> categories = new ArrayList<String>();
    private int mOrderId;

    public Menu(String restaurantName, List<MenuItem> items) {
        this.restaurantName = restaurantName;
        this.items = items;
        for (MenuItem item : items) {
            if (!categories.contains(item.getCategory()))
                categories.add(item.getCategory());
        }
    }

    public Menu() {

    }

    public Menu(String restaurant) {
        restaurantName = restaurant;
    }

    public static Menu removeNonZero(Menu menu) {
        Menu retMenu = new Menu(menu.getRestaurantName());
        for (MenuItem item : menu) {
            if (item.getQuantity() != 0)
                retMenu.add(item);
        }
        return retMenu;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public int getOrderId() {
        return mOrderId;
    }

    public void setOrderId(int orderId) {
        mOrderId = orderId;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void sortCategories() {
        for (MenuItem item : items) {
            if (!categories.contains(item.getCategory()))
                categories.add(item.getCategory());
        }
    }

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

    /*public void resetQuantities() {
        for (MenuItem item : items) {
            item.setQuantity(0);
        }
    }*/

    public int size() {
        return items.size();
    }

    public ArrayList<MenuItem> getCategoryItems(String category) {
        ArrayList<MenuItem> sortedItems = new ArrayList<MenuItem>();
        for (MenuItem item : items) {
            if (item.getCategory().equals(category))
                sortedItems.add(item);
        }
        return sortedItems;
    }

    public int totalPence() {
        GBP total = new GBP(0);
        for (MenuItem item : items) {
            if (item.getQuantity() != 0) {
                total = total.plus(item.getPrice().times(item.getQuantity()));
            }
        }
        return total.getPence();
    }

    public void resetQuantities() {
        for (MenuItem item : items) {
            item.setQuantity(0);
        }
    }
}