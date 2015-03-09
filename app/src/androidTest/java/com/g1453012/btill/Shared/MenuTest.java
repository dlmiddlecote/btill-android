package com.g1453012.btill.Shared;

import junit.framework.TestCase;

import java.util.ArrayList;

public class MenuTest extends TestCase {

    ArrayList<MenuItem> testList = new ArrayList<MenuItem>();
    Menu testMenu;

    @Override
    public void setUp() throws Exception {
        testList.add(new MenuItem("Coke", new GBP(150), "Drinks"));
        testMenu = new Menu(testList);
    }

    public void testSetOrderId() throws Exception {
        testMenu.setOrderId(100);
        assertNotNull(testMenu.getOrderId());
    }

    public void testGetOrderId() throws Exception {
        testMenu.setOrderId(100);
        assertEquals(100, testMenu.getOrderId());
    }

    public void testGetCategories() throws Exception {
        ArrayList<String> categoryList = new ArrayList<String>();
        categoryList.add("Drinks");
        assertEquals(categoryList, testMenu.getCategories());
    }

    public void testAdd() throws Exception {
        MenuItem newItem = new MenuItem("Chicken", new GBP(400), "Mains");
        testMenu.add(newItem);
        testMenu.sortCategories();
        ArrayList<String> categoryList = new ArrayList<String>();
        categoryList.add("Drinks");
        categoryList.add("Mains");
        assertEquals(categoryList, testMenu.getCategories());
    }

    public void testGet() throws Exception {
        assertEquals(new MenuItem("Coke", new GBP(150), "Drinks"), testMenu.get(0));
    }

    public void testSize() throws Exception {
        assertEquals(1, testMenu.size());
    }

    public void testGetCategoryItems() throws Exception {
        ArrayList<MenuItem> menuList = new ArrayList<MenuItem>();
        menuList.add(new MenuItem("Coke", new GBP(150), "Drinks"));
        assertEquals(menuList, testMenu.getCategoryItems("Drinks"));
    }

    public void testGetCategoryItemsNull() throws Exception {
        ArrayList<MenuItem> menuList = new ArrayList<MenuItem>();
        assertEquals(menuList, testMenu.getCategoryItems(null));
    }

    public void testRemoveNonZero() throws Exception {
        testMenu.get(0).setQuantity(1);
        Menu testNonZero = Menu.removeNonZero(testMenu);
        assertEquals(new MenuItem("Coke", new GBP(150), "Drinks").toString(), testNonZero.get(0).toString());
    }

    public void testResetQuantities() throws Exception {
        testMenu.resetQuantities();
        assertEquals(0, testMenu.get(0).getQuantity());
    }

    public void testToString() throws Exception {
        assertEquals("Menu{[MenuItem{Coke, £1.50}]}", testMenu.toString());
    }
}