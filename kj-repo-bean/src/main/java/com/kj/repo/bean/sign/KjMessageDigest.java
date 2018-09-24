package com.kj.repo.bean.sign;

import java.security.MessageDigest;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

import com.kj.repo.bean.pool.KjPool;

public class KjMessageDigest extends KjPool<MessageDigest, byte[]> implements KjSign {

    public KjMessageDigest(String algorithm) {
        super(new GenericObjectPool<>(new BasePooledObjectFactory<MessageDigest>() {
            @Override
            public PooledObject<MessageDigest> wrap(MessageDigest digest) {
                return new DefaultPooledObject<MessageDigest>(digest) {
                    @Override
                    public void invalidate() {
                        super.invalidate();
                    }
                };
            }

            @Override
            public MessageDigest create() throws Exception {
                return MessageDigest.getInstance(algorithm);
            }

        }, KjSignFactory.config()));
    }

    @Override
    public byte[] execute(MessageDigest digest, byte[] bytes, Object... args) throws Exception {
        digest.update(bytes);
        return digest.digest();
    }

    @Override
    public byte[] sign(byte[] src) throws Exception {
        return super.execute(src);
    }
}
