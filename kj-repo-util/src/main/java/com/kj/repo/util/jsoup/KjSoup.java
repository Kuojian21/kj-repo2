package com.kj.repo.util.jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.seimicrawler.xpath.JXDocument;

public class KjSoup {

	public static JXDocument xpath(String html) {
		JXDocument doc = JXDocument.create(html);
		return doc;
	}

	public static JXDocument xpath(Reader reader) throws IOException {
		try {
			BufferedReader br = new BufferedReader(reader);
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}
			return xpath(builder.toString());
		} catch (Exception e) {
			throw e;
		} finally {
			reader.close();
		}
	}

	public static Document jsoup(Reader reader) throws IOException {
		try {
			BufferedReader br = new BufferedReader(reader);
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}
			return jsoup(builder.toString());
		} catch (Exception e) {
			throw e;
		} finally {
			reader.close();
		}
	}

	public static Document jsoup(String html) {
		return Jsoup.parse(html);
	}

}