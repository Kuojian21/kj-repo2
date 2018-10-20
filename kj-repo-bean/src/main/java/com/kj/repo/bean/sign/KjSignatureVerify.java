package com.kj.repo.bean.sign;

import java.security.PublicKey;
import java.security.Signature;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

import com.kj.repo.bean.pool.KjPool;

public class KjSignatureVerify extends KjPool<Signature> implements KjVerify {

    public KjSignatureVerify(String algorithm, PublicKey publicKey) {
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
                signature.initVerify(publicKey);
                return signature;
            }

        }, KjSignFactory.config()));
    }

    @Override
    public boolean verify(byte[] src, byte[] sign) throws Exception {
        return super.execute(t -> {
            t.update(src);
            return t.verify(sign);
        });
    }
}
