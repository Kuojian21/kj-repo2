package com.kj.repo.base.func;

@FunctionalInterface
public interface KjConsumer<T> {

	void accept(T t) throws Exception;
	
}
