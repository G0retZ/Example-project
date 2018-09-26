package com.cargopull.executor_driver.backend.web;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.backend.web.incoming.ApiSimpleResult;
import com.google.gson.Gson;
import java.io.IOException;
import javax.inject.Inject;
import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Перехватчик для 4xx - 5xx HTTP кодов, чтобы распарсить их тело в эксепшн с кодом ошибки
 * для дальнейшего использования.
 */
public class ServerResponseInterceptor implements Interceptor {

  private final Gson gson = new Gson();

  @Inject
  public ServerResponseInterceptor() {
  }

  @Override
  public Response intercept(@NonNull Chain chain) throws IOException {
    // Проверить
    Response response = chain.proceed(chain.request());
    ResponseBody body = response.body();
    int httpCode = response.code();
    if (body != null && httpCode >= 400 && httpCode < 600) {
      ServerResponseException serverResponseException;
      try {
        ApiSimpleResult apiSimpleResult = gson.fromJson(body.string(), ApiSimpleResult.class);
        String code = apiSimpleResult.getCode();
        String message = apiSimpleResult.getMessage();
        serverResponseException = new ServerResponseException(
            code != null ? code : String.valueOf(httpCode),
            message != null ? message : ""
        );
      } catch (Exception e) {
        return response;
      }
      throw serverResponseException;
    }
    return response;
  }
}
