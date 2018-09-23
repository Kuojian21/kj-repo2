package com.kj.repo.test.bean.jedis;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kj.repo.bean.jedis.KjJedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class JedisTest {
    public static void main(String[] args) throws Exception {
        int circle = 10;
        int tasks = 1000;
        String str = "201803292024CORDER49316246";
        if (args.length == 3) {
            circle = Integer.parseInt(args[0]);
            tasks = Integer.parseInt(args[1]);
            str = args[2];
        }
        String value = str;

        KjJedis<Jedis> jedis0 = new KjJedis<Jedis>(new JedisSentinelPool("master",
                Sets.newHashSet("10.200.142.34:26381", "10.200.142.32:26381"), "wwfsjr_test"));
        KjJedis<Jedis> jedis1 = new KjJedis<Jedis>(new JedisSentinelPool("master",
                Sets.newHashSet("10.200.142.34:26387", "10.200.142.32:26387"), "eQLABUKW"));
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(-1);
        config.setMaxTotal(100);
        config.setMaxWaitMillis(30000);
        List<JedisShardInfo> infos = Lists.newArrayList();
        JedisShardInfo info = new JedisShardInfo("10.200.142.34", 6387);
        info.setPassword("eQLABUKW");
        infos.add(info);
        info = new JedisShardInfo("10.200.142.32", 6381);
        info.setPassword("wwfsjr_test");
        infos.add(info);
        KjJedis<ShardedJedis> jedis2 = new KjJedis<ShardedJedis>(new ShardedJedisPool(config, infos));

        jedis0.jedisCommands().del("TEST_QUEUE");
        jedis1.jedisCommands().del("TEST_QUEUE");
        for (int i = 0; i < 10; i++) {
            jedis0.jedisCommands().del("TEST_QUEUE" + i);
            jedis1.jedisCommands().del("TEST_QUEUE" + i);
        }

        jedis1.jedisCommands().del("TEST_SET");
        for (int i = 0; i < 2000; i++) {
            jedis1.jedisCommands().zadd("TEST_SET", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + i,
                    "184688.SZ|博时新兴成长混合型证券投资基金" + i);
        }

        for (int k = 0; k < circle; k++) {
            long b = System.currentTimeMillis();
            ExecutorService service = Executors.newFixedThreadPool(100);
            for (int i = 0; i < tasks; i++) {
                int j = i;
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        jedis2.jedisCommands().lpush("TEST_QUEUE" + j % 10, value);
                    }
                });
            }
            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
            System.out.println("redis集群:写:" + k + ":" + tasks + ":" + value + ":" + (System.currentTimeMillis() - b));

            b = System.currentTimeMillis();
            service = Executors.newFixedThreadPool(100);
            for (int i = 0; i < tasks; i++) {
                int j = i;
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        jedis2.jedisCommands().rpop("TEST_QUEUE" + j % 10);
                    }
                });
            }
            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
            System.out.println("redis集群:读:" + k + ":" + tasks + ":" + value + ":" + (System.currentTimeMillis() - b));

            b = System.currentTimeMillis();
            service = Executors.newFixedThreadPool(100);
            for (int i = 0; i < tasks; i++) {
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        jedis1.jedisCommands().zrevrange("TEST_SET", 0, 19);
                    }
                });
            }
            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
            System.out.println(
                    "redis1:读:" + k + ":" + tasks + ":2000:184688.SZ|博时新兴成长混合型证券投资基金:" + (System.currentTimeMillis() - b));

            b = System.currentTimeMillis();
            service = Executors.newFixedThreadPool(100);
            for (int i = 0; i < tasks; i++) {
                int j = i;
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        jedis0.jedisCommands().lpush("TEST_QUEUE" + j % 10, value);
                    }
                });
            }
            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
            System.out.println("redis0:写:" + k + ":" + tasks + ":" + value + ":" + (System.currentTimeMillis() - b));

            b = System.currentTimeMillis();
            service = Executors.newFixedThreadPool(100);
            for (int i = 0; i < tasks; i++) {
                int j = i;
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        jedis0.jedisCommands().rpop("TEST_QUEUE" + j % 10);
                    }
                });
            }
            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
            System.out.println("redis0:读:" + k + ":" + tasks + ":" + value + ":" + (System.currentTimeMillis() - b));

            b = System.currentTimeMillis();
            service = Executors.newFixedThreadPool(100);
            for (int i = 0; i < tasks; i++) {
                int j = i;
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        jedis1.jedisCommands().lpush("TEST_QUEUE" + j % 10, value);
                    }
                });
            }
            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
            System.out.println("redis1:写:" + k + ":" + tasks + ":" + value + ":" + (System.currentTimeMillis() - b));

            b = System.currentTimeMillis();
            service = Executors.newFixedThreadPool(100);
            for (int i = 0; i < tasks; i++) {
                int j = i;
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        jedis1.jedisCommands().rpop("TEST_QUEUE" + j % 10);
                    }
                });
            }
            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
            System.out.println("redis1:读:" + k + ":" + tasks + ":" + value + ":" + (System.currentTimeMillis() - b));

            b = System.currentTimeMillis();
            service = Executors.newFixedThreadPool(100);
            for (int i = 0; i < tasks; i++) {
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        jedis0.jedisCommands().lpush("TEST_QUEUE", value);
                    }
                });
            }
            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
            System.out.println("redis0单队列:写:" + k + ":" + tasks + ":" + value + ":" + (System.currentTimeMillis() - b));

            b = System.currentTimeMillis();
            service = Executors.newFixedThreadPool(100);
            for (int i = 0; i < tasks; i++) {
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        jedis0.jedisCommands().rpop("TEST_QUEUE");
                    }
                });
            }
            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
            System.out.println("redis0单队列:读:" + k + ":" + tasks + ":" + value + ":" + (System.currentTimeMillis() - b));

            b = System.currentTimeMillis();
            service = Executors.newFixedThreadPool(100);
            for (int i = 0; i < tasks; i++) {
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        jedis1.jedisCommands().lpush("TEST_QUEUE", value);
                    }
                });
            }
            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
            System.out.println("redis1单队列:写:" + k + ":" + tasks + ":" + value + ":" + (System.currentTimeMillis() - b));

            b = System.currentTimeMillis();
            service = Executors.newFixedThreadPool(100);
            for (int i = 0; i < tasks; i++) {
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        jedis1.jedisCommands().rpop("TEST_QUEUE");
                    }
                });
            }
            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
            System.out.println("redis1单队列:读:" + k + ":" + tasks + ":" + value + ":" + (System.currentTimeMillis() - b));
        }
        System.exit(0);
    }
}
