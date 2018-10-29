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
			t.addCookie("_ga=GA1.2.1718180206.1536158386", new URL(args[0]), null);
			t.addCookie("sidebar_collapsed=false", new URL(args[0]), null);
			t.addCookie("_biz_uid=9851a66153334d0cca6223edab1f0b24", new URL(args[0]), null);
			t.addCookie("_biz_nA=25", new URL(args[0]), null);
			t.addCookie("_biz_pendingA=%5B%5D", new URL(args[0]), null);
			t.addCookie("Hm_lvt_86a27b7db2c5c0ae37fee4a8a35033ee=1538699602", new URL(args[0]), null);
			t.addCookie("_gitlab_session=eb0c241843ce922c4beab0c22ace9e1c", new URL(args[0]), null);

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
