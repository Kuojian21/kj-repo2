package com.kj.repo.bean.crypt;

import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class KjCryptFactory {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private String algorithm;
    private Key key;
    private IvParameterSpec ivp;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private volatile KjDecrypt kjDecrypt;
    private volatile KjEncrypt kjEncrypt;

    public static GenericObjectPoolConfig config() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(10);
        config.setMaxTotal(100);
        config.setMaxWaitMillis(30000);
        return config;
    }

    public static KjCryptFactory factory(String algorithm, String padding, String keyAlgorithm, String keyValue)
            throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        KjCryptFactory kjCryptFactory = new KjCryptFactory();
        kjCryptFactory.algorithm = algorithm;
        kjCryptFactory.key = loadKey(keyAlgorithm, keyValue);
        kjCryptFactory.ivp = loadIvp(padding);
        return kjCryptFactory;
    }

    public static SecretKey loadKey(String keyAlgorithm, String keyValue)
            throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException {
        return SecretKeyFactory.getInstance(keyAlgorithm)
                .generateSecret(new DESedeKeySpec(keyValue.getBytes(Charset.forName("UTF-8"))));
    }

    public static IvParameterSpec loadIvp(String padding) {
        return new IvParameterSpec(padding.getBytes(Charset.forName("UTF-8")));
    }

    public static PublicKey laodPublicKey(String algorithm, byte[] key) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
            return KeyFactory.getInstance(algorithm).generatePublic(keySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PrivateKey loadPrivateKey(String algorithm, byte[] key) {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
            return KeyFactory.getInstance(algorithm).generatePrivate(keySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SecretKey generateKey(String algorithm, Integer keysize, AlgorithmParameterSpec params) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
            kgen.init(new SecureRandom());
            if (keysize != null) {
                kgen.init(keysize);
            }
            if (params != null) {
                kgen.init(params);
            }
            return kgen.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static KeyPair generateKeyPair(String algorithm, Integer keysize,
                                          AlgorithmParameterSpec params) {
        try {
            KeyPairGenerator kgen = KeyPairGenerator.getInstance(algorithm);
            if (keysize != null) {
                kgen.initialize(keysize, new SecureRandom());
            } else if (params != null) {
                kgen.initialize(params, new SecureRandom());
            }
            return kgen.generateKeyPair();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public KjDecrypt kjDecrypt() {
        if (kjDecrypt == null) {
            synchronized (this) {
                kjDecrypt = new KjDecrypt(KjCryptFactory.this.algorithm, KjCryptFactory.this.key,
                        KjCryptFactory.this.ivp);
            }
        }
        return kjDecrypt;
    }

    public KjEncrypt kjEncrypt() {
        if (kjEncrypt == null) {
            synchronized (this) {
                kjEncrypt = new KjEncrypt(KjCryptFactory.this.algorithm, KjCryptFactory.this.key,
                        KjCryptFactory.this.ivp);
            }
        }
        return kjEncrypt;
    }
    
}
