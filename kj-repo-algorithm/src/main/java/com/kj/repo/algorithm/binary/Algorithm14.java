package com.kj.repo.algorithm.binary;

/**
 * 题目：输入一个已经按升序排序过的数组和一个数字， 在数组中查找两个数，使得它们的和正好是输入的那个数字。
 * 要求时间复杂度是O(n)。如果有多对数字的和等于输入的数字，输出任意一对即可。 例如输入数组1、2、4、7、11、15
 * 和数字15。由于4+11=15，因此输出4 和11。
 * 
 * @author bjzhangkuojian
 *
 */
public class Algorithm14 {
	public int[] compute(int[] a, int sum) {
		int s = 0, e = a.length;
		while (s < e) {
			if (a[s] + a[e] == sum) {
				return new int[] { s, e };
			} else if (a[s] + a[e] > sum) {
				e--;
			} else {
				s++;
			}
		}
		return new int[] { -1, -1 };
	}
}
