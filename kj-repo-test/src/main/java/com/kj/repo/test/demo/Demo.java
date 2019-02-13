package com.kj.repo.test.demo;

import java.io.IOException;
import java.util.Random;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Demo {
	private String name;

	Gson gson = new Gson();

	public Demo(String name) {
		super();
		this.name = name;
	}

	public String toString() {
		return this.name;
	}

	public int add(int a, int b) {
		log.info("{} {}", a, b);
		Test test = new Test();
		int result = 0;
		try {
			result = test.add(a, b);
			System.out.println(this.getClass().getClassLoader());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void main(String[] args) {
		Random random = new Random();
		Demo demo = new Demo("this is a Demo1 instace");
		while (true) {
			int a = random.nextInt(100);
			int b = random.nextInt(100);
			int c = demo.add(a, b);
			System.out.println("a:" + a);
			System.out.println("b:" + b);
			System.out.println("a+b:" + c);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class Test {
	public int add(int a, int b) throws IOException {
		if (a > 50 && a < 80)
			throw new IOException("this is a exception!");
		return a + b;
	}
}
