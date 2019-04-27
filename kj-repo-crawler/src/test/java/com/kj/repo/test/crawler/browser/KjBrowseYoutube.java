package com.kj.repo.test.crawler.browser;

import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.kj.repo.browser.KjBrowser;

public class KjBrowseYoutube {

	public static KjBrowser kjBrowser = new KjBrowser(BrowserVersion.CHROME);

	public static void main(String[] args) throws Exception {
		kjBrowser.execute(t -> {
			HtmlPage page = t
							.getPage("https://www.youtube.com/results?search_query=%E7%81%AB%E5%BD%B1%E5%BF%8D%E7%9D%80");
			System.out.println(page.asXml());
			List<HtmlForm> forms = page.getByXPath("//form[@id='captcha-form']");
			page = forms.get(0).click();
			System.out.println(page.asXml());
		});
	}

}
