package com.g1453012.btill.Shared;

import com.google.gson.Gson;

import org.bitcoin.protocols.payments.Protos;

public class BTMessage {
    private String header;
    private byte[] body;

    public BTMessage(String header) {
        this.header = header;
        this.body = null;
    }

    public BTMessage(String header, byte[] body) {
        this.header = header;
        this.body = body;
    }

    public BTMessage(String header, String body) {
        this.header = header;
        this.body = body.getBytes();
    }

    /*public BTMessage(String receivedString) {
        BTMessage message = new Gson().fromJson(receivedString, BTMessage.class);
        this.header = message.header;
        this.body = message.body;
    }*/

    public BTMessage(Protos.Payment payment) {
        this.header = "SETTLE_BILL";
        this.body = payment.toByteArray();
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBodyString() {
        return new String(body, 0, body.length);
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setBodyString(String body) {
        this.body = body.getBytes();
    }



    public byte[] getBytes() {
        String json = new Gson().toJson(this, BTMessage.class);
        return json.getBytes();
    }
}
