package com.kj.repo.algorithm.binary;

/**
 * 把查找二叉树转化为双向链表，不能新建节点
 * @author ThinkPad
 *
 */
public class Transfer<T> {


	public void transfer(AvlNode<T> node) {
		if (node.left != null) {
			transfer(node.left);
			setLeft(node, node.left);
		}
		if (node.right != null) {
			transfer(node.right);
			setRight(node, node.right);
		}
	}

	public void setLeft(AvlNode<T> node, AvlNode<T> left) {
		if (left.right != null) {
			setLeft(node, left.right);
		} else {
			node.left = left;
			left.right = node;
		}
	}

	public void setRight(AvlNode<T> node, AvlNode<T> right) {
		if (right.left != null) {
			setRight(node, right.left);
		} else {
			node.right = right;
			right.left = node;
		}
	}

}
