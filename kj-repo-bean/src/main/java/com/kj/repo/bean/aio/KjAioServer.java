package com.kj.repo.bean.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Semaphore;

public class KjAioServer {
	private final Action action;
	private final AsynchronousServerSocketChannel server;

	private KjAioServer(int port, Action action) throws IOException {
		this.action = action;
		AsynchronousServerSocketChannel server = null;
		try {
			server = AsynchronousServerSocketChannel.open();
			server.bind(new InetSocketAddress(port));
			server.accept(null, ACCEPT);
		} finally {
			this.server = server;
			if (this.server != null) {
				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						try {
							KjAioServer.this.server.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
	}

	public static interface Action {
		void response(Holder holder,byte[] data);
	}

	public static class Holder {
		final AsynchronousSocketChannel channel;
		final ByteBuffer reader;
		final Semaphore semaphore;

		Holder(AsynchronousSocketChannel channel, int rsize) {
			this.channel = channel;
			this.reader = ByteBuffer.allocate(rsize);
			this.semaphore = new Semaphore(1);
		}
	}

	private CompletionHandler<AsynchronousSocketChannel, Void> ACCEPT = new CompletionHandler<AsynchronousSocketChannel, Void>() {

		@Override
		public void completed(AsynchronousSocketChannel channel, Void attachment) {
			KjAioServer.this.server.accept(null, ACCEPT);
			Holder holder = new Holder(channel, 1024);
			channel.read(holder.reader, holder, READER);
		}

		@Override
		public void failed(Throwable exc, Void attachment) {

		}

	};

	private CompletionHandler<Integer, Holder> READER = new CompletionHandler<Integer, Holder>() {

		@Override
		public void completed(Integer result, Holder attachment) {
			ByteBuffer read = attachment.reader;
			read.flip();
			byte[] data = new byte[read.limit()];
			read.get(data);
			read.rewind();
			KjAioServer.this.action.response(attachment, data);
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

		}

	};

	public static KjAioServer bind(int port, Action action) {
		try {
			return new KjAioServer(port, action);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
