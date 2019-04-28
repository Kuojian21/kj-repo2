package com.kj.infra.pool;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.google.common.collect.Lists;

/**
 * @author kuojian21 http://htmlunit.sourceforge.net/
 */
public class KjBrowser<T> {

	private final GenericObjectPool<T> pool;

	public KjBrowser(final GenericObjectPool<T> pool) {
		this.pool = pool;
	}

	public final <R> R execute(Function<T, R> function) {
		T t = null;
		try {
			t = this.pool.borrowObject();
			return function.apply(t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (t != null) {
				this.pool.returnObject(t);
			}
		}
	}

	public final void execute(Consumer<T> consumer) {
		T t = null;
		try {
			t = this.pool.borrowObject();
			consumer.accept(t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (t != null) {
				this.pool.returnObject(t);
			}
		}
	}

	public static KjBrowser<WebClient> browser(BrowserVersion version) {
		return new KjBrowser<WebClient>(new GenericObjectPool<WebClient>(new BasePooledObjectFactory<WebClient>() {
			@Override
			public PooledObject<WebClient> wrap(WebClient webClient) {
				return new DefaultPooledObject<WebClient>(webClient);
			}

			@Override
			public WebClient create() throws Exception {
				WebClient webClient = new WebClient(version);
				webClient.getOptions().setThrowExceptionOnScriptError(false);
				webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
				webClient.getOptions().setActiveXNative(true);
				webClient.getOptions().setCssEnabled(true);
				webClient.getOptions().setJavaScriptEnabled(true);
				webClient.getOptions().setRedirectEnabled(true);
				webClient.getOptions().setActiveXNative(true);
				webClient.setAjaxController(new NicelyResynchronizingAjaxController());
				return webClient;
			}

			@Override
			public void destroyObject(final PooledObject<WebClient> obj)
							throws Exception {
				obj.getObject().close();
			}
		}));
	}

	/**
	 * 
	 * @author kuojian21
	 *
	 */
	public static class Helper {
		public static <T> List<String> parseText(HtmlPage page, String xpath) {
			List<String> result = Lists.newArrayList();
			List<T> nodes = page.getByXPath(xpath);
			for (T node : nodes) {
				switch (node.getClass().getName()) {
				case "DomAttr":
					result.addAll(parse((DomAttr) node));
					break;
				case "HtmlTable":
					result.addAll(parse((HtmlTable) node));
					break;
				default:

				}

			}
			return result;
		}

		public static <T> List<String> parseURL(HtmlPage page, String xpath) {
			List<String> result = Lists.newArrayList();
			List<T> nodes = page.getByXPath(xpath);
			for (T node : nodes) {
				switch (node.getClass().getName()) {
				case "DomAttr":
					result.addAll(parse((DomAttr) node));
					break;
				case "HtmlTable":
					break;
				default:

				}

			}
			return result;
		}

		public static List<String> parse(DomAttr node) {
			return Lists.newArrayList(node.getValue().trim());
		}

		public static List<String> parse(HtmlTable node) {
			List<String> result = Lists.newArrayList();
			List<HtmlTableBody> bodies = node.getBodies();
			for (HtmlTableBody body : bodies) {
				for (HtmlTableRow row : body.getRows()) {
					for (HtmlTableCell cell : row.getCells()) {
						result.add(cell.asText().trim());
					}
				}
			}
			return result;
		}

	}

}
