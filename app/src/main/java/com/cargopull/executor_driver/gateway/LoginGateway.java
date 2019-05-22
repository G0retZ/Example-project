package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cargopull.executor_driver.backend.settings.AppSettingsService;
import com.cargopull.executor_driver.interactor.PersistentDataSharer;
import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

import io.reactivex.Observable;

public class LoginGateway extends PersistentDataSharer<String> {

  @Inject
  public LoginGateway(@NonNull AppSettingsService appSettingsService) {
    super(appSettingsService);
  }

  @NonNull
  @Override
  protected String getKey() {
    return "authorizationLogin";
  }

  @Nullable
  @Override
  protected String serialize(@Nullable String data) {
    return data;
  }

  @NonNull
  @Override
  protected String deserialize(@NonNull String string) {
    return string;
  }

  @NonNull
  @Override
  public Observable<String> get() {
    return super.get().doOnNext(identifier -> {
      try{
        Crashlytics.setUserIdentifier(identifier);
      } catch (Exception ignored) {
      }
    });
  }
}
