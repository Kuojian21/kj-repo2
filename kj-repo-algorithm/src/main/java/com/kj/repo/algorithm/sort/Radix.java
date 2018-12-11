package com.kj.repo.algorithm.sort;

/**
 * 基数排序
 * 
 * @author bjzhangkuojian
 *
 */
public class Radix extends Sort {

	@Override
	public void sort(int[] a) {
		for (int k = 1, max = super.max(a), mlen = this.length(max); k <= mlen; k++) {
			for (int i = 1, length = a.length; i <= length - 1; i++) {
				boolean swap = false;
				for (int j = 0; j < length - i; j++) {
					if (super.compare(this.digital(a[j], k), this.digital(a[j + 1], k))) {
						super.swap(a, j, j + 1);
						swap = true;
					}
				}
				if (!swap) {
					break;
				}
			}
		}

	}

	private int digital(int x, int k) {
		for (int i = 2; i <= k; i++) {
			x = x / 10;
		}
		return x % 10;
	}

	private int length(int x) {
		int length = 1;
		while (x / 10 > 0) {
			x = x / 10;
			length++;
		}
		return length;
	}

	public static void main(String[] args) {
		Radix sort = new Radix();
		int[] a = new int[] { 9, 8, 7, 0, 99, 0, 4, 5, 993, 3943, 8454, 9343, 6, 7, 7, 7, 5, 55, 2, 1, 0, 999, 777,
				7777, 79734 };
		sort.sort(a);
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}
	}
}
