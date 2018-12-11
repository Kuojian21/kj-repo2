package com.kj.repo.algorithm.binary;

import java.util.Stack;

/**
 * 第15 题：
题目：输入一颗二元查找树，将该树转换为它的镜像，
即在转换后的二元查找树中，左子树的结点都大于右子树的结点。
用递归和循环两种方法完成树的镜像转换。
例如输入：
8/\
6 10
/\ /\
5 7 9 11
输出：
8/
\
10 6
/\ /\
11 9 7 5
 * @author bjzhangkuojian
 *
 */
public class Algorithm15 {

	public void change(AvlNode<Integer> root) {
		if(root == null) {
			return;
		}
		change(root.left);
		change(root.right);
		this.swap(root);
	}
	
	public void change2(AvlNode<Integer> root) {
		if(root == null) {
			return;
		}
		Stack<AvlNode<Integer>> nodes = new Stack<AvlNode<Integer>>();
		nodes.push(root);
		while(!nodes.isEmpty()) {
			AvlNode<Integer> node = nodes.pop();
			if(node.left != null) {
				nodes.push(node.left);
			}
			if(node.right != null) {
				nodes.push(node.right);
			}
			this.swap(node);
		}
	}
	
	public void swap(AvlNode<Integer> node) {
		if(node == null) {
			return;
		}
		AvlNode<Integer> left = node.left;
		node.left = node.right;
		node.right = left;
	}
	
	
}
