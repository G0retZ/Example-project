package com.fasten.executor_driver.backend.web;

import android.support.annotation.NonNull;
import java.io.IOException;
import java.util.List;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Перехватчик для проверки авторизации. Если сервер ответил 401 (unauthorized / token expired) с
 * заголовком Code не равным 401.0, то кидает соответствующее исключение.
 */
public class AuthorizationInterceptor implements Interceptor {

  private final static String HEADER_NAME = "Code";

  @Override
  public Response intercept(@NonNull Chain chain) throws IOException {
    // Проверить
    Response response = chain.proceed(chain.request());
    List<String> headers = response.headers(HEADER_NAME);
    if (response.code() == 401 && !headers.contains("401.0")) {
      throw new AuthorizationException();
    }
    return response;
  }
}
