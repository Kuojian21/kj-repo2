package org.kj.infra.test.pool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kj.repo.browser.KjBrowser;
import com.kj.repo.util.executor.KjExecutor;

public class KjBrowserProxy {

	public static KjBrowser kjBrowser = new KjBrowser(BrowserVersion.FIREFOX_52);

	public static void main(String[] args) throws Exception {
		// System.setProperty("http.proxySet", "true");
		// System.setProperty("http.proxyHost", "80.252.130.111");
		// System.setProperty("http.proxyPort", "38555");
		//
		// System.setProperty("https.proxyHost", "");
		// System.setProperty("https.proxyPort", "");
		System.setProperty("socksProxyHost", "127.0.0.1");
		System.setProperty("socksProxyPort", "8088");
		ExecutorService service = KjExecutor.newExecutorService();
		Map<String, Future<Void>> futures = Maps.newHashMap();
		// futures.put("freeproxylists", service.submit(() -> {
		// handle("freeproxylists", "http://www.freeproxylists.net/zh/",
		// "//div[@class='page']/a",
		// "//table[@class='DataGrid']");
		// return null;
		// }));
		// futures.put("free-proxy", service.submit(() -> {
		// handle("free-proxy", "http://free-proxy.cz/en/",
		// "//div[@class='paginator']/a",
		// "//table[@id='proxy_list']");
		// return null;
		// }));
		List<String> urls = getURL("http://www.gatherproxy.com/sockslist/sockslistbycountry",
				"//ul[@class='pc-list']/li/a/@href");
		for (String url : urls) {
			String suffix = url.substring(url.indexOf("=") + 1);
			System.out.println(url);
			futures.put("gatherproxy-socket-" + suffix, service.submit(() -> {
				handle("gatherproxy-socket-" + suffix, "http://www.gatherproxy.com" + url, "",
						"//table[@id='tblproxy']");
				return null;
			}));
		}

		for (Map.Entry<String, Future<Void>> entry : futures.entrySet()) {
			try {
				entry.getValue().get();
			} catch (Exception e) {
				System.out.println("service exception" + entry.getKey());
				e.printStackTrace();
			}
		}
	}

	
	
	public static List<String> getURL(String url, String uxPath) throws Exception {
		List<String> result = Lists.newArrayList();
		kjBrowser.execute(t -> {
			HtmlPage page = t.getPage(url);
			List<DomAttr> attrs = page.getByXPath(uxPath);
			for (DomAttr attr : attrs) {
				result.add(attr.getValue());
			}
		});
		return result;
	}

	public static void handle(String name, String url, String axPath, String txPath) throws Exception {
		Stopwatch stopwatch = Stopwatch.createStarted();
		kjBrowser.execute(t -> {
			File file = new File(System.getProperty("user.home") + File.separator + "kj" + File.separator + "crawler"
					+ File.separator + name + ".list");
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			HtmlPage page = t.getPage(url);
			parseTable(writer, page, txPath);
			if (!Strings.isNullOrEmpty(axPath)) {
				List<HtmlAnchor> anchors = page.getByXPath(axPath);
				for (HtmlAnchor anchor : anchors) {
					writer.println(anchor.asText());
					parseTable(writer, (HtmlPage) anchor.click(), txPath);
				}
			}
			writer.close();
		});
		System.out.println("elapsed time:" + stopwatch.elapsed(TimeUnit.MILLISECONDS));
	}

	public static void gatherproxy() throws Exception {
		ExecutorService service = KjExecutor.newExecutorService();
		kjBrowser.execute(t -> {
			Map<String, Future<Void>> futures = Maps.newHashMap();
			HtmlPage page = t.getPage("http://www.gatherproxy.com/sockslist/sockslistbycountry");
			List<DomAttr> attrs = page.getByXPath("//ul[@class='pc-list']/li/a/@href");
			for (DomAttr attr : attrs) {
				System.out.println(attr.getValue());
				Future<Void> future = service.submit(() -> {
					parse("http://www.gatherproxy.com" + attr.getValue());
					return null;
				});
				futures.put("http://www.gatherproxy.com" + attr.getValue(), future);
			}
			for (Map.Entry<String, Future<Void>> entry : futures.entrySet()) {
				try {
					entry.getValue().get();
				} catch (Exception e) {
					System.out.println(entry.getKey());
					e.printStackTrace();
				}
			}
		});
	}

	public static void parse(String url) throws Exception {
		kjBrowser.execute(t -> {
			String name = url.substring(url.indexOf("=") + 1);
			File file = new File(System.getProperty("user.home") + File.separator + "kj" + File.separator + "crawler"
					+ File.separator + name + ".list");
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));

			HtmlPage page = t.getPage(url);
			parseTable(writer, page, "//div[@class='proxy-list']/table[@id='tblproxy']");
			List<HtmlSubmitInput> submits = page.getByXPath("//form/p/input[@type='submit']");
			if (!submits.isEmpty()) {
				HtmlSubmitInput submit = submits.get(0);
				page = submit.click();
				List<HtmlAnchor> anchors = page.getByXPath("//form[@id='psbform']/div[@class='pagenavi']/a");
				for (HtmlAnchor anchor : anchors) {
					parseTable(writer, (HtmlPage) anchor.click(), "//div[@class='proxy-list']/table[@id='tblproxy']");
				}
			}

			writer.close();

		});
	}

	public static void parseTable(PrintWriter writer, HtmlPage page, String xpath) {
		List<HtmlTable> tables = page.getByXPath(xpath);
		for (HtmlTable table : tables) {
			List<HtmlTableBody> bodies = table.getBodies();
			for (HtmlTableBody body : bodies) {
				List<HtmlTableRow> rows = body.getRows();
				for (HtmlTableRow row : rows) {
					List<HtmlTableCell> cells = row.getCells();
					if (writer == null) {
						for (HtmlTableCell cell : cells) {
							System.out.print(cell.asText() + ",");
						}
						System.out.println();
					} else {
						for (HtmlTableCell cell : cells) {
							writer.print(cell.asText() + ",");
						}
						writer.println();
					}
				}
			}
		}
	}

}
