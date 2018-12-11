package com.kj.repo.algorithm.sort;

/**
 * 希尔排序
 * @author bjzhangkuojian
 *
 */
public class Shell extends Sort {

	@Override
	public void sort(int[] a) {
		int length = a.length;
		int gap = maxGap(length);
		while (gap > 0) {
			for (int i = gap; i < length; i++) {
				for (int j = i - gap; j >= 0; j = j - gap) {
					if (this.compare(a[j], a[j + gap])) {
						this.swap(a, j, j + gap);
					} else {
						break;
					}
				}
			}
			gap = nextGap(gap);
		}
	}

	private int maxGap(int length) {
		int gap = 1;
		while (length / (3 * gap + 1) > 2) {
			gap = 3 * gap + 1;
		}
		return gap;
	}

	private int nextGap(int gap) {
		if (gap == 1) {
			return 0;
		} else {
			return (gap - 1) / 3;
		}
	}

	public static void main(String[] args) {
		Shell sort = new Shell();
		int[] a = new int[] { 9, 8, 7, 0, 99, 0, 4, 5, 993, 3943, 8454, 9343, 6, 7, 7, 7, 5, 55, 2, 1, 0, 999, 777,
				7777, 79734 };
		sort.sort(a);
		for (int i = 0; i < a.length; i++) {
			System.out.println(a[i]);
		}
	}
	
}
