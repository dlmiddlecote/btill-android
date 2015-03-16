package com.g1453012.btill.Shared;

import junit.framework.TestCase;

public class StatusTest extends TestCase {

    public void testValueOK() throws Exception {
        Status status = Status.valueOf("OK");
        assertEquals(Status.OK, status);
    }

    public void testValueNotFound() throws Exception {
        Status status = Status.valueOf("NOT_FOUND");
        assertEquals(Status.NOT_FOUND, status);
    }

}