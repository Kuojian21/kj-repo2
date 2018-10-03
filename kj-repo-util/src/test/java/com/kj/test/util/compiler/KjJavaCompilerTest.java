package com.kj.test.util.compiler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.kj.repo.util.compiler.KjJavaCompiler;

public class KjJavaCompilerTest {

	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		String body = "public class CalculatorTest { "
				+ " public static int multiply(int multiplicand, int multiplier) { "
				+ " System.out.println(multiplicand); " + " System.out.println(multiplier);"
				+ "return multiplicand + multiplier; }" + "}";
		Class<?> clazz = KjJavaCompiler.compile("CalculatorTest", body);

		Method method = clazz.getMethod("multiply", new Class<?>[] { int.class, int.class });
		System.out.println(method.invoke(null, 2, 3));
	}

}
