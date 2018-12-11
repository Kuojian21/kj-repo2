package com.kj.repo.algorithm.binary;

/**
 * 第11 题 求二叉树中节点的最大距离... 如果我们把二叉树看成一个图，父子节点之间的连线看成是双向的， 我们姑且定义"距离"为两节点之间边的个数。
 * 写一个程序， 求一棵二叉树中相距最远的两个节点之间的距离。
 * 
 * @author bjzhangkuojian
 *
 */
public class Algorithm11 {

	public int[] max(AvlNode<Integer> node) {
		if (node == null) {
			return new int[] { 0, 0 };
		}
		int[] l = max(node.left);
		int[] r = max(node.right);
		return new int[] { max(l[0], r[0], l[1] + r[1]), max(l[1], r[1]) + 1 };
	}

	public int max(int... x) {
		int m = x[0];
		for (int i = 1, len = x.length; i < len; i++) {
			if (x[i] > m) {
				m = x[i];
			}
		}
		return m;
	}

}
