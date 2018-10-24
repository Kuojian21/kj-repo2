package com.kj.repo.hystrix;

import java.util.concurrent.Future;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import rx.Observable;

public class CommandHelloWorld extends HystrixCommand<String> {

	private final String name;

	public CommandHelloWorld(String name) {
		super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup")); // 必须
		this.name = name;
	}

	@Override
	protected String run() {
		/*
		 * 网络调用 或者其他一些业务逻辑，可能会超时或者抛异常
		 */
		System.out.println("inner run");
		return "Hello " + name + "!";
	}

	public static void main(String[] args) {
		System.out.println(new CommandHelloWorld("Bob").execute()); //
		Future<String> s1 = new CommandHelloWorld("Bob").queue();
//		Observable<String> s2 = new CommandHelloWorld("Bob").observe();
		Observable<String> s3 = new CommandHelloWorld("Bob").toObservable();
	}
}