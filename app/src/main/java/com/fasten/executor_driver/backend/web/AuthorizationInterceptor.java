package com.fasten.executor_driver.backend.web;

import android.support.annotation.NonNull;
import io.reactivex.subjects.Subject;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Отправляет событие "не авторизован" если сервер ответил 401 (unauthorized / token expired).
 */
public class AuthorizationInterceptor implements Interceptor {

  private final static String HEADER_NAME = "Code";

  @NonNull
  private final Subject<String> logoutEventSubject;

  @Inject
  public AuthorizationInterceptor(@NonNull Subject<String> logoutEventSubject) {
    this.logoutEventSubject = logoutEventSubject;
  }

  @Override
  public Response intercept(@NonNull Chain chain) throws IOException {
    // Проверить
    Response response = chain.proceed(chain.request());
    List<String> headers = response.headers(HEADER_NAME);
    if (response.code() == 401 && !headers.isEmpty()) {
      logoutEventSubject.onNext("");
    }
    return response;
  }
}
