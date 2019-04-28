package com.kj.infra.savor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Data;

/**
 * 
 * @author kuojian21
 *
 */
public abstract class Savor<T> {

	private final Class<T> clazz;
	private final RowMapper<T> rowMapper;
	private final Model model;

	@SuppressWarnings("unchecked")
	protected Savor() {
		clazz = (Class<T>) (((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
		rowMapper = new BeanPropertyRowMapper<T>(clazz);
		model = Helper.model(clazz);
	}

	public int insert(List<T> objs) {
		if (objs == null || objs.isEmpty()) {
			return 0;
		}
		int rtn = 0;
		try {
			Map<String, List<T>> objMap = this.table(objs);
			for (Map.Entry<String, List<T>> entry : objMap.entrySet()) {
				SqlParams sqlParams = Helper.insert(getModel(), entry.getKey(), entry.getValue());
				rtn += this.getWriter().update(sqlParams.getSql().toString(), sqlParams.getParams());
			}
			return rtn;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int upsert(T obj, List<String> updateExprs) {
		if (obj == null) {
			return 0;
		}
		if (updateExprs == null || updateExprs.isEmpty()) {
			return this.insert(Lists.newArrayList(obj));
		}
		try {
			SqlParams sqlParams = Helper.upsert(getModel(), this.table(obj), obj, updateExprs);
			return this.getWriter().update(sqlParams.getSql().toString(), sqlParams.getParams());
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int delete(Map<String, Object> params) {
		try {
			SqlParams sqlParams = Helper.delete(getModel(), this.table(params), params);
			return this.getWriter().update(sqlParams.getSql().toString(), sqlParams.getParams());
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int update(Map<String, Object> newValues, Map<String, Object> params) {
		if (newValues == null || newValues.isEmpty()) {
			return 0;
		}
		try {
			SqlParams sqlParams = Helper.update(this.model, this.table(params), newValues, params);
			return this.getWriter().update(sqlParams.getSql().toString(), sqlParams.getParams());
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public List<T> select(Map<String, Object> params) {
		return this.select(null, params, null, null, null);
	}

	public List<T> select(List<String> names,
					Map<String, Object> params, List<String> orderExprs, Integer offset, Integer limit) {
		return this.select(names, params, orderExprs, offset, limit, this.getRowMapper());
	}

	@SuppressWarnings("checkstyle:HiddenField")
	public <R> List<R> select(List<String> names,
					Map<String, Object> params, List<String> orderExprs, Integer offset, Integer limit,
					RowMapper<R> rowMapper) {
		try {
			SqlParams sqlParams = Helper.select(this.model, this.table(params), names, params, orderExprs,
							offset, limit);
			return this.getWriter().query(sqlParams.getSql().toString(), sqlParams.getParams(), rowMapper);
		} catch (Exception e) {
			e.printStackTrace();
			return Lists.newArrayList();
		}
	}

	public Model getModel() {
		return model;
	}

	public Class<T> getClazz() {
		return clazz;
	}

	public RowMapper<T> getRowMapper() {
		return rowMapper;
	}

	protected Function<Object[], String> table() {
		return (objs) -> {
			return this.model.table;
		};
	}

	protected String table(Map<String, Object> params) {
		List<Property> properties = this.model.getTableProperties();
		if (properties.isEmpty()) {
			return this.model.getTable();
		}
		return this.table().apply(properties.stream().map(p -> params.get(p.getName())).toArray());
	}

	protected String table(T obj) {
		List<Property> properties = this.model.getTableProperties();
		if (properties.isEmpty()) {
			return this.model.getTable();
		}
		return this.table().apply(properties.stream().map(p -> p.get(obj)).toArray());
	}

	protected Map<String, List<T>> table(List<T> objs) {
		List<Property> properties = this.model.getTableProperties();
		if (properties.isEmpty()) {
			return Helper.newHashMap(this.model.getTable(), objs);
		} else {
			Map<Integer, String> tableMap = IntStream.range(0, objs.size())
							.boxed()
							.collect(Collectors.toMap(i -> i,
											i -> this.table().apply(properties.stream().map(p -> p.get(objs.get(i)))
															.toArray())));
			Map<String, List<T>> result = Helper.newHashMap();
			IntStream.range(0, objs.size())
							.boxed().forEach(i -> {
								result.getOrDefault(tableMap.get(i), Lists.newArrayList()).add(objs.get(i));
							});
			return result;
		}

	}

	public abstract NamedParameterJdbcTemplate getReader();

	public abstract NamedParameterJdbcTemplate getWriter();

	/**
	 * @author kuojian21
	 */
	public static class Helper {

		private static final ConcurrentMap<Class<?>, Model> MODELS = Maps.newConcurrentMap();

		public static Model model(Class<?> clazz) {
			return MODELS.computeIfAbsent(clazz, k -> {
				List<Property> properties = Arrays.stream(k.getDeclaredFields())
								.filter(f -> !Modifier.isStatic(f.getModifiers())
												&& !Modifier.isFinal(f.getModifiers()))
								.map(f -> {
									f.setAccessible(true);
									return new Property(f.getName(),
													CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, f.getName()),
													f,
													f.getAnnotation(PrimaryKey.class) != null,
													f.getAnnotation(PrimaryKey.class) == null
																	|| f.getAnnotation(PrimaryKey.class).insert());
								}).collect(Collectors.toList());
				List<String> tableKeys = Arrays.stream(k.getDeclaredFields())
								.filter(f -> f.getAnnotation(TableKey.class) != null)
								.map(f -> new Map.Entry<String, Integer>() {
									@Override
									public String getKey() {
										return f.getName();
									}

									@Override
									public Integer getValue() {
										return f.getAnnotation(TableKey.class).index();
									}

									@Override
									public Integer setValue(Integer value) {
										return null;
									}
								})
								.sorted(Comparator.comparing(Map.Entry::getValue))
								.map(Map.Entry::getKey)
								.collect(Collectors.toList());
				return new Model(clazz.getSimpleName(),
								CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, clazz.getSimpleName()),
								properties, tableKeys);
			});
		}

		@SuppressWarnings("unchecked")
		public static <K, V> Map<K, V> newHashMap(Object... objs) {
			Map<K, V> result = Maps.newHashMap();
			for (int i = 0, len = objs.length; i < len; i += 2) {
				result.put((K) objs[i], (V) objs[i + 1]);
			}
			return result;
		}

		public static <T> SqlParams insert(Model model, String table, List<T> objs) {
			StringBuilder sql = new StringBuilder();
			sql.append("insert ignore into ")
							.append(Strings.isNullOrEmpty(table) ? model.getTable() : table)
							.append("\n")
							.append(" (")
							.append(Joiner.on(",")
											.join(model.getInsertProperties().stream()
															.map(Property::getColumn)
															.collect(Collectors.toList())))
							.append(") ")
							.append("\n")
							.append("values")
							.append("\n")
							.append(Joiner.on(",\n").join(
											IntStream.range(0, objs.size()).boxed()
															.map(i -> {
																StringBuilder values = new StringBuilder();
																values.append("(")
																				.append(Joiner.on(",").join(model
																								.getInsertProperties()
																								.stream()
																								.map(p -> ":" + p
																												.getName()
																												+ i)
																								.collect(Collectors
																												.toList())))
																				.append(")");
																return values.toString();
															})
															.collect(Collectors.toList())));
			Map<String, Object> params = Maps.newHashMap();
			IntStream.range(0, objs.size()).boxed().forEach(i -> {
				model.getInsertProperties().stream().forEach(p -> {
					params.put(p.getName() + i, p.get(objs.get(i)));
				});
			});
			return SqlParams.model(sql, params);
		}

		public static <T> SqlParams upsert(Model model, String table, T obj, List<String> exprs) {
			StringBuilder sql = new StringBuilder();
			sql.append("insert into ")
							.append(Strings.isNullOrEmpty(table) ? model.getTable() : table)
							.append("\n")
							.append(" (")
							.append(Joiner.on(",")
											.join(model.getInsertProperties().stream().map(Property::getColumn)
															.collect(Collectors.toList())))
							.append(")\nvalues\n(")
							.append(Joiner.on(",")
											.join(model.getInsertProperties().stream().map(e -> ":" + e.getName())
															.collect(Collectors.toList())))
							.append(")\n ")
							.append("  on duplicate key update ")
							.append(Joiner.on(",").join(
											Lists.newArrayList(exprs).stream().sorted().collect(Collectors.toList())));
			Map<String, Object> params = Maps.newHashMap();
			return SqlParams.model(sql, params);
		}

		public static SqlParams delete(Model model, String table, Map<String, Object> params) {
			StringBuilder sql = new StringBuilder();
			params = Maps.newHashMap(params);
			sql.append("delete from ")
							.append(Strings.isNullOrEmpty(table) ? model.getTable() : table)
							.append("\n")
							.append(Helper.where(model, params));
			return SqlParams.model(sql, params);
		}

		public static SqlParams update(Model model, String table, Map<String, Object> newValues,
						Map<String, Object> params) {
			StringBuilder sql = new StringBuilder();
			newValues = Maps.newHashMap(newValues);
			if (params == null) {
				params = Maps.newHashMap();
			} else {
				params = Maps.newHashMap(params);
			}
			sql.append("update ")
							.append(Strings.isNullOrEmpty(table) ? model.getTable() : table)
							.append("\n")
							.append(" set ")
							.append(Joiner.on(",").join(
											newValues.entrySet().stream()
															.sorted(Comparator.comparing(Map.Entry::getKey))
															.map(e -> {
																String[] s = e.getKey().split("#");
																Property p = model.getProperty(s[0]);
																if (s.length == 2) {
																	switch (s[1].trim().toUpperCase()) {
																	case "EXPR":
																		return p.getColumn() + " = " + e.getValue();
																	case "ADD":
																		return p.getColumn() + " = " + p.getColumn()
																						+ " + :$newValuePrefix$"
																						+ e.getKey();
																	case "SUB":
																		return p.getColumn() + " = " + p.getColumn()
																						+ " - :$newValuePrefix$"
																						+ e.getKey();
																	default:
																		return p.getColumn() + " = :$newValuePrefix$"
																						+ e.getKey();
																	}
																} else {
																	return p.getColumn() + " = :$newValuePrefix$"
																					+ e.getKey();
																}
															}).collect(Collectors.toList())))
							.append("\n")
							.append(Helper.where(model, params));
			params.putAll(newValues.entrySet().stream()
							.collect(Collectors.toMap(e -> "$newValuePrefix$" + e.getKey(), e -> e.getValue())));
			return SqlParams.model(sql, params);
		}

		public static SqlParams select(Model model, String table, List<String> names,
						Map<String, Object> params, List<String> orderExprs, Integer offset, Integer limit) {
			if (params == null) {
				params = Maps.newHashMap();
			} else {
				params = Maps.newHashMap(params);
			}
			StringBuilder sql = new StringBuilder();
			sql.append("select ");
			if (names == null || names.isEmpty()) {
				sql.append("*");
			} else {
				sql.append(Joiner.on(",").join(Lists.newArrayList(names).stream().sorted().map(n -> {
					Property property = model.getProperty(n);
					if (property == null) {
						return n;
					}
					return property.getColumn();
				}).collect(Collectors.toList())));
			}
			sql.append(" from ")
							.append(Strings.isNullOrEmpty(table) ? model.getTable() : table)
							.append(Helper.where(model, params));

			if (orderExprs != null && !orderExprs.isEmpty()) {
				sql.append(orderExprs != null
								? " order by " + Joiner.on(",").join(orderExprs)
								: "");
			}
			if (offset != null) {
				sql.append(" limit " + offset + "," + (limit == null ? 1 : limit));
			} else if (limit != null) {
				sql.append(" limit " + limit);
			}
			return SqlParams.model(sql, params);
		}

		public static String where(Model model, Map<String, Object> params) {
			if (params == null || params.isEmpty()) {
				return "";
			}

			return " where " + Joiner.on(" \nand ").join(
							params.entrySet().stream()
											.sorted(Comparator.comparing(Map.Entry::getKey))
											.map(e -> {
												String[] s = e.getKey().split("#");
												Property p = model.getProperty(s[0]);
												if (s.length == 2) {
													switch (s[1].trim().toUpperCase()) {
													case "IN":
														return p.getColumn() + " in (:" + e.getKey() + ")";
//													case "<=":
													case "LE":
														return p.getColumn() + " <= :" + e.getKey();
//													case "<":
													case "LT":
														return p.getColumn() + " < :" + e.getKey();
//													case ">=":
													case "GE":
														return p.getColumn() + " >= :" + e.getKey();
//													case ">":
													case "GT":
														return p.getColumn() + " > :" + e.getKey();
//													case "!=":
													case "NE":
														return p.getColumn() + " != :" + e.getKey();
													default:
														return p.getColumn() + " = :" + e.getKey();
													}
												} else {
													return p.getColumn() + " = :" + e.getKey();
												}
											}).collect(Collectors.toList()));
		}

	}

	/**
	 * 
	 * @author kuojian21
	 *
	 */
	@Data
	public static class Model {
		private final String name;
		private final String table;
		private final List<Property> properties;
		private final Map<String, Property> propertyMap;
		private final List<Property> insertProperties;
		private final List<Property> tableProperties;

		public Model(String name, String table, List<Property> properties, List<String> tableKeys) {
			super();
			this.name = name;
			this.table = table;
			this.properties = Collections.unmodifiableList(properties);
			this.propertyMap = Collections
							.unmodifiableMap(properties.stream().collect(Collectors.toMap(p -> p.getName(), p -> p)));
			this.insertProperties = Collections
							.unmodifiableList(
											properties.stream().filter(p -> p.isInsert()).collect(Collectors.toList()));
			this.tableProperties = Collections
							.unmodifiableList(this.getProperties(tableKeys));
		}

		@SuppressWarnings("checkstyle:HiddenField")
		public Property getProperty(String name) {
			return this.propertyMap.get(name);
		}

		public List<Property> getProperties(List<String> names) {
			return names.stream().map(this.propertyMap::get).collect(Collectors.toList());
		}

		public String getTable(String suffix) {
			return this.table + suffix;
		}

	}

	/**
	 * 
	 * @author kuojian21
	 *
	 */
	@Data
	public static class Property {

		private final String name;
		private final String column;
		private final String type;
		private final Field field;
		private final boolean primaryKey;
		private final boolean insert;
		private final String comment;

		public Property(String name, String column, Field field, boolean primaryKey, boolean insert) {
			this(name, column, null, field, primaryKey, insert, null);
		}

		public Property(String name, String column, String type, boolean primaryKey, boolean insert, String comment) {
			this(name, column, type, null, primaryKey, insert, comment);
		}

		public Property(String name, String column, String type, Field field, boolean primaryKey, boolean insert,
						String comment) {
			super();
			this.name = name;
			this.column = column;
			this.type = type;
			this.field = field;
			this.primaryKey = primaryKey;
			this.insert = insert;
			this.comment = comment;
		}

		public Object get(Object obj) {
			try {
				return this.field.get(obj);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * 
	 * @author kuojian21
	 *
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface PrimaryKey {
		boolean insert() default false;
	}

	/**
	 * 
	 * @author kuojian21
	 *
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface TableKey {
		int index() default 0;
	}

	/**
	 * 
	 * @author kuojian21
	 *
	 */
	@Data
	public static class SqlParams {
		private final StringBuilder sql;
		private final Map<String, Object> params;

		public SqlParams(StringBuilder sql, Map<String, Object> params) {
			super();
			this.sql = sql;
			this.params = params;
		}

		public static SqlParams model(StringBuilder sql, Map<String, Object> params) {
			return new SqlParams(sql, params);
		}

	}

}
