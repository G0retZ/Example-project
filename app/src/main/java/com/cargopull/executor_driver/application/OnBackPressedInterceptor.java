package com.cargopull.executor_driver.application;

import android.app.Activity;

/**
 * Перехватчик нажатия "Назад" в {@link Activity}
 */

public interface OnBackPressedInterceptor {

  /**
   * перехватывает вызов  метода onBackPressed для обработки нажатия кнопки "назад".
   *
   * @return Истину, если нажатие "назад" было обработано, ложь позволяет обработать нажатие
   * вызвавшему этот метод.
   */
  @SuppressWarnings("SameReturnValue")
  boolean onBackPressed();
}
