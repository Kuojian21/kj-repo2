package com.kj.repo.tool.tree;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class KjTraverse {

    public KjTraverse() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("close " + KjTraverse.class.getName());
                service.shutdown();
                service.awaitTermination(10, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

    /**
     * tasks count.
     */
    private final AtomicInteger taskCount = new AtomicInteger(0);
    /**
     * shutdown flag.
     */
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    /**
     * thread pool service.
     */
    private final ExecutorService service = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors(), 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(4096), new ThreadPoolExecutor.CallerRunsPolicy()) {
        /**
         * increment task count before task execute.
         */
        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            taskCount.incrementAndGet();
        }

        /**
         * decrement task count after task execute, and if the shutdown-flag is true,
         * then shutdown the thread pool.
         */
        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            if (taskCount.decrementAndGet() == 0 && shutdown.get()) {
                super.shutdown();
            }
        }

        /**
         * set the shutdown-flag of the thread pool, and if the tasks-count is 0, then
         * shutdown the thread pool.
         */
        @Override
        public void shutdown() {
            if (shutdown.compareAndSet(false, true) && taskCount.get() == 0) {
                super.shutdown();
            }
        }
    };

    public <T extends KjNode> void compute(T node, Predicate<T> predicate, Consumer<T> consumer) {
        if (node == null) {
            return;
        } else if (!predicate.test(node)) {
            return;
        }
        switch (node.type()) {
            case LEAF:
                consumer.accept(node);
                break;
            case COMP:
                consumer.accept(node);

                T[] nodes = node.getChilds();
                int length = nodes.length;
                if (length > 0) {
                    for (int i = 0; i < length - 1; i++) {
                        this.submitTask(nodes[i], predicate, consumer);
                    }
                    this.compute(nodes[length - 1], predicate, consumer);
                }
                break;
        }
    }

    public void shutdown() {
        this.service.shutdown();
    }

    public <T extends KjNode> void submitTask(T node, Predicate<T> predicate, Consumer<T> consumer) {
        this.service.submit(() -> KjTraverse.this.compute(node, predicate, consumer));
    }

    public <T extends KjNode> void traverse(T node, Predicate<T> predicate, Consumer<T> consumer) {
        this.submitTask(node, predicate, consumer);
    }

    public static KjTraverse getInstance() {
        return new KjTraverse();
    }

}
