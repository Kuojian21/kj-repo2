package com.kj.repo.net.base;

import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class KjFuture extends FutureTask<Void> {

    private final ByteBuffer buffer;

    public KjFuture(ByteBuffer buffer) {
        this(buffer, () -> null);
    }

    public KjFuture(ByteBuffer buffer, Callable<Void> callable) {
        super(callable);
        this.buffer = buffer;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }
}
