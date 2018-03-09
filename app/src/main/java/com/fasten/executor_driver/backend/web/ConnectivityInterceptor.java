package com.fasten.executor_driver.backend.web;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
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

  @NonNull
  private final Context context;

  @Inject
  public ConnectivityInterceptor(@NonNull Context context) {
    this.context = context;
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
    ConnectivityManager connectivityManager = (ConnectivityManager)
        context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivityManager == null) {
      return false;
    }
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnected();
  }
}
