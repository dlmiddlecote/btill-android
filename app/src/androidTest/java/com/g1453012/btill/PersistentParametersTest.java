package com.g1453012.btill;

import com.g1453012.btill.Shared.GBP;
import com.g1453012.btill.Shared.NewBill;

import junit.framework.TestCase;

public class PersistentParametersTest extends TestCase {


    PersistentParameters params = new PersistentParameters();

    public void testGetBill() throws Exception {
        NewBill testBill = new NewBill(new GBP(500));
        params.setBill(testBill);
        assertEquals(testBill, params.getBill());
    }

    public void testSetBill() throws Exception {
        NewBill testBill = new NewBill(new GBP(500));
        params.setBill(testBill);
        assertEquals(testBill, params.getBill());
    }

    public void testResetBill() throws Exception {
        params.setBill(new NewBill(new GBP(500)));
        params.resetBill();
        assertEquals(null, params.getBill());
    }
}