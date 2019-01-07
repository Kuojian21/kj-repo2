package org.kj.repo.rxjava;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;

import lombok.Data;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Observable.OnSubscribe;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

public class RxJavaTest {

	public static <T> Subscriber<T> observer() {
		return new Subscriber<T>() {

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
				System.out.println("next " + t + " thread " + Thread.currentThread().getName());
			}

		};
	}

	static Subscriber<Object> subscriber;

	public static void create() {
		Observer<String> observer = observer();
		Observable.create(t -> {
			subscriber = t;
		}).map(t -> t + "map").subscribe(observer);
		subscriber.onNext("zhang");
		subscriber.onNext("kj");
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

	public static void map() {
		Observer<Integer> observer = observer();
		Observable.from(Lists.newArrayList("3", "6", "9")).map(f -> Integer.parseInt(f)).subscribe(observer);
	}

	public static void buffer() {
		Observer<List<String>> observer = observer();
		Observable.from(Lists.newArrayList("3", "6", "9")).buffer(3).subscribe(observer);
	}

	public static void subscribeOn() {
		Observable.from(Lists.newArrayList("3", " 6", "9")).map(f -> Integer.parseInt(f))
				.subscribeOn(Schedulers.from(Executors.newFixedThreadPool(15)));
	}

	public static void observeOn() {
		Observable.from(Lists.newArrayList("3", " 6", "9")).map(f -> Integer.parseInt(f))
				.observeOn(Schedulers.from(Executors.newFixedThreadPool(15)));
	}

	public static void flatMap() {
		Observer<String> observer = observer();
		Observable.from(Lists.newArrayList(Lists.newArrayList("3", " 6", "9"), Lists.newArrayList("3", " 6", "9"),
				Lists.newArrayList("3", " 6", "9"))).flatMap(t -> Observable.from(t)).subscribe(observer);
	}

	@Data
	public static class Item {
		private Integer key;
		private String value;

		public Item(Integer key, String value) {
			super();
			this.key = key;
			this.value = value;
		}

		public String toString() {
			return "key:" + key + ",value=" + value;
		}
	}

	public static List<Item> items() {
		return Lists.newArrayList(new Item(3, "3"), new Item(6, "6"), new Item(9, "9"), new Item(3, "3"),
				new Item(6, "6"), new Item(9, "9"));
	}

	public static void groupBy() {
		Observer<GroupedObservable<Integer, Item>> observer = new Observer<GroupedObservable<Integer, Item>>() {

			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable e) {

			}

			@Override
			public void onNext(GroupedObservable<Integer, Item> t) {
				Observer<Item> observer = observer();
				System.out.println("key:" + t.getKey() + " thread:" + Thread.currentThread().getName());
				t.subscribe(observer);
			}

		};
		Observable<Item> obs = Observable.create(f -> {
			System.out.println("first ?");
			for (Item item : items()) {
				f.onNext(item);
			}
		});
		obs.groupBy(Item::getKey).subscribe(observer);
	}

	public static void scan() {
		Observer<Integer> observer = observer();
		Observable.from(Lists.newArrayList(3, 6, 8)).scan((t1, t2) -> t1 + t2).subscribe(observer);
	}

	public static void window() {
		Observer<Integer> observer = observer();
		Observable.from(Lists.newArrayList(3, 6, 8, 9, 10)).window(2).subscribe(new Subscriber<Observable<Integer>>() {

			@Override
			public void onCompleted() {
				System.out.println("finish");
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("exception " + e.getMessage());
			}

			@Override
			public void onNext(Observable<Integer> t) {
				// System.out.println("next " + t + " thread " +
				// Thread.currentThread().getName());
				t.subscribe(observer);
			}

		});
	}

	public static void debounce() throws InterruptedException {
		Observer<Object> observer = observer();
		Observable.create(t -> {
			subscriber = t;
		}).debounce(3, TimeUnit.SECONDS, Schedulers.from(Executors.newFixedThreadPool(15))).subscribe(observer);
		subscriber.onNext("1");
		subscriber.onNext("11");
		subscriber.onNext("12");
		Thread.sleep(4000);
		subscriber.onNext("2");
		Thread.sleep(4000);
		subscriber.onNext("3");
	}

	public static void when() {
		Observer<Integer> observer = observer();
		Observable.from(Lists.newArrayList(3, 6, 8)).subscribe(observer);
	}

	public static void main(String[] args) throws InterruptedException {
		// create();
		// range();
		// just();
		// timer();
		// buffer();
		// flatMap();
		// interval();
		// defer();
		// groupBy();
		// map();
		// scan();
		debounce();
		// window();
	}

}
