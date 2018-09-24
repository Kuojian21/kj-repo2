package com.kj.repo.test.tool.tree;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.kj.repo.tool.tree.KjFileNode;
import com.kj.repo.tool.tree.KjTraverse;

public class KjTreeTest {

	public static AtomicLong count = new AtomicLong(0);

	public static void searchFile(File file, String name) {

		KjTraverse.getInstance().traverse(new KjFileNode(file), p -> {
			return true;
		}, f -> {
			System.out.println(count.incrementAndGet());
			if (f.getFile().getName().indexOf(name) >= 0) {
				System.out.println(f.getFile().getAbsoluteFile());
			}
			if (f.isZip()) {
				try {
					ZipFile zipFile = new ZipFile(f.getFile());
					Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
					while (zipEntries.hasMoreElements()) {
						ZipEntry zipEntry = zipEntries.nextElement();
						if (zipEntry.getName().indexOf(name) >= 0) {
							System.out.println(f.getFile().getAbsoluteFile() + "******" + zipEntry.getName());
						}
					}
					zipFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void searchText(File file, String str) {
		KjTraverse.getInstance().traverse(new KjFileNode(file), p -> {
			return true;
		}, f -> {
			if (f.isZip()) {

			} else {

			}
		});

	}

	public static void main(String[] args) throws InterruptedException {
		KjTreeTest.searchFile(new File("/Users/kuojian21/.m2"), "Register");
//		Thread.sleep(1000);
//		System.exit(0);
	}

}
