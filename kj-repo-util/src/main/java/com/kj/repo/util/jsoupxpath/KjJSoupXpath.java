package com.kj.repo.util.jsoupxpath;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.seimicrawler.xpath.JXDocument;

public class KjJSoupXpath {

	public static JXDocument parse(String html) {
		return JXDocument.create(html);
	}

	public static JXDocument parse(Reader reader) throws IOException {
		try {
			BufferedReader br = new BufferedReader(reader);
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}
			return parse(builder.toString());
		} catch (Exception e) {
			throw e;
		} finally {
			reader.close();
		}
	}
}