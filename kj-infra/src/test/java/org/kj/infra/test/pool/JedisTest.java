package org.kj.infra.test.pool;

import com.google.common.collect.Sets;
import com.kj.infra.pool.KjJedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

public class JedisTest {
	public static void main(String[] args) throws Exception {
		Jedis jedis = KjJedis.Jedis.jedis(new JedisSentinelPool("master", Sets.newHashSet("ip:port", "ip:port"),
						"password"), Jedis.class);
		jedis.get("");
		System.exit(0);
	}
}
