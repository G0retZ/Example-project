package com.cargopull.executor_driver.backend.analytics;

import android.util.Log;

import androidx.annotation.NonNull;

import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.backend.web.AuthorizationException;
import com.cargopull.executor_driver.backend.web.ConnectionClosedException;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.backend.web.ServerResponseException;
import com.cargopull.executor_driver.entity.OrderOfferDecisionException;
import com.cargopull.executor_driver.entity.OrderOfferExpiredException;
import com.crashlytics.android.Crashlytics;

public class ErrorReporterImpl implements ErrorReporter {

  @Override
  public void reportError(@NonNull Throwable throwable) {
    if (BuildConfig.DEBUG) {
      Log.w(getClass().getSimpleName(), throwable);
    }
    // Игнорируем ошибки доступа исетевые ошибки
    if (throwable instanceof SecurityException
        || throwable instanceof AuthorizationException
        || throwable instanceof ServerResponseException
        || throwable instanceof NoNetworkException
        || throwable instanceof ConnectionClosedException
        || throwable instanceof OrderOfferExpiredException
        || throwable instanceof OrderOfferDecisionException) {
      return;
    }
    Crashlytics.logException(throwable);
  }
}
