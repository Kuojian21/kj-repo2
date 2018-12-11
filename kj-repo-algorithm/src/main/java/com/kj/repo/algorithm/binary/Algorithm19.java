package com.kj.repo.algorithm.binary;

/**
 * 第19 题：
题目：定义Fibonacci 数列如下：
/ 0 n=0
f(n)= 1 n=1
\ f(n-1)+f(n-2) n=2
输入n，用最快的方法求该数列的第n 项。
 * @author ThinkPad
 *
 */
public class Algorithm19 {

	public int fibonacci(int n) {
		if(n == 0 || n == 1) {
			return 1;
		}
		return fibonacci(n - 1) + fibonacci(n - 2);
	}
	
	public int fibonacci1(int n) {
		if(n == 0 || n == 1) {
			return 1;
		}
		int f1 = 1,f2 = 1,f3 = 2;
		for(int i = 3;i <= n;i++) {
			f1 = f2;
			f2 = f3;
			f3 = f1 + f2;
		}
		return f3;
	}
	
	public static void main(String[] args) {
		System.out.println(new Algorithm19().fibonacci(100));
		System.out.println(new Algorithm19().fibonacci1(10000000));
	}
	
}
