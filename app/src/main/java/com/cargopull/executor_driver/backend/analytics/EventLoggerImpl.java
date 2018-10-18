package com.cargopull.executor_driver.backend.analytics;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.interactor.DataReceiver;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.inject.Inject;

public class EventLoggerImpl implements EventLogger {

  @NonNull
  private final DataReceiver<String> loginReceiver;
  @NonNull
  private final FirebaseAnalytics mFirebaseAnalytics;

  @Inject
  public EventLoggerImpl(@NonNull DataReceiver<String> loginReceiver, @NonNull Context context) {
    this.loginReceiver = loginReceiver;
    this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
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
              if (BuildConfig.DEBUG) {
                Log.d(getClass().getSimpleName(),
                    "Sending log: " + event + "; " + bundle.toString());
              }
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
              if (BuildConfig.DEBUG) {
                Log.d(getClass().getSimpleName(),
                    "Sending log: " + event + "; " + bundle.toString());
              }
              mFirebaseAnalytics.logEvent(event, bundle);
            }
        ).isDisposed();
  }
}
