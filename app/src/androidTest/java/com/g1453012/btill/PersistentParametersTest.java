package com.g1453012.btill;

import com.g1453012.btill.Shared.Bill;
import com.g1453012.btill.Shared.GBP;

import junit.framework.TestCase;

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;

public class PersistentParametersTest extends TestCase {

    Bill testBill = new Bill("This is a memo", "http://www.b-till.com", null, Coin.valueOf(1000), new GBP(500), new Wallet(TestNet3Params.get()));
    PersistentParameters params = new PersistentParameters();

    public void testSetBill() throws Exception {
        params.setBill(testBill);
        assertEquals(testBill, params.getBill());
    }

    public void testGetBill() throws Exception {
        params.setBill(testBill);
        assertEquals(testBill, params.getBill());
    }

    public void testResetBill() throws Exception {
        params.setBill(testBill);
        params.resetBill();
        assertNull(params.getBill());
    }

    public void testSetWallet() throws Exception {
        params.setWallet(new Wallet(TestNet3Params.get()));
        assertNotNull(params.getWallet());
    }
}