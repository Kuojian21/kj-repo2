package com.kj.repo.algorithm.binary;

/**
 * 3.求子数组的最大和
 * 题目：
 * 输入一个整形数组，数组里有正数也有负数。数组中连续的一个或多个整数组成一个子数组，每个子数组都有一个和。求所有子数组的和的最大值。
 * 要求时间复杂度为O(n)。例如输入的数组为1, -2, 3, 10, -4, 7, 2, -5，和最大的子数组为3, 10, -4, 7, 2，因此输出为该子数组的和18。
 * @author bjzhangkuojian
 *
 */
public class Algorithm3 {

	public void algorithm(int[] a) {
		int msum = 0;
		int ms = -1;
		int me = -1;

		int tmsum = 0;
		int tms = -1;
		int tme = -1;

		int sum = 0;
		int s = -1;
		int e = -1;
		for (int i = 0; i < a.length; i++) {
			if (s == -1) {
				if (a[i] > 0) {
					s = i;
					e = i;
					sum = a[i];
					tms = s;
					tme = e;
					tmsum = sum;
				}
			} else if (sum + a[i] <= 0 || i == a.length -1) {
				s = -1;
				e = -1;
				sum = 0;
				if (ms == -1 || tmsum > msum) {
					msum = tmsum;
					ms = tms;
					me = tme;
				}
				tmsum = 0;
				tms = -1;
				tme = -1;
			} else {
				sum = sum + a[i];
				e = i;
				if (sum > tmsum) {
					tme = e;
					tmsum = sum;
				}
			}
		}

		for (int i = ms; i <= me; i++) {
			System.out.print(a[i]+",");
		}
		System.out.println("\t" + msum);
	}

	public static void main(String[] args) {
		new Algorithm3().algorithm(new int[] { 1, -2, 3, 10, -4, 7, 2, -5 });
	}

}
