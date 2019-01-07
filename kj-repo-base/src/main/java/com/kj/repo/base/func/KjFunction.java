package com.kj.repo.base.func;

/**
 * @author kj
 */
@FunctionalInterface
public interface KjFunction<T, R> {

    R apply(T t) throws Exception;

}
