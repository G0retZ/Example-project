package com.fasten.executor_driver.presentation.persistence;

import android.support.annotation.StringRes;

/**
 * Действия для смены состояния сервиса присутствия.
 */
public interface PersistenceViewActions {

  /**
   * Запустить сервис присутсвия с заголовком и текстом.
   *
   * @param title ИД строки с заголовком
   * @param text ИД строки с текстом
   */
  void startService(@StringRes int title, @StringRes int text);

  /**
   * Остановить сервис присутсвия.
   */
  void stopService();
}
