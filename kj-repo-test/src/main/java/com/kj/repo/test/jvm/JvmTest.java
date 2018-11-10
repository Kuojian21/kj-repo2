package com.kj.repo.test.jvm;

public class JvmTest {

	private static final int _1MB = 1024 * 1024;

	/**
	 * VM参数：-Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
	 * -XX:-HandlePromotionFailure -XX:PretenureSizeThreshold=3145728
	 * -XX:+UseSerialGC -XX:+PrintTenuringDistribution
	 */
	@SuppressWarnings("unused")
	public static void testHandlePromotion() {
		byte[] allocation1, allocation2, allocation3, allocation4, allocation5, allocation6, allocation7, allocation8;
		allocation1 = new byte[2 * _1MB];
		allocation2 = new byte[2 * _1MB];
		allocation3 = new byte[2 * _1MB];
		allocation1 = new byte[_1MB / 4];
		allocation4 = new byte[2 * _1MB];
		allocation5 = new byte[2 * _1MB];
		allocation6 = new byte[2 * _1MB];
		allocation4 = null;
		allocation5 = null;
		allocation6 = null;
		allocation2 = null;
		allocation3 = null;
		allocation7 = new byte[4 * _1MB];
		allocation7 = null;
		allocation7 = new byte[4 * _1MB];
		allocation7 = new byte[4 * _1MB];
		System.out.println();
	}

	public static void main(String[] args) {
		testHandlePromotion();
	}

}
