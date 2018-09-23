package com.kj.repo.bean.pool;

public interface KjFactory<T> {

    T borrowObject() throws Exception;

    void returnObject(T obj) throws Exception;

    void close() throws Exception;
}
