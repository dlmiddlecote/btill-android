package com.g1453012.btill.Shared;

import junit.framework.TestCase;

import java.util.ArrayList;

public class BTMessageBuilderTest extends TestCase {

    public void testBuildHeaderOnly() throws Exception {
        BTMessageBuilder builder = new BTMessageBuilder("OK");
        BTMessage message = builder.build();
        assertEquals(Status.OK.toString(), message.getHeader());
        assertNull(message.getBody());
    }

    public void testBuildMenu() throws Exception {
        ArrayList<MenuItem> testList = new ArrayList<MenuItem>();
        testList.add(new MenuItem("Coke", new GBP(150), "Drinks"));
        Menu testMenu = new Menu(testList);
        BTMessage message = new BTMessageBuilder(testMenu).build();
        assertEquals(Command.MAKE_ORDER.toString(), message.getHeader());
        assertNotNull(message.getBody());
    }


    public void testSetHeader() throws Exception {

    }

    public void testSetBody() throws Exception {

    }

    public void testSetBody1() throws Exception {

    }
}