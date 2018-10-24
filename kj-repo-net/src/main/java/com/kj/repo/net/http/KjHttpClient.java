package com.kj.repo.net.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.Function;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.kj.repo.base.future.KjFuture;
import com.kj.repo.util.resource.KjResource;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class KjHttpClient {

    public static HttpEntity newHttpEntity(List<BasicNameValuePair> list) {
        return new UrlEncodedFormEntity(list, Charset.forName("UTF-8"));
    }

    public static HttpEntity newHttpEntity(String json) {
        StringEntity stringEntity = new StringEntity(json, "UTF-8");
        stringEntity.setContentEncoding("UTF-8");
        stringEntity.setContentType("application/json");
        return stringEntity;
    }

    public static RequestBody newRequestBody(String json) {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
    }
    
    

    public static String toString(HttpEntity entity) throws ParseException, IOException {
        return EntityUtils.toString(entity, "UTF-8");
    }

    public static <T> T httpSyncGet(String baseUrl, Function<CloseableHttpResponse, T> func)
            throws ClientProtocolException, IOException {
        CloseableHttpResponse response = null;
        try {
            HttpGet httpGet = new HttpGet(baseUrl);
            response = KjHttpComponentSync.DEFAULT.execute(httpGet);
            return func.apply(response);
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
    }

    public static <T> T httpSyncPost(String baseUrl, HttpEntity httpEntity, Function<CloseableHttpResponse, T> func)
            throws ClientProtocolException, IOException {
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(baseUrl);
            httpPost.setEntity(httpEntity);
            response = KjHttpComponentSync.DEFAULT.execute(httpPost);
            return func.apply(response);
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
    }

    public static Future<HttpResponse> httpAsyncGet(String baseUrl, FutureCallback<HttpResponse> callback) {
        try {
            HttpGet httpGet = new HttpGet(baseUrl);
            return KjHttpComponentAsync.DEFAULT.execute(httpGet, callback);
        } catch (Exception e) {
            e.printStackTrace();
            return new FutureTask<HttpResponse>(new Callable<HttpResponse>() {
                @Override
                public HttpResponse call() throws Exception {
                    throw e;
                }
            });
        }
    }

    public static Future<HttpResponse> httpAsyncPost(String baseUrl, HttpEntity httpEntity,
                                                     FutureCallback<HttpResponse> callback) {
        try {
            HttpPost httpPost = new HttpPost(baseUrl);
            httpPost.setEntity(httpEntity);
            return KjHttpComponentAsync.DEFAULT.execute(httpPost, callback);
        } catch (Exception e) {
            e.printStackTrace();
            return new FutureTask<HttpResponse>(new Callable<HttpResponse>() {
                @Override
                public HttpResponse call() throws Exception {
                    throw e;
                }
            });
        }
    }

    public static <T> T okSyncGet(String url, Function<Response, T> func) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            response = KjOkHttp.DEFAULT.newCall(request).execute();
            return func.apply(response);
        } finally {
            KjResource.close(response);
        }

    }

    public static <T> T okSyncPost(String url, RequestBody body, Function<Response, T> func) throws IOException {
        Request request = new Request.Builder().url(url).post(body).build();
        Response response = null;
        try {
            response = KjOkHttp.DEFAULT.newCall(request).execute();
            return func.apply(response);
        } finally {
            KjResource.close(response);
        }

    }

    public static <T> KjFuture<T> okAsyncGet(String url, Function<Response, T> func) {
        KjFuture<T> future = new KjFuture<T>();
        Request request = new Request.Builder().url(url).build();
        Call call = KjOkHttp.DEFAULT.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.setException(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                future.set(func.apply(response));
            }
        });
        return future;
    }

    public static <T> KjFuture<T> okAsyncPost(String url, RequestBody body, Function<Response, T> func) {
        KjFuture<T> future = new KjFuture<T>();
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = KjOkHttp.DEFAULT.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.setException(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                future.set(func.apply(response));
            }
        });
        return future;
    }

}
