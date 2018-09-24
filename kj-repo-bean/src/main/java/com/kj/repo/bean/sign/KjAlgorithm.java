package com.kj.repo.bean.sign;

public enum KjAlgorithm {

    HmacMD5("HmacMD5"), HmacSHA1("HmacSHA1"), HmacSHA256("HmacSHA256"),
    MD5("MD5"), SHA_1("SHA-1"), SHA_256("SHA-256"),
    SHA1withDSA("SHA1withDSA"), SHA1withRSA("SHA1withRSA"), SHA256withRSA("SHA256withRSA");

    private String algorithm;

    KjAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public static KjAlgorithm[] MAC = new KjAlgorithm[]{
            HmacMD5, HmacSHA1, HmacSHA256
    };

    public static KjAlgorithm[] DIGEST = new KjAlgorithm[]{
            MD5, SHA_1, SHA_256
    };

    public static KjAlgorithm[] SIGN = new KjAlgorithm[]{
            SHA1withDSA, SHA1withRSA, SHA256withRSA
    };

    public enum Type {
        MAC, DIGEST, SIGN, VERIFY
    }

}