package com.kj.repo.test.net.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.List;

import com.kj.repo.net.aio.KjAio;
import com.kj.repo.net.aio.KjAioClient;
import com.kj.repo.net.aio.KjAioHandler;
import com.kj.repo.net.aio.KjAioPipeline;

public class AioClientTest {

    public static void main(String[] args) throws Exception {
        KjAioPipeline pipeline = new KjAioPipeline();
        pipeline.getHandlers().add(new ClientAioHandler());
        KjAioClient kjAio = KjAioClient.connect("127.0.0.1", 8888, pipeline);

        for (int i = 0; i < 10000; i++) {
            kjAio.write(("" + i).getBytes());
        }

    }

}

class ClientAioHandler implements KjAioHandler {

    @Override
    public ByteBuffer read(AsynchronousSocketChannel channel, KjAio kjAio, ByteBuffer buffer) throws Exception {
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        System.out.println("response:" + new String(bytes));
        return null;
    }

    @Override
    public void wrap(List<byte[]> bytesList) {

    }
}