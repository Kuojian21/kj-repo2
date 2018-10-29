package com.kj.repo.net.http;

import java.nio.charset.CodingErrorAction;

import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.ssl.SSLContexts;

public class KjHttpComponentAsync {

	public static final CloseableHttpAsyncClient DEFAULT;

	static {
		try {
			DEFAULT = getClient(3000, 2000, 1000, "", "", 10000, 1500, false, "", 0);
		} catch (IOReactorException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static CloseableHttpAsyncClient getClient() throws IOReactorException {
		return DEFAULT;
	}

	/**
	 * @param connectionRequestTimeout
	 *            从连接池获取后去连接的超时时间
	 * @param connectTimeout
	 *            连接超时
	 * @param socketTimeout
	 *            等待数据超时时间
	 * @param username
	 * @param password
	 * @param poolSize
	 *            连接池最大连接数
	 * @param maxPerRoute
	 *            每个主机的最大并发
	 * @param proxy
	 * @param host
	 * @param port
	 * @return
	 * @throws IOReactorException
	 */
	public static CloseableHttpAsyncClient getClient(int connectionRequestTimeout, int connectTimeout,
			int socketTimeout, String username, String password, int poolSize, int maxPerRoute, boolean proxy,
			String host, int port) throws IOReactorException {

		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)
				.setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();

		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, credentials);

		// 设置协议http和https对应的处理socket链接工厂的对象
		SSLContext sslcontext = SSLContexts.createDefault();
		Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = RegistryBuilder.<SchemeIOSessionStrategy>create()
				.register("http", NoopIOSessionStrategy.INSTANCE)
				.register("https", new SSLIOSessionStrategy(sslcontext)).build();

		// 配置io线程
		IOReactorConfig ioReactorConfig = IOReactorConfig.custom().setSoKeepAlive(false).setTcpNoDelay(true)
				.setIoThreadCount(Runtime.getRuntime().availableProcessors()).build();
		// 设置连接池大小
		ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
		PoolingNHttpClientConnectionManager conMgr = new PoolingNHttpClientConnectionManager(ioReactor,
				sessionStrategyRegistry);
		conMgr.setMaxTotal(poolSize);
		conMgr.setDefaultMaxPerRoute(maxPerRoute);
		ConnectionConfig connectionConfig = ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE)
				.setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8).build();
		Lookup<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create()
				.register(AuthSchemes.BASIC, new BasicSchemeFactory())
				.register(AuthSchemes.DIGEST, new DigestSchemeFactory())
				.register(AuthSchemes.NTLM, new NTLMSchemeFactory())
				.register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory())
				.register(AuthSchemes.KERBEROS, new KerberosSchemeFactory()).build();
		conMgr.setDefaultConnectionConfig(connectionConfig);

		CloseableHttpAsyncClient client = null;
		if (proxy) {
			client = HttpAsyncClients.custom().setConnectionManager(conMgr)
					.setDefaultCredentialsProvider(credentialsProvider).setDefaultAuthSchemeRegistry(authSchemeRegistry)
					.setProxy(new HttpHost(host, port)).setDefaultCookieStore(new BasicCookieStore())
					.setDefaultRequestConfig(requestConfig).build();
		} else {
			client = HttpAsyncClients.custom().setConnectionManager(conMgr)
					.setDefaultCredentialsProvider(credentialsProvider).setDefaultAuthSchemeRegistry(authSchemeRegistry)
					.setDefaultCookieStore(new BasicCookieStore()).build();
		}
		client.start();
		return client;
	}

}
