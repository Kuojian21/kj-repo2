package com.kj.repo.test.bean.crypt;

import com.kj.repo.bean.crypt.KjCryptFactory;
import com.kj.repo.bean.crypt.KjDecrypt;
import com.kj.repo.bean.crypt.KjEncrypt;

public class CryptTest {

    public static void main(String[] args) {
        try {
            KjCryptFactory factory = KjCryptFactory.factory("DESede/CBC/PKCS5Padding", "01234567", "DESede",
                    "wnwT1v1kkhoEwnnEGSnE6ciw6S8E4w5U");

            KjEncrypt encrypt = factory.kjEncrypt();
            KjDecrypt decrypt = factory.kjDecrypt();
            for (int i = 0; i < 10; i++) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
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
