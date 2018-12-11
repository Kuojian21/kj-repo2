package com.kj.repo.algorithm.binary;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 4.在二元树中找出和为某一值的所有路径
题目：输入一个整数和一棵二元树。
从树的根结点开始往下访问一直到叶结点所经过的所有结点形成一条路径。
打印出和与输入整数相等的所有路径。
 例如输入整数 22 ，如下图二元树：
                                            10
                                           /  \
                                          5   12
                                         / \   
                                        4   7 

   则打印出两条路径：10, 12和10, 5, 7。
 * @author bjzhangkuojian
 *
 */
public class Algorithm4 {

	public void search(AvlNode<Integer> node, int sum, List<AvlNode<Integer>> path, int value) {
		sum += node.data;
		path.add(node);
		if (sum == value) {
			for (int i = 0, len = path.size(); i < len; i++) {
				System.out.print(path.get(i).data + "\t");
			}
			System.out.println();
		}
		if (node.left != null) {
			search(node.left, sum, path, value);
		}
		if (node.right != null) {
			search(node.right, sum, path, value);
		}
		path.remove(node);

	}

	public static void main(String[] args) {
		AvlNode<Integer> root = new AvlNode<Integer>(10);
		root.left = new AvlNode<Integer>(5);
		root.right = new AvlNode<Integer>(12);
		root.left.left = new AvlNode<Integer>(4);
		root.left.right = new AvlNode<Integer>(7);
		new Algorithm4().search(root, 0, Lists.newArrayList(), 22);
	}
}
