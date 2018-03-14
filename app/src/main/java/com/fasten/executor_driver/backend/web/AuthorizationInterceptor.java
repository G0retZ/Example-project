package com.fasten.executor_driver.backend.web;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.interactor.UnAuthGateway;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.CompletableSubject;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Отправляет событие подписчикам если сервер ответил 401 (unauthorized / token expired)
 * с заголовком Code не равным 401.0.
 */
public class AuthorizationInterceptor implements Interceptor, UnAuthGateway {

  private final static String HEADER_NAME = "Code";

  @NonNull
  private CompletableSubject logoutEventSubject;

  @Inject
  public AuthorizationInterceptor() {
    logoutEventSubject = CompletableSubject.create();
  }

  @Override
  public Response intercept(@NonNull Chain chain) throws IOException {
    // Проверить
    Response response = chain.proceed(chain.request());
    List<String> headers = response.headers(HEADER_NAME);
    if (response.code() == 401 && !headers.contains("401.0")) {
      logoutEventSubject.onComplete();
      logoutEventSubject = CompletableSubject.create();
    }
    return response;
  }

  @NonNull
  @Override
  public Completable waitForUnauthorized() {
    return logoutEventSubject
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.single());
  }
}
