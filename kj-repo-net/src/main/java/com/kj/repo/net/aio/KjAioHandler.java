package com.kj.repo.net.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.List;

public interface KjAioHandler {

    ByteBuffer read(AsynchronousSocketChannel channel, ByteBuffer buffer) throws IOException;

    void wrap(List<byte[]> bytesList);

}
