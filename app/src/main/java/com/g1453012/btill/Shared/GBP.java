package com.g1453012.btill.Shared;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class GBP {
    private final int pence;

    public GBP(int pence) {
        this.pence = pence;
    }

    public GBP() {
        this.pence = 0;
    }

    public int getPence() {
        return pence;
    }

    public GBP plus(GBP that) {
        return new GBP(pence + that.pence);
    }

    public GBP minus(GBP that) {
        return new GBP(pence - that.pence);
    }

    public GBP times(int mult) {
        return new GBP(pence * mult);
    }

    @Override
    public String toString() {
        float f = (float) (pence / 100.0);
        return String.format("£%.2f", f);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
