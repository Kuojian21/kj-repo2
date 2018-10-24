package com.kj.repo.util.reader;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import com.google.common.collect.Lists;
import com.kj.repo.util.resource.KjResource;

public class KjReader {

	public static List<String> readLines(InputStream is) throws IOException {
		List<String> result = Lists.newLinkedList();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String line = null;
			while ((line = br.readLine()) != null) {
				result.add(line);
			}
			return result;
		} finally {
			KjResource.close(is);
		}
	}

	public static byte[] readBytes(InputStream is) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[1024];
			while (true) {
				int rtn = is.read(buffer);
				if (rtn == -1) {
					break;
				}
				os.write(buffer, 0, rtn);
			}
			return os.toByteArray();
		} finally {
			KjResource.close(is, os);
		}
	}

	public static List<String> readLines(String file) {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			return readLines(is);
		} catch (IOException e) {
			e.printStackTrace();
			return Lists.newLinkedList();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
