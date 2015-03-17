package com.g1453012.btill.Shared;

import com.google.protobuf.InvalidProtocolBufferException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bitcoin.protocols.payments.Protos;
import org.bitcoin.protocols.payments.Protos.PaymentRequest;
import org.bitcoin.protocols.payments.Protos.PaymentRequest.Builder;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.protocols.payments.PaymentProtocol;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Bill implements Serializable {

    private static final long serialVersionUID = 8676831317792422678L;

    // TRANSIENT VARIABLES - NOT TRANSMITTED TO PHONE OR BACK
    private transient TestNet3Params net3Params = TestNet3Params.get();
    private transient String memo = null;
    private transient String paymentURL = null;
    private transient byte[] merchantData = null;

    private transient Wallet wallet = null;
    private transient PaymentRequest paymentRequest = null;

    private GBP gbpAmount = null;
    private byte[] request = null;
    private Coin coinAmount = null;
    private int orderId = 0;
    private Date orderIdDate;

    @Override
    public String toString() {
        return String.format("Bill for " + coinAmount.toFriendlyString());
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public Bill(String memo, String paymentURL, byte[] merchantData,
                Coin amount, GBP gbpAmount, Wallet wallet) {
        this.memo = memo;
        this.paymentURL = paymentURL;
        this.merchantData = merchantData;
        this.coinAmount = amount;
        this.gbpAmount = gbpAmount;
        this.wallet = wallet;
        orderId = setOrderId();
        orderIdDate = setOrderIdDate();
        buildPaymentRequest();
    }


    public GBP getGbpAmount() {
        return gbpAmount;
    }

    public Coin getCoinAmount() {
        return coinAmount;
    }

    public Protos.PaymentRequest getRequest() {
        try {
            return Protos.PaymentRequest.parseFrom(request);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return  null;
        }
    }

    public int getOrderId() {
        return orderId;
    }

    private int setOrderId() {
        return new Random().nextInt(99);
    }

    private Date setOrderIdDate() {
        return new Date(System.currentTimeMillis());
    }

    public String getDateAsString() {
        SimpleDateFormat format = new SimpleDateFormat("d/M/y HH:mm");
        return format.format(orderIdDate);
    }

    public void buildPaymentRequest() {
        Builder requestBuilder = PaymentProtocol.createPaymentRequest(net3Params,
                coinAmount, wallet.freshReceiveAddress(), memo, paymentURL,
                merchantData);
        paymentRequest = requestBuilder.build();
        request = paymentRequest.toByteArray();
    }

}
