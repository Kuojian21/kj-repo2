package com.kj.repo.algorithm.binary;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 第25 题：
写一个函数,它的原形是int continumax(char *outputstr,char *intputstr)
功能：
在字符串中找出连续最长的数字串，并把这个串的长度返回，
并把这个最长数字串付给其中一个函数参数outputstr 所指内存。
例如："abcd12345ed125ss123456789"的首地址传给intputstr 后，函数将返回9，
outputstr 所指的值为123456789
 * @author ThinkPad
 *
 */
public class Algorithm25 {
	
	public List<String> continumax(char[] str) {
		int s = 0,len = 0;
		int mlen = 0;
		List<String> r = Lists.newArrayList();
		
		for(int i = 0,l = str.length;i < l; i++) {
			if(str[i] >= '0' && str[i] <= '9') {
				if(len == 0) {
					s = i;
				}
				len++;
			}
			
			if(len > 0 && (!(str[i] >= '0' && str[i] <= '9') || i == l - 1)){
				if(len > mlen) {
					r.clear();
					r.add(new String(str,s,len));
					mlen = len;
				}else if(len == mlen){
					r.add(new String(str,s,len));
				}
				len = 0;
				s = 0;
			}
		}
		return r;
	}
	
	public static void main(String[] args) {
		for(String s : new Algorithm25().continumax("abcd12345ed125ss123456789".toCharArray())) {
			System.out.println(s);
		}
	}
	
}
