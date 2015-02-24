package com.g1453012.btill.Shared;


import org.bitcoinj.core.Coin;

import java.util.Date;

public class Receipt {
    public static Receipt receipt(GBP gbp, Coin bitcoins, String memo) {
        return new Receipt(new Date(), gbp, bitcoins,memo );
    }


    public Date getDate() {
        return date;
    }

    public GBP getGbp() {
        return gbp;
    }

    public Coin getBitcoins() {
        return bitcoins;
    }

    public String getMemo() {
        return memo;
    }

    private final Date date;
    private final GBP gbp;
    private final Coin bitcoins;
    private final String memo;




    public Receipt(Date date, GBP gbp, Coin bitcoins, String memo) {
        this.date = date;
        this.gbp = gbp;
        this.bitcoins = bitcoins;
        this.memo = memo;
    }
}
