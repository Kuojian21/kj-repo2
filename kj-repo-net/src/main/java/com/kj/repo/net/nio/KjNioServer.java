package com.kj.repo.net.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

public class KjNioServer extends KjNio {

    KjNioServer(KjNioPipeline pipeline) {
        super(pipeline);
    }

    public static KjNioServer bind(int port, KjNioPipeline pipeline) throws IOException {
        KjNioServer kjNio = new KjNioServer(pipeline);
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.socket().bind(new InetSocketAddress(port));
        server.register(selector, SelectionKey.OP_ACCEPT, kjNio);
        return kjNio;
    }

}
