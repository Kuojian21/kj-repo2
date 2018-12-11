package com.kj.repo.algorithm.sort;

/**
 * 选择排序
 * 
 * @author bjzhangkuojian
 *
 */
public class Select extends Sort {

	@Override
	public void sort(int[] a) {
		for (int i = 0, length = a.length; i < length; i++) {
			int tIndex = i;
			int tValue = a[i];
			for (int j = i + 1; j < length; j++) {
				if (super.compare(tValue, a[j])) {
					tValue = a[j];
					tIndex = j;
				}
			}
			super.swap(a, i, tIndex);
		}
	}

	public static void main(String[] args) {
		Select sort = new Select();
		int[] a = new int[] { 9, 8, 7, 0, 99, 0, 4, 5, 993, 3943, 8454, 9343, 6, 7, 7, 7, 5, 55, 2, 1, 0, 999, 777,
				7777, 79734 };
		sort.sort(a);
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}
	}
}
