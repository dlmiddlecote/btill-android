package com.g1453012.btill.Shared;

/**
 * Created by dlmiddlecote on 21/03/15.
 */
public class OrderConfirmation {

    private Integer tableNumber;
    private Receipt receipt;

    public OrderConfirmation(Receipt receipt, Integer tableNumber) {
        this.receipt = receipt;
        this.tableNumber = tableNumber;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public Receipt getReceipt() {
        return receipt;
    }
}

