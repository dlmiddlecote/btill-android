package com.g1453012.btill.Shared;

import com.google.protobuf.InvalidProtocolBufferException;

import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.core.Coin;

/**
 * Created by dlmiddlecote on 26/02/15.
 */
public class SignedBill {

    private byte[] serialisedPayment;
    private GBP gbpAmount;
    private Coin btcAmount;
    private int orderId;

    public SignedBill(Protos.Payment payment, GBP gbpAmount, Coin btcAmount, int orderId) {
        serialisedPayment = payment.toByteArray();
        this.gbpAmount = gbpAmount;
        this.btcAmount = btcAmount;
        this.orderId = orderId;
    }

    public byte[] getSerialisedPayment() {
        return serialisedPayment;
    }

    public Protos.Payment getPayment() throws InvalidProtocolBufferException {
        return Protos.Payment.parseFrom(serialisedPayment);
    }

    public GBP getGbpAmount() {
        return gbpAmount;
    }

    public Coin getBtcAmount() {
        return btcAmount;
    }

    public int getOrderId() {
        return orderId;
    }
}
