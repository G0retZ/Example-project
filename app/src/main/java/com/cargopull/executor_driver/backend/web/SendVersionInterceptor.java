package com.cargopull.executor_driver.backend.web;

import com.cargopull.executor_driver.BuildConfig;
import java.io.IOException;
import javax.inject.Inject;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Перехватчик для подстановки версии приложения в заголовки.
 */
public class SendVersionInterceptor implements Interceptor {

  private final static String HEADER_NAME = "X-app-version";

  @Inject
  public SendVersionInterceptor() {
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request.Builder builder = chain.request().newBuilder();
    System.out.println("OkHttp Adding Header: android-" + BuildConfig.VERSION_NAME);
    builder.addHeader(HEADER_NAME, "android-" + BuildConfig.VERSION_NAME);
    return chain.proceed(builder.build());
  }
}