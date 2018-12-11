package com.kj.repo.algorithm.binary;

/**
 * 实现一个挺高级的字符匹配算法： 给一串很长字符串，要求找到符合要求的字符串，例如目的串：123 1******3***2 ,12*****3
 * 这些都要找出来 其实就是类似一些和谐系统。。。。。
 * 
 * @author bjzhangkuojian
 *
 */
public class Algorithm33 {

	public boolean match(char[] str, char[] s) {
		int x = 0;
		for (char c : str) {
			x |= 1 << c;
		}

		for (char c : s) {
			if ((x & (1 << c)) == 0) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		System.out.println(new Algorithm33().match("1*张*扩**建**3***2".toCharArray(), "张扩建".toCharArray()));
		System.out.println(new Algorithm33().match("1******3***3".toCharArray(), "123".toCharArray()));
		char c = '张';
		System.out.println(c);

	}

}
