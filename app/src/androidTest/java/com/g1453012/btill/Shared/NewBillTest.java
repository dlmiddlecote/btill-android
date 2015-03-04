package com.g1453012.btill.Shared;

import junit.framework.TestCase;

public class NewBillTest extends TestCase {

    NewBill bill = new NewBill(new GBP(500));

    public void testGetAmount() throws Exception {
        GBP gbp = bill.getAmount();
        assertEquals("Should be 500", new GBP(500), gbp);
    }
}