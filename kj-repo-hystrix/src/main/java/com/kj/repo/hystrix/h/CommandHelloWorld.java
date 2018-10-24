package com.kj.repo.hystrix.h;

import java.util.concurrent.TimeUnit;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class CommandHelloWorld extends HystrixCommand<String> {

	private final String name;

	public CommandHelloWorld(String name) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup")) // 必须
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("ExampleGroup-pool")) // 可选,默认 使用
																							// this.getClass().getSimpleName();
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(4)));

		this.name = name;
	}

	@Override
	protected String run() throws InterruptedException {
		System.out.println("running");
		TimeUnit.MILLISECONDS.sleep(1000);
		return "Hello " + name + "!";
	}

}