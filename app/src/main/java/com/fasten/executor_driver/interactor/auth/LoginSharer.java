package com.fasten.executor_driver.interactor.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasten.executor_driver.backend.settings.AppSettingsService;
import com.fasten.executor_driver.interactor.PersistentDataSharer;
import javax.inject.Inject;

public class LoginSharer extends PersistentDataSharer<String> {

  @Inject
  LoginSharer(@NonNull AppSettingsService appSettingsService) {
    super(appSettingsService);
  }

  @NonNull
  @Override
  protected String getKey() {
    return "authorizationLogin";
  }

  @Nullable
  @Override
  protected String serialize(String data) {
    return data;
  }

  @Nullable
  @Override
  protected String deserialize(String string) {
    return string;
  }
}
