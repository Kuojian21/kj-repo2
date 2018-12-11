package com.kj.repo.algorithm.binary;

import java.util.Random;

public class AvlTree<T> {

	public AvlNode<T> insert(AvlNode<T> node, T data) {
		if (node == null) {
			node = new AvlNode<T>(data);
		} else if (this.compareTo(node.data, data) > 0) {
			node.left = this.insert(node.left, data);
			if (this.height(node.left) - this.height(node.right) >= 2) {
				if (this.height(node.left.left) > this.height(node.left.right)) {
					node = ll(node);
				} else {
					node = lr(node);
				}
			} else {
				node.height = Math.max(this.height(node.left), this.height(node.right)) + 1;
			}
		} else if (this.compareTo(node.data, data) < 0) {
			node.right = this.insert(node.right, data);
			if (this.height(node.right) - this.height(node.left) >= 2) {
				if (this.height(node.right.right) > this.height(node.right.left)) {
					node = rr(node);
				} else {
					node = rl(node);
				}
			} else {
				node.height = Math.max(this.height(node.left), this.height(node.right)) + 1;
			}
		}
		return node;
	}

	public void delete(AvlNode<T> node, T data) {

	}

	protected int compareTo(T t1, T t2) {
		if (t1.getClass().isAssignableFrom(Integer.class)) {
			return (Integer) t1 - (Integer) t2;
		}
		return -1;
	}

	private int height(AvlNode<T> node) {
		if (node == null) {
			return -1;
		}

		return Math.max(this.height(node.left), this.height(node.right)) + 1;
		// return node.height;
	}

	private AvlNode<T> ll(AvlNode<T> node) {
		AvlNode<T> t = node;
		node = node.left;
		t.left = node.right;
		node.right = t;
		t.height -= 1;
		return node;

	}

	private AvlNode<T> rr(AvlNode<T> node) {
		AvlNode<T> t = node;
		node = node.right;
		t.right = node.left;
		node.left = t;
		t.height -= 1;
		return node;

	}

	private AvlNode<T> lr(AvlNode<T> node) {
//		System.out.println("lr");
		node.left = rr(node.left);
		node.left.height += 1;
		return ll(node);

	}

	private AvlNode<T> rl(AvlNode<T> node) {
//		System.out.println("rl");
		node.right = ll(node.right);
		node.right.height += 1;
		return rr(node);

	}

	public void traverse(AvlNode<T> node) {
		if (node.left != null) {
			System.out.print(node.left.data + "\t");
		}

		if (node.right != null) {
			System.out.print(node.right.data + "\t");
		}
		
		System.out.println();
		
		if (node.left != null) {
			this.traverse(node.left);
		}

		if (node.right != null) {
			this.traverse(node.right);
		}

	}

	public static void main(String[] args) {
		AvlTree<Integer> tree = new AvlTree<Integer>();
		AvlNode<Integer> root = null;
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			root = tree.insert(root, random.nextInt(100));
		}
//		System.out.println(root);

//		tree.traverse(root);
		new Transfer<Integer>().transfer(root);

		AvlNode<Integer> node = root;
		while (node.left != null) {
			node = node.left;
		}

		do {
			System.out.print(node.data + "\t\n");
			node = node.right;
		} while (node != null);

	}
}
