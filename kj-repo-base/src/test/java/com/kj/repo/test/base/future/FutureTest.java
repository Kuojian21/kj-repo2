package com.kj.repo.test.base.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.kj.repo.base.future.KjFuture;

/**
 * @author kj
 */
public class FutureTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        KjFuture<Void> future = new KjFuture<>();

        new Thread(() -> {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(1));
                future.set(null);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        future.get();
        System.out.println("finish");
    }

}
