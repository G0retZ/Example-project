package com.fasten.executor_driver.backend.web;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Перехватчик для сохранения полученного токена из заголовка
 */
class ReceiveTokenInterceptor implements Interceptor {

	private final static String HEADER_NAME = "Authorization";

	@NonNull
	private final TokenKeeper tokenKeeper;

	ReceiveTokenInterceptor(@NonNull TokenKeeper tokenKeeper) {
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