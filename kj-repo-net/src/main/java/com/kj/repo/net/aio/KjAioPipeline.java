package com.kj.repo.net.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.kj.repo.util.base.KjInt;

public class KjAioPipeline {

	private List<KjAioHandler> handlers = Lists.newArrayList();

	public KjAioPipeline() {
		handlers.add(new KjAioHandler() {
			@Override
			public ByteBuffer read(AsynchronousSocketChannel channel, KjAio kjAio, ByteBuffer buffer) throws Exception {
				if (buffer.position() < 3) {
					return buffer;
				}
				int bLen = buffer.position();
				buffer.flip();
				byte[] bytes = new byte[4];
				buffer.get(bytes);
				int len = KjInt.toInt(bytes, 0, 3);
				if (len < bLen) {
					ByteBuffer bb = ByteBuffer.allocate(4096);
					bb.put(buffer);
					buffer.position(4);
					buffer.limit(len - 1);
					next(this).read(channel, kjAio, buffer);
					return bb;
				} else if (len == bLen) {
					next(this).read(channel, kjAio, buffer);
					return ByteBuffer.allocate(4096);
				} else if (len > buffer.capacity()) {
					ByteBuffer bb = ByteBuffer.allocate(len);
					bb.put(bytes);
					bb.put(buffer);
					return bb;
				} else {
					buffer.position(bLen);
					buffer.limit(buffer.capacity());
					return buffer;
				}
			}

			@Override
			public void wrap(List<byte[]> bytesList) {
				int len = 4;
				for (byte[] b : bytesList) {
					len += b.length;
				}
				bytesList.add(0, KjInt.toBytes(len));
			}
		});
	}

	public ByteBuffer read(AsynchronousSocketChannel channel, KjAio kjAio, ByteBuffer buffer) throws Exception {
		return this.getHandlers().get(0).read(channel, kjAio, buffer);
	}

	public ByteBuffer wrap(byte[] bytes) {
		List<KjAioHandler> handlers = this.getHandlers();
		List<byte[]> bytesList = Lists.newArrayList(bytes);
		for (int i = handlers.size() - 1; i >= 0; i--) {
			handlers.get(i).wrap(bytesList);
		}
		ByteBuffer buffer = ByteBuffer.allocate(KjInt.toInt(bytesList.get(0), 0, 3));
		for (byte[] b : bytesList) {
			buffer.put(b);
		}

		return buffer;
	}

	public List<KjAioHandler> getHandlers() {
		return handlers;
	}

	public KjAioHandler next(KjAioHandler handler) {
		Iterator<KjAioHandler> iterator = this.getHandlers().iterator();
		while (iterator.hasNext()) {
			if (handler.equals(iterator.next())) {
				if (iterator.hasNext()) {
					return iterator.next();
				} else {
					return null;
				}
			}
		}
		return null;
	}

}
