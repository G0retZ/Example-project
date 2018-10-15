package com.cargopull.executor_driver.utils;

import android.support.annotation.NonNull;
import java.util.HashMap;

/**
 * Отправитель отчетов о событиях
 */
@SuppressWarnings("WeakerAccess")
public interface EventLogger {

  /**
   * Отправить отчет о событии.
   *
   * @param event - имя события
   * @param params - параметры события
   */
  @SuppressWarnings("unused")
  void reportEvent(@NonNull String event, @NonNull HashMap<String, String> params);
}
