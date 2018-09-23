package com.kj.repo.bean.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class KjNio {

	static Selector selector;
	static {
		try {
			KjNio.selector = Selector.open();
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (KjNio.selector.isOpen()) {
						Set<SelectionKey> keys = KjNio.selector.selectedKeys();
						Iterator<SelectionKey> iterator = keys.iterator();
						while (iterator.hasNext()) {
							try {
								SelectionKey key = iterator.next();
								if (key.isAcceptable()) {
									ServerSocketChannel server = (ServerSocketChannel) key.channel();
									server.register(KjNio.selector, SelectionKey.OP_ACCEPT);
									SocketChannel channel = server.accept();
									channel.configureBlocking(false);
									Handle handle = (Handle) key.attachment();
									channel.register(KjNio.selector, SelectionKey.OP_READ, new Holder(channel, handle));
								} else if (key.isConnectable()) {
									SocketChannel channel = (SocketChannel) key.channel();
									channel.configureBlocking(false);
									Holder holder = (Holder) key.attachment();
									channel.register(KjNio.selector, SelectionKey.OP_READ, holder);
									holder.semaphore.release();
								} else if (key.isReadable()) {
									SocketChannel channel = (SocketChannel) key.channel();
									Holder holder = (Holder) key.attachment();
									holder.handle.read(holder);
									channel.register(KjNio.selector, SelectionKey.OP_READ, holder);
								} else if (key.isWritable()) {
									Holder holder = (Holder) key.attachment();
									holder.handle.write(holder);
									holder.semaphore.release();
								}
								iterator.remove();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			});
			thread.setDaemon(false);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}

	public static interface Handle {
		void read(Holder holder) throws Exception;

		void write(Holder holder) throws Exception;
	}

	public static class Holder {
		final SocketChannel channel;
		final Semaphore semaphore;
		ByteBuffer writer;
		Handle handle;

		Holder(SocketChannel channel) {
			this.channel = channel;
			this.semaphore = new Semaphore(1);
		}

		Holder(SocketChannel channel, Handle handle) {
			this.channel = channel;
			this.handle = handle;
			this.semaphore = new Semaphore(1);
		}
	}

}
