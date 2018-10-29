package com.kj.repo.test.net.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.concurrent.FutureCallback;

import com.google.common.collect.Lists;
import com.kj.repo.net.http.KjHttpClient;

public class HttpClientTest {

	public static void main(String[] args) {

		for (int i = 0; i < 100000; i++) {
			KjHttpClient.httpAsyncPost(args[0], Lists.newArrayList(KjHttpClient.newHeader("TOKEN", args[1])),
					KjHttpClient.newHttpEntity("{\"accountId\":\"1111111\"}"), new FutureCallback<HttpResponse>() {

						@Override
						public void completed(HttpResponse result) {
							try {
								System.out.print(KjHttpClient.toString(result.getEntity()));
							} catch (ParseException | IOException e) {
								System.out.println("exception " + e.getClass().getName() + " " + e.getMessage());
								e.printStackTrace();
							}
						}

						@Override
						public void failed(Exception ex) {
							System.out.println("exception " + ex.getClass().getName() + " " + ex.getMessage());
						}

						@Override
						public void cancelled() {
							System.out.println("cancelled");
						}
					});
		}

	}

}
