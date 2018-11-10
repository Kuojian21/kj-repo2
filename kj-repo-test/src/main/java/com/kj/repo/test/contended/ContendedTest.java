package com.kj.repo.test.contended;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.kj.repo.util.executor.KjExecutor;

public class ContendedTest {

	public static void main(String[] args) throws InterruptedException {
		HasContended c = new HasContended();
		CountDownLatch ls = new CountDownLatch(1);
		CountDownLatch lf = new CountDownLatch(2);
		new Thread(() -> {
			KjExecutor.run(() -> {
				ls.await();
				for (int j = 0; j < 1000000000; j++) {
					c.setA(j);
				}
				lf.countDown();
			});
		}).start();
		new Thread(() -> {
			KjExecutor.run(() -> {
				ls.await();
				for (int j = 0; j < 1000000000; j++) {
					c.setB(j);
				}
			});
			lf.countDown();
		}).start();
		Stopwatch stopwatch = Stopwatch.createStarted();
		ls.countDown();
		lf.await();
		System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));
	}

}
