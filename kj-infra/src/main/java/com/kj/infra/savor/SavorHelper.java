package com.kj.infra.savor;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.common.base.CaseFormat;

import lombok.Data;

/**
 * 
 * @author kuojian21
 *
 */
public class SavorHelper {

	public static RowMapper<Property> rowMapper = new RowMapper<Property>() {
		@Override
		public Property mapRow(ResultSet rs, int rowNum) throws SQLException {
			String type = "";
			if (rs.getString("Type").startsWith("tinyint")
							|| rs.getString("Type").startsWith("int")) {
				type = "Integer";
			} else if (rs.getString("Type").startsWith("bigint")) {
				type = "Long";
			} else if (rs.getString("Type").startsWith("varchar")
							|| rs.getString("Type").endsWith("text")
							|| rs.getString("Type").equals("json")) {
				type = "String";
			}
			return new Property(
							CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,
											rs.getString("Field")),
							rs.getString("Field"),
							type,
							"PRI".equals(rs.getString("Key")),
							"auto_increment".equals(rs.getString("Extra")),
							rs.getString("Comment"));
		}
	};

	public static Model mysql(DataSource dataSource, String table) {
		List<Property> properties = new JdbcTemplate(dataSource)
						.query("show full columns from " + table, rowMapper);
		return new Model(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, table), table, properties);
	}

	public static void code(Model model) {
		System.out.println("/**");
		System.out.println(" * @author kj");
		System.out.println(" */");
		System.out.println("public class " + model.getName() + "{");
		for (Property property : model.getProperties()) {
			System.out.println("\t/*" + property.getComment() + "*/");
			if (property.isPrimaryKey()) {
				System.out.print("\t@Savor.PrimaryKey");
				if (property.isInsert()) {
					System.out.print("(insert=true)");
				}
				System.out.println();
			}
			System.out.println("\tprivate " + property.getType() + " " + property.getName() + ";");
		}
		System.out.println("}");
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

		public Model(String name, String table, List<Property> properties) {
			super();
			this.name = name;
			this.table = table;
			this.properties = properties;
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
		private final boolean primaryKey;
		private final boolean insert;
		private final String comment;

		public Property(String name, String column, String type, boolean primaryKey, boolean insert, String comment) {
			super();
			this.name = name;
			this.column = column;
			this.type = type;
			this.primaryKey = primaryKey;
			this.insert = insert;
			this.comment = comment;
		}

	}

}
