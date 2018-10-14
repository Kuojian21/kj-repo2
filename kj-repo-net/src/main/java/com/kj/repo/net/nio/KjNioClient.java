package com.kj.repo.net.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.kj.repo.bean.pool.KjFactory;
import com.kj.repo.bean.pool.KjFactoryImpl;

public class KjNioClient extends KjNio {

    private KjFactory<SocketChannel> kjFactory;

    KjNioClient(KjNioPipeline pipeline) {
        super(pipeline);
    }

    public static KjNioClient connect(String ip, int port, KjNioPipeline pipeline) throws IOException {
        KjNioClient kjNio = new KjNioClient(pipeline);
        kjNio.kjFactory = new KjFactoryImpl<SocketChannel>(100) {
            @Override
            public SocketChannel create() throws Exception {
                SocketChannel channel = SocketChannel.open();
                channel.connect(new InetSocketAddress(ip, port));
                channel.register(selector, SelectionKey.OP_CONNECT, kjNio);
                return null;
            }
        };
        return kjNio;
    }

    public void write(byte[] bytes) throws Exception {
        SocketChannel channel = kjFactory.borrowObject();
        try {
            super.write(channel, bytes).get();
        } finally {
            kjFactory.returnObject(channel);
        }
    }

    public void register(SocketChannel channel) throws Exception {
        this.kjFactory.returnObject(channel);
    }


}
