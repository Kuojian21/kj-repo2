package com.kj.repo.browser;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.kj.repo.bean.pool.KjFactoryImpl;
import com.kj.repo.bean.pool.KjPool;

/**
 * @author kuojian21 http://htmlunit.sourceforge.net/
 */
public class KjBrowser extends KjPool<WebClient> {

    public KjBrowser(BrowserVersion version) {
        super(new KjFactoryImpl<WebClient>(100) {
            @Override
            public WebClient create() throws Exception {
                WebClient webClient = new WebClient(version);
                webClient.getOptions().setThrowExceptionOnScriptError(false);
                webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
                webClient.getOptions().setActiveXNative(false);
                webClient.getOptions().setCssEnabled(false);
                webClient.getOptions().setJavaScriptEnabled(true);
                webClient.getOptions().setRedirectEnabled(true);
                webClient.getOptions().setActiveXNative(true);
                webClient.setAjaxController(new NicelyResynchronizingAjaxController());
                return webClient;
            }
        });
    }

}
