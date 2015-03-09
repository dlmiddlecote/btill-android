package com.g1453012.btill.Shared;

import junit.framework.TestCase;

public class CommandTest extends TestCase {

    public void testToRequestMenu() throws Exception {
        Command command = Command.toCommand("Request_Menu");
        assertEquals(Command.REQUEST_MENU, command);
    }

    public void testToMakeOrder() throws Exception {
        Command command = Command.toCommand("Make_Order");
        assertEquals(Command.MAKE_ORDER, command);
    }

    public void testToSettleBill() throws Exception {
        Command command = Command.toCommand("Settle_bill");
        assertEquals(Command.SETTLE_BILL, command);
    }

    public void testToOther() throws Exception {
        Command command = null;
        try {
            command = Command.toCommand(null);
        } catch (Command.InvalidCommand e) {

        }
        assertNull(command);
    }
}