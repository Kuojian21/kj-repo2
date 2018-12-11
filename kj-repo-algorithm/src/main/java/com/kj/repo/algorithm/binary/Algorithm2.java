package com.kj.repo.algorithm.binary;

import java.util.Random;

//import com.alibaba.fastjson.JSON;
/**
 * 2.设计包含min 函数的栈。
 * 定义栈的数据结构，要求添加一个min 函数，能够得到栈的最小元素。要求函数min、push 以及pop 的时间复杂度都是O(1)。
 * @author bjzhangkuojian
 *
 */
public class Algorithm2 {
	final int MAX_LENTH = 10;
	final int[] datas = new int[MAX_LENTH];
	final int[] mins = new int[MAX_LENTH];
	int top;
	int index;

	public boolean push(int data) {
		if (top >= MAX_LENTH) {
			return false;
		}
		if (top == 0) {
			index = 0;
			mins[index] = top;
			index++;
		} else if (data < datas[mins[index - 1]]) {
			mins[index] = top;
			index++;
		}
		datas[top] = data;
		top++;
		return true;
	}

	public int pop() {
		if(top <= 0) {
			return Integer.MIN_VALUE;
		}
		if(top == mins[index-1]) {
			index--;
		}
		return datas[top--];
		
	}

	public int min() {
		if(top <= 0) {
			return Integer.MIN_VALUE;
		}
		return datas[mins[index - 1]];
	}

	public static void main(String[] args) {
		Random random = new Random();
		Algorithm2 stack = new Algorithm2();
		for (int i = 0; i < 12; i++) {
			stack.push(random.nextInt(100));
		}
//		System.out.println(JSON.toJSONString(stack));
	}
	
}
