package com.kj.bean;

import java.io.Closeable;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import redis.clients.util.Pool;

public class KjPool<T> {

	private final GenericObjectPool<T> pool;

	public KjPool(final GenericObjectPool<T> pool) {
		this.pool = pool;
	}

	public final <R> R execute(Function<T, R> function) throws Exception {
		T t = null;
		try {
			t = this.pool.borrowObject();
			return function.apply(t);
		} finally {
			if (t != null) {
				this.pool.returnObject(t);
			}
		}
	}

	public final void execute(Consumer<T> consumer) throws Exception {
		T t = null;
		try {
			t = this.pool.borrowObject();
			consumer.accept(t);
		} finally {
			if (t != null) {
				this.pool.returnObject(t);
			}
		}
	}

	public static class Jedis {
		public static <T extends Closeable> KjPool<T> jedis(Pool<T> pool) {
			return new KjPool<T>(new GenericObjectPool<T>(new BasePooledObjectFactory<T>() {
				@Override
				public T create() throws Exception {
					return pool.getResource();
				}

				@Override
				public PooledObject<T> wrap(T obj) {
					return new DefaultPooledObject<T>(obj);
				}

				@Override
				public void destroyObject(final PooledObject<T> obj)
								throws Exception {
					obj.getObject().close();
				}
			}) {
				@Override
				public void close() {
					pool.close();
					super.close();
				}
			});
		}
	}

	public static class Jsch {
		public static KjPool<ChannelSftp> jsch(String host, int port, String username, String password) {
			return new KjPool<ChannelSftp>(
							new GenericObjectPool<ChannelSftp>(new BasePooledObjectFactory<ChannelSftp>() {
								@Override
								public ChannelSftp create() throws Exception {
									JSch jsch = new JSch();
									Session sshSession = jsch.getSession(username, host, port);
									sshSession.setPassword(password);
									Properties sshConfig = new Properties();
									sshConfig.put("StrictHostKeyChecking", "no");
									sshSession.setConfig(sshConfig);
									sshSession.connect();
									Channel channel = sshSession.openChannel("sftp");
									channel.connect();
									return (ChannelSftp) channel;
								}

								@Override
								public PooledObject<ChannelSftp> wrap(ChannelSftp obj) {
									return new DefaultPooledObject<ChannelSftp>(obj);
								}

								@Override
								public void destroyObject(final PooledObject<ChannelSftp> obj)
												throws Exception {
									obj.getObject().disconnect();
									obj.getObject().getSession().disconnect();
								}
							}));
		}

		public static KjPool<ChannelSftp> jsch(String host, int port, String username, String prvfile, String pubfile,
						byte[] passphrase) {
			return new KjPool<ChannelSftp>(
							new GenericObjectPool<ChannelSftp>(new BasePooledObjectFactory<ChannelSftp>() {
								@Override
								public ChannelSftp create() throws Exception {
									JSch jsch = new JSch();
									jsch.addIdentity(prvfile, pubfile, passphrase);
									Session sshSession = jsch.getSession(username, host, port);
									Properties sshConfig = new Properties();
									sshConfig.put("StrictHostKeyChecking", "no");
									sshSession.setConfig(sshConfig);
									sshSession.connect();
									Channel channel = sshSession.openChannel("sftp");
									channel.connect();
									return (ChannelSftp) channel;
								}

								@Override
								public PooledObject<ChannelSftp> wrap(ChannelSftp obj) {
									return new DefaultPooledObject<ChannelSftp>(obj);
								}

								@Override
								public void destroyObject(final PooledObject<ChannelSftp> obj)
												throws Exception {
									obj.getObject().disconnect();
									obj.getObject().getSession().disconnect();
								}
							}));
		}
	}

}
