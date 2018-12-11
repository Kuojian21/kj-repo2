package com.kj.repo.algorithm.binary;

/**
 * 第20 题：
题目：输入一个表示整数的字符串，把该字符串转换成整数并输出。
例如输入字符串"345"，则输出整数345。
 * @author ThinkPad
 *
 */
public class Algorithm20 {

	public int parse(char[] str) {
		int s = 0;
		int f = 1;
		if(str[0] == '+') {
			f = 1;
			s = 1;
		}else if(str[0] == '-'){
			f = -1;
			s = 1;
		}
		
		int n = 0;
		for(int i = s,len = str.length;i < len;i++) {
			n = 10 * n + str[i] - '0';
		}
		return n * f;
	}
	
	public static void main(String[] args) {
		System.out.println(new Algorithm20().parse(new String("-24").toCharArray()));
	}
	
}
