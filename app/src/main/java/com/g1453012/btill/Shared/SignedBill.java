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
    private LocationData locationData;

    public SignedBill(int orderId, Protos.Payment payment, GBP gbpAmount, Coin btcAmount, LocationData locationData) {
        this.orderId = orderId;
        serialisedPayment = payment.toByteArray();
        this.gbpAmount = gbpAmount;
        this.btcAmount = btcAmount;
        this.locationData = locationData;
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

    public LocationData getLocationData() {
        return locationData;
    }
}
