package com.kj.repo.algorithm.binary;
/**
 * 第 7 题（链表）
微软亚院之编程判断俩个链表是否相交
给出俩个单向链表的头指针，比如 h1，h2，判断这俩个链表是否相交。
为了简化问题，我们假设俩个链表均不带环。
问题扩展：
1.如果链表可能有环列?
2.如果需要求出俩个链表相交的第一个节点列?
 * @author ThinkPad
 *
 */
public class Algorithm7 {

	public boolean isExistLoop(LinkNode root) {
		LinkNode n1 = root;
		LinkNode n2 = root.next;
		while(n2 != null && n2.next != null) {
			if(n1 == n2) {
				return true;
			}
			n1 = n1.next;
			n2 = n2.next.next;
		}
		return false;
	}
	
	public LinkNode loopNode(LinkNode root) {
		LinkNode n1 = root;
		LinkNode n2 = root.next;
		while(n2 != null && n2.next != null) {
			if(n1 == n2) {
				n2 = root;
				while(n1 != n2) {
					n1 = n1.next;
					n2 = n2.next;
				}
				return n1;
			}
			n1 = n1.next;
			n2 = n2.next.next;
		}
		return null;
	}
	
	public void isCross(LinkNode root1,LinkNode root2) {
		LinkNode n1 = root1,n2 = root2;
		int l1 = 1,l2 = 2;
		while(n1.next != null) {
			n1 = n1.next;
			l1++;
		}
		while(n2.next != null) {
			n2 = n2.next;
			l2++;
		}
		n1 = root1;
		n2 = root2;
		if(l1 > l2) {
			for(int i = 0,len = l1-l2;i < len;i++) {
				n1 = n1.next;
			}
		}else {
			for(int i = 0,len = l2-l1;i < len;i++) {
				n2 = n2.next;
			}
		}
		while(n1 != n2) {
			n1 = n1.next;
			n2 = n2.next;
		}
	}
	
}
