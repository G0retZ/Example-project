package com.fasten.executor_driver.entity;

import android.support.annotation.Nullable;

/**
 * Извлекатель кода из тела сообщения
 */
public interface CodeExtractor {

  /**
   * Извлечь код из тела сообщения.
   *
   * @param message {@link String} сообщения
   * @return извлеченный код строкой
   */
  @Nullable
  String extractCode(@Nullable String message);
}
