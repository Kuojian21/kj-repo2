package com.kj.repo.algorithm.sort;

/**
 * 插入排序
 * 
 * @author bjzhangkuojian
 *
 */
public class Insert extends Sort {

	@Override
	public void sort(int[] a) {
		for (int i = 1, length = a.length; i < length; i++) {
			for (int j = i - 1; j >= 0; j--) {
				if (super.compare(a[j], a[j + 1])) {
					super.swap(a, j, j + 1);
				} else {
					break;
				}
			}
		}
	}
	public static void main(String[] args) {
		Insert sort = new Insert();
		int[] a = new int[] { 9, 8, 7, 0, 99, 0, 4, 5, 993, 3943, 8454, 9343, 6, 7, 7, 7, 5, 55, 2, 1, 0, 999, 777,
				7777, 79734 };
		sort.sort(a);
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}
	}
}
