package com.kj.repo.bean.crypt;

public enum KjAlgorithm {
    DiffieHellman("DiffieHellman", 1024), DSA("DSA", 1024), RSA_1024("RSA", 1024), RSA_2048("RSA", 2048), AES(
            "AES ", 128), DES("DES", 56), DESede("DESede", 168), HmacSHA1("HmacSHA1", 0), HmacSHA256("", 0);

    private KjAlgorithm(String name, int keysize) {
        this.name = name;
        this.keysize = keysize;
    }

    private final String name;
    private final int keysize;

    public String getName() {
        return name;
    }

    public int getKeysize() {
        return keysize;
    }

}