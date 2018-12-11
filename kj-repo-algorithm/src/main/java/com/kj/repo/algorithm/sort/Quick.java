package com.kj.repo.algorithm.sort;

/**
 * 快速排序
 * @author bjzhangkuojian
 *
 */
public class Quick extends Sort {
	
	@Override
	public void sort(int[] a) {
		int length = a.length;
		this.quick(a, 0, length - 1);
	}
	
	private void quick(int[] a, int low, int high) {
		if (low >= high) {
			return;
		}
		int tLow = low + 1;
		int tHigh = high;

		boolean forward = true;
		while (tLow <= tHigh) {
			if (forward) {
				if (this.compare(a[low], a[tLow])) {
					tLow++;
				} else {
					forward = false;
				}
			} else {
				if (!this.compare(a[low], a[tHigh])) {
					tHigh--;
				} else {
					forward = true;
					this.swap(a, tLow, tHigh);
					tLow++;
					tHigh--;
				}
			}
		}
		tLow--;
		this.swap(a, low, tLow);
		this.quick(a, low, tLow - 1);
		this.quick(a, tLow + 1, high);
	}

	public static void main(String[] args) {
		Quick sort = new Quick();
		int[] a = new int[] { 8888, 9, 8, 7, 0, 99, 0, 4, 5, 993, 3943, 8454, 9343, 6, 7, 7, 7, 5, 55, 2, 1, 0, 999,
				777, 7777, 79734 ,0};
		sort.sort(a);
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}
	}
	
}
