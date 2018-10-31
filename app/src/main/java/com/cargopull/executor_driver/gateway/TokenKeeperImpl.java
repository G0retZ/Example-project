package com.cargopull.executor_driver.gateway;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.backend.settings.AppSettingsService;
import com.cargopull.executor_driver.backend.web.TokenKeeper;
import javax.inject.Inject;

public class TokenKeeperImpl implements TokenKeeper {

  private static final String TOKEN = "token";
  private static final byte[] KEY = {
      14, -55, -48, 7, -65, -87, -23, 113, -69, -22, -68, -24, -96, 103, 16, 114
  };
  private static final byte[] SALT = {
      2, -78, 71, 42, 30, -52, 87, 0, -14, 39, 113, -72, -1, 9, 74, -34
  };

  @NonNull
  private final AppSettingsService appSettingsService;

  @Inject
  public TokenKeeperImpl(@NonNull AppSettingsService appSettingsService) {
    this.appSettingsService = appSettingsService;
  }

  @Override
  public void saveToken(String token) {
    appSettingsService.saveEncryptedData(KEY, SALT, TOKEN, token);
  }

  @Override
  public String getToken() {
    return appSettingsService.getEncryptedData(KEY, SALT, TOKEN);
  }
}
