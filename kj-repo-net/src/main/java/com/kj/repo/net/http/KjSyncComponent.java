package com.kj.repo.net.http;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KjSyncComponent {

    public static final CloseableHttpClient DEFAULT;

    static {
        try {
            DEFAULT = getClient(3000, 1000, 2000, 200, 20);
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static CloseableHttpClient getClient() {
        return DEFAULT;
    }

    /**
     * @param connectionRequestTimeout
     * @param connectTimeout
     * @param socketTimeout
     * @param maxTotal
     * @param maxPerRoute
     * @return
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public static CloseableHttpClient getClient(int connectionRequestTimeout, int connectTimeout, int socketTimeout,
                                                int maxTotal, int maxPerRoute) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();

        SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", new SSLConnectionSocketFactory(sslcontext,
                        SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
                .build();
        PoolingHttpClientConnectionManager poolConnManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);
        poolConnManager.setMaxTotal(maxTotal);
        poolConnManager.setDefaultMaxPerRoute(maxPerRoute);

        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(socketTimeout).build();
        poolConnManager.setDefaultSocketConfig(socketConfig);

        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(poolConnManager)
                .setDefaultRequestConfig(requestConfig).build();
        if (poolConnManager != null && poolConnManager.getTotalStats() != null) {
            log.info("now client pool " + poolConnManager.getTotalStats().toString());
        }
        return httpClient;
    }
}
