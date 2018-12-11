package com.kj.repo.algorithm.sort;

/**
 * 基数排序
 * @author bjzhangkuojian
 *
 */
public class Quick2 extends Sort {

	@Override
	public void sort(int[] a) {
		int length = a.length;
		this.quick2(a, 0, length - 1);
	}

	private void quick2(int[] a, int low, int high) {
		if (low >= high) {
			return;
		}

		int tIndex = low;

		for (int i = low + 1; i <= high; i++) {
			if (this.compare(a[low], a[i])) {
				tIndex++;
				this.swap(a, tIndex, i);
			}
		}

		this.swap(a, low, tIndex);
		this.quick2(a, low, tIndex - 1);
		this.quick2(a, tIndex + 1, high);
	}

	public static void main(String[] args) {
		Quick2 sort = new Quick2();
		int[] a = new int[] { 8888, 9, 8, 7, 0, 99, 0, 4, 5, 993, 3943, 8454, 9343, 6, 7, 7, 7, 5, 55, 2, 1, 0, 999,
				777, 7777, 79734 };
		sort.sort(a);
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}
	}

}
