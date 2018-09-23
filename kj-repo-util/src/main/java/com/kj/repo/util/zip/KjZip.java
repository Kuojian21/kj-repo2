package com.kj.repo.util.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class KjZip {

    public static interface Filter {
        boolean filter(ZipEntry entry);
    }

    public static void unzip(File src, File tar, Filter filter) throws IOException {
        if (!tar.exists()) {
            tar.mkdirs();
        }
        if (!src.exists() || !tar.isDirectory()) {
            return;
        }
		
        ZipFile file = null;
        try {
            file = new ZipFile(src);
            Enumeration<? extends ZipEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!filter.filter(entry)) {
                    continue;
                }
                File entryFile = new File(tar.getAbsoluteFile() + File.separator + entry.getName());
                if (entry.isDirectory()) {
                    entryFile.mkdirs();
                    continue;
                }
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(entryFile));
                BufferedInputStream bis = new BufferedInputStream(file.getInputStream(entry));
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                bis.close();
                bos.flush();
                bos.close();
            }
        } finally {
            if (file != null) {
                file.close();
            }
        }
    }

}
