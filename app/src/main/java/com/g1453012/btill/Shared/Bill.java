package com.g1453012.btill.Shared;

import com.google.protobuf.InvalidProtocolBufferException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bitcoin.protocols.payments.Protos;
import org.bitcoin.protocols.payments.Protos.PaymentRequest;
import org.bitcoin.protocols.payments.Protos.PaymentRequest.Builder;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.protocols.payments.PaymentProtocol;
import org.bitcoinj.protocols.payments.PaymentProtocolException;
import org.bitcoinj.protocols.payments.PaymentSession;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public void buildPaymentRequest() {
        Builder requestBuilder = PaymentProtocol.createPaymentRequest(net3Params,
                coinAmount, wallet.freshReceiveAddress(), memo, paymentURL,
                merchantData);
        paymentRequest = requestBuilder.build();
        request = paymentRequest.toByteArray();
    }

    /**
     * Pay this Bill using the {@link Wallet}
     */
    public SignedBill pay() {

        Protos.Payment payment = null;

        if (paymentRequest == null && request != null)
            try {
                paymentRequest = PaymentRequest.parseFrom(request);
            } catch (InvalidProtocolBufferException e) {
                System.err.println("Could not rebuild Payment Request!"); // TODO CHANGE TO LOGGING FORMAT
                e.printStackTrace();
            }

        try {
            // Create a PaymentSession using the PaymentRequest for helpful
            // functions
            PaymentSession session = new PaymentSession(paymentRequest, false);

            wallet.completeTx(session.getSendRequest());

            Wallet.SendRequest sendRequest = session.getSendRequest();

            sendRequest.ensureMinRequiredFee = false;
            sendRequest.fee = Coin.ZERO;
            sendRequest.feePerKb = Coin.ZERO;
            sendRequest.signInputs = false;

            System.out.println(sendRequest.tx.toString());

            wallet.completeTx(sendRequest);

            System.out.println("COMPLETED: "+sendRequest.tx);

            wallet.signTransaction(sendRequest);

            System.out.println("SIGNED: "+sendRequest.tx);

            List<Transaction> txil = new ArrayList<Transaction>();

            txil.add(sendRequest.tx);

            payment = session.getPayment(txil,wallet.currentReceiveAddress(),session.getMemo());

        } catch (PaymentProtocolException e) {
            System.err.println("Payment Protocol Exception!"); // TODO CHANGE TO LOGGING FORMAT
            e.printStackTrace();
        } catch (InsufficientMoneyException e) {
            System.err.println("Insufficient money to pay!");  // TODO CHANGE TO LOGGING FORMAT
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new SignedBill(payment, gbpAmount, coinAmount);
    }


}
