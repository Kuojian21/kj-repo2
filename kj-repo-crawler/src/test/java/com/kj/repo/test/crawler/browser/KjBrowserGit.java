package com.kj.repo.test.crawler.browser;

import java.net.URL;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.collect.Lists;
import com.kj.repo.browser.KjBrowser;

public class KjBrowserGit {

	public static KjBrowser kjBrowser = new KjBrowser(BrowserVersion.CHROME);

	public static void main(String[] args) throws Exception {
		// System.setProperty("socksProxyHost", "127.0.0.1");
		// System.setProperty("socksProxyPort", "8088");
		kjBrowser.execute(t -> {
			t.addCookie("_ga=GA1.2.1465489996.1545289282", new URL(args[0]), null);
			t.addCookie("_gid=GA1.2.80586127.1546051065", new URL(args[0]), null);
			t.addCookie("_gitlab_session=d9e0fd41d26db5e581ab3ed4cbc18db6", new URL(args[0]), null);
			t.addCookie("ticket=ST-42393-wFjdSSIbAjrLPrpY5gHOmq-e9q0-kssso", new URL(args[0]), null);
			
			t.addCookie("sidebar_collapsed=false", new URL(args[0]), null);
			t.addCookie("_biz_nA=2", new URL(args[0]), null);
			t.addCookie("_biz_pendingA=%5B%5D", new URL(args[0]), null);
			HtmlPage page = t.getPage(args[0]);
			System.out.println(page.asXml());
			List<String> all = parse(page);
			do {
				List<HtmlAnchor> anchors = page.getByXPath("//li[@class='next']/a");
				if (anchors.isEmpty()) {
					System.out.println(all.size());
					all.stream().sorted().forEach(System.out::println);
					break;
				}
				page = anchors.get(0).click();
				all.addAll(parse(page));
			} while (true);
		});
	}

	public static List<String> parse(HtmlPage page) {
		List<String> result = Lists.newArrayList();
		List<DomAttr> attrs = page.getByXPath("//a[@class='project']/@href");
		for (DomAttr attr : attrs) {
			result.add(attr.getValue().substring(1));
		}
		return result;
	}

}
