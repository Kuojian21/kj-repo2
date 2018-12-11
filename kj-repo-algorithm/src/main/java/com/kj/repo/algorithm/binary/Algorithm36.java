package com.kj.repo.algorithm.binary;

/**
 * 谷歌笔试： n 支队伍比赛，分别编号为0，1，2。。。。n-1，已知它们之间的实力对比关系， 存储在一个二维数组w[n][n]中，w[i][j]
 * 的值代表编号为i，j 的队伍中更强的一支。 所以w[i][j]=i 或者j，现在给出它们的出场顺序，并存储在数组order[n]中， 比如order[n]
 * = {4,3,5,8,1......}，那么第一轮比赛就是4 对3， 5 对8。.......
 * 胜者晋级，败者淘汰，同一轮淘汰的所有队伍排名不再细分，即可以随便排， 下一轮由上一轮的胜者按照顺序，再依次两两比，比如可能是4 对5,直至出现第一名
 * 编程实现，给出二维数组w，一维数组order 和用于输出比赛名次的数组result[n]， 求出result。
 * 
 * @author ThinkPad
 *
 */
public class Algorithm36 {

	public void rank(int[][] w, int[] order) {
		int n = order.length - 1;
		while (n > 0) {
			int index = 0;
			for (int i = 0; i <= n; i += 2) {
				if(i + 1 <= n) {
					if(w[order[i]][order[i+1]] == order[i]) {
						this.swap(order, index, i);
					}else {
						this.swap(order, index, i + 1);
					}
				}else {
					this.swap(order, index, i);
				}
				index++;
			}
			n = index - 1;
		}
		

	}

	public void swap(int[] a,int i,int j) {
		if(i == j) {
			return;
		}
		a[i] = a[i] ^ a[j];
		a[j] = a[i] ^ a[j];
		a[i] = a[i] ^ a[j];
	}
	
	public static void main(String[] args) {
		
		int[][] w = {{0,1,2,3,4},{1,1,2,3,4},{2,2,2,3,4},{3,3,3,3,4},{4,4,4,4,4}};  
        int[] order = {4,3,1,2,0}; 
		
        new Algorithm36().rank(w, order);
        
        for(int i : order) {
        	System.out.print(i + "\t");
        }
        
		
	}
	
}
