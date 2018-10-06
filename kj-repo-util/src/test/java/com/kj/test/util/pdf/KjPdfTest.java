package com.kj.test.util.pdf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.kj.repo.util.pdf.KjPdf;

public class KjPdfTest {

	public static void main(String[] args) throws FileNotFoundException {
		KjPdf.fromHtml(new FileInputStream("/Users/kuojian21/kj/git/html/1.html"), null,
				new FileOutputStream("/Users/kuojian21/Downloads/qq空间_百度搜索.pdf"));
	}

}
