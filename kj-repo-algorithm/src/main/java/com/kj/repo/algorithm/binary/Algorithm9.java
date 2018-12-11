package com.kj.repo.algorithm.binary;

/**
 * 第9 题 判断整数序列是不是二元查找树的后序遍历结果 题目：输入一个整数数组，判断该数组是不是某二元查找树的后序遍历的结果。
 * 如果是返回true，否则返回false。 例如输入5、7、6、9、11、10、8，由于这一整数序列是如下树的后序遍历结果： 8/ \ 6 10 / \ /
 * \ 5 7 9 11 因此返回true。 如果输入7、4、6、5，没有哪棵树的后序遍历的结果是这个序列，因此返回false。
 * 
 * @author bjzhangkuojian
 *
 */
public class Algorithm9 {

	public boolean judge(int a[], int s, int e) {
		if (s == e) {
			return true;
		}
		int i = e - 1;
		while (i >= s && a[e] < a[i]) {
			i--;
		}
		if (!judge(a, i + 1, e - 1)) {
			return false;
		}
		
		if(i + 1 == s) {
			return true;
		}
		
		int k = i;
		while (i >= s && a[e] > a[i]) {
			i--;
		}
		if (i >= s) {
			return false;
		}
		return judge(a, s, k);
	}

}
