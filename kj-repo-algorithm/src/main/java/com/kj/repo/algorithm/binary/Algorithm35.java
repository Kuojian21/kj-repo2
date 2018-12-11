package com.kj.repo.algorithm.binary;

/**
 * 35. 求一个矩阵中最大的二维矩阵(元素和最大).如: 1 2 0 3 4 2 3 4 5 1 1 1 5 3 0 中最大的是: 4 5 5 3
 * 要求:(1)写出算法;(2)分析时间复杂度;(3)用C 写出关键代码
 * 
 * @author bjzhangkuojian
 *
 */
public class Algorithm35 {

	public int[] max(int[][] a) {
		int mi = 0, mj = 0, m = a[0][0] + a[1][0] + a[0][1] + a[1][1];
		for (int i = 0, ilen = a.length; i < ilen - 1; i++) {
			int last = a[i][0] + a[i + 1][0];
			for (int j = 0, jlen = a[0].length; j < jlen - 1; j++) {
				int t = a[i][j + 1] + a[i + 1][j + 1];
				if (t + last > m) {
					m = t + last;
					mi = i;
					mj = j;
				}
				last = t;
			}
		}
		return new int[] { mi, mj, m };
	}
	
	public int[] max(int[] a,int n) {
		int mi = 0, mj = 0, m = a[0] + a[n] + a[1] + a[n + 1];
		for (int i = 0, ilen = a.length / n; i < ilen - 1; i++) {
			int last = a[i * n] + a[(i+1) * n];
			for (int j = 0, jlen = n; j < jlen - 1; j++) {
				int t = a[i * n + j + 1] + a[(i+1) * n + j + 1];
				if (t + last > m) {
					m = t + last;
					mi = i;
					mj = j;
				}
				last = t;
			}
		}
		return new int[] { mi, mj, m };
	}

	public static void main(String[] args) {
		for (int i : new Algorithm35().max(new int[][] { { 1, 2, 0, 3, 4 }, { 2, 3, 4, 5, 1 }, { 1, 1, 5, 3, 0 } })) {
			System.out.print(i + "\t");
		}
		for (int i : new Algorithm35().max(new int[] {  1, 2, 0, 3, 4 ,  2, 3, 4, 5, 1 ,  1, 1, 5, 3, 0  },5)) {
			System.out.print(i + "\t");
		}
	}

}
