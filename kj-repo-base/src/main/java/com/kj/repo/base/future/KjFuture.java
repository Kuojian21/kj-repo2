package com.kj.repo.base.future;

import java.util.concurrent.FutureTask;

public class KjFuture<T> extends FutureTask<T> {

    public KjFuture() {
        super(() -> null);
    }

    public void set(T t) {
        super.set(t);
    }

    public void setException(Exception e) {
        super.setException(e);
    }

}
