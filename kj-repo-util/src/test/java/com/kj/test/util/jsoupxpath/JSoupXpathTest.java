package com.kj.test.util.jsoupxpath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.seimicrawler.xpath.JXNode;
import org.seimicrawler.xpath.exception.XpathSyntaxErrorException;

import com.google.common.collect.Sets;
import com.kj.repo.util.jsoup.KjSoup;

public class JSoupXpathTest {

	public static void git() throws FileNotFoundException, XpathSyntaxErrorException, IOException {
		String xpath = "//a[@class='project']/@href";
		// String xpath = "//li[@class='project-row']/div/a/@href";

		Set<String> sets = Sets.newHashSet();
		for (File file : new File("/Users/kuojian21/kj/git/html").listFiles()) {
			List<Object> objs = KjSoup.xpath(new FileReader(file)).sel(xpath);
//			KjSoup.jsoup(new FileReader(file)).getElementById(id)
			for (Object obj : objs) {
				sets.add(obj.toString());
			}
		}
		for (String s : sets.stream().sorted().collect(Collectors.toList())) {
			System.out.println(s.substring(1));
		}
	}

	public static void m() throws FileNotFoundException, XpathSyntaxErrorException, IOException {
		String xpath = "//tr";
		// String xpath = "//li[@class='project-row']/div/a/@href";

		Set<String> sets = Sets.newHashSet();
		File file = new File("/Users/kuojian21/kj/m.list");
		List<JXNode> objs = KjSoup.xpath(new FileReader(file)).selN(xpath);
		for (JXNode obj : objs) {
			List<JXNode> o = obj.sel("//td");
			if (o.isEmpty()) {
				continue;
			}
			sets.add(o.get(0).asElement().textNodes().get(0).text().trim() + "/"
					+ o.get(1).asElement().textNodes().get(0).text().trim());
		}
		for (String s : sets.stream().sorted().collect(Collectors.toList())) {
			System.out.println(s);
		}
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, XpathSyntaxErrorException {
		m();
	}

}
