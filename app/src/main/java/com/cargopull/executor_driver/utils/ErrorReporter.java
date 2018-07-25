package com.cargopull.executor_driver.utils;

/**
 * Отправитель отчетов об ошибках
 */
public interface ErrorReporter {

  /**
   * Отправить отчет об ошибке.
   *
   * @param throwable - ошибка
   */
  void reportError(Throwable throwable);
}
