package com.kj.repo.algorithm.binary;

public class Algorithm8 {
	/**
	 * 3. ★用一种算法来颠倒一个链接表的顺序。现在在不用递归式的情况下做一遍。
	 * 
	 * @param root
	 * @return
	 */
	public LinkNode reverse(LinkNode root) {
		if (root == null || root.next == null) {
			return root;
		}
		LinkNode head = reverse(root.next);
		root.next.next = root;
		root.next = null;
		return head;
	}

	public LinkNode reverse2(LinkNode root) {
		LinkNode head = null;
		LinkNode node = null;
		while (root != null) {
			node = root.next;
			root.next = head;
			head = root;
			root = node;
		}
		return head;
	}

	/**
	 * 用一种算法使通用字符串相匹配。
	 * 
	 * @param str
	 * @param si
	 * @param com
	 * @param ci
	 * @return
	 */
	public boolean match(char[] str, int si, char[] com, int ci) {
		if (ci >= com.length && si >= str.length) {
			return true;
		}
		if (ci >= com.length) {
			return false;
		}
		if (com[ci] == '*') {
			do {
				if (match(str, si++, com, ci + 1)) {
					return true;
				}
			} while (si < str.length);
			return false;
		} else if (com[ci] == '?' || com[ci] == str[si]) {
			return match(str, si + 1, com, ci + 1);
		} else {
			return false;
		}
	}

	/**
	 * 颠倒一个字符串。优化速度。优化空间。
	 * 
	 * @param str
	 */
	public void reverse(char[] str, int s, int e) {
		while (s < e) {
			char c = str[s];
			str[s] = str[e];
			str[e] = c;
			s++;
			e--;
		}
	}

	/**
	 * 颠倒一个句子中的词的顺序，比如将“我叫克丽丝”转换为“克丽丝叫我”， 实现速度最快，移动最少。
	 * 
	 * @param str
	 */
	public void reverseWord(char[] str) {
		reverse(str, 0, str.length - 1);
		int s = 0;
		for (int i = 0; i < str.length; i++) {
			if (str[i] == ' ') {
				if (s < i - 1) {
					reverse(str, s, i - 1);
				}
				s = i + 1;
			}
		}
	}

	/**
	 * ★找到一个子字符串。优化速度。优化空间。
	 * 
	 * @param str
	 * @param s
	 * @return
	 */
	public int instr(char[] str, char[] s) {
		for (int i = 0, ilen = str.length; i < ilen; i++) {
			int j = 0, jlen = s.length;
			if (i + jlen >= ilen) {
				return -1;
			}
			for (; j < jlen; j++) {
				if (str[i + j] != s[j]) {
					break;
				}
			}
			if (j >= jlen) {
				return i;
			}
		}
		return -1;
	}

	public int instr2(char[] str, char[] s) {
		int hash = 0;
		for (int i = 0, len = s.length; i < len; i++) {
			hash += s[i] << (i * 8);
		}

		int t = 0;
		for (int i = 0, len = str.length; i < len; i++) {
			if (i < s.length) {
				t += str[i] << (i * 8);
			} else {
				t = t >> 8 + str[i] << (s.length - 1) * 8;
			}
			if (t == hash) {
				return i - len;
			}
		}
		return -1;
	}

	/**
	 * 比较两个字符串，用O(n)时间和恒量空间。
	 * 
	 * @param s
	 * @param t
	 * @return
	 */
	public int strcmp(char[] s, char[] t) {
		int slen = s.length, tlen = t.length;
		int i = 0;
		while (i < slen && i < tlen && s[i] == t[i]) {
			i++;
		}
		if (i >= slen && i >= tlen) {
			return 0;
		} else if (i >= slen) {
			return -t[i];
		} else if (i >= tlen) {
			return s[i];
		} else {
			return s[i] - t[i];
		}
	}

	/**
	 * ★假设你有一个用1001 个整数组成的数组，这些整数是任意排列的，但是你知道所有的整数都在1 到
	 * 1000(包括1000)之间。此外，除一个数字出现两次外，其他所有数字只出现一次。假设你只能对这个数组做
	 * 一次处理，用一种算法找出重复的那个数字。如果你在运算中使用了辅助的存储方式，那么你能找到不用这种 方式的算法吗?
	 * 
	 * @param a
	 * @return
	 */
	public int result(int[] a) {
		int k = a[0];
		for (int i = 1, len = a.length; i < len; i++) {
			k ^= a[i] ^ i;
		}
		return k;
	}

}
