package com.kj.repo.algorithm.binary;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 30.在从1 到n 的正数中1 出现的次数 题目：输入一个整数n，求从1 到n 这n 个整数的十进制表示中1 出现的次数。 例如输入12，从1 到12
 * 这些整数中包含1 的数字有1，10，11 和12，1 一共出现了5 次。 分析：这是一道广为流传的google 面试题。
 * 
 * @author ThinkPad
 *
 */
public class Algorithm30 {

	public int compute(int n) {
		List<int[]> digits = Lists.newArrayList();

		int base = 1;
		while (n / base > 0) {
			digits.add(new int[] { base, n / base / 10, n / base % 10, n % base });
			base *= 10;
		}

		int sum = 0;
		for (int[] digit : digits) {
			if (digit[2] < 1) {
				sum += digit[1] * digit[0];
			} else if (digit[2] == 1) {
				sum += digit[1] * digit[0] + digit[3] + 1;
			} else {
				sum += (digit[1] + 1) * digit[0];
			}
		}
		return sum;
	}

	public static void main(String[] args) {
		System.out.println(new Algorithm30().compute(100));
	}
	
}
