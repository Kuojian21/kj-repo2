package com.kj.repo.bean.jedis;

import java.io.Closeable;

import com.kj.repo.bean.pool.KjFactory;
import com.kj.repo.bean.pool.KjPool;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.util.Pool;

public class KjJedis<T extends Closeable> extends KjPool<T> {

    private final Jedis JEDIS;

    public KjJedis(Pool<T> pool) {
        super(new KjFactory<T>() {
            @Override
            public T borrowObject() throws Exception {
                return pool.getResource();
            }

            @Override
            public void returnObject(T obj) throws Exception {
                obj.close();
            }

            @Override
            public void close() throws Exception {
                pool.close();
            }
        });
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Jedis.class);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> KjJedis.this.execute(t -> {
            return method.invoke(t, args);
        }));
        JEDIS = (Jedis) enhancer.create();
    }

    public JedisCommands jedisCommands() {
        return JEDIS;
    }

    public Jedis jedis() {
        return JEDIS;
    }

}