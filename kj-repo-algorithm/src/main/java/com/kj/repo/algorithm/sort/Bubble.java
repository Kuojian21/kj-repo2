package com.kj.repo.algorithm.sort;

/**
 * 冒泡排序
 * 
 * @author bjzhangkuojian
 *
 */
public class Bubble extends Sort{

	@Override
	public void sort(int[] a) {
		for (int i = 1, length = a.length; i <= length - 1; i++) {
			boolean swap = false;
			for (int j = 0; j < length - i; j++) {
				if (super.compare(a[j], a[j + 1])) {
					super.swap(a, j, j + 1);
					swap = true;
				}
			}
			if (!swap) {
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		Bubble sort = new Bubble();
		int[] a = new int[] { 9, 8, 7, 0, 99, 0, 4, 5, 993, 3943, 8454, 9343, 6, 7, 7, 7, 5, 55, 2, 1, 0, 999, 777,
				7777, 79734 };
		sort.sort(a);
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}
	}
}
