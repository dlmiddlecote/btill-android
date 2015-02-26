package com.g1453012.btill.Shared;

import com.google.protobuf.InvalidProtocolBufferException;

import org.bitcoin.protocols.payments.Protos;

/**
 * Created by dlmiddlecote on 26/02/15.
 */
public class SignedBill {

    private byte[] serialisedPayment;

    public SignedBill(Protos.Payment payment) {
        serialisedPayment = payment.toByteArray();
    }

    public byte[] getSerialisedPayment() {
        return serialisedPayment;
    }

    public Protos.Payment getPayment() throws InvalidProtocolBufferException {
        return Protos.Payment.parseFrom(serialisedPayment);
    }
}
