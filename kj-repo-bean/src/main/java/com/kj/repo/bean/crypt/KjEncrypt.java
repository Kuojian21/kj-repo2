package com.kj.repo.bean.crypt;

import java.nio.charset.Charset;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class KjEncrypt extends KjCrypt {

    public KjEncrypt(String algorithm, SecretKey key, IvParameterSpec ivp) {

        super(new GenericObjectPool<>(new BasePooledObjectFactory<Cipher>() {
            @Override
            public PooledObject<Cipher> wrap(Cipher cipher) {
                return new DefaultPooledObject<Cipher>(cipher) {
                    @Override
                    public void invalidate() {
                        super.invalidate();
                    }
                };
            }

            @Override
            public Cipher create() throws Exception {
                Cipher cipher = Cipher.getInstance(algorithm);
                cipher.init(Cipher.ENCRYPT_MODE, key, ivp);
                return cipher;
            }

        }, KjCryptFactory.config()));
    }

    public String encrypt(String str) {
        try {
            return new String(Base64.encodeBase64(this.crypt(str.getBytes(Charset.forName("UTF-8")))), Charset.forName("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
