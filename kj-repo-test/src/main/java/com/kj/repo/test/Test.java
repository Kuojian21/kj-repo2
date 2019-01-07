package com.kj.repo.test;

public class Test {

	public static void main(String[] args) {
		String[] strs = ("45828196\n" + "45828282\n" + "45828312\n" + "45828337\n" + "45828370\n" + "45828397\n"
				+ "45828424\n" + "45828446\n" + "45828509\n" + "45828528\n" + "45828582\n" + "45828616\n" + "45828637\n"
				+ "45828686\n" + "45828719\n" + "45828737\n" + "45828750\n" + "45828771").split("\n");
		for (String str : strs) {
			System.out.println(Long.parseLong(str));
		}
		System.out.println("45828719 ");
	}

}
