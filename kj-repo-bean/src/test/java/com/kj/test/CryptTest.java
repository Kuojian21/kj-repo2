package com.kj.test;

import java.security.Key;
import java.security.KeyPair;
import java.util.Base64;

import javax.crypto.Cipher;
import com.kj.bean.KjCrypt;

public class CryptTest {

	public static void main(String[] args) {
		try {
			Key key = KjCrypt.Factory.generateKey(KjCrypt.Algorithm.Crypt.DESede.getName(),
							KjCrypt.Algorithm.Crypt.DESede.getKeysize(),
							null);
			System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));
			KeyPair keyPair = KjCrypt.Factory.generateKeyPair(
							KjCrypt.Algorithm.Crypt.RSA_2048.getName(),
							KjCrypt.Algorithm.Crypt.RSA_2048.getKeysize(), null);
			System.out.println(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
			System.out.println(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));

			KjCrypt<Cipher, byte[]> encrypt = KjCrypt.KjCipher.encrypt("DESede/CBC/PKCS5Padding",
							KjCrypt.Factory.loadKey("DESede",
											Base64.getDecoder().decode("L/gx33BYzjQLj12FhlSXlEosuVSwKl1M")),
							null);
			KjCrypt<Cipher, byte[]> decrypt = KjCrypt.KjCipher.decrypt("DESede/CBC/PKCS5Padding",
							KjCrypt.Factory.loadKey("DESede",
											Base64.getDecoder().decode("L/gx33BYzjQLj12FhlSXlEosuVSwKl1M")),
							null);

			for (int i = 0; i < 10; i++) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							for (int i = 0; i < 1; i++) {
								System.out.println(new String(decrypt.crypt(encrypt.crypt("kj".getBytes()))));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		} catch (Exception e) {

		}
	}
}
