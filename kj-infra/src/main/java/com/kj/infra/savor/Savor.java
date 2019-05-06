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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.CollectionUtils;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author kuojian21
 */
public abstract class Savor<T> {

	private static Logger logger = LoggerFactory.getLogger(Savor.class);
	private static final String NEW_VALUE_SUFFIX = "$newValueSuffix$";

	private final Class<T> clazz;
	private final RowMapper<T> rowMapper;
	private final Model model;

	@SuppressWarnings("unchecked")
	protected Savor() {
		clazz = (Class<T>) (((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
		rowMapper = new BeanPropertyRowMapper<>(clazz);
		model = ModelHelper.model(clazz);
	}

	public int update(SqlParams sqlParams) {
		return this.getWriter().update(sqlParams.getSql().toString(), sqlParams.getParams());
	}

	public int update(Stream<SqlParams> stream) {
		return stream.map(this::update).mapToInt(r -> r).sum();
	}

	@SuppressWarnings("checkstyle:HiddenField")
	public <R> List<R> select(SqlParams sqlParams, RowMapper<R> rowMapper) {
		return this.getReader().query(sqlParams.getSql().toString(), sqlParams.getParams(), rowMapper);
	}

	@SuppressWarnings("checkstyle:HiddenField")
	public <R> List<R> select(Stream<SqlParams> stream, RowMapper<R> rowMapper) {
		return stream.map(s -> this.select(s, rowMapper)).flatMap(Collection::stream).collect(Collectors.toList());
	}

	public int insert(List<T> objs) {
		if (objs == null || objs.isEmpty()) {
			return 0;
		}
		return this.update(this.table(objs).entrySet().stream()
				.map(e -> SqlHelper.insert(this.model, e.getKey(), e.getValue(), true)));
	}

	public int upsert(List<T> objs, List<String> names) {
		if (objs == null || objs.isEmpty()) {
			return 0;
		}
		Map<String, Object> values = Maps.newHashMap();
		if (!CollectionUtils.isEmpty(names)) {
			values = this.model.getProperties(names).stream()
					.collect(Collectors.toMap(p -> p.getName() + "#EXPR", p -> "values(" + p.getColumn() + ")"));
		}
		return this.upsert(objs, values);
	}

	public int upsert(List<T> objs, Map<String, Object> values) {
		if (objs == null || objs.isEmpty()) {
			return 0;
		}
		return this.update(this.table(objs).entrySet().stream().map(e -> SqlHelper.upsert(this.model, e.getKey(),
				e.getValue(), Helper.initValues(this.model, values, true))));
	}

	public int delete(Map<String, Object> params) {
		return this.update(this.table(params).entrySet().stream()
				.map(e -> SqlHelper.delete(this.model, e.getKey(), e.getValue())));
	}

	public int update(Map<String, Object> values, Map<String, Object> params) {
		return this.update(this.table(params).entrySet().stream()
				.map(e -> SqlHelper.update(model, e.getKey(), Helper.initValues(model, values, false), e.getValue())));
	}

	public List<T> select(Map<String, Object> params) {
		return this.select(params, null, null, null);
	}

	public List<T> select(Map<String, Object> params, List<String> orderExprs, Integer offset, Integer limit) {
		return this.select(null, params, orderExprs, offset, limit, this.getRowMapper());
	}

	@SuppressWarnings("checkstyle:HiddenField")
	public <R> List<R> select(List<String> names, Map<String, Object> params, RowMapper<R> rowMapper) {
		return this.select(names, params, null, null, null, rowMapper);
	}

	@SuppressWarnings("checkstyle:HiddenField")
	public <R> List<R> select(List<String> names, Map<String, Object> params, List<String> orderExprs, Integer offset,
			Integer limit, RowMapper<R> rowMapper) {
		return this.select(
				this.table(params).entrySet().stream().map(
						e -> SqlHelper.select(this.model, e.getKey(), names, e.getValue(), orderExprs, offset, limit)),
				rowMapper);
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

	protected Function<Object, String> table() {
		throw new RuntimeException("not supported");
	}

	protected List<String> tables() {
		throw new RuntimeException("not supported");
	}

	protected Map<String, Map<String, List<Param>>> table(Map<String, Object> paramMap) {
		Property property = this.model.getTableKeyProperty();
		Map<String, List<Param>> params = Helper.initParams(this.model, paramMap);
		if (property == null) {
			return Helper.newHashMap(this.model.getTable(), params);
		}
		List<Param> paramList = params.get(property.getName());
		if (paramList == null) {
			String table = this.table().apply(null);
			if (!Strings.isNullOrEmpty(table)) {
				return Helper.newHashMap(table, params);
			}
			return this.tables().stream().collect(Collectors.toMap(t -> t, t -> params));
		} else if (paramList.size() == 1) {
			Param param = paramList.get(0);
			switch (param.op) {
			case EQ:
				return Helper.newHashMap(this.table().apply(param.getValue()), params);
			case IN:
				return ((Collection<?>) param.getValue()).stream().map(e -> Tuple.tuple(this.table().apply(e), e))
						.collect(Collectors.groupingBy(Tuple::getX)).entrySet().stream()
						.collect(Collectors.toMap(Map.Entry::getKey, e -> {
							Map<String, List<Param>> result = Maps.newHashMap(params);
							result.put(property.getName(), Lists.newArrayList(new Param(property, Param.OP.IN,
									e.getValue().stream().map(Tuple::getY).collect(Collectors.toList()))));
							return result;
						}));
			default:
				return this.tables().stream().collect(Collectors.toMap(t -> t, t -> params));
			}
		} else {
			return this.tables().stream().collect(Collectors.toMap(t -> t, t -> params));
		}

	}

	protected String table(T obj) {
		Property property = this.model.getTableKeyProperty();
		if (property == null) {
			return this.model.getTable();
		}
		return this.table().apply(property.getOrInsertDef(obj));
	}

	protected Map<String, List<T>> table(List<T> objs) {
		Property property = this.model.getTableKeyProperty();
		if (property == null) {
			return Helper.newHashMap(this.model.getTable(), objs);
		} else {
			return objs.stream().collect(Collectors.groupingBy(this::table));
		}

	}

	public abstract NamedParameterJdbcTemplate getReader();

	public abstract NamedParameterJdbcTemplate getWriter();

	/**
	 * @author kuojian21
	 */
	public static class ModelHelper {

		private static final ConcurrentMap<Class<?>, Model> MODELS = Maps.newConcurrentMap();

		public static Model model(Class<?> clazz) {
			return MODELS.computeIfAbsent(clazz, Model::new);
		}

		static Supplier<Object> insertDef(Field f) {
			TimeInsert inDef = f.getAnnotation(TimeInsert.class);
			if (inDef != null) {
				return parseDef(f.getType(), inDef.value());
			} else {
				return () -> null;
			}
		}

		static Supplier<Object> updateDef(Field f) {
			TimeUpdate upDef = f.getAnnotation(TimeUpdate.class);
			if (upDef != null) {
				return parseDef(f.getType(), upDef.value());
			} else {
				return () -> null;
			}
		}

		static Supplier<Object> parseDef(Class<?> type, String value) {
			if (Long.class.equals(type)) {
				return System::currentTimeMillis;
			} else if (Date.class.equals(type)) {
				return () -> new Date(System.currentTimeMillis());
			} else if (Timestamp.class.equals(type)) {
				return () -> new Timestamp(System.currentTimeMillis());
			}
			return () -> null;
		}

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

		public static Map<String, Object> paramMap(Map<String, List<Param>> params) {
			Map<String, Object> paramMap = Maps.newHashMap();
			params.forEach((key, value) -> value.forEach(v -> paramMap.put(v.getVName(), v.getValue())));
			return paramMap;
		}

		public static Map<String, Object> valueMap(Map<String, Object> paramMap, Map<String, Value> values) {
			values.values().forEach(v -> paramMap.put(v.getVName(), v.getValue()));
			return paramMap;
		}

		public static Map<String, Value> initValues(Model model, Map<String, Object> values, boolean upsert) {
			Map<String, Value> result = Maps.newHashMap();
			if (CollectionUtils.isEmpty(values)) {
				model.getUpdateTimeProperties().forEach(p -> result.putIfAbsent(p.getName(),
						new Value(p, Value.OP.EQ, upsert, p.getUpdateDef().get())));
				return result;
			}
			values.forEach((key, value) -> {
				String[] s = key.split("#");
				s[0] = s[0].trim();
				Property p = model.getProperty(s[0]);
				Value.OP op = Value.OP.EQ;
				if (s.length == 2) {
					switch (s[1].trim().toUpperCase()) {
					case "EXPR":
						op = Value.OP.EXPR;
						break;
					case "ADD":
						op = Value.OP.ADD;
						break;
					case "SUB":
						op = Value.OP.SUB;
						break;
					default:
						break;
					}
				}
				result.putIfAbsent(p.getName(), new Value(p, op, upsert, value));
			});
			model.getUpdateTimeProperties().forEach(
					p -> result.putIfAbsent(p.getName(), new Value(p, Value.OP.EQ, upsert, p.getUpdateDef().get())));
			return result;
		}

		public static Map<String, List<Param>> initParams(Model model, Map<String, Object> params) {
			Map<String, List<Param>> result = Maps.newHashMap();
			if (CollectionUtils.isEmpty(params)) {
				return result;
			}
			params.forEach((key, value) -> {
				String[] s = key.split("#");
				s[0] = s[0].trim();
				Property p = model.getProperty(s[0]);
				Param.OP op = Param.OP.EQ;
				if (s.length == 2) {
					switch (s[1].trim().toUpperCase()) {
					case "<=":
					case "LE":
						op = Param.OP.LE;
						break;
					case "<":
					case "LT":
						op = Param.OP.LT;
						break;
					case ">=":
					case "GE":
						op = Param.OP.GE;
						break;
					case ">":
					case "GT":
						op = Param.OP.GT;
						break;
					case "!=":
					case "!":
					case "<>":
					case "NE":
						op = Param.OP.NE;
						break;
					case "IN":
					case "=":
					case "EQ":
						if (value != null && (value instanceof Collection || value.getClass().isArray())) {
							op = Param.OP.IN;
						} else {
							op = Param.OP.EQ;
						}
						break;
					default:
						logger.error("invalid syntax:{}", key);
						throw new RuntimeException("invalid syntax:" + key);
					}
				}
				if (op == Param.OP.IN && value.getClass().isArray()) {
					result.getOrDefault(p.getName(), Lists.newArrayList())
							.add(new Param(p, Param.OP.IN, Arrays.asList((Object[]) value)));
					return;
				}
				result.getOrDefault(p.getName(), Lists.newArrayList()).add(new Param(p, op, value));
			});
			return result;
		}
	}

	/**
	 * @author kuojian21
	 */
	public static class SqlHelper {

		public static <T> SqlParams insert(Model model, String table, List<T> objs, boolean ignore) {
			StringBuilder sql = new StringBuilder();
			sql.append("insert");
			if (ignore) {
				sql.append(" ignore	 ");
			}
			sql.append(" into ").append(table).append("\n").append(" (")
					.append(Joiner.on(",").join(
							model.getInsertProperties().stream().map(Property::getColumn).collect(Collectors.toList())))
					.append(") ").append("\n").append("values").append("\n")
					.append(Joiner.on(",\n").join(IntStream
							.range(0, objs.size()).boxed().map(
									i -> "(" + Joiner.on(",")
											.join(model.getInsertProperties().stream().map(p -> ":" + p.getName() + i)
													.collect(Collectors.toList()))
											+ ")")
							.collect(Collectors.toList())));
			Map<String, Object> params = Maps.newHashMap();
			IntStream.range(0, objs.size()).boxed().forEach(i -> model.getInsertProperties()
					.forEach(p -> params.put(p.getName() + i, p.getOrInsertDef(objs.get(i)))));
			return SqlParams.model(sql, params);
		}

		public static <T> SqlParams upsert(Model model, String table, List<T> objs, Map<String, Value> values) {
			if (CollectionUtils.isEmpty(values)) {
				return SqlHelper.insert(model, table, objs, true);
			}
			SqlParams sqlParams = SqlHelper.insert(model, table, objs, false);
			sqlParams.getSql().append(" on duplicate key update ").append(Joiner.on(",").join(values.entrySet().stream()
					.sorted(Comparator.comparing(Map.Entry::getKey)).collect(Collectors.toList())));
			Helper.valueMap(sqlParams.getParams(), values);
			return sqlParams;
		}

		public static SqlParams delete(Model model, String table, Map<String, List<Param>> params) {
			StringBuilder sql = new StringBuilder();
			sql.append("delete from ").append(table).append("\n").append(SqlHelper.where(params));
			return SqlParams.model(sql, Helper.paramMap(params));
		}

		public static SqlParams update(Model model, String table, Map<String, Value> values,
				Map<String, List<Param>> params) {
			if (CollectionUtils.isEmpty(values)) {
				logger.error("invalid syntax");
				throw new RuntimeException("invalid syntax");
			}
			StringBuilder sql = new StringBuilder();
			sql.append("update ").append(table).append("\n").append(" set ")
					.append(Joiner.on(",")
							.join(values.values().stream().sorted(Comparator.comparing(Value::getVName))
									.map(Value::getExpr).collect(Collectors.toList())))
					.append("\n").append(SqlHelper.where(params));
			return SqlParams.model(sql, Helper.valueMap(Helper.paramMap(params), values));
		}

		public static SqlParams select(Model model, String table, List<String> names, Map<String, List<Param>> params,
				List<String> orderExprs, Integer offset, Integer limit) {
			StringBuilder sql = new StringBuilder();
			sql.append("select ");
			if (CollectionUtils.isEmpty(names)) {
				sql.append("*");
			} else {
				sql.append(Joiner.on(",").join(Lists.newArrayList(names).stream().map(String::trim).sorted().map(n -> {
					Property property = model.getProperty(n);
					if (property == null) {
						return n;
					}
					return property.getColumn();
				}).collect(Collectors.toList())));
			}
			sql.append(" from ").append(table).append(SqlHelper.where(params));
			if (!CollectionUtils.isEmpty(orderExprs)) {
				sql.append(" order by ")
						.append(Joiner.on(",").join(orderExprs.stream().map(String::trim).sorted().map(e -> {
							Property p = model.getProperty(e);
							if (p == null) {
								return e;
							}
							return p.getColumn();
						}).collect(Collectors.toList())));
			}
			if (offset != null) {
				sql.append(" limit ").append(offset).append(",").append(limit == null ? 1 : limit);
			} else if (limit != null) {
				sql.append(" limit ").append(limit);
			}
			return SqlParams.model(sql, Helper.paramMap(params));
		}

		public static String where(Map<String, List<Param>> params) {
			if (params == null || params.isEmpty()) {
				return "";
			}
			return " where " + Joiner.on(" \nand ").join(params.entrySet().stream().flatMap(e -> e.getValue().stream())
					.sorted(Comparator.comparing(Param::getVName)).map(Param::getExpr).collect(Collectors.toList()));
		}

	}

	/**
	 * @author kuojian21
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
	 * @author kuojian21
	 */
	@Data
	public static class Model {
		private final String name;
		private final String table;
		private final List<Property> properties;
		private final Map<String, Property> propertyMap;
		private final List<Property> insertProperties;
		// private final List<Property> tableProperties;
		private final Property tableKeyProperty;
		private final List<Property> updateTimeProperties;

		public Model(Class<?> clazz) {
			super();
			Table tTable = clazz.getAnnotation(Table.class);
			this.name = clazz.getSimpleName();
			this.table = (tTable != null && !Strings.isNullOrEmpty(tTable.name())) ? tTable.name()
					: CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.name);
			this.properties = Collections.unmodifiableList(Arrays.stream(clazz.getDeclaredFields())
					.filter(f -> !Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers()))
					.map(Property::new).collect(Collectors.toList()));
			this.propertyMap = Collections
					.unmodifiableMap(properties.stream().collect(Collectors.toMap(Property::getName, p -> p)));
			this.tableKeyProperty = (tTable != null && !Strings.isNullOrEmpty(tTable.key()))
					? this.getProperty(tTable.key())
					: null;
			this.updateTimeProperties = Collections.unmodifiableList(
					Arrays.stream(clazz.getDeclaredFields()).filter(f -> f.getAnnotation(TimeUpdate.class) != null)
							.map(f -> this.getProperty(f.getName())).collect(Collectors.toList()));
			this.insertProperties = Collections
					.unmodifiableList(properties.stream().filter(Property::isInsertable).collect(Collectors.toList()));
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
	 * @author kuojian21
	 */
	@Data
	public static class Property {

		private final String name;
		private final String column;
		private final Class<?> type;
		private final Field field;
		private final boolean primaryKey;
		private final boolean insertable;
		private final Supplier<Object> insertDef;
		private final Supplier<Object> updateDef;

		public Property(Field f) {
			f.setAccessible(true);
			this.field = f;
			this.name = f.getName();
			this.column = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, f.getName());
			this.type = f.getType();
			this.primaryKey = f.getAnnotation(PrimaryKey.class) != null;
			this.insertable = f.getAnnotation(PrimaryKey.class) == null || f.getAnnotation(PrimaryKey.class).insert();
			this.insertDef = ModelHelper.insertDef(f);
			this.updateDef = ModelHelper.updateDef(f);
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

	}

	/**
	 * @author kuojian21
	 */
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Table {
		String name() default "";

		String key() default "";
	}

	/**
	 * @author kuojian21
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface PrimaryKey {
		boolean insert() default false;
	}

	/**
	 * @author kuojian21
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface TimeInsert {
		String value() default "";
	}

	/**
	 * @author kuojian21
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface TimeUpdate {
		String value() default "";
	}

	/**
	 * @author kuojian21
	 */
	@Data
	public static class Tuple<X, Y> {
		private final X x;
		private final Y y;

		public static <X, Y> Tuple<X, Y> tuple(X x, Y y) {
			return new Tuple<>(x, y);
		}

		public Tuple(X x, Y v) {
			super();
			this.x = x;
			this.y = v;
		}
	}

	/**
	 * @author kuojian21
	 */
	@Data
	public static class Expr {
		private final String vName;
		private final String expr;
		private final Object value;

		public Expr(String vName, String expr, Object value) {
			this.vName = vName;
			this.expr = expr;
			this.value = value;
		}
	}

	/**
	 * @author kuojian21
	 */
	@EqualsAndHashCode(callSuper = false)
	@Data
	public static class Param extends Expr {

		private final OP op;

		public Param(Property p, OP op, Object value) {
			super(getVName(p, op), getExpr(p, op), value);
			this.op = op;
		}

		public static String getVName(Property p, OP op) {
			return op == OP.EQ ? p.name : p.name + "#" + op.name();
		}

		public static String getExpr(Property p, OP op) {
			switch (op) {
			case IN:
				return p.getColumn() + " in ( :" + getVName(p, op) + " )";
			case LT:
			case LE:
			case GT:
			case GE:
			case NE:
			case EQ:
				return p.getColumn() + " " + op.symbol + " :" + getVName(p, op);
			default:
				return "";
			}
		}

		public enum OP {
			EQ("="), IN("in"), LT("<"), LE("<="), GT(">"), GE(">="), NE("!=");
			private final String symbol;

			OP(String symbol) {
				this.symbol = symbol;
			}

			public String getSymbol() {
				return symbol;
			}
		}
	}

	/**
	 * @author kuojian21
	 */
	@EqualsAndHashCode(callSuper = false)
	@Data
	public static class Value extends Expr {

		private final OP op;

		public Value(Property p, OP op, boolean upsert, Object value) {
			super(getVName(p, op), getExpr(p, op, upsert, value), value);
			this.op = op;
		}

		public static String getVName(Property p, OP op) {
			return (op == OP.EQ ? p.name : p.name + "#" + op.name()) + NEW_VALUE_SUFFIX;
		}

		public static String getExpr(Property p, OP op, boolean upsert, Object value) {
			switch (op) {
			case EXPR:
				return p.getColumn() + " = " + value;
			case ADD:
			case SUB:
				if (upsert) {
					return p.getColumn() + " = values(" + p.getColumn() + ") " + op.symbol + " :" + getVName(p, op);
				}
				return p.getColumn() + " = " + p.getColumn() + " " + op.symbol + " :" + getVName(p, op);
			case EQ:
			default:
				return p.getColumn() + " = :" + getVName(p, op);
			}
		}

		public enum OP {
			EQ("="), ADD("+"), SUB("-"), EXPR("EXPR");
			private final String symbol;

			OP(String symbol) {
				this.symbol = symbol;
			}

			public String getSymbol() {
				return symbol;
			}
		}
	}

}
