package com.kj.repo.algorithm.binary;

/**
 * 28.整数的二进制表示中1 的个数
题目：输入一个整数，求该整数的二进制表达中有多少个1。
例如输入10，由于其二进制表示为1010，有两个1，因此输出2。
 * @author ThinkPad
 *
 */
public class Algorithm28 {

	public int count(int n) {
		int c = 0;
		while(n != 0) {
			c++;
			n = n & (n - 1);
		}
		
		return c;
	}
	
	public static void main(String[] args) {
		System.out.println(new Algorithm28().count(10));
	}
	
}
