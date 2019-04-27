package org.kj.infra.test.savor;

import java.sql.SQLException;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.kj.infra.savor.Savor;
import com.kj.infra.savor.SavorHelper;
import com.kj.infra.savor.Savor.PrimaryKey;
import com.mysql.cj.jdbc.Driver;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SavorTest {

	/**
	 * create table savor_base_test( id bigint(20) unsigned not null primary key
	 * comment '自增主键', hash_key varchar(64) comment 'key', value varchar(128)
	 * comment 'value', name varchar(64) comment 'name', sex varchar(64) comment
	 * 'sex', age tinyint comment 'age', create_time bigint(20) comment '创建时间',
	 * update_time bigint(20) comment '创建时间' )ENGINE=INNODB DEFAULT CHARSET=UTF8MB4;
	 */
	public static void code(String[] args) throws SQLException {
		/*
		 * -DsocksProxyHost= -DsocksProxyPort=8088
		 */
//		System.setProperty("socksProxyHost", "127.0.0.1");
//		System.setProperty("socksProxyPort", "8088");
		Savor.Model model = SavorHelper.mysql(new SimpleDriverDataSource(new Driver(), args[0], args[1], args[2]),
						args[3]);
		SavorHelper.code(model);
	}

	public static void sql() throws Exception {
		System.out.println(Savor.Helper.insert(Savor.Helper.model(SavorBaseTest.class), null,
						Lists.newArrayList(new SavorBaseTest(), new SavorBaseTest())));
		System.out.println(Savor.Helper.upsert(Savor.Helper.model(SavorBaseTest.class), null, new SavorBaseTest(),
						Lists.newArrayList("name=value(name)")));
		System.out.println(Savor.Helper.delete(Savor.Helper.model(SavorBaseTest.class), null,
						Savor.Helper.newHashMap("id", 100)));
		System.out.println(Savor.Helper.update(Savor.Helper.model(SavorBaseTest.class), null,
						Savor.Helper.newHashMap("name", "kj"),
						Savor.Helper.newHashMap("id", 100)));
		System.out.println(
						Savor.Helper.select(Savor.Helper.model(SavorBaseTest.class), null, null,
										Savor.Helper.newHashMap("id#lt", 100),
										null, null, null));
	}

	public static void test(String[] args) throws SQLException {
		SavorBaseTestDao dao = new SavorBaseTestDao(
						new SimpleDriverDataSource(new Driver(), args[0], args[1], args[2]));
		LongStream.range(0, 110).boxed().forEach(i -> {
			log.info("{}", dao.insert(LongStream.range(0, 10).boxed().map(j -> {
				SavorBaseTest test = new SavorBaseTest();
				test.setId(i * 10 + j);
				test.setCreateTime(System.currentTimeMillis());
				test.setUpdateTime(System.currentTimeMillis());
				return test;
			}).collect(Collectors.toList())));
		});
		dao.delete(Savor.Helper.newHashMap("id", 1));
		IntStream.range(0, 10).boxed()
						.forEach(i -> dao.update(Savor.Helper.newHashMap("name", "kj"),
										Savor.Helper.newHashMap("id", new Random().nextInt(100))));
		log.info("{}", dao.select(Savor.Helper.newHashMap("name", "kj")));
		log.info("{}", dao.select(Savor.Helper.newHashMap("id#le", 10)));
		log.info("{}", dao.select(Savor.Helper.newHashMap("id#ge", 90)));
		dao.update(Savor.Helper.newHashMap("id#sub", 1), Savor.Helper.newHashMap("id#LE", "100"));
	}

	public static class SavorBaseTestDao extends Savor<SavorBaseTest> {

		private final DataSource dataSource;

		public SavorBaseTestDao(DataSource dataSource) {
			super();
			this.dataSource = dataSource;
		}

		@Override
		public NamedParameterJdbcTemplate getReader() {
			return new NamedParameterJdbcTemplate(this.dataSource);
		}

		@Override
		public NamedParameterJdbcTemplate getWriter() {
			return new NamedParameterJdbcTemplate(this.dataSource);
		}

	}

	/**
	 * @author kj
	 */
	@Data
	public static class SavorBaseTest {
		/* 自增主键 */
		@PrimaryKey(insert = true)
		private Long id;
		/* key */
		private String hashKey;
		/* value */
		private String value;
		/* name */
		private String name;
		/* sex */
		private String sex;
		/* age */
		private Integer age;
		/* 创建时间 */
		private Long createTime;
		/* 创建时间 */
		private Long updateTime;

		@Override
		public String toString() {
			return JSON.toJSONString(this);
		}
	}

}
