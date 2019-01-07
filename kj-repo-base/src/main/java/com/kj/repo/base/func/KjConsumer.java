package com.kj.repo.base.func;

/**
 * @author kj
 */
@FunctionalInterface
public interface KjConsumer<T> {

    void accept(T t) throws Exception;

}
