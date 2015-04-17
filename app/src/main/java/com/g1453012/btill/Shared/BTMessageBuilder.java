package com.g1453012.btill.Shared;

import com.google.gson.Gson;

import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.Coin;

public class BTMessageBuilder {

    private String header = null;
    private byte[] body = null;

    public BTMessageBuilder(String header) {
        this.header = header;
    }

    public BTMessageBuilder(int orderId, Protos.Payment payment, GBP gbpAmount, Coin btcAmount, LocationData locationData) {
        this.header = "SETTLE_BILL";
        String json = new Gson().toJson(new SignedBill(orderId, payment, gbpAmount, btcAmount, locationData), SignedBill.class);
        this.body = json.getBytes();
    }

    public BTMessageBuilder(Menu menu) {
        this.header = "MAKE_ORDER";
        String json = new Gson().toJson(menu, Menu.class);
        this.body = json.getBytes();
    }

    public BTMessageBuilder(LocationData data) {
        this.header = "LOCATION";
        String json = new Gson().toJson(data, LocationData.class);
        this.body = json.getBytes();
    }

    public BTMessage build() {
        return new BTMessage(header, body);
    }
}
