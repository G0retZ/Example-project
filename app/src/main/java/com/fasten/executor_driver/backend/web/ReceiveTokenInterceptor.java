package com.fasten.executor_driver.backend.web;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Перехватчик для сохранения полученного токена из заголовка
 */
@SuppressWarnings("unused")
class ReceiveTokenInterceptor implements Interceptor {

	private final static String HEADER_NAME = "Authorization";

	private final TokenKeeper tokenKeeper;

	ReceiveTokenInterceptor(TokenKeeper tokenKeeper) {
		this.tokenKeeper = tokenKeeper;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Response originalResponse = chain.proceed(chain.request());
		List<String> headers = originalResponse.headers(HEADER_NAME);
		if (!headers.isEmpty()) {
			tokenKeeper.saveToken(headers.get(0));
		}
		return originalResponse;
	}

}