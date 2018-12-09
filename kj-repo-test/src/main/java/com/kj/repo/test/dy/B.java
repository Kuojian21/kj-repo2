package com.kj.repo.test.dy;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

class A {
	static void foo(Runnable runnable) {
		System.out.println("A.foo()");
		runnable.run();
	}

	static <T> T foo(Supplier<T> supplier) {
		System.out.println("A.foo() NEW");
		return supplier.get();
	}
	
	static <T> T foo(Function<String,T> fun) {
		System.out.println("A.foo() NEW");
		return fun.apply("a");
	}
}

public class B {
	public static void main(String[] args) {
		A.foo(B::myTest);
	}

	private static String myTest() {
		System.out.println("B.myTest() 1");
		return "test";
	}

	// private static String myTest(Object obj) {
	// System.out.println("B.myTest() 2");
	// return "test";
	// }
}