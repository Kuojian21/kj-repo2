package com.kj.repo.bean.sign;

import java.security.PrivateKey;
import java.security.Signature;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

import com.kj.repo.bean.pool.KjPool;

public class KjSignatureSign extends KjPool<Signature, byte[]> implements KjSign {
    public KjSignatureSign(String algorithm, PrivateKey privateKey) {
        super(new GenericObjectPool<>(new BasePooledObjectFactory<Signature>() {
            @Override
            public PooledObject<Signature> wrap(Signature signature) {
                return new DefaultPooledObject<Signature>(signature) {
                    @Override
                    public void invalidate() {
                        super.invalidate();
                    }
                };
            }

            @Override
            public Signature create() throws Exception {
                Signature signature = Signature.getInstance(algorithm);
                signature.initSign(privateKey);
                return signature;
            }

        }, KjSignFactory.config()));
    }

    @Override
    public byte[] execute(Signature signature, byte[] bytes, Object... args) throws Exception {
        signature.update(bytes);
        return signature.sign();
    }

    @Override
    public byte[] sign(byte[] src) throws Exception {
        return super.execute(src);
    }

}
