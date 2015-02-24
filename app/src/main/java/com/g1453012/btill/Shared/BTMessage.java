package com.g1453012.btill.Shared;

import com.google.gson.Gson;

public class BTMessage {

    private String header;
    private byte[] body;

    public BTMessage(String header, byte[] body) {
        this.header = header;
        this.body = body;
    }

    public String getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }

    public String getBodyString() {
        return new String(body, 0, body.length);
    }

    public byte[] getBytes() {
        String json = new Gson().toJson(this, BTMessage.class);
        return json.getBytes();
    }
}
