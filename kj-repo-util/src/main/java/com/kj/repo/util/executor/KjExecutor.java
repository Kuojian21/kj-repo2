package com.kj.repo.util.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.kj.repo.base.func.KjAction;
import com.kj.repo.util.base.KjExit;

public class KjExecutor {

    public static void run(KjAction action) {
        try {
            action.doAction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void run(KjAction action, KjAction fi) {
        try {
            action.doAction();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fi.doAction();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> T call(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Thread newThread(KjAction action) {
        Thread thread = new Thread(() -> {
            try {
                action.doAction();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(false);
        thread.start();
        KjExit.exit(thread);
        return thread;
    }

    public static ExecutorService newMasterWorker(KjAction action) {
        newThread(action);
        return newExecutorService();
    }

    public static ExecutorService newExecutorService() {
        ExecutorService service = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() - 1,
                Runtime.getRuntime().availableProcessors() - 1, 100, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(40960), /* Executors.defaultThreadFactory() */new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        }, new ThreadPoolExecutor.CallerRunsPolicy());
        KjExit.exit(service);
        return service;
    }

}
