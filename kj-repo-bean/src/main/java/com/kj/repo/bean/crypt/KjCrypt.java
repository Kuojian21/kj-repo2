package com.kj.repo.bean.crypt;

import javax.crypto.Cipher;

import org.apache.commons.pool2.impl.GenericObjectPool;

import com.kj.repo.bean.pool.KjPool;

abstract class KjCrypt extends KjPool<Cipher> {

    public KjCrypt(GenericObjectPool<Cipher> pool) {
        super(pool);
    }
    
    public byte[] crypt(byte[] src) throws Exception {
        return super.execute(t -> {
            return t.doFinal(src);
        });
    }

}
