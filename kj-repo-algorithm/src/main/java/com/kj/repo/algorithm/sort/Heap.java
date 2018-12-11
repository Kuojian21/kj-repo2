package com.kj.repo.algorithm.sort;

/**
 * 堆排序
 * 
 * @author bjzhangkuojian
 *
 */
public class Heap extends Sort {

	@Override
	public void sort(int[] a) {
		int size = a.length;
		for (int i = (size - 1) / 2; i >= 0; i--) {
			this.adjust(a, i,size);
		}
		while (size > 1) {
			size -= 1;
			this.swap(a, 0, size);
			this.adjust(a, 0, size);
		}

	}

	private void adjust(int[] a, int s, int size) {
		int tIndex = s;
		int tValue = a[s];
		int lIndex = s * 2 + 1;
		int rIndex = s * 2 + 2;
		if (lIndex < size && this.compare(a[lIndex], tValue)) {
			tIndex = lIndex;
			tValue = a[lIndex];
		}

		if (rIndex < size && this.compare(a[rIndex], tValue)) {
			tIndex = rIndex;
			tValue = a[rIndex];
		}

		if (tIndex != s) {
			this.swap(a, tIndex, s);
			this.adjust(a, tIndex, size);
		}

	}

	public static void main(String[] args) {
		Heap sort = new Heap();
		int[] a = new int[] { 8888, 9, 8, 7, 0, 99, 0, 4, 5, 993, 3943, 8454, 9343, 6, 7, 7, 7, 5, 55, 2, 1, 0, 999,
				777, 7777, 79734 };
		sort.sort(a);
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}
	}
}
