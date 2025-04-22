package com.example.RSA_Algorithm;

import java.math.BigInteger;

public class PrivateKey {
    private BigInteger d;
    private BigInteger p;
    private BigInteger q;

    public PrivateKey(BigInteger d, BigInteger p, BigInteger q) {
        this.d = d;
        this.p = p;
        this.q = q;
    }

    public PrivateKey() {
    }

    public BigInteger getD() {
        return d;
    }

    public void setD(BigInteger d) {
        this.d = d;
    }

    public BigInteger getP() {
        return p;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public BigInteger getQ() {
        return q;
    }

    public void setQ(BigInteger q) {
        this.q = q;
    }
}
