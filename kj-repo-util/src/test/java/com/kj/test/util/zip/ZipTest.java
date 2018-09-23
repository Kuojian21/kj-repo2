package com.kj.test.util.zip;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;

import com.kj.repo.util.zip.KjZip;

public class ZipTest {

    public static void main(String[] args) throws IOException {
        KjZip.unzip(new File("/Users/kuojian21/Downloads/template/Brazil-6-1920x1080.zip"),
                new File("/Users/kuojian21/Downloads/template"), new KjZip.Filter() {
                    @Override
                    public boolean filter(ZipEntry entry) {
                        System.out.println(entry.getName());
                        if (entry.getName().startsWith("__MACOSX")) {
                            return false;
                        }
                        return true;
                    }
                });
    }

}
