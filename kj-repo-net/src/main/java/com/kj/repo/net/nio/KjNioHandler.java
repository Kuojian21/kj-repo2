package com.kj.repo.net.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

public interface KjNioHandler {

    ByteBuffer read(SocketChannel channel, ByteBuffer buffer) throws IOException;

    void wrap(List<byte[]> bytesList);

}
