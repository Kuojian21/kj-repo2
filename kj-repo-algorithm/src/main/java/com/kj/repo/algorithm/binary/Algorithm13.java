package com.kj.repo.algorithm.binary;

/**
 * 题目：输入一个单向链表，输出该链表中倒数第k 个结点。链表的倒数第0 个结点为链表的尾指针。
 * @author bjzhangkuojian
 *
 */
public class Algorithm13 {

	public LinkNode last(LinkNode head,int k) {
		LinkNode node = head;
		for(int i = 1;i <= k - 1;i++) {
			node = node.next;
		}
		while(node.next != null) {
			node = node.next;
			head = head.next;
		}
		return head;
	}
	
}
