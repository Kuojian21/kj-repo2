package com.kj.repo.algorithm.sort;

/**
 * 基数排序
 * @author bjzhangkuojian
 *
 */
public class Count extends Sort {

	@Override
	public void sort(int[] a) {
		int length = a.length;
		int max = super.max(a);
		int[] count = new int[max + 1];
		for (int i = 0; i < length; i++) {
			count[a[i]]++;
		}
		int[] b = new int[length];
		int index = 0;
		for (int i = 0, len = max + 1; i < len; i++) {
			for (int j = 1; j <= count[i]; j++) {
				b[index++] = i;
			}
		}

		for (int i = 0; i < length; i++) {
			a[i] = b[i];
		}
	}

	public static void main(String[] args) {
		Count sort = new Count();
		int[] a = new int[] { 9, 8, 7, 0, 99, 0, 4, 5, 993, 3943, 8454, 9343, 6, 7, 7, 7, 5, 55, 2, 1, 0, 999, 777,
				7777, 79734 };
		sort.sort(a);
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}
	}
}
