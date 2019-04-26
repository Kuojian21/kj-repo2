package com.kj.test;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import com.google.common.collect.Sets;
import com.kj.bean.Helper;
import com.kj.bean.KjPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

public class JedisTest {
	public static void main(String[] args) throws Exception {
		Jedis jedis = Helper.enhancer(Jedis.class, (method, arg) -> {
			Function<Jedis, Object> func = (j) -> {
				try {
					return method.invoke(j);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			};
			try {
				return KjPool.Jedis
								.jedis(new JedisSentinelPool("master", Sets.newHashSet("ip:port", "ip:port"),
												"password"))
								.execute(func);
			} catch (Exception e) {
				return null;
			}
		});

		jedis.get("");
		System.exit(0);
	}
}
