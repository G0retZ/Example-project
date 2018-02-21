package com.fasten.executor_driver.presentation.code;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна ввода кода.
 */
public interface CodeViewModel extends ViewModel<CodeViewActions> {

  /**
   * Передает введенный/измененный код для валидации и проверки.
   *
   * @param code - код из звонка или смс
   */
  void setCode(@NonNull String code);
}
