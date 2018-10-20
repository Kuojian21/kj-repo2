package com.kj.repo.test.base.future;

import java.util.concurrent.ExecutionException;

import com.kj.repo.base.future.KjFuture;

public class FutureTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        KjFuture<Void> future = new KjFuture<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    future.set(null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        future.get();
        System.out.println("finish");
    }

}
