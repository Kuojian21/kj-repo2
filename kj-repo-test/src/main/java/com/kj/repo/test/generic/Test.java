package com.kj.repo.test.generic;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

	public static void main(String[] args) {
		System.out.println(GenericHelper.extract(A.class, C.class));
		System.out.println(A.class.isAssignableFrom(B.class));
	}

	public static class A<T> {

	}

	public static class B<ST, Y> extends A<ST> {

	}

	public static class C extends B<Integer, Byte> {

	}

}
