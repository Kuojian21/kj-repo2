package com.kj.repo.net.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.kj.repo.util.base.KjInt;

public class KjAioPipeline {

    public ByteBuffer read(AsynchronousSocketChannel channel, ByteBuffer buffer) throws IOException {
        return this.getHandlers().get(0).read(channel, buffer);
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
        return Lists.newArrayList(new KjAioHandler() {
            @Override
            public ByteBuffer read(AsynchronousSocketChannel channel, ByteBuffer buffer) throws IOException {
                if (buffer.limit() < 3) {
                    return buffer;
                }
                buffer.mark();
                buffer.rewind();
                byte[] bytes = new byte[4];
                buffer.get(bytes);
                int len = KjInt.toInt(bytes, 0, 3);
                if (len == buffer.limit()) {
                    next(this).read(channel, buffer);
                    return ByteBuffer.allocate(4096);
                } else if (len > buffer.capacity()) {
                    ByteBuffer bb = ByteBuffer.allocate(len);
                    bb.put(bytes);
                    bb.put(buffer);
                    return bb;
                } else {
                    buffer.reset();
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
