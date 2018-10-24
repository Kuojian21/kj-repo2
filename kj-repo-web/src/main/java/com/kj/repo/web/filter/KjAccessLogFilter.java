package com.kj.repo.web.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.kj.repo.util.reader.KjReader;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kj
 */
@Slf4j
public class KjAccessLogFilter implements Filter {

	private final Gson gson = new Gson();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String contentType = request.getContentType();
		HttpServletRequest req = (HttpServletRequest) request;
		if (Strings.isNullOrEmpty(contentType) || contentType.indexOf("application/json") < 0) {
			log.info(this.getClass().getName() + ":{}:{}", req.getRequestURI(), gson.toJson(request.getParameterMap()));
		} else {
			AccessLogServletRequestWrapper requestWrapper = new AccessLogServletRequestWrapper(req);
			StringBuilder json = new StringBuilder();
			String line = null;
			BufferedReader br = requestWrapper.getReader();
			while ((line = br.readLine()) != null) {
				json.append(line);
			}
			request = requestWrapper;
			log.info(this.getClass().getName() + ":{}:{}", req.getRequestURI(), json);
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

	/**
	 * @author kj
	 */
	public class AccessLogServletRequestWrapper extends HttpServletRequestWrapper {

		private final byte[] body;

		public AccessLogServletRequestWrapper(HttpServletRequest request) throws IOException {
			super(request);
			body = KjReader.readBytes(request.getInputStream());
		}

		@Override
		public BufferedReader getReader() throws IOException {
			return new BufferedReader(new InputStreamReader(getInputStream()));
		}

		@Override
		public ServletInputStream getInputStream() {
			final ByteArrayInputStream bais = new ByteArrayInputStream(body);
			return new ServletInputStream() {

				@Override
				public int read() throws IOException {
					return bais.read();
				}

				@Override
				public boolean isFinished() {
					return bais.available() <= 0;
				}

				@Override
				public boolean isReady() {
					return true;
				}

				@Override
				public void setReadListener(ReadListener readListener) {

				}
			};
		}
	}

}
