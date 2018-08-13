package com.cargopull.executor_driver.backend.settings;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Хранитель настроек.
 */

public interface AppSettingsService {

  /**
   * Получить строковые данные.
   *
   * @param key - ключ данных
   */
  @Nullable
  String getData(@NonNull String key);

  /**
   * Сохранить строковые данные.
   *
   * @param key - ключ данных
   * @param data - сами данные
   */
  void saveData(@NonNull String key, @Nullable String data);

  /**
   * Получить зашифрованные строковые данные.
   *
   * @param raw - ключ шифрования
   * @param salt - соль шифрования
   * @param key - ключ данных
   */
  @Nullable
  String getEncryptedData(@NonNull byte[] raw, @NonNull byte[] salt, @NonNull String key);

  /**
   * Сохранить строковые данные в зашифрованном виде.
   *
   * @param raw - ключ шифрования
   * @param salt - соль шифрования
   * @param key - ключ данных
   * @param data - сами данные
   */
  void saveEncryptedData(@NonNull byte[] raw, @NonNull byte[] salt, @NonNull String key,
      @Nullable String data);
}