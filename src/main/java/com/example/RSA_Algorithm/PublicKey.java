package com.example.RSA_Algorithm;

import java.io.Serializable;
import java.math.BigInteger;

public class PublicKey implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigInteger e;
    private BigInteger n;

    public PublicKey(BigInteger e, BigInteger n) {
        this.e = e;
        this.n = n;
    }

    public PublicKey() {
    }

    public BigInteger getE() {
        return e;
    }

    public void setE(BigInteger e) {
        this.e = e;
    }

    public BigInteger getN() {
        return n;
    }

    public void setN(BigInteger n) {
        this.n = n;
    }
}
