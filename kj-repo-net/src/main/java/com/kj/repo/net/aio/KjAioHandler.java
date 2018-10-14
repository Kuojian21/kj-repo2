package com.kj.repo.net.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.List;

public interface KjAioHandler {

    ByteBuffer read(AsynchronousSocketChannel channel, KjAio kjAio, ByteBuffer buffer) throws Exception;

    void wrap(List<byte[]> bytesList);

}
