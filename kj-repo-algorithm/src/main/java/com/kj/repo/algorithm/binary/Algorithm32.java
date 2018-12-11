package com.kj.repo.algorithm.binary;

import java.util.List;

import com.google.common.collect.Lists;


/**
 * 32. 有两个序列a,b，大小都为n,序列元素的值任意整数，无序； 要求：通过交换a,b 中的元素，使[序列a 元素的和]与[序列b
 * 元素的和]之间的差最小。 例如: var a=[100,99,98,1,2, 3]; var b=[1, 2, 3, 4,5,40];
 * 
 * @author bjzhangkuojian
 *
 */
public class Algorithm32 {

	public int choose(int[] a, int s, int sum, int n, List<Integer> nums) {
		if (n == 0) {
			return Math.abs(sum);
		} else if (a.length - s == n) {
			for (int i = s, len = a.length; i < len; i++) {
				sum -= a[i];
				nums.add(i);
			}
			return Math.abs(sum);
		}
		List<Integer> nums1 = Lists.newArrayList();
		int rtn1 = choose(a, s + 1, sum, n, nums1);
		List<Integer> nums2 = Lists.newArrayList();
		nums2.add(s);
		int rtn2 = choose(a, s + 1, sum - a[s], n - 1, nums2);
		if (rtn1 <= rtn2) {
			nums.addAll(nums1);
			return rtn1;
		}
		nums.addAll(nums2);
		return rtn1;
	}

	public void compute(int[] a, int[] b) {
		int[] c = new int[a.length * 2];
		int sum = 0;
		for (int i = 0, len = a.length; i < len; i++) {
			c[i] = a[i];
			sum += a[i];
		}
		for (int i = 0, len = a.length; i < len; i++) {
			c[i + len] = b[i];
			sum += b[i];
		}
		List<Integer> l = Lists.newArrayList();
		l.add(0);
		choose(c, 1, sum / 2 - c[0], a.length - 1, l);

		int ai = 0;
		int bi = 0;
		for (int i = 0, len = c.length; i < len; i++) {
			if (l.contains(i)) {
				a[ai++] = c[i];
			} else {
				b[bi++] = c[i];
			}
		}

	}

	public void compute2(int[] a, int[] b) {
		while (true) {
			int[] s = sum(a, b);
			int x = s[0] - s[1];
			int min = Math.abs(x - (a[0] - b[0]) * 2);
			int mini = 0, minj = 0;
			for (int i = 0, len = a.length; i < len; i++) {
				for (int j = 0; j < len; j++) {
					int y = Math.abs(x - (a[i] - b[j]) * 2);
					if (min > y) {
						mini = i;
						minj = j;
						min = y;
					}
				}
			}

			if (min >= Math.abs(x)) {
				return;
			} else {
				int t = a[mini];
				a[mini] = b[minj];
				b[minj] = t;
			}
		}
	}

	public int[] sum(int[] a, int[] b) {
		int sa = 0, sb = 0;
		for (int i = 0, len = a.length; i < len; i++) {
			sa += a[i];
			sb += b[i];
		}
		return new int[] { sa, sb };
	}

	public static void main(String[] args) {
		// int[][] l = new Algorithm32().compute(new int[] { 100, 99, 98, 1, 2, 3 }, new
		// int[] { 1, 2, 3, 4, 5, 40 });

		int[] a = new int[] { 1, 2, 3, 10, 11, 12, 8 };
		int[] b = new int[] { 7, 8, 9, 4, 5, 6, 7 };
		new Algorithm32().compute2(a, b);
		int[][] l = new int[][] { a, b };

		for (int[] i : l) {
			int sum = 0;
			for (int j : i) {
				System.out.print(j + "\t");
				sum += j;
			}
			System.out.println("\tsum=" + sum);
		}
	}

}
