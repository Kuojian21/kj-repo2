package com.kj.repo.test.generic;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericHelper {

	public static Type extract(Class<?> parent, Class<?> clazz) {
		if (parent.isAssignableFrom(clazz) && !clazz.equals(parent) && !Object.class.equals(parent)) {
			if (parent.isInterface()) {

			} else {
				while (!clazz.getSuperclass().equals(parent)) {
					clazz = clazz.getSuperclass();
					if (clazz.equals(Object.class)) {
						return null;
					}
				}
			}

			ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
			System.out.println(parameterizedType);
			Type[] types = parameterizedType.getActualTypeArguments();
			if (types.length > 0) {
				return types[0];
			}

		}
		return null;
	}

}
