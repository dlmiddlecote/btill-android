package com.g1453012.btill.Shared;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bitcoin.protocols.payments.Protos;
import org.bitcoin.protocols.payments.Protos.PaymentRequest;
import org.bitcoin.protocols.payments.Protos.PaymentRequest.Builder;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Wallet;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.protocols.payments.PaymentProtocol;
import org.bitcoinj.protocols.payments.PaymentProtocolException;
import org.bitcoinj.protocols.payments.PaymentSession;
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.uri.BitcoinURIParseException;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class Bill implements Serializable {

    private GBP amount;
    private static final long serialVersionUID = 8676831317792422678L;

    // TRANSIENT VARIABLES - NOT TRANSMITTED TO PHONE OR BACK
    private transient TestNet3Params net3Params = TestNet3Params.get();
    private transient String memo = null;
    private transient String paymentURL = null;
    private transient byte[] merchantData = null;
    private transient Coin coinAmount = null;
    private transient Wallet wallet = null;
    private transient Builder requestBuilder = null;
    // PaymentRequest IS SENT TO PHONE
    private PaymentRequest request = null;

    public Bill(GBP amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return String.format("Bill for " + amount);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public PaymentRequest getRequest() {
        return request;
    }

    public void setRequest(PaymentRequest request) {
        this.request = request;
    }

    public Bill(String memo, String paymentURL, byte[] merchantData,
                Coin amount, Wallet wallet) {
        this.memo = memo;
        this.paymentURL = paymentURL;
        this.merchantData = merchantData;
        this.coinAmount = amount;
        this.wallet = wallet;
        buildPaymentRequest();
    }
    public Protos.PaymentRequest getRequest(String uri) {
        BitcoinURI mUri = null;
        try {
            mUri = new BitcoinURI(TestNet3Params.get(), uri);
        } catch (BitcoinURIParseException e) {
            System.out.println("Bitcoin URI Parse Exception");
        }

        org.bitcoinj.core.Address address = mUri.getAddress();
        Coin amount = mUri.getAmount();
        String memo = mUri.getMessage();
        String url = mUri.getPaymentRequestUrl();
        Protos.PaymentRequest.Builder requestBuilder = PaymentProtocol.createPaymentRequest(TestNet3Params.get(), amount, address, memo, url, null);
        Protos.PaymentRequest request = requestBuilder.build();
        return request;
    }


    public void buildPaymentRequest() {/*
        requestBuilder = PaymentProtocol.createPaymentRequest(net3Params,
                coinAmount, wallet.currentReceiveAddress(), memo, paymentURL,
                merchantData);
        request = requestBuilder.build();*/
    }

    /**
     * Pay this Bill using the {@link Wallet} - currently not functional due to
     * empty wallets :(
     */
    public void pay(Wallet wallet) {
        // TODO Pay this bill - currently not working as my wallets contain no
        // money!!!
        this.wallet = wallet;

        try {
            // Create a PaymentSession using the PaymentRequest for helpful
            // functions
            PaymentSession session = new PaymentSession(request, false);
            // Currently always throws error as I can't fill my wallets!
            Wallet.SendRequest sendRequest = session.getSendRequest();
            // Theoretically, this would create a transaction sendRequest.tx...
            this.wallet.completeTx(sendRequest);
        } catch (PaymentProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InsufficientMoneyException e) {
            // TODO Auto-generated catch block
            System.err.println("Insufficient money to pay!");
            e.printStackTrace();
        }
    }

    /*
     * Prints out session info for debugging purposes...
     */
    @Deprecated
    public void printSession() {

        try {
            PaymentSession session = new PaymentSession(request, false);

            System.out.println("NetworkParameters [1?]:\n"
                    + session.getNetworkParameters().getId());

            for (int i = 0; i < session.getOutputs().size(); i++)
                try {
                    System.out.println("Outputs [2]:\n"
                            + i
                            + " "
                            + session.getOutputs().get(i).amount
                            + " -> "
                            + new String(
                            session.getOutputs().get(i).scriptData,
                            "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    System.err.println("Outputs [2]:\n" + i
                            + ":\nFAILED TO ENCODE");
                    e.printStackTrace();
                }

            System.out.println("Creation Date [3]:\n"
                    + session.getDate().toString());

            if (session.getExpires() != null)
                System.out.println("Expiry Date [4]:\n"
                        + session.getExpires().toString());
            else
                System.out.println("Expiry Date [4]:\n(Does not expire)");

            System.out.println("Memo [5]:\n" + session.getMemo());

            System.out.println("PaymentURL [6]:\n" + session.getPaymentUrl());

            try {
                System.out.println("Merchant Data [7]:\n"
                        + new String(session.getMerchantData(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                System.err.println("Merchant Data [7]:\nFAILED TO ENCODE");
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Merchant Data [7]:\n(None found)");
            }

        } catch (PaymentProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }
}
