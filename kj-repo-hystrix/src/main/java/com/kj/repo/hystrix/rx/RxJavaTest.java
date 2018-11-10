package com.kj.repo.hystrix.rx;

import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;

public class RxJavaTest {

	public static <T> Observer<T> observer() {
		return new Observer<T>() {

			@Override
			public void onCompleted() {
				System.out.println("finish");
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("exception " + e.getMessage());
			}

			@Override
			public void onNext(T t) {
				System.out.println("next " + t);
			}

		};
	}

	public static void create() {
		Observer<String> observer = observer();
		Observable.create(t -> {
			t.onNext("kj");
		}).map(t -> t + "map").subscribe(observer).unsubscribe();
	}

	public static void range() {
		Observer<Integer> observer = observer();
		Observable.range(2, 10).subscribe(observer);
	}

	public static void from() {
		Observer<Integer> observer = observer();
		Observable.from(Lists.newArrayList(1, 2, 4, 6)).subscribe(observer);
	}

	public static void just() {
		Observer<Integer> observer = observer();
		Observable.just(1).subscribe(observer);
	}

	public static void timer() {
		Observer<Long> observer = observer();
		Observable.timer(5, TimeUnit.SECONDS).subscribe(observer);
	}

	public static void interval() {
		Observer<Long> observer = observer();
		Observable.interval(5, TimeUnit.SECONDS).subscribe(observer);
	}

	public static void defer() {
		Observer<Integer> observer = observer();
		Observable.defer(() -> Observable.from(Lists.newArrayList(3, 6, 9))).subscribe(observer);

	}

	public static void buffer() {

	}

	public static void main(String[] args) throws InterruptedException {
		// create();
		// range();
		// just();
		// timer();
		// interval();
		// defer();
		from();

		Thread.sleep(100000);

	}

}
