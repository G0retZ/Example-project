package com.fasten.executor_driver.presentation.smsbutton;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * ViewModel кнопки отправки СМС с таймером.
 */
public interface SmsButtonViewModel {

  /**
   * Возвращает состояние вида для применения.
   *
   * @return - {@link ViewState} состояние вида.
   */
  @NonNull
  LiveData<ViewState<SmsButtonViewActions>> getViewStateLiveData();

  /**
   * Запрашивает отправку СМС с кодом.
   */
  void sendMeSms();
}
