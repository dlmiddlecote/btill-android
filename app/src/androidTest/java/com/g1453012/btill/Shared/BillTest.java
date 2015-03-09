package com.g1453012.btill.Shared;

import junit.framework.TestCase;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;

public class BillTest extends TestCase {

    Bill testBill = new Bill("This is a memo", "http://www.b-till.com", null, Coin.valueOf(1000), new GBP(500), new Wallet(TestNet3Params.get()));

    public void testGetGbpAmount() throws Exception {
        assertEquals(new GBP(500), testBill.getGbpAmount());
    }

    public void testGetCoinAmount() throws Exception {
        assertEquals(Coin.valueOf(1000), testBill.getCoinAmount());
    }

    public void testGetRequest() throws Exception {
        assertNotNull(testBill.getRequest());
    }

    /*public void testPay() throws Exception {
        SignedBill testSigned = testBill.pay();
        assertEquals(new GBP(500), testSigned.getGbpAmount());
    }*/

    public void testToString() throws Exception {
        assertEquals("Bill for 0.00001 BTC", testBill.toString());
    }
}