package com.kj.repo.bean.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class KjNioClient extends KjNio {
	private final GenericObjectPool<Holder> pool;

	public KjNioClient(String ip, int port) {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMinIdle(-1);
		config.setMaxTotal(100);
		config.setMaxWaitMillis(30000);
		pool = new GenericObjectPool<Holder>(new BasePooledObjectFactory<Holder>() {
			@Override
			public PooledObject<Holder> wrap(Holder holder) {
				return new DefaultPooledObject<Holder>(holder) {
					@Override
					public void invalidate() {
						try {
							holder.channel.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						super.invalidate();
					}
				};
			}

			@Override
			public Holder create() throws Exception {
				SocketChannel channel = SocketChannel.open();
				channel.register(KjNio.selector, SelectionKey.OP_CONNECT);
				channel.configureBlocking(false);
				channel.connect(new InetSocketAddress(ip, port));
				Holder holder = new Holder(channel);
				holder.semaphore.acquire();
				holder.semaphore.acquire();
				holder.semaphore.release();
				return holder;

			}
		}, config);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				KjNioClient.this.pool.close();
			}
		});
	}

	
	public static interface Request{
		void request(Holder holder) throws Exception;
	}
	
	public void request(Request request) {
		Holder holder = null;
		try {
			holder = pool.borrowObject();
			request.request(holder);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (holder != null && holder.channel.isOpen()) {
				pool.returnObject(holder);
			}
		}
	}

	public byte[] request(byte[] request) {
		this.request(new Request() {
			@Override
			public void request(Holder holder) throws Exception {
				holder.semaphore.acquire();
				holder.writer = ByteBuffer.wrap(request);
				holder.channel.register(KjNio.selector, SelectionKey.OP_WRITE);
			}
		});
		return null;
	}

}
