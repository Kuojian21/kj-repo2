package com.kj.repo.base.func;

@FunctionalInterface
public interface KjFunction<T, R> {

	R apply(T t) throws Exception;

}
