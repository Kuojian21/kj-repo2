package com.kj.repo.net.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.kj.repo.net.base.KjFuture;

public class KjAio {

    private final ConcurrentMap<AsynchronousSocketChannel, ConcurrentLinkedQueue<KjFuture>> queueMaps = Maps
            .newConcurrentMap();
    private final ConcurrentMap<AsynchronousSocketChannel, KjFuture> futuresMaps = Maps.newConcurrentMap();
    private final KjAioPipeline pipeline;

    public KjAio(KjAioPipeline pipeline) {
        super();
        this.pipeline = pipeline;
    }

    public static void read(AsynchronousSocketChannel channel, KjAio kjAio, ByteBuffer buffer) {
        channel.read(buffer, kjAio, new CompletionHandler<Integer, KjAio>() {
            @Override
            public void completed(Integer result, KjAio kjAio) {
                try {
                    ByteBuffer bb = kjAio.pipeline.read(channel, buffer);
                    read(channel, kjAio, bb);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, KjAio kjAio) {
                exc.printStackTrace();
            }

        });
    }

    public static void write(AsynchronousSocketChannel channel, KjAio kjAio, KjFuture kjFuture) {
        if (kjFuture == null) {
            return;
        }
        synchronized (channel) {
            if (kjAio.futuresMaps.putIfAbsent(channel, kjFuture) == null) {
                channel.write(kjFuture.getBuffer(), kjFuture, new CompletionHandler<Integer, KjFuture>() {
                    @Override
                    public void completed(Integer result, KjFuture kjFuture) {
                        if (kjFuture.getBuffer().remaining() > 0) {
                            write(channel, kjAio, kjFuture);
                        } else {
                            kjFuture.run();
                            synchronized (channel) {
                                kjAio.futuresMaps.remove(channel, kjFuture);
                                write(channel, kjAio, kjAio.queueMaps.get(channel).poll());
                            }
                        }
                    }

                    @Override
                    public void failed(Throwable exc, KjFuture buffer) {
                        exc.printStackTrace();
                    }
                });
            } else {
                kjAio.queueMaps.get(channel).add(kjFuture);
            }
        }
    }

    public KjFuture write(AsynchronousSocketChannel channel, byte[] bytes) throws Exception {
        KjFuture kjFuture = new KjFuture(this.pipeline.wrap(bytes));
        write(channel, this, kjFuture);
        return kjFuture;
    }

}
