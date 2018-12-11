package com.kj.repo.algorithm.binary;

/**
 * 第17 题：
题目：在一个字符串中找到第一个只出现一次的字符。如输入abaccdeff，则输出b。
分析：这道题是2006 年google 的一道笔试题。
 * @author bjzhangkuojian
 *
 */
public class Algorithm17 {

	public int first(char[] str) {
		int[] s = new int[256];
		for(int i = 0,len = str.length;i < len ;i++) {
			s[str[i]]++;
		}
		for(int i = 0,len = str.length;i < len ;i++) {
			if(s[str[i]] == 1) {
				return i;
			}
		}
		return -1;
	}
	
}
