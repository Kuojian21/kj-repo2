package com.kj.repo.bean.pool;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.kj.repo.util.resource.KjResource;

public abstract class KjFactoryImpl<T> implements KjFactory<T> {

	private final AtomicInteger count = new AtomicInteger(0);
	private final Integer limit;
	private final ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<T>();

	private final AtomicBoolean shutdown = new AtomicBoolean(false);
	private final AtomicInteger notify = new AtomicInteger(0);
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition cond = lock.newCondition();

	public KjFactoryImpl(int limit) {
		this.limit = limit;
	}

	public abstract T create() throws Exception;

	@Override
	public T borrowObject() throws Exception {
		if (this.shutdown.get()) {
			return null;
		}
		T t = this.queue.poll();
		if (t != null) {
			return t;
		}
		try {
			lock.lock();
			if (this.count.get() < limit) {
				this.count.incrementAndGet();
				t = this.create();
				if (t != null) {
					return t;
				}
			}
			this.notify.incrementAndGet();
			t = this.queue.poll();
			if (t != null) {
				this.notify.decrementAndGet();
				return t;
			}
			this.cond.await(300, TimeUnit.MILLISECONDS);
			this.notify.decrementAndGet();
			return this.borrowObject();
		} finally {
			lock.unlock();
		}

	}

	@Override
	public void returnObject(T obj) throws Exception {
		if (obj == null) {
			return;
		}
		this.queue.add(obj);
		if (this.notify.get() >= 1) {
			try {
				lock.lock();
				if (this.notify.get() >= 1) {
					cond.signalAll();
				}
			} finally {
				lock.unlock();
			}
		}

	}

	@Override
	public void close() throws Exception {
		this.shutdown.set(true);
		T t = null;
		while ((t = this.queue.poll()) != null) {
			this.count.decrementAndGet();
			KjResource.close(t);
		}

		while (this.count.get() > 0) {
			try {
				lock.lock();
				this.notify.incrementAndGet();
				t = this.queue.poll();
				if (t != null) {
					this.count.decrementAndGet();
					this.notify.decrementAndGet();
					KjResource.close(t);
					continue;
				}
				this.cond.await(300, TimeUnit.MILLISECONDS);
				this.notify.decrementAndGet();
			} finally {
				lock.unlock();
			}
		}
	}

}
