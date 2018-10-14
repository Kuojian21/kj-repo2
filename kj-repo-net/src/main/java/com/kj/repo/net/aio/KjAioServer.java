package com.kj.repo.net.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;

import com.kj.repo.util.executor.KjExecutor;

public class KjAioServer extends KjAio {

    private KjAioServer(KjAioPipeline pipeline) {
        super(pipeline);
    }

    public static KjAioServer bind(int port, KjAioPipeline pipeline) throws IOException {
        KjAioServer kjAio = new KjAioServer(pipeline);
        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
        server.bind(new InetSocketAddress(port));
        KjExecutor.newThread(() -> {
            while (true) {
                try {
                    AsynchronousSocketChannel channel = server.accept().get();
                    read(channel, kjAio, ByteBuffer.allocate(4096));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        });
        return kjAio;
    }


}
