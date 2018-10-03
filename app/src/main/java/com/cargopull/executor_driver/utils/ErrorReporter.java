package com.cargopull.executor_driver.utils;

import android.support.annotation.NonNull;

/**
 * Отправитель отчетов об ошибках
 */
public interface ErrorReporter {

  /**
   * Отправить отчет об ошибке.
   *
   * @param throwable - ошибка
   */
  void reportError(@NonNull Throwable throwable);
}
