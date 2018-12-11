package com.kj.repo.algorithm.sort;

/**
 * 合并排序
 * @author bjzhangkuojian
 *
 */
public class Merge extends Sort {

	@Override
	public void sort(int[] a) {
		merge(a, 0, a.length - 1);
	}

	private void merge(int[] a, int low, int high) {
		if (low >= high) {
			return;
		}
		int mid = (high + low) / 2;
		merge(a, low, mid);
		merge(a, mid + 1, high);
		int i = low, j = mid + 1, k = 0, blen = high - low + 1;
		int b[] = new int[blen];
		while (i <= mid && j <= high) {
			if (this.compare(a[i], a[j])) {
				b[k++] = a[j++];
			} else {
				b[k++] = a[i++];
			}
		}
		while (i <= mid) {
			b[k++] = a[i++];
		}
		while (j <= high) {
			b[k++] = a[j++];
		}
		for (i = 0; i < blen; i++) {
			a[i + low] = b[i];
		}
	}

	public static void main(String[] args) {
		Merge sort = new Merge();
		int[] a = new int[] { 8888, 9, 8, 7, 0, 99, 0, 4, 5, 993, 3943, 8454, 9343, 6, 7, 7, 7, 5, 55, 2, 1, 0, 999,
				777, 7777, 79734, 0 };
		sort.sort(a);
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}
	}

}
