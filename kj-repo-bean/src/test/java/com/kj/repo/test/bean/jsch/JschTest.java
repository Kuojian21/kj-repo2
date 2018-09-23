package com.kj.repo.test.bean.jsch;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;

import com.kj.repo.bean.jsch.KjJsch;

public class JschTest {

    public void upload() throws FileNotFoundException {
        KjJsch sch = new KjJsch("trans.kaifae.com", 2022, "LMLC", "9X9XWa$f");

        if (sch.upload("/upload/test", "test.txt", new ByteArrayInputStream(new String("Hello World!").getBytes()))) {
            for (int i = 0; i < 10; i++) {
                sch.download("/upload/test", "test.txt", new FileOutputStream(new File("test.txt")));
            }
            System.out.println(new File("test.txt").getAbsolutePath());
        } else {
            return;
        }

    }

    public void upload2() throws UnsupportedEncodingException {
        KjJsch sch = new KjJsch("123.57.157.2", 22, "lmlctest", "SPLIT_classes:classes/exchange_njjjs.pub",
                "SPLIT_classes:classes/exchange_njjjs.ppk", "njjjs".getBytes("UTF-8"));
        try {
            sch.upload("upload", "test.txt", new ByteArrayInputStream(new String("Hello World!123").getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        KjJsch sch = new KjJsch("trans.kaifae.com", 2022, "LMLC", "9X9XWa$f");
        for (int i = 0; i < 10; i++) {
            int x = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            if (sch.upload("/upload/test" + x, "test.txt",
                                    new ByteArrayInputStream(new String("Hello World!").getBytes()))) {
                                for (int i = 0; i < 10; i++) {
                                    sch.download("/upload/test" + x, "test.txt",
                                            new FileOutputStream(new File("test" + x + ".txt")));
                                }
                                System.out.println(x);
                                Thread.sleep(1000);
                            } else {
                                return;
                            }
                        }
                    } catch (FileNotFoundException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }).start();
        }
    }

}
