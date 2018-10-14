package com.kj.repo.util.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.kj.repo.base.func.Action;
import com.kj.repo.util.base.KjExit;

public class KjExecutor {

    public static void run(Action action) {
        try {
            action.doAction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void run(Action action, Action fi) {
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

    public static Thread newThread(Action action) {
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

    public static ExecutorService newMasterWorker(Action action) {
        newThread(action);
        return newExecutorService();
    }

    public static ExecutorService newExecutorService() {
        ExecutorService service = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() - 1,
                Runtime.getRuntime().availableProcessors() - 1, 100, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(40960), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
        KjExit.exit(service);
        return service;
    }

}
