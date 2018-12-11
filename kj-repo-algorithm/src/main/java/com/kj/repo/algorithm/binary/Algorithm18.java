package com.kj.repo.algorithm.binary;

/**
 * 第18 题：
题目：n 个数字（0,1,…,n-1）形成一个圆圈，从数字0 开始，
（第一个为当前数字本身，第二个为当前数字的下一个数
字）。
当一个数字删除后，从被删除数字的下一个继续删除第m 个数字。
求出在这个圆圈中剩下的最后一个数字。
 * @author bjzhangkuojian
 *
 */
public class Algorithm18 {

	/**
	 * y	x
	 * m	0
	 * ..	..
	 * n-1	n-1-m
	 * 0	n-m
	 * ..	..
	 * m-2	n-2
	 * 
	 * y = (x + m)/n;
	 * @param n
	 * @param m
	 * @return
	 */
	public int func(int n,int m) {
		if(n == 1) {
			return 0;
		}
		return (func(n - 1,m) + m) %n;
	}
	
	public int func2(int n, int m) {
		int y = 0;
		for(int i = 2;i <=n;i++) {
			y = (y + m)%i;
		}
		return y;
	}
	
	
	public static void main(String[] args) {
		System.out.println(new Algorithm18().func2(5,2));
	}
}
