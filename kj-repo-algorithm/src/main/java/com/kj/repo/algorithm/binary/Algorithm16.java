package com.kj.repo.algorithm.binary;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 输入一颗二元树，从上往下按层打印树的每个结点，同一层中按照从左往右的顺序打印。
 * @author bjzhangkuojian
 *
 */
public class Algorithm16 {
	
	public void print(AvlNode<Integer> node) {
		AvlNode<Integer> empty = new AvlNode<Integer>(1);
		Queue<AvlNode<Integer>> queue = new LinkedList<AvlNode<Integer>>();
		queue.add(node);
		queue.add(empty);
		while(!queue.isEmpty()){
			AvlNode<Integer> n = queue.remove();
			if(n == empty) {
				System.out.println();
				if(!queue.isEmpty()) {
					queue.add(empty);
				}
			}else {
				System.out.print(n.data + "\t");
				if(n.left != null) {
					queue.add(n.left);
				}
				if(n.right != null) {
					queue.add(n.right);
				}
			}
		}
	}
	
}
