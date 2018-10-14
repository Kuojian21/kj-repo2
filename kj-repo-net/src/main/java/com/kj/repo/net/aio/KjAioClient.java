package com.kj.repo.net.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import com.kj.repo.bean.pool.KjFactory;
import com.kj.repo.bean.pool.KjFactoryImpl;

public class KjAioClient extends KjAio {
    private KjFactory<AsynchronousSocketChannel> kjFactory;

    KjAioClient(KjAioPipeline pipeline) {
        super(pipeline);
    }

    public static KjAioClient connect(String ip, int port, KjAioPipeline pipeline) throws IOException {
        KjAioClient kjAio = new KjAioClient(pipeline);
        kjAio.kjFactory = new KjFactoryImpl<AsynchronousSocketChannel>(100) {
            @Override
            public AsynchronousSocketChannel create() throws Exception {
                AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
                channel.connect(new InetSocketAddress(ip, port), null, new CompletionHandler<Void, Void>() {
                    @Override
                    public void completed(Void result, Void attachment) {
                        try {
                            kjAio.kjFactory.returnObject(channel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failed(Throwable exc, Void attachment) {

                    }
                });
                return null;
            }
        };
        return kjAio;
    }

    public void write(byte[] bytes) throws Exception {
        AsynchronousSocketChannel channel = kjFactory.borrowObject();
        try {
            super.write(channel, bytes).get();
        } finally {
            kjFactory.returnObject(channel);
        }
    }
}
