package com.kj.repo.tool.code;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;

public class KjCode {

    public static void mysql(DataSource dataSource, String table) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<KjCell> cells = jdbcTemplate.query("show full columns from " + table, new RowMapper<KjCell>() {
            @Override
            public KjCell mapRow(ResultSet rs, int rowNum) throws SQLException {

                KjCell cell = new KjCell();
                cell.setName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, rs.getString("Field")));
                if (rs.getString("Type").startsWith("tinyint") || rs.getString("Type").startsWith("int")) {
                    cell.setType("Integer");
                } else if (rs.getString("Type").startsWith("bigint")) {
                    cell.setType("Long");
                } else if (rs.getString("Type").startsWith("varchar") || rs.getString("Type").endsWith("text")) {
                    cell.setType("String");
                }

                cell.setNullable("NO".equals(rs.getString("Null")));
                cell.setPrimary("PRI".equals(rs.getString("Key")));
                cell.setDef(rs.getString("Default"));
                cell.setComment(rs.getString("Comment"));
                return cell;
            }
        });
        KjModel model = new KjModel();
        model.setName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, table));
        model.setCells(cells);
        model(model);
    }

    public static void model(KjModel model) {
        List<String> builder = Lists.newLinkedList();
        System.out.println("/**");
        System.out.println(" * @author kj");
        System.out.println(" */");
        System.out.println("public class " + model.getName() + "{");
        builder.add("/**");
        builder.add(" * @author kj");
        builder.add(" */");
        builder.add("public static class Builder{");
        for (KjCell cell : model.getCells()) {
            System.out.println("\t/*" + cell.getComment() + "*/");
            System.out.println("\tprivate " + cell.getType() + " " + cell.getName() + ";");


        }

        System.out.println("}");
    }

}
