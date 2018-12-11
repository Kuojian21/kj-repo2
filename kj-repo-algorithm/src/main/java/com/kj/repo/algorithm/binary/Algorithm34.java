package com.kj.repo.algorithm.binary;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 34. 实现一个队列。 队列的应用场景为： 一个生产者线程将int 类型的数入列，一个消费者线程将int 类型的数出列
 * 
 * @author bjzhangkuojian
 *
 */
public class Algorithm34 {

	@SuppressWarnings("rawtypes")
	public void demo() {
		BlockingQueue queue = new LinkedBlockingQueue();
		Thread producer = new Thread(new Runnable() {

			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				try {
					while (true) {
						queue.put(new Object());
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		Thread consumer = new Thread(new Runnable() {

			@SuppressWarnings("unused")
			@Override
			public void run() {
				try {
					while (true) {
						Object obj = queue.take();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		producer.start();
		consumer.start();
	}

}
