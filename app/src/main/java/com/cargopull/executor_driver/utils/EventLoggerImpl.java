package com.cargopull.executor_driver.utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.cargopull.executor_driver.interactor.DataReceiver;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.inject.Inject;

@SuppressWarnings("unused")
public class EventLoggerImpl implements EventLogger {

  @NonNull
  private final DataReceiver<String> loginReceiver;
  @NonNull
  private final FirebaseAnalytics mFirebaseAnalytics;

  @Inject
  public EventLoggerImpl(@NonNull DataReceiver<String> loginReceiver,
      @NonNull FirebaseAnalytics mFirebaseAnalytics) {
    this.loginReceiver = loginReceiver;
    this.mFirebaseAnalytics = mFirebaseAnalytics;
  }

  @Override
  public void reportEvent(@NonNull String event, @NonNull HashMap<String, String> params) {
    loginReceiver.get()
        .firstOrError()
        .subscribe(
            login -> {
              Bundle bundle = new Bundle();
              for (Entry<String, String> entry : params.entrySet()) {
                bundle.putString(entry.getKey(), entry.getValue());
              }
              bundle.putString(Param.CHARACTER, login);
              mFirebaseAnalytics.logEvent(event, bundle);
            },
            throwable1 -> {
              Crashlytics.logException(
                  new Exception("Не удалось выснить номер телефона водителя", throwable1)
              );
              Bundle bundle = new Bundle();
              for (Entry<String, String> entry : params.entrySet()) {
                bundle.putString(entry.getKey(), entry.getValue());
              }
              mFirebaseAnalytics.logEvent(event, bundle);
            }
        ).isDisposed();
  }
}
