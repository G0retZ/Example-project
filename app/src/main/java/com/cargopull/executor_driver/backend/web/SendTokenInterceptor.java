package com.cargopull.executor_driver.backend.web;

import androidx.annotation.NonNull;
import java.io.IOException;
import javax.inject.Inject;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Перехватчик для подстановки полученного токена в заголовки.
 */
public class SendTokenInterceptor implements Interceptor {

  private final static String HEADER_NAME = "Authorization";

  @NonNull
  private final TokenKeeper tokenKeeper;

  @Inject
  public SendTokenInterceptor(@NonNull TokenKeeper tokenKeeper) {
    this.tokenKeeper = tokenKeeper;
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request.Builder builder = chain.request().newBuilder();
    if (!chain.request().url().encodedPath().contains("/login")) {
      String token = tokenKeeper.getToken();
      if (token != null) {
        System.out.println("OkHttp Adding Header: " + token);
        builder.addHeader(HEADER_NAME, token);
      }
    }
    return chain.proceed(builder.build());
  }
}