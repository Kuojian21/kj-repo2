package com.kj.repo.bean.sign;

import java.security.PublicKey;
import java.security.Signature;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

import com.kj.repo.bean.pool.KjPool;

public class KjSignatureVerify extends KjPool<Signature, byte[]> implements KjVerify {

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
    public Boolean execute(Signature signature, byte[] bytes, Object... args) throws Exception {
        signature.update(bytes);
        return signature.verify((byte[]) args[0]);
    }

    @Override
    public boolean verify(byte[] src, byte[] sign) throws Exception {
        return super.execute(src, sign);
    }
}
