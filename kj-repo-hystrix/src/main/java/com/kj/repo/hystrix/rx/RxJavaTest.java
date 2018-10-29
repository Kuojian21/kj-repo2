package com.kj.repo.hystrix.rx;

import rx.Observable;
import rx.Subscriber;

public class RxJavaTest {

	public static void main(String[] args) {

		Observable.create(t -> {
			t.onNext("kj");
		}).map(t -> t + "map").subscribe(new Subscriber<String>() {

			@Override
			public void onCompleted() {
				// TODO Auto-generated method stub
				System.out.println("kj finish");
			}

			@Override
			public void onError(Throwable e) {
				// TODO Auto-generated method stub
				e.printStackTrace();
			}

			@Override
			public void onNext(String t) {
				// TODO Auto-generated method stub
				System.out.println(t);
			}

		}).unsubscribe();

	}

}
