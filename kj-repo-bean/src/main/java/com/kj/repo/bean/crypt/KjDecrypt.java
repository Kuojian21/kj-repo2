package com.kj.repo.bean.crypt;

import java.nio.charset.Charset;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class KjDecrypt extends KjCrypt {

    public KjDecrypt(String algorithm, Key key, IvParameterSpec ivp) {
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
                cipher.init(Cipher.DECRYPT_MODE, key, ivp);
                return cipher;
            }

        }, KjCryptFactory.config()));
    }

    public String decrypt(String str) {
        try {
            return new String(this.crypt(Base64.decodeBase64(str.getBytes(Charset.forName("UTF-8")))), Charset.forName("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
