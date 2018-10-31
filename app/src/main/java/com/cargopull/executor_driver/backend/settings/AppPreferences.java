package com.cargopull.executor_driver.backend.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
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

  @Override
  public int getNumber(@NonNull String key) {
    return preferences.getInt(key, 0);
  }

  @Override
  public void saveNumber(@NonNull String key, int number) {
    preferences.edit().putInt(key, number).apply();
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
  public String getEncryptedData(@NonNull byte[] raw, @NonNull byte[] salt, @NonNull String key) {
    String value = preferences.getString(key, null);
    return value == null ? null : decrypt(raw, salt, value);
  }

  @Override
  public void saveEncryptedData(@NonNull byte[] raw, @NonNull byte[] salt, @NonNull String key,
      @Nullable String data) {
    preferences.edit().putString(key, data == null ? null : encrypt(raw, salt, data)).apply();
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Nullable
  private String encrypt(@NonNull byte[] raw, @NonNull byte[] salt, @NonNull String data) {
    try {
      byte[] dataBytes = data.getBytes("UTF-8");
      IvParameterSpec parameterSpec = new IvParameterSpec(salt);
      SecretKey secret_key = new SecretKeySpec(raw, "AES");
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, secret_key, parameterSpec);
      byte[] encrypted = cipher.doFinal(dataBytes);
      return Base64.encodeToString(encrypted, Base64.DEFAULT);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Nullable
  private String decrypt(@NonNull byte[] raw, @NonNull byte[] salt, @NonNull String data) {
    try {
      byte[] dataBytes = Base64.decode(data, Base64.DEFAULT);
      IvParameterSpec parameterSpec = new IvParameterSpec(salt);
      SecretKey secret_key = new SecretKeySpec(raw, "AES");
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, secret_key, parameterSpec);
      byte[] decrypted = cipher.doFinal(dataBytes);
      return new String(decrypted, "UTF-8");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
