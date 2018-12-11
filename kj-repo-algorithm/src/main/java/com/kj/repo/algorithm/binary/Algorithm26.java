package com.kj.repo.algorithm.binary;
/**
 * 题目描述：定义字符串的左旋转操作：把字符串前面的若干个字符移动到字符串的尾部。
如把字符串abcdef左旋转2位得到字符串cdefab。
请实现字符串左旋转的函数，要求对长度为n的字符串操作的时间复杂度为O(n)，空间复杂度为O(1)。
 * @author ThinkPad
 *
 */
public class Algorithm26 {

	public void reverse(char[] str, int s, int e) {
		while (s < e) {
			char c = str[s];
			str[s] = str[e];
			str[e] = c;
			s++;
			e--;
		}
	}
	
	public void left(char[] str,int k) {
		reverse(str,0,str.length - 1);
		reverse(str,0,str.length - k - 1);
		reverse(str,str.length - k,str.length - 1);
	}
	
	public static void main(String[] args) {
		char[] str = "abcdef".toCharArray();
		new Algorithm26().left(str,2);
		System.out.println(new String(str));
	}
	
}
