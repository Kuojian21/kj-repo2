package com.kj.repo.algorithm.sort;

import java.util.Random;

public abstract class Sort {

	public boolean compare(int x, int y) {
		if (x > y) {
			return true;
		}
		return false;
	}

	public void swap(int[] a, int p, int q) {
		if (p != q) {
			int temp = a[p];
			a[p] = a[q];
			a[q] = temp;
		}
	}

	public int max(int[] a) {
		int length = a.length;
		int max = a[0];
		for (int i = 1; i < length; i++) {
			if (max < a[i]) {
				max = a[i];
			}
		}
		return max;
	}

	public abstract void sort(int[] a);

	public static void main(String[] args) {
		Sort[] sorts = new Sort[] { new Bubble(), new Count(), new Heap(), new Insert(), new Merge(), new Quick(),
				new Quick2(), new Radix(), new Select(), new Shell() };

		int[] a = new int[10000] /*
									 * { 9, 8, 7, 0, 99, 0, 4, 5, 993, 3943, 8454, 9343, 6, 7, 7, 7, 5, 55, 2, 1, 0,
									 * 999, 777, 7777, 79734 }
									 */;

		Random r = new Random();
		for (int i = 0, len = a.length; i < len; i++) {
			a[i] = r.nextInt(len);
		}

		for (int i = 0; i < a.length; i++) {
			System.out.print(a[i] + " ");
		}
		System.out.println();

		int[] b = a.clone();
		sorts[0].sort(b);

		for (int i = 0; i < b.length; i++) {
			System.out.print(b[i] + " ");
		}
		System.out.println();

		for (Sort sort : sorts) {
			int[] s = a.clone();
			sort.sort(s);
			if (!equals(s, b)) {
				System.out.println(sort.getClass() + "ERROR");
			} else {
				System.out.println(sort.getClass());
			}
		}
	}

	private static boolean equals(int[] a, int[] b) {
		for (int i = 0, len = a.length; i < len; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}
		return true;
	}

}
