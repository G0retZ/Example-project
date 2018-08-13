package com.cargopull.executor_driver.utils;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.backend.web.AuthorizationException;
import com.cargopull.executor_driver.backend.web.NoNetworkException;
import com.cargopull.executor_driver.backend.websocket.ConnectionClosedException;
import com.cargopull.executor_driver.interactor.DataReceiver;
import com.crashlytics.android.Crashlytics;
import javax.inject.Inject;

public class ErrorReporterImpl implements ErrorReporter {

  @NonNull
  private final DataReceiver<String> loginReceiver;

  @Inject
  public ErrorReporterImpl(@NonNull DataReceiver<String> loginReceiver) {
    this.loginReceiver = loginReceiver;
  }

  @Override
  public void reportError(Throwable throwable) {
    if (BuildConfig.DEBUG) {
      throwable.printStackTrace();
    }
    // Игнорируем сетевые ошибки
    if (throwable instanceof AuthorizationException || throwable instanceof NoNetworkException
        || throwable instanceof ConnectionClosedException) {
      return;
    }
    loginReceiver.get()
        .firstOrError()
        .subscribe(
            login -> Crashlytics.logException(
                new Exception("Ошибка для водителя с номером телефона +" + login, throwable)
            ),
            throwable1 -> {
              Crashlytics.logException(
                  new Exception("Не удалось выснить номер телефона водителя", throwable1)
              );
              Crashlytics.logException(
                  new Exception("Ошибка для водителя", throwable)
              );
            }
        ).isDisposed();
  }
}