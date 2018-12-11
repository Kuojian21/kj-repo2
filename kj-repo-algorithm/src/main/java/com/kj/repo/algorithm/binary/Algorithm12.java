package com.kj.repo.algorithm.binary;

/**
 * 第12 题
题目：求1+2+…+n，
要求不能使用乘除法、for、while、if、else、switch、case 等关键字以及条件判断语句
（A?B:C）。
 * @author bjzhangkuojian
 *
 */
public class Algorithm12 {

	public int compute(int n) {
		int sum = 0;
		boolean b = n > 0 && (sum = compute(n - 1) + n ) > 0;
		return sum;
	}
	
}
