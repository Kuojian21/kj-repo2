package com.kj.repo.bean.crypt;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

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
    private SecretKey key;
    private IvParameterSpec ivp;

    private volatile KjDecrypt kjDecrypt;
    private volatile KjEncrypt kjEncrypt;

    public static GenericObjectPoolConfig config() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(10);
        config.setMaxTotal(100);
        config.setMaxWaitMillis(30000);
        return config;
    }

    public static KjCryptFactory factory(String algorithm, String padding, String keyAlgorithm, String keyValue) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        KjCryptFactory kjCryptFactory = new KjCryptFactory();
        kjCryptFactory.algorithm = algorithm;
        kjCryptFactory.key = SecretKeyFactory.getInstance(keyAlgorithm)
                .generateSecret(new DESedeKeySpec(keyValue.getBytes(Charset.forName("UTF-8"))));
        kjCryptFactory.ivp = new IvParameterSpec(padding.getBytes(Charset.forName("UTF-8")));
        return kjCryptFactory;
    }

    public KjDecrypt kjDecrypt() {
        if (kjDecrypt == null) {
            synchronized (this) {
                kjDecrypt = new KjDecrypt(KjCryptFactory.this.algorithm, KjCryptFactory.this.key, KjCryptFactory.this.ivp);
            }
        }
        return kjDecrypt;
    }

    public KjEncrypt kjEncrypt() {
        if (kjEncrypt == null) {
            synchronized (this) {
                kjEncrypt = new KjEncrypt(KjCryptFactory.this.algorithm, KjCryptFactory.this.key, KjCryptFactory.this.ivp);
            }
        }
        return kjEncrypt;
    }


}
