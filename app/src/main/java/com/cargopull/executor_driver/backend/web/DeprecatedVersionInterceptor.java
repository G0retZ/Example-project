package com.cargopull.executor_driver.backend.web;

import androidx.annotation.NonNull;
import java.io.IOException;
import javax.inject.Inject;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Перехватчик для проверки допустимости версии приложения. Если сервер ответил 403 (Forbidden) с, то кидает соответствующее исключение.
 */
public class DeprecatedVersionInterceptor implements Interceptor {

  @Inject
  public DeprecatedVersionInterceptor() {
  }

  @Override
  public Response intercept(@NonNull Chain chain) throws IOException {
    // Проверить
    Response response = chain.proceed(chain.request());
    if (response.code() == 403) {
      throw new DeprecatedVersionException();
    }
    return response;
  }
}
