package com.kj.repo.test.net.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.List;

import com.kj.repo.net.aio.KjAio;
import com.kj.repo.net.aio.KjAioHandler;
import com.kj.repo.net.aio.KjAioPipeline;
import com.kj.repo.net.aio.KjAioServer;

public class AioServerTest {

    public static void main(String[] args) throws IOException {
        KjAioPipeline pipeline = new KjAioPipeline();
        pipeline.getHandlers().add(new ServerAioHandler());
        KjAioServer.bind(8888, pipeline);
    }
}

class ServerAioHandler implements KjAioHandler {

    @Override
    public ByteBuffer read(AsynchronousSocketChannel channel, KjAio kjAio, ByteBuffer buffer) throws Exception {
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        System.out.println("request:" + new String(bytes));
        kjAio.write(channel, (Integer.parseInt(new String(bytes)) * 10 + "").getBytes());
        return null;
    }

    @Override
    public void wrap(List<byte[]> bytesList) {

    }
}