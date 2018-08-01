package com.cargopull.executor_driver.utils;

public interface TimeUtils {

  /**
   * Задать текущее время сервера
   *
   * @param millis ткущее юникс время сервера в миллисекундах
   */
  void setServerCurrentTime(long millis);

  /**
   * Текущее время с учетом временной ошибки с сервером
   *
   * @return текущее юникс время в миллисекундах
   */
  long currentTimeMillis();
}
