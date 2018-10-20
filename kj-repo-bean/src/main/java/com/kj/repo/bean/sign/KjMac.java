package com.kj.repo.bean.sign;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

import com.kj.repo.bean.pool.KjPool;

public class KjMac extends KjPool<Mac> implements KjSign {

    public KjMac(String algorithm, SecretKey key) {
        super(new GenericObjectPool<>(new BasePooledObjectFactory<Mac>() {
            @Override
            public PooledObject<Mac> wrap(Mac mac) {
                return new DefaultPooledObject<Mac>(mac) {
                    @Override
                    public void invalidate() {
                        super.invalidate();
                    }
                };
            }

            @Override
            public Mac create() throws Exception {
                Mac mac = Mac.getInstance(algorithm);
                mac.init(key);
                return mac;
            }

        }, KjSignFactory.config()));
    }

    @Override
    public byte[] sign(byte[] src) throws Exception {
        return super.execute(t -> {
            t.update(src);
            return t.doFinal();
        });
    }
}
