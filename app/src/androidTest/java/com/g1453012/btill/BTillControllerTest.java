package com.g1453012.btill;

import android.graphics.Bitmap;

import junit.framework.TestCase;

import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;

public class BTillControllerTest extends TestCase {

    public void testGenerateQR() throws Exception {
        Wallet testWallet = new Wallet(TestNet3Params.get());
        Bitmap map = BTillController.generateQR(testWallet);
        assertNotNull(map);
    }
}