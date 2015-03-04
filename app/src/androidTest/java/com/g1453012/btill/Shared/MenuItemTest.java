package com.g1453012.btill.Shared;

import junit.framework.TestCase;

public class MenuItemTest extends TestCase {

    MenuItem testItem = new MenuItem("Item", new GBP(300), "Mains");

    public void testGetCategory() throws Exception {
        assertEquals("Mains", testItem.getCategory());
    }

    public void testSetCategory() throws Exception {
        MenuItem newItem = testItem;
        newItem.setCategory("Drinks");
        assertEquals("Drinks", newItem.getCategory());
    }

    public void testGetName() throws Exception {
        assertEquals("Item", testItem.getName());
    }

    public void testGetPrice() throws Exception {
        assertEquals(new GBP(300), testItem.getPrice());
    }

    public void testGetQuantity() throws Exception {
        testItem.setQuantity(4);
        assertEquals(4, testItem.getQuantity());
    }

    public void testSetQuantity() throws Exception {
        testItem.setQuantity(5);
        assertEquals(5, testItem.getQuantity());
    }

    public void testIncrementQuantity() throws Exception {
        testItem.setQuantity(5);
        testItem.incrementQuantity();
        assertEquals(6, testItem.getQuantity());
    }

    public void testDecrementQuantity() throws Exception {
        testItem.setQuantity(5);
        testItem.decrementQuantity();
        assertEquals(4, testItem.getQuantity());
    }

    public void testToString() throws Exception {
        assertEquals("MenuItem{" + testItem.getName() + ", " + testItem.getPrice() + "}", "MenuItem{Item, £3.00}");
    }
}