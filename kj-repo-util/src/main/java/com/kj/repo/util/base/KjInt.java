package com.kj.repo.util.base;

public class KjInt {

	public static int toInt(byte[] data, int f, int t) {
		int rtn = 0;
		for (int i = t, j = 0; i >= f; i--, j++) {
			rtn |= data[i] << j * 8;
		}
		return rtn;
	}

	public static byte[] toBytes(int i) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (i & 0xff);
		bytes[1] = (byte) ((i >> 8) & 0xff);
		bytes[2] = (byte) ((i >> 16) & 0xff);
		bytes[3] = (byte) ((i >> 24) & 0xff);
		return bytes;
	}

}
