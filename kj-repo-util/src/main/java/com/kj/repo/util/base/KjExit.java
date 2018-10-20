package com.kj.repo.util.base;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.kj.repo.base.func.KjAction;

public class KjExit {

	public static void exit(KjAction action) {
		if (action != null) {
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					action.doAction();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}));
		}
	}

	public static void exit(Thread thread) {
		if (thread != null) {
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				thread.interrupt();
			}));
		}
	}

	public static void exit(ExecutorService service) {
		if (service != null) {
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					service.shutdown();
					service.awaitTermination(3, TimeUnit.MINUTES);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}));
		}
	}

}
