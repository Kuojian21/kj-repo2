package com.kj.repo.algorithm.binary;

import java.util.Random;

/**
 * 1.把二元查找树转变成排序的双向链表
 * 题目：
 * 输入一棵二元查找树，将该转换成个排序的双向链表。要求不能创建任何新的结点，只调整指针向。
 * @param node
 * @return
 */
public class Algorithm {
	
	public <T> AvlNode<T> left(AvlNode<T> node) {
		if(node.left != null) {
			node.left = left(node.left);
			node.left.right = node;
		}
		if(node.right != null) {
			node.right = right(node.right);
			node.right.left = node;
		}
		while(node.right != null) {
			node = node.right;
		}
		return node;
	}
	
	public <T> AvlNode<T> right(AvlNode<T> node) {
		if(node.left != null) {
			node.left = left(node.left);
			node.left.right = node;
		}
		if(node.right != null) {
			node.right = right(node.right);
			node.right.left = node;
		}
		while(node.left != null) {
			node = node.left;
		}
		return node;
	}
	
	public <T> void transfer(AvlNode<T> node) {
		if (node.left != null) {
			transfer(node.left);
			AvlNode<T> t = node.left;
			while(t.right != null) {
				t = t.right;
			}
			node.left = t;
			t.right = node;
		}
		if (node.right != null) {
			transfer(node.right);
			AvlNode<T> t = node.right;
			while(t.left != null) {
				t = t.left;
			}
			node.right = t;
			t.left = node;
		}
	}
	
	public static void main(String[] args) {
		AvlTree<Integer> tree = new AvlTree<Integer>();
		AvlNode<Integer> root = null;
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			root = tree.insert(root, random.nextInt(100));
		}
		AvlNode<Integer> left = new Algorithm().right(root);
		do {
			System.out.print(left.data + "\t\n");
			left = left.right;
		} while (left != null);
		
		tree = new AvlTree<Integer>();
		root = null;
		for (int i = 0; i < 100; i++) {
			root = tree.insert(root, random.nextInt(100));
		}
		new Algorithm().transfer(root);
		AvlNode<Integer> t = root;
		do {
			System.out.print(t.data + "\t\n");
			t = t.right;
		} while (t != null);
		
	}
	
}
