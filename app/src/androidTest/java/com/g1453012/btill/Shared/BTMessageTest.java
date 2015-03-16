package com.g1453012.btill.Shared;

import com.google.gson.Gson;

import junit.framework.TestCase;

public class BTMessageTest extends TestCase {

    BTMessage testMessage = new BTMessage(Status.OK.toString(), new String("This is a body").getBytes());

    public void testGetHeader() throws Exception {
        assertEquals(Status.OK.toString(), testMessage.getHeader());
    }

    public void testGetBodyString() throws Exception {
        assertEquals("This is a body", testMessage.getBodyString());
    }

    public void testGetBytes() throws Exception {
        BTMessage newMessage = new Gson().fromJson(new String(testMessage.getBytes()), BTMessage.class);
        assertEquals(Status.OK.toString(), newMessage.getHeader());
    }
}