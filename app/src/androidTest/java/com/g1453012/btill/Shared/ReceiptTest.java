package com.g1453012.btill.Shared;

import junit.framework.TestCase;

import org.bitcoinj.core.Coin;

import java.util.Date;

public class ReceiptTest extends TestCase {

    Receipt testReceipt = new Receipt(new Date(1015, 1, 15), new GBP(500) , Coin.valueOf(1000));

    public void testReceipt() throws Exception {
        Receipt newReceipt = Receipt.receipt(new GBP(500), Coin.valueOf(1000));
        assertEquals("GBP 500", new GBP(500), newReceipt.getGbp());
        assertEquals("SATOSHIS 1000", Coin.valueOf(1000), newReceipt.getBitcoins());
    }

    public void testGetDate() throws Exception {
        assertEquals(new Date(1015, 1, 15), testReceipt.getDate());
    }

    public void testGetGbp() throws Exception {
        assertEquals(new GBP(500), testReceipt.getGbp());
    }

    public void testGetBitcoins() throws Exception {
        assertEquals(Coin.valueOf(1000), testReceipt.getBitcoins());
    }
}