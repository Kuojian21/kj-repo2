package com.kj.repo.algorithm.binary;


public class AvlNode<T> {
	public AvlNode<T> left;
	public AvlNode<T> right;
	public T data;
	public int height;
	
	public AvlNode(T data) {
		super();
		this.data = data;
	}
}
