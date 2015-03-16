package com.g1453012.btill.Shared;

import junit.framework.TestCase;

public class GBPTest extends TestCase {

    GBP testGBP = new GBP(500);
    GBP anotherGBP = new GBP(100);

    public void testGetPence() throws Exception {
        assertEquals(500, testGBP.getPence());
    }

    public void testPlus() throws Exception {
        assertEquals(new GBP(600), testGBP.plus(anotherGBP));
    }

    public void testMinus() throws Exception {
        assertEquals(new GBP(400), testGBP.minus(anotherGBP));
    }

    public void testTimes() throws Exception {
        assertEquals(new GBP(1000), testGBP.times(2));
    }

    public void testToString() throws Exception {
        assertEquals("£5.00", testGBP.toString());
    }
}