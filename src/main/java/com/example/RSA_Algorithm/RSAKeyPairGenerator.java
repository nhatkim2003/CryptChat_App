package com.example.RSA_Algorithm;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSAKeyPairGenerator {
    public static final int BIT_LENGTH = 512 ;
//    public static final int BIT_LENGTH = 8 ;

    private static final SecureRandom random = new SecureRandom();
    public static PublicKey publicKey;
    public static PrivateKey privateKey;

    public RSAKeyPairGenerator() {
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        RSAKeyPairGenerator.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        RSAKeyPairGenerator.privateKey = privateKey;
    }

    private static BigInteger generateSafePrime() {
        while (true) {
            // Generate a random prime p' with bit length - 1
            BigInteger pPrime = BigInteger.probablePrime(BIT_LENGTH - 1, random);
            // Calculate p = 2 * p' + 1
            BigInteger p = pPrime.multiply(BigInteger.TWO).add(BigInteger.ONE);
            // Check if p is also prime
            if (p.isProbablePrime(100)) {
                return p; // Return p if it is a safe prime
            }
        }
    }

    public static void generateKeys() {
        privateKey = new PrivateKey();
        publicKey = new PublicKey();

        BigInteger p = generateSafePrime();
        BigInteger q = generateSafePrime();

        System.out.println("p: " + p);
        System.out.println("q: " + q);

        BigInteger n = p.multiply(q);
        System.out.println("n: " + n);

        BigInteger m = (p.subtract(BigInteger.valueOf(1)).multiply((q.subtract(BigInteger.valueOf(1)))));
        System.out.println("m: " + m);

        BigInteger e;
        do {
            e = new BigInteger(m.bitLength(), random); // Tạo giá trị ngẫu nhiên cho e
        } while (e.compareTo(BigInteger.ONE) <= 0 || e.compareTo(m) >= 0 || !e.gcd(m).equals(BigInteger.ONE));
        System.out.println("e: " + e);

        BigInteger d = algorithm_Extended_Euclidean(e, m);

        publicKey.setE(e);
        publicKey.setN(n);

        privateKey.setD(d);
        privateKey.setP(p);
        privateKey.setQ(q);

        System.out.println("Khoá public (" + publicKey.getE() + ", " + publicKey.getN() +")" );
        System.out.println("Khoá private (" + privateKey.getD() + ", " + privateKey.getP() + ", " + privateKey.getQ() +")" );

    }

    private static BigInteger algorithm_Extended_Euclidean(BigInteger e, BigInteger m) {
        BigInteger n1 = e;
        BigInteger n2 = m;

        BigInteger a1 = BigInteger.ONE;
        BigInteger b1 = BigInteger.ZERO;
        BigInteger a2 = BigInteger.ZERO;
        BigInteger b2 = BigInteger.ONE;

        while (!n2.equals(BigInteger.ZERO)) {
            BigInteger[] quotientAndRemainder = n1.divideAndRemainder(n2);
            BigInteger q = quotientAndRemainder[0];
            BigInteger r = quotientAndRemainder[1];

            BigInteger t = a2;
            a2 = a1.subtract(q.multiply(a2));
            a1 = t;

            t = b2;
            b2 = b1.subtract(q.multiply(b2));
            b1 = t;

            n1 = n2;
            n2 = r;
        }

        // At the end of the loop, n1 is the gcd, and a1 is the modular inverse if gcd is 1
        if (n1.equals(BigInteger.ONE)) {
            return a1.mod(m); // Ensure the result is positive by taking mod m
        } else {
            return null; // No modular inverse exists if gcd is not 1
        }
    }

    public static void main(String[] args) {
        BigInteger p = BigInteger.valueOf(13);
        BigInteger q = BigInteger.valueOf(17);
//        System.out.println("p: " + p);
//        System.out.println("q: " + q);
        BigInteger m = (p.subtract(BigInteger.valueOf(1)).multiply((q.subtract(BigInteger.valueOf(1)))));
        BigInteger d = RSAKeyPairGenerator.algorithm_Extended_Euclidean(BigInteger.valueOf(7), m);
        System.out.println("d: " + d);
//        RSAKeyPairGenerator.generateKeys();

//        while (true) {
//            BigInteger pPrime = BigInteger.probablePrime(BIT_LENGTH - 1, random);
//            System.out.println(pPrime);
//        }
    }
}
