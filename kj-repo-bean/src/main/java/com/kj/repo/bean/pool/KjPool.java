package com.kj.repo.bean.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;

import com.kj.repo.base.func.KjConsumer;
import com.kj.repo.base.func.KjFunction;
import com.kj.repo.bean.base.KjBase;

public abstract class KjPool<T> extends KjBase {

    private final KjFactory<T> kjFactory;

    public KjPool(KjFactory<T> kjFactory) {
        super(kjFactory::close);
        this.kjFactory = kjFactory;
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

    public final <R> R execute(KjFunction<T, R> function) throws Exception {
        T t = null;
        try {
            t = kjFactory.borrowObject();
            return function.apply(t);
        } finally {
            if (t != null) {
                kjFactory.returnObject(t);
            }
        }
    }

    public final void execute(KjConsumer<T> consumer) throws Exception {
        T t = null;
        try {
            t = kjFactory.borrowObject();
            consumer.accept(t);
        } finally {
            if (t != null) {
                kjFactory.returnObject(t);
            }
        }
    }

}
