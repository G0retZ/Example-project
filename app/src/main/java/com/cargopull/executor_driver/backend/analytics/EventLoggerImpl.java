package com.cargopull.executor_driver.backend.analytics;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import com.cargopull.executor_driver.BuildConfig;
import com.cargopull.executor_driver.backend.settings.AppSettingsService;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.inject.Inject;

public class EventLoggerImpl implements EventLogger {

  @NonNull
  private final AppSettingsService appSettings;
  @NonNull
  private final FirebaseAnalytics mFirebaseAnalytics;

  @Inject
  public EventLoggerImpl(@NonNull AppSettingsService appSettings, @NonNull Context context) {
    this.appSettings = appSettings;
    this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
  }

  @Override
  public void reportEvent(@NonNull String event, @NonNull HashMap<String, String> params) {
    String lastPhoneNumber = appSettings.getData("authorizationLogin");
    if (lastPhoneNumber == null) {
      Crashlytics.logException(new RuntimeException("Не удалось выснить номер телефона водителя"));
    } else {
      params.put(Param.CHARACTER, lastPhoneNumber);
    }
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
}
