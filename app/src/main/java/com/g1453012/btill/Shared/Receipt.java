package com.g1453012.btill.Shared;


import com.google.protobuf.InvalidProtocolBufferException;

import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.Coin;
import org.bitcoinj.protocols.payments.PaymentProtocol;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Receipt {

    private final GBP gbp;
    private final Coin bitcoins;
    private byte[] paymentACKbytes = null;
    private Date date;
    private int orderId;

    public Receipt(Protos.Payment forPayment, GBP amount, Coin btcAmount, Date date, int orderId) {
        paymentACKbytes = PaymentProtocol.createPaymentAck(forPayment, "TRANSACTION SUCCEEDED:\n" + forPayment.getMemo()).toByteArray();
        gbp = amount;
        bitcoins = btcAmount;
        this.date = date;
        this.orderId = orderId;
    }

    public GBP getGbp() {
        return gbp;
    }

    public Coin getBitcoins() {
        return bitcoins;
    }

    public String getDateAsString() {
        SimpleDateFormat format = new SimpleDateFormat("d/M/y HH:mm");
        return format.format(date);
    }

    public int getOrderId() {
        return orderId;
    }

    public Protos.PaymentACK getPaymentACK() {
        try {
            return Protos.PaymentACK.parseFrom(paymentACKbytes);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }
}
