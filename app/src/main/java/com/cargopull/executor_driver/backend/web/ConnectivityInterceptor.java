package com.cargopull.executor_driver.backend.web;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.IOException;
import javax.inject.Inject;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Перехватчик для проверки соединения с сетью. Если соединения нет, то кидает соответствующее
 * исключение.
 */
public class ConnectivityInterceptor implements Interceptor {

  private final ConnectivityManager connectivityManager;

  @Inject
  public ConnectivityInterceptor(ConnectivityManager connectivityManager) {
    this.connectivityManager = connectivityManager;
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    if (!isOnline()) {
      throw new NoNetworkException();
    }
    Request.Builder builder = chain.request().newBuilder();
    return chain.proceed(builder.build());
  }

  private boolean isOnline() {
    if (connectivityManager == null) {
      return false;
    }
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnected();
  }
}
