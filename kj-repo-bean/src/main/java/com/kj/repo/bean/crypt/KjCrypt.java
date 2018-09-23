package com.kj.repo.bean.crypt;

import javax.crypto.Cipher;

import org.apache.commons.pool2.impl.GenericObjectPool;

import com.kj.repo.bean.pool.KjPool;

abstract class KjCrypt extends KjPool<Cipher, byte[]> {

    public KjCrypt(GenericObjectPool<Cipher> pool) {
        super(pool);
    }

    @Override
    public byte[] execute(Cipher cipher, byte[] h, Object... args) throws Exception {
        return cipher.doFinal(h);
    }

    public byte[] crypt(byte[] src) throws Exception {
        return (byte[]) super.execute(src);
    }

}
