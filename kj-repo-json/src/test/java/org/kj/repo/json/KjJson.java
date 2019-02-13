package org.kj.repo.json;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class KjJson {

	public static final ConcurrentMap<Class<?>, PropertyDescriptor[]> decriptorsMap = new ConcurrentHashMap<Class<?>, PropertyDescriptor[]>();

	public static StringBuilder toString(Object obj, Set<Object> sets) {
		StringBuilder sb = new StringBuilder();
		if (obj == null) {
			sb.append("null");
			return sb;
		}
		Class<?> clazz = obj.getClass();
		if (clazz.isArray()) {
			sb.append("[");
			Object[] objs = (Object[]) obj;
			if (objs.length > 0) {
				if (!sets.contains(obj)) {
					sets = new HashSet<Object>(sets);
					sets.add(obj);
					sb.append(toString(objs[0], sets));
					for (int i = 1; i < objs.length; i++) {
						sb.append(",");
						sb.append(toString(objs[i], sets));
					}
				}

			}
			sb.append("]");
		} else if (clazz.isPrimitive()) {
			sb.append(obj.toString());
		} else if (clazz.equals(String.class)) {
			sb.append("\"");
			sb.append(obj.toString());
			sb.append("\"");
		} else {

			PropertyDescriptor[] despritors = decriptorsMap.get(clazz);
			if (despritors == null) {
				try {
					despritors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
				} catch (IntrospectionException e) {
					e.printStackTrace();
					despritors = new PropertyDescriptor[0];
				}
				decriptorsMap.putIfAbsent(clazz, despritors);
			}

			sb.append("{");
			if (despritors.length > 0) {
				if (!sets.contains(obj)) {
					sets = new HashSet<Object>(sets);
					sets.add(obj);
					sb.append("\"" + despritors[0].getName() + "\":");
					try {
						sb.append(toString(despritors[0].getReadMethod().invoke(obj, new Object[0]), sets));
					} catch (Exception e) {
						sb.append("\"\"");
					}
					for (int i = 1; i < despritors.length; i++) {
						sb.append("\"" + despritors[i].getName() + "\":");
						try {
							sb.append(toString(despritors[0].getReadMethod().invoke(obj, new Object[0]), sets));
						} catch (Exception e) {
							sb.append("\"\"");
						}
					}
				}
			}
			sb.append("}");
		}
		return sb;
	}

}
