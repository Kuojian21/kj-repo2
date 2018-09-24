package com.kj.repo.bean.sign;

import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.google.common.base.Strings;

public class KjSignFactory {

    private KjAlgorithm.Type type;
    private String algorithm;
    private SecretKey key;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private volatile KjSign kjSign;
    private volatile KjVerify kjVerify;

    public static GenericObjectPoolConfig config() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(10);
        config.setMaxTotal(100);
        config.setMaxWaitMillis(30000);
        return config;
    }

    public static KjSignFactory factory(KjAlgorithm.Type type, String algorithm, String key, PublicKey publicKey, PrivateKey privateKey) {
        KjSignFactory factory = new KjSignFactory();
        factory.algorithm = algorithm;
        factory.key = loadKey(algorithm, key);
        factory.publicKey = publicKey;
        factory.privateKey = privateKey;
        return factory;
    }

    public static SecretKey loadKey(String keyAlgorithm, String keyValue) {
        try {
            if (Strings.isNullOrEmpty(keyValue)) {
                return null;
            }
            return SecretKeyFactory.getInstance(keyAlgorithm)
                    .generateSecret(new DESedeKeySpec(keyValue.getBytes(Charset.forName("UTF-8"))));
        } catch (Exception e) {
            return null;
        }

    }

    public void init() {
        switch (this.type) {
            case MAC:
                this.kjSign = new KjMac(this.algorithm, this.key);
                break;
            case DIGEST:
                this.kjSign = new KjMessageDigest(this.algorithm);
                break;
            case SIGN:
                this.kjSign = new KjSignatureSign(this.algorithm, this.privateKey);
                break;
            case VERIFY:
                this.kjVerify = new KjSignatureVerify(this.algorithm, this.publicKey);
                break;
            default:
        }
    }

    public KjSign kjSign() {
        if (this.kjSign == null) {
            synchronized (this) {
                if (this.kjSign == null) {
                    this.init();
                }
            }
        }
        return this.kjSign;
    }


    public KjVerify kjVerify() {
        if (this.kjVerify == null) {
            synchronized (this) {
                if (this.kjVerify == null) {
                    this.init();
                }
            }
        }
        return this.kjVerify;
    }

}
