package com.kj.repo.bean.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class KjAioClient {

	private final GenericObjectPool<Holder> pool;

	public KjAioClient(String ip, int port) {
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
				AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
				CountDownLatch latch = new CountDownLatch(1);
				channel.connect(new InetSocketAddress(ip, port), null, new CompletionHandler<Void, Void>() {
					@Override
					public void completed(Void result, Void attachment) {
						latch.countDown();
					}

					@Override
					public void failed(Throwable exc, Void attachment) {
						latch.countDown();
					}
				});
				latch.await();
				Holder holder = new Holder(channel, 1024);
				return holder;

			}
		}, config);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				KjAioClient.this.pool.close();
			}
		});
	}

	public static class Holder {
		public final AsynchronousSocketChannel channel;
		public final ByteBuffer reader;
		public final Semaphore semaphore;

		public Holder(AsynchronousSocketChannel channel, int rsize) {
			this.channel = channel;
			this.reader = ByteBuffer.allocate(rsize);
			this.semaphore = new Semaphore(1);
		}
	}

	public CompletionHandler<Integer, Holder> READER = new CompletionHandler<Integer, Holder>() {

		@Override
		public void completed(Integer result, Holder attachment) {
			ByteBuffer read = attachment.reader;
			read.flip();
			byte[] data = new byte[read.limit()];
			read.get(data);
			read.rewind();
			attachment.channel.read(read, attachment, READER);
		}

		@Override
		public void failed(Throwable exc, Holder attachment) {

		}

	};

	public CompletionHandler<Integer, Holder> WRITER = new CompletionHandler<Integer, Holder>() {

		@Override
		public void completed(Integer result, Holder attachment) {
			attachment.semaphore.release();
		}

		@Override
		public void failed(Throwable exc, Holder attachment) {
			attachment.semaphore.release();
		}

	};

	public static interface Action {
		void request(Holder holder) throws Exception;
	}

	public void request(Action action) {
		Holder holder = null;
		try {
			holder = pool.borrowObject();
			action.request(holder);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (holder != null && holder.channel.isOpen()) {
				pool.returnObject(holder);
			}
		}
	}

	public byte[] request(byte[] request) {
		this.request(new Action() {
			@Override
			public void request(Holder holder) throws Exception {
				holder.semaphore.acquire();
				holder.channel.write(ByteBuffer.wrap(request), holder, WRITER);
			}
		});
		return null;
	}
}
