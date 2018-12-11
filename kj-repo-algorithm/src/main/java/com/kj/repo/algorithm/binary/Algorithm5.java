package com.kj.repo.algorithm.binary;

/**
 * 5.查找最小的k 个元素 题目：输入n 个整数，输出其中最小的k 个。 例如输入1，2，3，4，5，6，7 和8 这8 个数字，则最小的4
 * 个数字为1，2，3 和4。
 * 
 * @author bjzhangkuojian
 *
 */
public class Algorithm5 {

	public int[] top(int[] a, int k) {
		int[] r = new int[k];
		int index = 0;
		r[index] = a[0];
		for (int i = 1; i <= a.length - 1; i++) {
			if (r[index] > a[i]) {
				for (int j = index; j >= 0; j--) {
					if (r[j] <= a[i]) {
						r[j + 1] = a[i];
						break;
					} else {
						if (j < k - 1) {
							r[j + 1] = r[j];
						}
						if (j == 0) {
							r[j] = a[i];
						}
					}
				}
				if(index < k - 1) {
					index++;
				}
			} else if (index < k - 1) {
				r[++index] = a[i];
			}
		}
		return r;
	}

	public static void main(String[] args) {
		int[] r = new Algorithm5().top(new int[] { 8, 3,7, 6, 5, 4,5,7, 3, 2, 1 }, 4);
		for (int i = 0; i < 4; i++) {
			System.out.print(r[i] + "\t");
		}
	}
}
