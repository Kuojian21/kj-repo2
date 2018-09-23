package com.kj.repo.bean.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;

public abstract class KjPool<T, H> {

    private final KjFactory<T> kjFactory;

    public KjPool(KjFactory<T> kjFactory) {
        this.kjFactory = kjFactory;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                kjFactory.close();
                System.out.println("close com.java.kj.base.KjPool:" + KjPool.this.getClass().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    public KjPool(GenericObjectPool<T> pool) {
        this(new KjFactory<T>() {
            @Override
            public T borrowObject() throws Exception {
                return pool.borrowObject();
            }

            @Override
            public void returnObject(T obj) throws Exception {
                pool.returnObject(obj);
            }

            @Override
            public void close() throws Exception {
                pool.close();
            }
        });
    }

    public final <R> R execute(H h, Object... objs) throws Exception {
        T t = null;
        try {
            t = kjFactory.borrowObject();
            return this.execute(t, h, objs);
        } finally {
            if (t != null) {
                kjFactory.returnObject(t);
            }
        }
    }

    public abstract <R> R execute(T t, H h, Object... args) throws Exception;

}
