package com.g1453012.btill;

import org.bitcoinj.uri.BitcoinURI;

public class Order {
    private String title;
    private double price;
    private int quantity = 0;


    public Order(String title, double price)
    {
        this.title = title;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
