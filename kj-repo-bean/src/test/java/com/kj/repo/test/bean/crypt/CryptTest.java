package com.kj.repo.test.bean.crypt;

import java.security.Key;
import java.security.KeyPair;

import org.apache.commons.codec.binary.Base64;

import com.kj.repo.bean.crypt.KjAlgorithm;
import com.kj.repo.bean.crypt.KjCryptFactory;
import com.kj.repo.bean.crypt.KjDecrypt;
import com.kj.repo.bean.crypt.KjEncrypt;

public class CryptTest {

	public static void main(String[] args) {
		try {
			Key key = KjCryptFactory.generateKey(KjAlgorithm.DESede.getName(), KjAlgorithm.DESede.getKeysize(), null);
			System.out.println(Base64.encodeBase64String(key.getEncoded()));
			KeyPair keyPair = KjCryptFactory.generateKeyPair(KjAlgorithm.RSA_2048.getName(),
					KjAlgorithm.RSA_2048.getKeysize(), null);
			System.out.println(Base64.encodeBase64String(keyPair.getPublic().getEncoded()));
			System.out.println(Base64.encodeBase64String(keyPair.getPrivate().getEncoded()));

			KjEncrypt encrypt = KjCryptFactory.kjEncrypt("DESede/CBC/PKCS5Padding", "01234567", "DESede",
					"wnwT1v1kkhoEwnnEGSnE6ciw6S8E4w5U");
			KjDecrypt decrypt = KjCryptFactory.kjDecrypt("DESede/CBC/PKCS5Padding", "01234567", "DESede",
					"wnwT1v1kkhoEwnnEGSnE6ciw6S8E4w5U");
			for (int i = 0; i < 10; i++) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						for (int i = 0; i < 1; i++) {
							String str = encrypt.encrypt("kuojian");
							System.out.println(decrypt.decrypt(str));
						}
					}
				}).start();
			}
		} catch (Exception e) {

		}
	}
}
