package org.kj.infra.test.pool;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.kj.infra.pool.KjBrowser;
import com.kj.infra.pool.KjBrowser.Helper;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;

public class BrowserTest {

	public static KjBrowser<WebClient> kjBrowser = KjBrowser.browser(BrowserVersion.CHROME);

	public static void git(String[] args) throws Exception {
		URL domain = new URL(args[0]);
		kjBrowser.execute(t -> {
			try {
				t.addCookie("_did=web_966972270286159", domain, null);
				t.addCookie("_gitlab_session=d44e489f50cd9710f4a014130ca79f17", domain, null);
				t.addCookie("adm_tk=UuurUbV49MebcJCjA15Fif8OVICxxw0u-983795515", domain, null);
				t.addCookie("kwai_adm_tk=UuurUbV49MebcJCjA15Fif8OVICxxw0u-983795515", domain, null);
				t.addCookie("sidebar_collapsed=false", domain, null);
				HtmlPage page = t.getPage(args[0]);
				List<String> all = KjBrowser.Helper.parseURL(page, "//a[@class='project']/@href");
				do {
					List<HtmlAnchor> anchors = page.getByXPath("//li[@class='next']/a");
					if (anchors.isEmpty()) {
						System.out.println(all.size());
						all.stream().sorted().forEach(System.out::println);
						break;
					}
					page = anchors.get(0).click();
					all.addAll(KjBrowser.Helper.parseURL(page, "//a[@class='project']/@href"));
				} while (true);
			} catch (Exception e) {
//				e.printStackTrace();
			}
		});
	}

	public static void gatherproxy() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(32);
		executor.submit(() -> {
			kjBrowser.execute(w -> {
				try {
					HtmlPage page = w.getPage("http://www.gatherproxy.com/sockslist/sockslistbycountry");
					List<String> urls = KjBrowser.Helper.parseURL(page, "//ul[@class='pc-list']/li/a/@href");
					urls.stream().forEach(url -> {
						executor.submit(() -> {

						});
					});
				} catch (FailingHttpStatusCodeException | IOException e) {
					e.printStackTrace();
				}
			});
		});
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("socksProxyHost", "127.0.0.1");
		System.setProperty("socksProxyPort", "8088");
		git(args);
	}

}
