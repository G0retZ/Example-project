package com.fasten.executor_driver.presentation.code;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.fasten.executor_driver.presentation.ViewState;

/**
 * ViewModel окна ввода кода.
 */
public interface CodeViewModel {

  /**
   * Возвращает состояние вида для применения.
   *
   * @return - {@link ViewState} состояние вида
   */
  @NonNull
  LiveData<ViewState<CodeViewActions>> getViewStateLiveData();

  /**
   * Передает введенный/измененный код для валидации и проверки.
   *
   * @param code - код из звонка или смс.
   */
  void setCode(@NonNull String code);

  /**
   * Запрашивает отправку СМС с кодом.
   */
  void sendMeSms();
}
