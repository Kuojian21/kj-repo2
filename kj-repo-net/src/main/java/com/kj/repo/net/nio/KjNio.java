package com.kj.repo.net.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.kj.repo.net.base.KjFuture;
import com.kj.repo.util.executor.KjExecutor;

public class KjNio {

    final static Selector selector;
    private final static ConcurrentMap<SocketChannel, KjNio> kjNioMaps = Maps.newConcurrentMap();
    private final ConcurrentMap<SocketChannel, ConcurrentLinkedQueue<KjFuture>> queueMaps = Maps.newConcurrentMap();
    private final ConcurrentMap<SocketChannel, KjFuture> futuresMaps = Maps.newConcurrentMap();
    private final KjNioPipeline pipeline;

    static {
        try {
            selector = Selector.open();
            KjExecutor.newThread(() -> {
                while (selector.isOpen()) {
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keys.iterator();
                    while (iterator.hasNext()) {
                        try {
                            SelectionKey key = iterator.next();
                            if (key.isAcceptable()) {
                                ServerSocketChannel server = (ServerSocketChannel) key.channel();
                                KjNio kjNio = (KjNio) key.attachment();
                                server.register(selector, SelectionKey.OP_ACCEPT);
                                SocketChannel channel = server.accept();
                                channel.configureBlocking(false);
                                kjNioMaps.put(channel, kjNio);
                                kjNio.queueMaps.put(channel, new ConcurrentLinkedQueue<KjFuture>());
                                channel.register(selector, SelectionKey.OP_READ);
                            } else if (key.isConnectable()) {
                                SocketChannel channel = (SocketChannel) key.channel();
                                KjNio kjNio = (KjNio) key.attachment();
                                channel.configureBlocking(false);
                                kjNioMaps.put(channel, kjNio);
                                ((KjNioClient) kjNio).register(channel);
                                kjNio.queueMaps.put(channel, new ConcurrentLinkedQueue<KjFuture>());
                                channel.register(selector, SelectionKey.OP_READ);
                            } else if (key.isReadable()) {
                                SocketChannel channel = (SocketChannel) key.channel();
                                ByteBuffer buffer = (ByteBuffer) key.attachment();
                                read(kjNioMaps.get(channel), channel, buffer);
                            } else if (key.isWritable()) {
                                SocketChannel channel = (SocketChannel) key.channel();
                                KjFuture future = (KjFuture) key.attachment();
                                channel.write(future.getBuffer());
                                if (future.getBuffer().remaining() > 0) {
                                    channel.register(selector, SelectionKey.OP_WRITE, future);
                                } else {
                                    future.run();
                                    KjNio kjNio = kjNioMaps.get(channel);
                                    synchronized (channel) {
                                        kjNio.futuresMaps.remove(channel, future);
                                        write(kjNio, channel, kjNio.queueMaps.get(channel).poll());
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void read(KjNio kjNio, SocketChannel channel, ByteBuffer buffer) throws IOException {
        if (buffer == null) {
            buffer = ByteBuffer.allocate(4096);
        } else if (buffer.limit() == 4096 && channel.read(buffer) > 0) {
            buffer = kjNioMaps.get(channel).pipeline.read(channel, buffer);
        }
        channel.register(selector, SelectionKey.OP_READ,
                kjNioMaps.get(channel).pipeline.read(channel, buffer));
    }


    public static void write(KjNio kjNio, SocketChannel channel, KjFuture kjFuture) throws Exception {
        if (kjFuture == null) {
            return;
        }
        synchronized (channel) {
            if (kjNio.futuresMaps.putIfAbsent(channel, kjFuture) == null) {
                channel.register(selector, SelectionKey.OP_WRITE, kjFuture);
            } else {
                kjNio.queueMaps.get(channel).add(kjFuture);
            }
        }
    }

    KjNio(KjNioPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public KjFuture write(SocketChannel channel, byte[] bytes) throws Exception {
        KjFuture kjFuture = new KjFuture(this.pipeline.wrap(bytes));
        write(this, channel, kjFuture);
        return kjFuture;
    }

}
