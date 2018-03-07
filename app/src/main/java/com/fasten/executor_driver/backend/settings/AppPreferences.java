package com.fasten.executor_driver.backend.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

public class AppPreferences implements AppSettingsService {

  private static final String PREFERENCE_FILE_NAME = "settings";

  @NonNull
  private final SharedPreferences preferences;

  @Inject
  public AppPreferences(@NonNull Context context) {
    this.preferences = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
  }

  // Конструктор для тестов
  @SuppressWarnings("SameParameterValue")
  AppPreferences(@NonNull Context context, boolean clear) {
    this.preferences = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
    if (clear) {
      preferences.edit().clear().apply();
    }
  }

  @Override
  @Nullable
  public String getData(@NonNull String key) {
    return preferences.getString(key, null);
  }

  @Override
  public void saveData(@NonNull String key, @Nullable String data) {
    preferences.edit().putString(key, data).apply();
  }

  @Override
  @Nullable
  public String getEncryptedData(@NonNull byte[] raw, @NonNull String key) {
    String value = preferences.getString(key, null);
    return value == null ? null : decrypt(raw, value);
  }

  @Override
  public void saveEncryptedData(@NonNull byte[] raw, @NonNull String key, @Nullable String data) {
    preferences.edit().putString(key, data == null ? null : encrypt(raw, data)).apply();
  }

  @Nullable
  private String encrypt(@NonNull byte[] raw, @NonNull String data) {
    try {
      byte[] dataBytes = data.getBytes("UTF-8");
      SecretKeySpec sKeySpec = new SecretKeySpec(raw, "AES");
      @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
      byte[] encrypted = cipher.doFinal(dataBytes);
      return Base64.encodeToString(encrypted, Base64.DEFAULT);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Nullable
  private String decrypt(@NonNull byte[] raw, @NonNull String data) {
    try {
      byte[] dataBytes = Base64.decode(data, Base64.DEFAULT);
      SecretKeySpec sKeySpec = new SecretKeySpec(raw, "AES");
      @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.DECRYPT_MODE, sKeySpec);
      byte[] decrypted = cipher.doFinal(dataBytes);
      return new String(decrypted, "UTF-8");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
