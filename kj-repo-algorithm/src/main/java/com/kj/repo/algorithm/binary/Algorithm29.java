package com.kj.repo.algorithm.binary;

import java.util.Stack;

/**
 * 题目：输入两个整数序列。其中一个序列表示栈的push 顺序， 判断另一个序列有没有可能是对应的pop 顺序。 为了简单起见，我们假设push
 * 序列的任意两个整数都是不相等的。 比如输入的push 序列是1、2、3、4、5，那么4、5、3、2、1 就有可能是一个pop 系列。
 * 因为可以有如下的push 和pop 序列： push 1，push 2，push 3，push 4，pop，push 5，pop，pop，pop，pop，
 * 这样得到的pop 序列就是4、5、3、2、1。 但序列4、3、5、1、2 就不可能是push 序列1、2、3、4、5 的pop 序列。
 * 
 * @author ThinkPad
 *
 */
public class Algorithm29 {

	public boolean judge(int[] push, int[] pop) {
		Stack<Integer> stack = new Stack<Integer>();
		int ui = 0;
		int oi = 0;
		while (ui < push.length || oi < pop.length) {
			if (stack.isEmpty() || stack.peek() != pop[oi]) {
				if (ui >= push.length) {
					return false;
				}
				stack.push(push[ui]);
				ui++;
			} else {
				stack.pop();
				oi++;
			}
		}
		return true;
	}

	public static void main(String[] args) {

		System.out.println(new Algorithm29().judge(new int[] { 1, 2, 3, 4, 5 }, new int[] { 4, 5, 3, 2, 1 }));
		System.out.println(new Algorithm29().judge(new int[] { 1, 2, 3, 4, 5 }, new int[] { 4, 3, 5, 1, 2 }));
	}

}
