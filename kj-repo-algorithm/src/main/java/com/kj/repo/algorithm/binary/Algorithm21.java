package com.kj.repo.algorithm.binary;

import java.util.List;

import com.google.common.collect.Lists;

//import com.beust.jcommander.internal.Lists;

/**
 * 第21 题
2010 年中兴面试题
编程求解：
输入两个整数n 和m，从数列1，2，3.......n 中随意取几个数,
使其和等于m ,要求将其中所有的可能组合列出来.
 * @author ThinkPad
 *
 */
public class Algorithm21 {

	public List<List<Integer>> find(int[] a,int s,int i) {
		List<List<Integer>> result = Lists.newArrayList(); 
		for(int len = a.length;i < len;i++) {
			if(a[i] == s) {
				List<Integer> l = Lists.newArrayList();
				l.add(a[i]);
				result.add(l);
			}
			List<List<Integer>> r = find(a,s - a[i],i+1);
			for(List<Integer> l : r) {
				l.add(a[i]);
			}
			result.addAll(r);
		}
		return result;
	}
	
	public static void main(String[] args) {
		
		int[] a = new int[50];
		for(int i = 0; i < a.length;i++) {
			a[i] = i + 1;
		}
		
		List<List<Integer>> r = new Algorithm21().find(a, 160,0);
		
		for(List<Integer> l : r) {
			for(int i : l) {
				System.out.print(i + "\t");
			}
			System.out.println();
		}
		
		
		
	}
	
}
