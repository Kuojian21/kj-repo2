package com.kj.repo.bean.jedis;

import java.io.Closeable;
import java.lang.reflect.Method;

import com.kj.repo.bean.pool.KjFactory;
import com.kj.repo.bean.pool.KjPool;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.util.Pool;

public class KjJedis<T extends Closeable> extends KjPool<T, Method> {

    private final Jedis JEDIS;

    private final Pool<T> pool;

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
        this.pool = pool;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Jedis.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                return KjJedis.this.execute(method, args);
            }
        });
        JEDIS = (Jedis) enhancer.create();
    }

    public JedisCommands jedisCommands() {
        return JEDIS;
    }

    public Jedis jedis() {
        return JEDIS;
    }

    @Override
    public <R> R execute(T t, Method method, Object... args) throws Exception {
        Object[] objs = (Object[]) args[0];
        return (R) method.invoke(t, objs);
    }
}