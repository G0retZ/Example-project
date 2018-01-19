package com.fasten.executor_driver.backend.web;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Перехватчик для подстановки полученного токена в заголовки
 */
class SendTokenInterceptor implements Interceptor {

	private final static String HEADER_NAME = "Authorization";

	private final TokenKeeper tokenKeeper;

	SendTokenInterceptor(TokenKeeper tokenKeeper) {
		this.tokenKeeper = tokenKeeper;
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request.Builder builder = chain.request().newBuilder();
		String token = tokenKeeper.getToken();
		if (token != null) {
			System.out.println("OkHttp Adding Header: " + token);
			builder.addHeader(HEADER_NAME, token);
		}
		return chain.proceed(builder.build());
	}

}