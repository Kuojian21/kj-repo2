package com.kj.infra.savor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static Logger logger = LoggerFactory.getLogger(Savor.class);

	private final Class<T> clazz;
	private final RowMapper<T> rowMapper;
	private final Model model;

	@SuppressWarnings("unchecked")
	protected Savor() {
		clazz = (Class<T>) (((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
		rowMapper = new BeanPropertyRowMapper<T>(clazz);
		model = ModelHelper.model(clazz);
	}

	public int insert(List<T> objs) {
		if (objs == null || objs.isEmpty()) {
			return 0;
		}
		int rtn = 0;
		try {
			Map<String, List<T>> objMap = this.table(objs);
			for (Map.Entry<String, List<T>> entry : objMap.entrySet()) {
				SqlParams sqlParams = SqlHelper.insert(this.model, entry.getKey(), entry.getValue());
				rtn += this.getWriter().update(sqlParams.getSql().toString(), sqlParams.getParams());
			}
			return rtn;
		} catch (Exception e) {
			logger.error("", e);
			return -1;
		}
	}

	public int upsert(T obj, List<String> updateExprs) {
		if (obj == null) {
			return 0;
		}
		try {
			SqlParams sqlParams = SqlHelper.upsert(this.model, this.table(obj), obj, updateExprs);
			return this.getWriter().update(sqlParams.getSql().toString(), sqlParams.getParams());
		} catch (Exception e) {
			logger.error("", e);
			return -1;
		}
	}

	public int delete(Map<String, Object> params) {
		try {
			SqlParams sqlParams = SqlHelper.delete(this.model, this.table(params), params);
			return this.getWriter().update(sqlParams.getSql().toString(), sqlParams.getParams());
		} catch (Exception e) {
			logger.error("", e);
			return -1;
		}
	}

	public int update(Map<String, Object> newValues, Map<String, Object> params) {
		if (newValues == null || newValues.isEmpty()) {
			return 0;
		}
		try {
			SqlParams sqlParams = SqlHelper.update(this.model, this.table(params), newValues, params);
			return this.getWriter().update(sqlParams.getSql().toString(), sqlParams.getParams());
		} catch (Exception e) {
			logger.error("", e);
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
			SqlParams sqlParams = SqlHelper.select(this.model, this.table(params), names, params, orderExprs,
							offset, limit);
			return this.getWriter().query(sqlParams.getSql().toString(), sqlParams.getParams(), rowMapper);
		} catch (Exception e) {
			logger.error("", e);
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
		return this.table().apply(properties.stream().map(p -> p.getOrInsertDef(obj)).toArray());
	}

	protected Map<String, List<T>> table(List<T> objs) {
		List<Property> properties = this.model.getTableProperties();
		if (properties.isEmpty()) {
			return Helper.newHashMap(this.model.getTable(), objs);
		} else {
			Map<Integer, String> tableMap = IntStream.range(0, objs.size())
							.boxed()
							.collect(Collectors.toMap(i -> i, i -> table(objs.get(i))));
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

	public static class ModelHelper {

		private static final ConcurrentMap<Class<?>, Model> MODELS = Maps.newConcurrentMap();

		public static Model model(Class<?> clazz) {
			return MODELS.computeIfAbsent(clazz, Model::new);
		}

		static Supplier<Object> insertDef(Field f) {
			InsertDef inDef = f.getAnnotation(InsertDef.class);
			if (inDef != null) {
				return parseDef(f.getType(), inDef.value());
			} else {
				return () -> null;
			}
		}

		static Supplier<Object> updateDef(Field f) {
			UpdateDef upDef = f.getAnnotation(UpdateDef.class);
			if (upDef != null) {
				return parseDef(f.getClass(), upDef.value());
			} else {
				return () -> null;
			}
		}

		static Supplier<Object> parseDef(Class<?> type, String value) {
			if (Strings.isNullOrEmpty(value)) {
				return () -> null;
			}
			Object val = null;
			if (Long.class.equals(type)) {
				if ("NOW".equals(value.toUpperCase())) {
					return System::currentTimeMillis;
				} else {
					val = Long.parseLong(value);
				}
			} else if (Integer.class.equals(type)) {
				val = Integer.parseInt(value);
			} else if (String.class.equals(type)) {
				val = value;
			} else if (Date.class.equals(type)) {
				return () -> {
					return new Date(System.currentTimeMillis());
				};
			} else if (Timestamp.class.equals(type)) {
				return () -> {
					return new Timestamp(System.currentTimeMillis());
				};
			}
			Object def = val;
			return () -> def;
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
		private final List<Property> updateDefProperties;

		public Model(Class<?> clazz) {
			super();
			this.name = clazz.getSimpleName();
			this.table = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.name);
			this.properties = Collections.unmodifiableList(Arrays.stream(clazz.getDeclaredFields())
							.filter(f -> !Modifier.isStatic(f.getModifiers())
											&& !Modifier.isFinal(f.getModifiers()))
							.map(Property::new).collect(Collectors.toList()));
			this.propertyMap = Collections
							.unmodifiableMap(properties.stream()
											.collect(Collectors.toMap(p -> p.getName(), p -> p)));
			this.tableProperties = Collections.unmodifiableList(Arrays.stream(clazz.getDeclaredFields())
							.filter(f -> f.getAnnotation(TableKey.class) != null)
							.map(f -> Tuple.tuple(f.getName(), f.getAnnotation(TableKey.class).index()))
							.sorted(Comparator.comparing(Tuple::getY))
							.map(tuple -> this.getProperty(tuple.x))
							.collect(Collectors.toList()));
			this.updateDefProperties = Collections.unmodifiableList(Arrays.stream(clazz.getDeclaredFields())
							.filter(f -> f.getAnnotation(UpdateDef.class) != null)
							.map(f -> this.getProperty(f.getName()))
							.collect(Collectors.toList()));
			this.insertProperties = Collections.unmodifiableList(properties.stream()
							.filter(p -> p.isInsert())
							.collect(Collectors.toList()));
		}

		@SuppressWarnings("checkstyle:HiddenField")
		public Property getProperty(String name) {
			return this.propertyMap.get(name);
		}

		public List<Property> getProperties(List<String> names) {
			return names.stream().map(this.propertyMap::get).collect(Collectors.toList());
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
		private final Class<?> type;
		private final Field field;
		private final boolean primaryKey;
		private final boolean insert;
		private final Supplier<Object> insertDef;
		private final Supplier<Object> updateDef;
		private final String comment;

		public Property(Field f) {
			f.setAccessible(true);
			this.field = f;
			this.name = f.getName();
			this.column = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, f.getName());
			this.type = f.getType();
			this.primaryKey = f.getAnnotation(PrimaryKey.class) != null;
			this.insert = f.getAnnotation(PrimaryKey.class) == null
							|| f.getAnnotation(PrimaryKey.class).insert();
			this.insertDef = ModelHelper.insertDef(f);
			this.updateDef = ModelHelper.updateDef(f);
			this.comment = "";
		}

		public Object getOrUpdateDef(Object obj) {
			try {
				Object rtn = this.field.get(obj);
				if (rtn == null) {
					return this.updateDef.get();
				}
				return rtn;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				logger.error("", e);
				return null;
			}
		}

		public Object getOrInsertDef(Object obj) {
			try {
				Object rtn = this.field.get(obj);
				if (rtn == null) {
					return this.insertDef.get();
				}
				return rtn;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				logger.error("", e);
				return null;
			}
		}

		public Object updateDef() {
			return this.updateDef.get();
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
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface InsertDef {
		String value() default "";
	}

	/**
	 * 
	 * @author kuojian21
	 *
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface UpdateDef {
		String value() default "";
	}

	/**
	 * @author kuojian21
	 */
	public static class Helper {

		@SuppressWarnings("unchecked")
		public static <K, V> Map<K, V> newHashMap(Object... objs) {
			Map<K, V> result = Maps.newHashMap();
			for (int i = 0, len = objs.length; i < len; i += 2) {
				result.put((K) objs[i], (V) objs[i + 1]);
			}
			return result;
		}
	}

	/**
	 * @author kuojian21
	 */
	public static class SqlHelper {

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
					params.put(p.getName() + i, p.getOrInsertDef(objs.get(i)));
				});
			});
			return SqlParams.model(sql, params);
		}

		public static <T> SqlParams upsert(Model model, String table, T obj, List<String> exprs) {
			if ((exprs == null || exprs.isEmpty()) && model.getUpdateDefProperties().isEmpty()) {
				return insert(model, table, Lists.newArrayList(obj));
			}
			StringBuilder sql = new StringBuilder();
			exprs = Lists.newArrayList(exprs);
			exprs.addAll(model.getUpdateDefProperties().stream().map(p -> {
				return p.getColumn() + " = :$newValuePrefix$" + p.getName();
			}).collect(Collectors.toList()));
			sql.append("insert into ")
							.append(Strings.isNullOrEmpty(table) ? model.getTable() : table)
							.append("\n")
							.append(" (")
							.append(Joiner.on(",")
											.join(model.getInsertProperties().stream()
															.map(Property::getColumn)
															.collect(Collectors.toList())))
							.append(")\nvalues\n(")
							.append(Joiner.on(",")
											.join(model.getInsertProperties().stream().map(e -> ":" + e.getName())
															.collect(Collectors.toList())))
							.append(")\n ")
							.append("  on duplicate key update ")
							.append(Joiner.on(",").join(
											exprs.stream().sorted().collect(Collectors.toList())));
			Map<String, Object> params = Maps.newHashMap();
			model.getInsertProperties().stream().forEach(p -> {
				params.put(p.getName(), p.getOrInsertDef(obj));
			});
			model.getUpdateDefProperties().stream().forEach(p -> {
				params.put("$newValuePrefix$" + p.getName(), p.getOrUpdateDef(obj));
			});
			return SqlParams.model(sql, params);
		}

		public static SqlParams delete(Model model, String table, Map<String, Object> params) {
			StringBuilder sql = new StringBuilder();
			params = Maps.newHashMap(params);
			sql.append("delete from ")
							.append(Strings.isNullOrEmpty(table) ? model.getTable() : table)
							.append("\n")
							.append(SqlHelper.where(model, params));
			return SqlParams.model(sql, params);
		}

		public static SqlParams update(Model model, String table, Map<String, Object> newValues,
						Map<String, Object> params) {
			StringBuilder sql = new StringBuilder();
			Map<String, Object> innerNewValues = Maps.newHashMap(newValues);
			model.getUpdateDefProperties().stream().forEach(p -> {
				innerNewValues.putIfAbsent(p.getName(), p.updateDef());
			});
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
											innerNewValues.entrySet().stream()
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
							.append(SqlHelper.where(model, params));
			params.putAll(innerNewValues.entrySet().stream()
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
							.append(SqlHelper.where(model, params));

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
													case "LE":
														return p.getColumn() + " <= :" + e.getKey();
													case "LT":
														return p.getColumn() + " < :" + e.getKey();
													case "GE":
														return p.getColumn() + " >= :" + e.getKey();
													case "GT":
														return p.getColumn() + " > :" + e.getKey();
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

	/**
	 * 
	 * @author kuojian21
	 *
	 */
	@Data
	public static class Tuple<X, Y> {
		private final X x;
		private final Y y;

		public static <X, Y> Tuple<X, Y> tuple(X x, Y y) {
			return new Tuple<X, Y>(x, y);
		}

		public Tuple(X x, Y y) {
			super();
			this.x = x;
			this.y = y;
		}

	}

}
